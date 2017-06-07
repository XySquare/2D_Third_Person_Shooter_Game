package com.xyy.game.ai.Attack;

import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Buff;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Effect.TurnLargeAndFadeOutEffect;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个不可见的圆形范围攻击，生成后立即消失
 * 伴随产生一个可见的特效TurnLargeAndFadeOutEffect
 * 没有伤害，命中后叠加一个Buff
 * Created by berryice on 2017/4/21.
 */

public final class BuffArea extends Attack {

    //private float vx;
    //private float vy;
    //private float rotation;
    //伤害
    //private int damage;
    //伤害半径
    private int radius;
    //单位向量U(ux,uy)
    //private float ux,uy;
    //计时器
    //private float timer;

    //private List<Character> mAssistedList = new ArrayList<>();

    //private RadialGradient radialGradient;

    private int mBuffUid;

    public BuffArea(Stage stage) {
        super((char)3, stage);
    }

    public void initialize(@NonNull Character parent, float x, float y, /*float dx, float dy, int v,*/ int r, int buffUid){
        this.parent = parent;
        //ux = dx;uy=dy;
        //damage = dam;
        radius = r;
        //vx = dx* v;
        //vy = dy* v;
        //rotation = (float) (90+Math.acos((double)dx) * 180 / Math.PI* (dy < 0 ? -1 : 1));
        this.x = x;
        this.y = y;
        //timer = 0;
        //等待被移除
        isDead = true;
        //坐标必须为(0,0),半径应与攻击半径一致
        //radialGradient = new RadialGradient(0,0,r,0x0000FFFF,0x7700FFFF, Shader.TileMode.CLAMP);
        stage.addEffect(new TurnLargeAndFadeOutEffect().initialize(x,y,r, Assets.effect_ffffff_radial_00_99));
        mBuffUid = buffUid;
    }

    @Override
    public void update(float deltaTime) {
        /*float dvx = vx*deltaTime;
        float dvy = vy*deltaTime;
        this.x+=dvx;
        this.y+=dvy;
        //攻击将持续0.2秒
        timer+=deltaTime;
        if(timer>=1){
            isDead = true;//等待被移除
        }*/
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        //g.drawPixmapDegree(Assets.circleAtk, x - offsetX, y - offsetY, 0f);
        //g.drawCircle(x-offsetX,y-offsetY,radius*timer,radialGradient,timer);
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
        if(res /*&& !mAssistedList.contains(character)*/){
            assist(character);
            //mAssistedList.add(character);
            //注意，这里没进行“只进行一次攻击”的设定
            //如果需要，可增加个boolean标志
        }
        return false;
    }

    private void assist(Character character) {
        character.addBuff(mBuffUid);
    }

    @Override
    public int getEnergy() {
        return 0;
    }//TODO:此攻击由NPC发出，暂时为设置能量
}
