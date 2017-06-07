package com.xyy.game.ai.Character;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Attack.AtkInfo;
import com.xyy.game.ai.Effect.CircleEffect;
import com.xyy.game.ai.Effect.MultSquareEffect;
import com.xyy.game.ai.Environment;
import com.xyy.game.ai.GameState;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

/**
 * Created by ${XYY} on ${2016/8/5}.
 */
public class Player extends Character {
    private static final String TAG = "Player";

    //速度分量
    private float vx = 0;
    private float vy = 0;
    //旋转角
    private float rotation;

    private int mBasicAtk;

    private int mWeaponAtk;

    public Player(Stage stage) {
        super(stage,TAG);
        //this.name = "Player";
        this.r = 50;
        setV(450);
        setMaxHp(5000);
        setHp(getMaxHp());
        setAtk(5);
        setDef(5);
        setMaxEnergy(1000);
        setEnergy(1000);
    }

    /**
     * 设置玩家移动方向
     * 请输入标准化向量
     * @param ux x分量
     * @param uy y分量
     */
    public void setDirection(float ux,float uy){
        vx = ux * getV();
        vy = uy * getV();
        if(vx!=0 || vy!=0)
            rotation = (float) (90+Math.acos((double)ux) * 180 / Math.PI* (uy < 0 ? -1 : 1));
    }

    @Override
    public void setAtk(int atk) {
        mBasicAtk = atk;
        super.setAtk(atk + mWeaponAtk);
    }

    public void setWeaponAtk(int atk){
        mWeaponAtk = atk;
        super.setAtk(atk + mBasicAtk);
    }

    @Override
    protected final void updateInner(float deltaTime,Environment environment){
        //更新玩家坐标
        x += vx*deltaTime;
        y += vy*deltaTime;
        //获取bot所处的区块
        Line[] blockOfLines = environment.getBlockOfLines(x,y);
        //BOT与地图碰撞检测
        float[] output = new float[2];
        for (Line line : blockOfLines) {
            hitTest(output, x, y, r, line);
            x+=output[0];
            y+=output[1];
        }
        //更新环境
        environment.updatePlayerIndex(x,y);
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawPixmapDegree(Assets.player, x - offsetX, y - offsetY, rotation);
    }

    @Override
    protected int onDestroyed() {
        //停止移动
        vx = vy = 0;
        //产生圆形特效
        CircleEffect circleEffect = new CircleEffect();
        circleEffect.initialize(x,y,500,0xFF66CCFF);
        stage.addEffect(circleEffect);
        //产生方块特效
        MultSquareEffect effect = new MultSquareEffect();
        effect.initialize((int) x, (int) y, 0xFF66CCFF, 100, 80, 1, 1, 2);
        stage.addEffect(effect);
        //跳转至“MISSION FAIL”界面
        stage.setState(GameState.GAMEOVER);
        return 0;
    }

    @Override
    public void onHitCharacter(Character character, AtkInfo attack) {

    }

    @Override
    public void onHitByCharacter(Character character, AtkInfo attack) {

    }

    /**
     * 圆-线段的碰撞检测
     * @param output 输出
     * @param x 圆心X
     * @param y 圆心Y
     * @param r 半径
     * @param line 线段
     */
    private void hitTest(float[] output, float x, float y, int r, Line line){
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
        float u = ax*vx + ay*vy;
        //获得直线上离圆最近的点
        float x0, y0;
        if(u<0){
            x0 = x1;
            y0 = y1;
        }else if(u>len){
            x0 = line.getPoint2().x;
            y0 = line.getPoint2().y;
        }else{
            x0 = x1 + vx*u;
            y0 = y1 + vy*u;
        }
        x0 -= x;
        y0 -= y;
        float d = (float) Math.sqrt(x0*x0 + y0*y0);

        if(d<=r) {
            output[0] = (- x0 / d * (r - d));
            output[1] = (- y0 / d * (r - d));
        }
        else{
            output[0] = 0;
            output[1] = 0;
        }
    }

    @Override
    public boolean canBeDefended() {
        return false;
    }
}
