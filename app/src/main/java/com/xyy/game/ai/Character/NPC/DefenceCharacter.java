package com.xyy.game.ai.Character.NPC;

import android.support.annotation.NonNull;
import android.util.Log;

import com.xyy.game.ANN.Data;
import com.xyy.game.ANN.NeuralNet;
import com.xyy.game.AStar.AStarFindPath;
import com.xyy.game.FSM.FSMState;
import com.xyy.game.FSM.FSMTransition;
import com.xyy.game.FSM.FiniteStateMachine;
import com.xyy.game.ai.*;
import com.xyy.game.ai.Attack.AtkInfo;
import com.xyy.game.ai.Attack.Attack;
import com.xyy.game.ai.Attack.EnergyBox;
import com.xyy.game.ai.Attack.GeneralCircleAttack;
import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Attack.LifeBox;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Effect.MultSquareEffect;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.IntArrayList;
import com.xyy.game.util.Line;

import java.util.ArrayList;

/**
 * 一个防御型角色
 * 2017/4/14
 */
public final class DefenceCharacter extends Character implements NPC, Defender{

    public static final Data sData = new Data() {
        public double[][] GetInputSet() {
            return new double[][]{{-0.2,0,0,0.5}, {0,0,0,0.5}, {0.2,0,0,0.5},
                    {0,0,-1,0.5}, {0,0,-0.1,0.5}, {0,0,0.1,0.5},{0,0,1,0.5},
                    {0,-1,-1,0.5}, {0,-0.1,-1,0.5}, {0,0.1,-1,0.5}, {0,1,-1,0.5},
                    {0,-1,-1,0.1}, {0,-1,-1,1} };
        }

        public double[][] GetOutputSet() {
            return new double[][]{{0,1,0,0}, {0,0,0,0},{1,0,0,0},
                    {0,0,1,0}, {0,0,1,0}, {0,0,0,1}, {0,0,0,1},
                    {0,0,0,1}, {0,0,0,1}, {0,0,1,0}, {0,0,1,0},
                    {1,0,0,1}, {0,1,0,1}};
        }
    };

    /**
     * x/y方向上的速度（px/s）
     */
    private float vx, vy;
    /**
     * 旋转角（角度）
     */
    private float rotation;
    /**
     * 攻击间隔计时器
     */
    private float delayTimer;
    /**
     * 攻击间隔
     */
    private float atkDelay;
    /**
     * 适应度
     */
    private float fitness;
    /**
     * 存活时间
     */
    private int liveTime;
    /**
     * 有限状态机
     */
    private FiniteStateMachine FSM;
    /**
     * 输入：到玩家的距离，到最近的攻击对象的距离，到最近的攻击对象的连线与到玩家连线的夹角（弧度）
     * 输出：前/后/左/右
     */
    private NeuralNet neuralNet = new NeuralNet(4, 4, 6);
    /**
     * 缓存网络的输入
     */
    private double[] NetInputs = new double[4];
    /**
     * A*寻路
     */
    private AStarFindPath aStarFindPath;
    /**
     * 寻得的路径
     */
    private IntArrayList path;
    /**
     * true=玩家坐标更改，请求新路径
     */
    private boolean reqNewPath;
    /**
     * 寻路起点，终点
     */
    private int start, end;
    /**
     * 寻路线程
     */
    private Thread findPathThread;
    /**
     * 当前状态
     */
    private boolean findPath;

    private int minDistanceFromPlayer = 300;
    private int distanceSensitivity = 1000;
    private int avoidSensitivity = 200;
    private int defenceSensitivity = 50;

    private boolean shouldAddBuff;

    private Defended mDefended;

    private ArrayList<Character> hostileList;

    public DefenceCharacter(final Stage stage) {
        super(stage);
        this.r = 85;
        setV(200);
        setMaxHp(100);
        setHp(100);
        setAtk(1);
        /**
         * 初始化状态表
         */
        FSMState[] states = new FSMState[]{new State_Active(), new State_FindPath()};
        /**
         * 初始化有限状态机
         */
        FSM = new FiniteStateMachine(states);
        /**
         * 初始化A*寻路
         */
        aStarFindPath = new AStarFindPath();
        /**
         * 初始化当前状态为非寻路状态，即正常运行状态
         */
        findPath = false;

        start = end = -1;

        findPathThread = new Thread() {
            @Override
            public void run() {
                //等待，直到update()被调用
                while (start == -1 || end == -1) {
                    try { sleep(100); } catch (InterruptedException ignored) {}
                }
                while (!isDead()) {
                    if (!findPath) {
                        int ifSee = AStarFindPath.ifSeeDirectly(start, end);
                        if (ifSee == 2) {
                            path = aStarFindPath.findPath(start, end);
                            if(!path.isEmpty()) {
                                reqNewPath = false;
                                findPath = true;
                            }else{
                                findPath = false;
                            }
                        } else {
                            try { sleep(100); } catch (InterruptedException ignored) {}
                        }
                    } else {
                        int ifSee = AStarFindPath.ifSeeDirectly(start, end);
                        if (ifSee == 0) {
                            findPath = false;
                        } else if (reqNewPath) {
                            path = aStarFindPath.findPath(start, end);
                            if(!path.isEmpty()) {
                                reqNewPath = false;
                            }else{
                                findPath = false;
                            }
                        } else {
                            try { sleep(100); } catch (InterruptedException ignored) {}
                        }
                    }
                }
            }
        };
        findPathThread.start();
    }

    public void initialize(@NonNull NPC parent, String name, int x, int y) {
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
        //Log.e(getName(),"initialize x/y = "+x+" / "+y);

        setHp(getMaxHp());
        this.fitness = 0;
        this.liveTime = 0;

        vx = vy = 0;

        atkDelay = (float) (2 + Math.random());

        shouldAddBuff = false;
    }

    @Override
    public Defended defended(){
        return mDefended;
    }

    @Override
    public void defend(Defended defended) {
        if (mDefended != null) {
            unDefend(mDefended);
        }

        mDefended = defended;

        if (defended == null) {
            return;
        }

        defended.onDefendedBy(this);
    }

    @Override
    public void unDefend(Defended defended) {
        if (mDefended == defended) {
            mDefended = null;

            if (defended != null) {
                defended.onLostDefenceBy(this);
            }
        }
    }

    @Override
    public void onDefendedDestroyed(Defended defended) {
        if (defended == null) {
            return;
        }

        unDefend(defended);
    }

    @Override
    public boolean canBeDefended() {
        return false;
    }

    /**
     * 状态转换，从活动到寻路
     * 在FSMState中以线性表的形式储存
     */
    private class Active2FindPath implements FSMTransition {
        /**
         * 如果玩家不能直接可见
         *
         * @return true=玩家不能直接可见，需状态转换
         */
        @Override
        public boolean isValid(float deltaTime, Environment environment) {
            return findPath;
        }

        /**
         * 转换至寻路状态
         *
         * @return 寻路状态
         */
        @Override
        public int getNextState() {
            return 1;
        }
    }

    /**
     * 状态转换，从寻路到活动
     * 在FSMState中以线性表的形式储存
     */
    private class FindPath2Active implements FSMTransition {
        /**
         * 如果玩家直接可见
         *
         * @return true=玩家直接可见，需状态转换
         */
        @Override
        public boolean isValid(float deltaTime, Environment environment) {
            return !findPath;
        }

        /**
         * 转换至活动状态
         *
         * @return 活动状态
         */
        @Override
        public int getNextState() {
            return 0;
        }
    }

    /**
     * 活动状态
     */
    private class State_Active implements FSMState {
        /**
         * 初始化状态转换表
         */
        private FSMTransition[] transitions = new FSMTransition[]{new Active2FindPath()};

        @Override
        public void onEnter() {

        }

        @Override
        public void onUpdate(float deltaTime, Environment environment) {
            active(deltaTime, environment);
        }

        @Override
        public void onExit() {

        }

        @Override
        public FSMTransition[] getTransitions() {
            return transitions;
        }
    }

    /**
     * 寻路状态
     */
    private class State_FindPath implements FSMState {

        private int targetIndex;
        private int targetX, targetY;

        private float oldX, oldY;

        /**
         * 初始化状态转换表
         */
        private FSMTransition[] transitions;

        private State_FindPath() {
            transitions = new FSMTransition[]{new FindPath2Active()};
        }

        @Override
        public void onEnter() {
            targetIndex = path.removeLast();
        }

        @Override
        public void onUpdate(float deltaTime, Environment environment) {
            start = environment.getIndex(x, y);
            end = environment.getPlayerIndex();
            if (environment.isPlayerIndexChanged()) {
                reqNewPath = true;
            } else if (!path.isEmpty() && environment.getIndex(x, y) == targetIndex) {
                targetIndex = path.removeLast();
            }

            targetX = environment.index2X(targetIndex);
            targetY = environment.index2Y(targetIndex);

            //指向目标的向量
            float XComponentToTarget = targetX - x;
            float YComponentToTarget = targetY - y;

            //如果坐标与目标重合，则直接返回
            if(XComponentToTarget==0 && YComponentToTarget==0) return;

            //到目标的距离
            float distanceToTarget = (float) Math.sqrt
                    (XComponentToTarget * XComponentToTarget + YComponentToTarget * YComponentToTarget);

            //指向目标的单位向量
            float XUnitToTarget = XComponentToTarget / distanceToTarget;
            float YUnitToTarget = YComponentToTarget / distanceToTarget;

            //获取bot所处的区块
            Line[] blockOfLines = environment.getBlockOfLines(x, y);

            vx += XUnitToTarget * getV();
            vy += YUnitToTarget * getV();

            //clamp the velocity
            int min = -getV();
            int max = getV();
            float len = (float) Math.sqrt(vx * vx + vy * vy);
            if (len < min) {
                vx = vx / len * min;
                vy = vy / len * min;
            } else if (len > max) {
                vx = vx / len * max;
                vy = vy / len * max;
            }

            oldX = x;
            oldY = y;

            //更新角色坐标
            x += vx * deltaTime;
            y += vy * deltaTime;

            //BOT与地图碰撞检测
            float[] output = new float[2];
            for (Line line : blockOfLines) {
                hitTest(output, x, y, r, line);
                x += output[0];
                y += output[1];
            }

            //旋转角
            float dx = x - oldX;
            float dy = y - oldY;
            float l = (float) Math.sqrt(dx * dx + dy * dy);
            float ux = dx / l;
            float uy = dy / l;
            rotation = (float) (90 + Math.acos((double) ux) * 180 / Math.PI * (uy < 0 ? -1 : 1));

            oldX = x;
            oldY = y;

        }

        @Override
        public void onExit() {

        }

        @Override
        public FSMTransition[] getTransitions() {
            return transitions;
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawPixmapDegree(Assets.hostile, this.x - offsetX, this.y - offsetY, rotation);
    }

    @Override
    protected final void updateInner(float deltaTime, Environment environment) {
        FSM.update(deltaTime, environment);
    }

    @Override
    public void onHitCharacter(Character character, AtkInfo attack) {

    }

    @Override
    public void onHitByCharacter(Character character, AtkInfo attack) {
        if (getHp() <= 0 && shouldAddBuff) {
            character.addBuff(0);
        }
        character.accessEnergy(attack.getEnergy() * 2);//返还2被能量
    }

    @Override
    protected int onDestroyed() {
        stage.accessScore(10);

        //被销毁时产生范围攻击
        GeneralCircleAttack atkObj = new GeneralCircleAttack(stage);
        atkObj.initialize(this, x, y, 0, 0, 0, 25 * getAtk(), 100);
        stage.addAtkHostile(atkObj);
        //产生特效
        MultSquareEffect effect = new MultSquareEffect();
        effect.initialize((int) x, (int) y, 0xFFFF0000, 10, 30, 1, 1, 1);
        stage.addEffect(effect);
        //随机产生能量包或生命包
        double ran = Math.random();
        if(ran>0.9) {
            EnergyBox energyBox = new EnergyBox(stage);
            energyBox.initialize(this, x, y);
            stage.addAtkHostile(energyBox);
            stage.addToTrackList(energyBox, 1);
        }
        else if(ran>0.8){
            LifeBox lifeBox = new LifeBox(stage);
            lifeBox.initialize(this, x, y);
            stage.addAtkHostile(lifeBox);
            stage.addToTrackList(lifeBox,1);
        }
        //向父级回调被销毁
        parent.onChildrenDestroyed(this);
        parent = null;

        return 0;
    }

    @Override
    public void onChildrenDestroyed(NPC child) {
        //No Children, do nothing...
    }

    public int GetNumberOfWeights() {
        return neuralNet.GetNumberOfWeights();
    }

    @Override
    public void putWeights(double[] weights) {
        neuralNet.PutWeights(weights);
    }

    @Override
    public double[] getWeights() {
        return neuralNet.GetWeights();
    }

    @Override
    public float getFitness() {
        return fitness;
    }

    @Override
    public int getLiveTime() {
        return liveTime;
    }

    /**
     * line: Ax + By + C = 0;
     */
    private class MyLine {
        float A;
        float B;
        float C;

        MyLine(float a, float b, float c) {
            this.A = a;
            this.B = b;
            this.C = c;
        }

        float k() throws Exception{
            if (kIsNull()) {
                throw new Exception("k Is Not Existed");
            }
            return - A / B;
        }

        float offset(float x, float y) {
            return (A * x + B * y + C) / (float)Math.sqrt(A * A + B * B);
        }

        boolean kIsNull() {
            return B == 0;
        }

        MyLine perpendicular(float x, float y) {
            if (kIsNull()) {
                return new MyLine(0,A,- A * y);
            }

            float a = B;
            float b = - A;
            float c = A * y - B * x;

            return new MyLine(a,b,c);
        }
    }

    private MyLine getLine(float x1, float y1, float x2, float y2) {
        return new MyLine(y2 - y1, x1 - x2, x2*y1 - x1*y2);
    }

    private void active(float deltaTime, Environment environment) {

        start = environment.getIndex(x, y);
        //Log.e(getName(),"x/y = "+x+" / "+y);
        end = environment.getPlayerIndex();

        hostileList = environment.getHostileList();

        //TODO: 玩家的半径
        final int playerR = 65;

        //指向玩家的向量
        float XComponentToPlayer = environment.getPlayerX() - this.x;
        float YComponentToPlayer = environment.getPlayerY() - this.y;

        //如果坐标与玩家重合，则随机一个值
        if (XComponentToPlayer == 0 && YComponentToPlayer == 0) {
            XComponentToPlayer = (float) Math.random() + 1;
            YComponentToPlayer = (float) Math.random() + 1;
        }

        //到玩家的距离
        float distanceToPlayer = (float) Math.sqrt
                (XComponentToPlayer * XComponentToPlayer + YComponentToPlayer * YComponentToPlayer);

        //指向玩家的单位向量
        final float XUnitToPlayer = XComponentToPlayer / distanceToPlayer;
        final float YUnitToPlayer = YComponentToPlayer / distanceToPlayer;

        //到攻击对象的距离（的平方）
        float distanceToAtk = distanceToPlayer * distanceToPlayer;

        float atkX = environment.getPlayerX();
        float atkY = environment.getPlayerY();

        //遍历攻击对象，获得最近的攻击对象的距离以及坐标
        //如果不存在攻击对象，则以玩家的距离和坐标代替
        ArrayList<Attack> playerAtkList = environment.playerAtkList;
        final int len = playerAtkList.size();
        //if(len>0) {
        for (int i = 0; i < len; i++) {
            Attack atk = playerAtkList.get(i);
            final float dx = atk.getX() - this.x;
            final float dy = atk.getY() - this.y;
            final float l = dx * dx + dy * dy;
            if (l < distanceToAtk) {
                distanceToAtk = l;
                atkX = atk.getX();
                atkY = atk.getY();
            }
        }

        //旋转角
        rotation = (float) (90 + Math.acos((double) XUnitToPlayer) * 180 / Math.PI * (YUnitToPlayer < 0 ? -1 : 1));

        //获取bot所处的区块
        Line[] blockOfLines = environment.getBlockOfLines(x, y);

        //按一定时间间隔攻击
        delayTimer += deltaTime;
        if (delayTimer >= atkDelay) {
            delayTimer -= atkDelay;
            //将AttackObject(Hostile)置于舞台
            GeneralLineAttack atkObj = new GeneralLineAttack(stage);
            atkObj.initialize(this, x, y, XUnitToPlayer, YUnitToPlayer, 500, 50 * getAtk(), 0, 30, 0xFFFF0000);//TODO:NPC攻击的能量暂时为0
            stage.addAtkHostile(atkObj);
        }

        if (defended() == null) {

            Character[] hostiles = sortHostilesByDistance(this.x, this.y);
            Character hostile = null;

            for (int i = 1; i < hostiles.length; i++) {
                if (hostiles[i].canBeDefended() && hostiles[i].defender() == null) {
                    hostile = hostiles[i];
                    break;
                }
            }

            defend(hostile);
        }

        /**
         * lineToPlayer: Ax + By + C = 0;
         */
        MyLine lineToPlayer = getLine(this.x, this.y, environment.getPlayerX(), environment.getPlayerY());
        MyLine perpendicularToPlayer = lineToPlayer.perpendicular(this.x, this.y);

        // offsetToLine
        float offsetAtkToLine = lineToPlayer.offset(atkX, atkY);
        //       Log.e("DefenceCharacter", offsetAtkToLine + "");
        float offsetDefenceToLine = 65535;
        float offsetDefenceToPerpendicular = 65535;

        if (defended() != null) {
            float defenceX = defended().getX();
            float defenceY = defended().getY();

            offsetDefenceToLine = lineToPlayer.offset(defenceX, defenceY);
            offsetDefenceToPerpendicular = perpendicularToPlayer.offset(defended().getX(), defended().getY());

        }

        /**
         * NetInput[0]: distanceToPlayer domain:[-1,1];
         * NetInput[1]: offsetToAtk of lineToPlayer domain[-1,1];
         * NetInput[2]: offsetToDefence of lineToPlayer domain[-1,1];
         * NetInput[3]: offsetDefenceToPerpendicular domain[-1,1];
         */
        NetInputs[0] = (distanceToPlayer - minDistanceFromPlayer) / minDistanceFromPlayer;
        NetInputs[1] = offsetAtkToLine / avoidSensitivity;
        NetInputs[2] = offsetDefenceToLine / defenceSensitivity;
        NetInputs[3] = offsetDefenceToPerpendicular /  distanceSensitivity;

        for (int i = 0; i < NetInputs.length; i++) {
            if (NetInputs[i] < -1) NetInputs[i] = -1;
            if (NetInputs[i] > 1) NetInputs[i] = 1;
        }

        //      Log.e("DefenceCharacter", offsetAtkToLine + "_" + offsetDefenceToLine);

        //从网络获取下一个行动
        char action = GetActionFromNetwork(NetInputs);

        if (action == 0) {
            vx -= vx * deltaTime * 10;
            vy -= vy * deltaTime * 10;
        } else {
            switch (action & 0x3) {
                case 1:
                    vx += XUnitToPlayer * getV();
                    vy += YUnitToPlayer * getV();
                    break;
                case 2:
                    vx += -XUnitToPlayer * getV();
                    vy += -YUnitToPlayer * getV();
                    break;
                default:
                    switch (action & 0xC) {
                        case 4:
                            vx += -YUnitToPlayer * getV();
                            vy += XUnitToPlayer * getV();
                            break;
                        case 8:
                            vx += YUnitToPlayer * getV();
                            vy += -XUnitToPlayer * getV();
                            break;
                    }
            }
        }

        int min = -getV();
        int max = getV();
        float length = (float) Math.sqrt(vx * vx + vy * vy);
        if (length < min) {
            vx = vx / length * min;
            vy = vy / length * min;
        } else if (length > max) {
            vx = vx / length * max;
            vy = vy / length * max;
        }

        //更新角色坐标
        this.x += vx * deltaTime;
        this.y += vy * deltaTime;

        //BOT与地图碰撞检测
        float[] output = new float[2];
        for (Line line : blockOfLines) {
            hitTest(output, x, y, r, line);
            x += output[0];
            y += output[1];
        }

        //TODO: 适应性函数
        liveTime++;

        if (defended() != null) {

            double temp1 = NetInputs[2];
            double temp2 = NetInputs[3];
            if (temp1 < 0) temp1 = -temp1;
            if (temp2 < 0) temp2 = 0;

            temp2 = 0.5 - temp2;
            if (temp2 < 0) temp2 = -temp2;

            fitness = (float)((1 - temp1) * (1 - temp2));
        }
    }

    /**
     * 更新ANN并返回网络所选择的动作作为输出
     */
    private char GetActionFromNetwork(double[] netInputs) {
        //储存网络的输出
        double[] outputs;

  //      Log.e("DefenceCharacter", NetInputs[3]+ "_" + NetInputs[4]);

        //输入网络得到输出
        outputs = neuralNet.Update(NetInputs);

        //检查更新ANN时是否出现问题
        if (outputs == null) {
            Log.e("NPC", "神经网络输出错误！");
            return 0;
        }

        //determine which action is valid this frame. The highest valued
        //output over 0.9. If none are over 0.9 then just drift with
        //gravity
        double BiggestSoFar = 0;

        char action = 0;

        /*len = outputs.length;
        for (int i=0; i<len; ++i){
            if( (outputs[i] > BiggestSoFar) && (outputs[i] > 0.9)){
                action = (char) (i + 1);
                BiggestSoFar = outputs[i];
            }
        }*/

        if ((outputs[0] > BiggestSoFar) && (outputs[0] > 0.9)) {
            action = 1;
            BiggestSoFar = outputs[0];
        }
        if ((outputs[1] > BiggestSoFar) && (outputs[1] > 0.9)) {
            action = 2;
        }
        BiggestSoFar = 0;
        if ((outputs[2] > BiggestSoFar) && (outputs[2] > 0.9)) {
            action |= 4;
            BiggestSoFar = outputs[2];
        }
        if ((outputs[3] > BiggestSoFar) && (outputs[3] > 0.9)) {
            action &= 0xB;
            action |= 8;
        }
        return action;
    }

    /**
     * 线-线碰撞测试
     */
    private static boolean lineLine(float[] output, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        float k = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        float r = ((y1 - y3) * (x4 - x3) - (x1 - x3) * (y4 - y3)) / k;
        float s = ((y1 - y3) * (x2 - x1) - (x1 - x3) * (y2 - y1)) / k;
        if (r < 0 || r > 1 || s < 0 || s > 1 || (r == 0 && s != 0)) {
            //不相交或者平行则不发生碰撞
            return false;
        } else {
            //相交或者共线
            output[0] = x1 + r * (x2 - x1);
            output[1] = y1 + r * (y2 - y1);
            return true;
        }
    }

    /**
     * 圆-线段的碰撞检测
     *
     * @param output 输出
     * @param x      圆心X
     * @param y      圆心Y
     * @param r      半径
     * @param line   线段
     */
    private static void hitTest(float[] output, float x, float y, int r, Line line) {
        int x1 = line.getPoint1().x;
        int y1 = line.getPoint1().y;

        //P1到P2的向量b
        //b的长度
        float len = line.getLength();
        //单位化b
        float vx = line.getUnit()[0];
        float vy = line.getUnit()[1];
        //P1到圆心的向量a
        float ax = x - x1;
        float ay = y - y1;
        //计算a在b上的投影
        float u = ax * vx + ay * vy;
        //获得直线上离圆最近的点
        float x0, y0;
        if (u < 0) {
            x0 = x1;
            y0 = y1;
        } else if (u > len) {
            x0 = line.getPoint2().x;
            y0 = line.getPoint2().y;
        } else {
            x0 = x1 + vx * u;
            y0 = y1 + vy * u;
        }
        x0 -= x;
        y0 -= y;
        float d = (float) Math.sqrt(x0 * x0 + y0 * y0);

        if (d <= r) {
            output[0] = (-x0 / d * (r - d));
            output[1] = (-y0 / d * (r - d));
        } else {
            output[0] = 0;
            output[1] = 0;
        }
    }

    /**
     * 求直角坐标系中任意两点的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private static float distancePoint2Point(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 根据数组A的元素大小从小到大排序A的index，返回index数组
     * @param A
     * @return
     */
    private static int[] sortIndexesByValues(int[] A){

        int[] ATemp = new int[A.length];
        for (int i = 0; i < A.length; i++) {
            ATemp[i] = A[i];
        }

        int[] arr = new int[ATemp.length];
        float min;
        for(int i = 0; i < ATemp.length; i ++){
            min = ATemp[0];

            int j = 0;
            int k = j;
            for(; j < ATemp.length; j ++){
                if(ATemp[j] < min){
                    min = ATemp[j];
                    k = j;
                }
            }

            arr[i] = k;
            ATemp[k] = 65535;
        }
        return arr;
    }

    /**
     * 根据到点（x，y）的距离排序角色
     * @param x
     * @param y
     * @return
     */
    public Character[] sortHostilesByDistance(float x, float y) {
        int length = hostileList.size();
        Character[] hostiles = new Character[length];
        int[] distances = new int[length];

        for (int i = 0; i < length; i++) {
            distances[i] = (int)distancePoint2Point(hostileList.get(i).getX(), hostileList.get(i).getY(), x, y);
        }

        int[] indexes = sortIndexesByValues(distances);

        for (int i = 0; i < length; i++) {
            hostiles[i] = hostileList.get(indexes[i]);
        }
        return hostiles;
    }
}
