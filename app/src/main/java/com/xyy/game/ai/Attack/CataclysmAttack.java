package com.xyy.game.ai.Attack;


import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.util.Line;

/**
 * 伴随灾变产生的一个大型圆形范围攻击
 * Created by ${XYY} on ${2016/7/31}.
 */
public final class CataclysmAttack extends Attack {

    //伤害半径
    private final int radius;
    //单位向量U(ux,uy)
    //private float ux,uy;
    //计时器
    private float timer;

    private RadialGradient radialGradient;

    public CataclysmAttack(Stage stage) {
        super((char)2, stage);
        radius = 500;
    }

    public void initialize(@NonNull Character parent,float x, float y){
        this.parent = parent;
        this.x = x;
        this.y = y;
        timer = 0;
        isDead = false;

        //坐标必须为(0,0),半径应与攻击半径一致
        radialGradient = new RadialGradient(0,0,radius,0x00FF0000,0xFFFF0000, Shader.TileMode.CLAMP);
    }

    @Override
    public void update(float deltaTime) {
        //攻击将持续1秒
        timer+=deltaTime;
        if(timer>=1){
            isDead = true;//等待被移除
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        //g.drawPixmapDegree(Assets.circleAtk, x - offsetX, y - offsetY, 0f);
        g.drawCircle(x-offsetX,y-offsetY,radius*timer,radialGradient,timer);
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
                <= (radius*timer+character.getR())*(radius*timer+character.getR());
        if(res){
            character.setHp(0);//造成伤害
        }
        return res;
    }

    @Override
    public int getEnergy() {
        return 0;
    }//TODO:此攻击由NPC发出，暂时为设置能量
}
