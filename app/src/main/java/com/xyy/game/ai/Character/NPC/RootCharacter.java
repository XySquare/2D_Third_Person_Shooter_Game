package com.xyy.game.ai.Character.NPC;

import android.util.Log;

import com.xyy.game.ANN.GenPool;
import com.xyy.game.ANN.NeuralNet;
import com.xyy.game.ai.*;
import com.xyy.game.ai.Attack.AtkInfo;
import com.xyy.game.ai.Attack.InvisibleAttackBeforeCataclysm;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.component.ProcessingAnimationRever;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Utils;
import com.xyy.game.util.iPoint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ${XYY} on ${2016/8/24}.
 */
public final class RootCharacter extends Character implements NPC {

    private static final int NEXT_ROUND = 100;

    /**
     * 基因池
     */
    private GenPool genPool;

    /**
     * 子代计数器
     */
    private int childrenCounter;

    private int counterToNextRound;

    private int round;
    //private int lifeBoxCounter;
    //private int energyBoxCounter;

    private final NPCProducer[] npcProducer;

    //private final iPoint[] points;

    private ArrayList<iPoint> availPoints;
    private ArrayList<iPoint> usedPoints;

    private ProcessingAnimationRever loadPrc;

    private NeuralNet mNeuralNet;

    public RootCharacter(Stage stage) {
        super(stage);

        //初始化基因池
        genPool = GameDataManager.getGenPool("Data.dat");

        childrenCounter = 0;
        counterToNextRound = 0;
        round = 1;
        //lifeBoxCounter = 0;

        npcProducer = new NPCProducer[10];

        mNeuralNet = new NeuralNet(4, 4, 6);
        mNeuralNet.Train(DefenceCharacter.sData);

        for(int i=0;i<npcProducer.length;i++){
            npcProducer[i] = new NPCProducer(stage);
            npcProducer[i].setWeights(mNeuralNet.GetWeights());
        }

        iPoint[] points = new iPoint[]{new iPoint(415,2584),new iPoint(613,2384),
                new iPoint(510,2584),new iPoint(592,2782),new iPoint(1293,1583),
                new iPoint(1492,1545),new iPoint(1192,1782),new iPoint(1494,1784),
                new iPoint(2193,3669),new iPoint(1947,3670),new iPoint(1893,3469),
                new iPoint(1748,3512),new iPoint(1792,3133),new iPoint(1947,3033),
                new iPoint(1594,3032),new iPoint(1692,2883),new iPoint(1947,2783),
                new iPoint(1792,2683),new iPoint(2250,2584),new iPoint(2600,2682),
                new iPoint(2948,2783),new iPoint(3101,2935),new iPoint(3200,2735),
                new iPoint(2348,2585),new iPoint(2403,2434),new iPoint(2600,2485),
                new iPoint(2801,2585),new iPoint(2193,2385),new iPoint(2401,2433),
                new iPoint(2900,2383),new iPoint(2448,2284),new iPoint(2649,2285),
                new iPoint(2193,2183),new iPoint(2348,2135),new iPoint(2549,2084),
                new iPoint(2799,2184),new iPoint(2348,1944),new iPoint(2648,1883),
                new iPoint(2848,1984),new iPoint(3115,1962),new iPoint(2193,1748),
                new iPoint(2449,1684),new iPoint(2958,1583),new iPoint(2449,1346),
                new iPoint(2347,904),new iPoint(2601,843),new iPoint(3115,945),
                new iPoint(2802,804),new iPoint(2601,703),new iPoint(2947,703),
                new iPoint(2748,604),new iPoint(3217,250),new iPoint(3414,509),
                new iPoint(3554,1190),new iPoint(3820,2487),new iPoint(3920,2686),
                new iPoint(4074,2484),new iPoint(4273,2433),new iPoint(4273,2686),
                new iPoint(4749,2336),};

        availPoints = new ArrayList<>(points.length);
        Collections.addAll(availPoints, points);

        usedPoints = new ArrayList<>(points.length);

        loadPrc = new ProcessingAnimationRever(1280-400,10,300,25,0xFFFF0000);
    }

    @Override
    public void onHitCharacter(Character character, AtkInfo attack) {
        //if(attack.getFlag() == 100)
            //lifeBoxCounter--;
        //else if(attack.getFlag()==101)
            //energyBoxCounter--;
    }

    @Override
    public void onHitByCharacter(Character character, AtkInfo attack) {

    }

    @Override
    public void putWeights(double[] weights) {

    }

    @Override
    public double[] getWeights() {
        return null;
    }

    @Override
    public float getFitness() {
        return 0;
    }

    @Override
    public int getLiveTime() {
        return 0;
    }

    @Override
    public void updateInner(float deltaTime, Environment environment) {
        //float playerX = environment.getPlayerX();
        //float playerY = environment.getPlayerY();
        //随机生成敌人
        if(Math.random()>0.99 && childrenCounter <3){
            //随机从可用的点中获取一个点
            int i = Utils.RandInt(0,availPoints.size()-1);
            iPoint p = availPoints.get(i);
            //与所有已用的点进行比较
            final int len = usedPoints.size();
            for(int j=0;j<len;j++){
                iPoint u = usedPoints.get(j);
                float dx = p.x - u.x;
                float dy = p.y - u.y;
                if(dx*dx + dy*dy < 1000000){
                    p = null;
                    break;
                }//注意这里距离没开方
            }
            //如果所选的点符合要求
            if(p!=null) {
                availPoints.remove(i);
                usedPoints.add(p);
                for (NPCProducer aNpcProducer : npcProducer) {
                    if (aNpcProducer.isDead()) {
                        aNpcProducer.initialize(this, "NPCProducer"+childrenCounter, p.x, p.y);
                        stage.addHostile(aNpcProducer);
                        childrenCounter++;
                        break;
                    }
                }
            }
        }
        //随机生成回复包（调试）
        /*if(Math.random()>0.99 && lifeBoxCounter <3){
            int x = (int)(playerX+Math.random()*720)-360;
            int y = (int)(playerY+Math.random()*720)-360;
            if(environment.isAvailable(x,y)) {
                LifeBox lifeBox = new LifeBox(stage);//暂时没使用回收池
                lifeBox.initialize(this, x, y);
                stage.addAtkHostile(lifeBox);
                stage.addToTrackList(lifeBox,1);
                lifeBoxCounter++;
            }
        }*/
        //随机生成能量回复包（调试）
        /*if(Math.random()>0.99 &&  energyBoxCounter <3){
            int x = (int)(playerX+Math.random()*720)-360;
            int y = (int)(playerY+Math.random()*720)-360;
            if(environment.isAvailable(x,y)) {
                EnergyBox energyBox = new EnergyBox(stage);//暂时没使用回收池
                energyBox.initialize(this, x, y);
                stage.addAtkHostile(energyBox);
                stage.addToTrackList(energyBox,1);
                energyBoxCounter++;
            }
        }*/
        if(genPool.Cataclysm()){
            InvisibleAttackBeforeCataclysm atk = new InvisibleAttackBeforeCataclysm(stage);
            stage.addAtkPlayer(atk);
            stage.accessScore(100);
            Log.i("RootCharacter","Cataclysm Active!");
        }

        loadPrc.update(deltaTime,genPool.getCataclysmProcess());
    }

    @Override
    protected int onDestroyed() {
        //Would never be destroyed...
        return 1;
    }

    @Override
    public void onChildrenDestroyed(NPC child) {
        if(child.getName().startsWith("NPCProducer")) {
            childrenCounter--;
            final int len = usedPoints.size();
            for (int j = 0; j < len; j++) {
                iPoint u = usedPoints.get(j);
                if (u.equals((int) child.getX(), (int) child.getY())) {
                    usedPoints.remove(j);
                    availPoints.add(u);
                    break;
                }
            }
        }
        else if(child.getName().startsWith("SimpleCharacter")){
            counterToNextRound++;
            if(counterToNextRound>=NEXT_ROUND){
                round++;
                counterToNextRound=0;
            }
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        //g.drawText(genPool.getCataclysmProcess()+"%",10,100,0xFFFFFFFF,35);

        loadPrc.present(g);

        g.drawText(String.valueOf(round),1280/2-10,37,0xFFFFFFFF,35);
        //绘制环形进度条的半透明背景
        g.drawRing(1280/2, 0, 50, 0, 180, 0x7FFFFFFF,8);
        //绘制环形进度条
        g.drawRing(1280/2, 0, 50, 0, 180*(float)counterToNextRound/NEXT_ROUND, 0xFFFFFFFF,8);
    }

    @Override
    public boolean canBeDefended() {
        return false;
    }
}
