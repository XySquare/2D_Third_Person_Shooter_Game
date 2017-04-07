package com.xyy.game.ai.Attack;


import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import com.xyy.game.ai.*;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

/**
 * 一个简单的圆形范围攻击
 * Created by ${XYY} on ${2016/7/31}.
 */
public final class GeneralCircleAttack extends Attack {

    private float vx;
    private float vy;
    //private float rotation;
    //伤害
    private int damage;
    //伤害半径
    private int radius;
    //单位向量U(ux,uy)
    //private float ux,uy;
    //计时器
    private float timer;

    private RadialGradient radialGradient;

    public GeneralCircleAttack(Stage stage) {
        super((char)1, stage);
    }

    public void initialize(@NonNull Character parent,float x, float y, float dx,float dy,int v,int dam,int r){
        this.parent = parent;
        //ux = dx;uy=dy;
        damage = dam;
        radius = r;
        vx = dx* v;
        vy = dy* v;
        //rotation = (float) (90+Math.acos((double)dx) * 180 / Math.PI* (dy < 0 ? -1 : 1));
        this.x = x;
        this.y = y;
        timer = 0;
        isDead = false;

        //坐标必须为(0,0),半径应与攻击半径一致
        radialGradient = new RadialGradient(0,0,r,0x00FF0000,0xFFFF0000, Shader.TileMode.CLAMP);
    }

    @Override
    public void update(float deltaTime) {
        float dvx = vx*deltaTime;
        float dvy = vy*deltaTime;
        this.x+=dvx;
        this.y+=dvy;
        //攻击将持续0.2秒
        timer+=deltaTime;
        if(timer>=0.2){
            isDead = true;//等待被移除
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        //g.drawPixmapDegree(Assets.circleAtk, x - offsetX, y - offsetY, 0f);
        g.drawCircle(x-offsetX,y-offsetY,radius*timer*5,radialGradient,timer*5);
    }

    @Override
    public boolean hitTestLine(Line line) {
        return false;
        //范围攻击不受地图影响
    }

    @Override
    public boolean hitTestCharacter(Character character) {
        if(character.getHp()<=0) return false;//TODO:重要！避免角色死亡后多次回调
        boolean res = ((x-character.getX())*(x-character.getX())
                +(y-character.getY())*(y-character.getY()))
                <= (radius+character.getR())*(radius+character.getR());
        if(res){
            character.accessHp_Defence(-damage);//造成伤害
            //注意，这里没进行“只进行一次攻击”的设定
            //如果需要，可增加个boolean标志
        }
        return res;
    }

    @Override
    public int getEnergy() {
        return 0;
    }//TODO:此攻击由NPC发出，暂时为设置能量
}
