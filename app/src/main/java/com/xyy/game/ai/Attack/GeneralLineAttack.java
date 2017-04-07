package com.xyy.game.ai.Attack;

import android.support.annotation.NonNull;

import com.xyy.game.ai.*;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Effect.Effect;
import com.xyy.game.ai.Effect.MultSquareEffect;
import com.xyy.game.ai.Effect.SimpleEffect;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.util.Line;

/**
 * 一个简单的线性攻击
 * Created by ${XYY} on ${2016/7/31}.
 */
public final class GeneralLineAttack extends Attack {

    private float vx;
    private float vy;
    private float rotation;
    //伤害
    private int damage;
    //线段端点P1(x1,y1)
    private float x1,y1;
    //线段端点P2(x2,y2)
    private float x2,y2;
    //线段长度
    private int length;
    //单位向量U(ux,uy)
    private float ux,uy;
    //产生攻击所需的能量
    private int energy;

    private int color;

    public GeneralLineAttack(Stage stage) {
        super((char)0, stage);
    }

    public void initialize(@NonNull Character parent,float x,float y,float dx,float dy,int v,int dam,int eng,int len,int color){
        this.parent = parent;
        ux = dx;uy=dy;
        damage = dam;
        length = len;
        energy = eng;
        vx = dx* v;
        vy = dy* v;
        rotation = (float) (90+Math.acos((double)dx) * 180 / Math.PI* (dy < 0 ? -1 : 1));
        x1 = x;
        y1 = y;
        x2 = x+dx*len;
        y2 = y+dy*len;
        this.x = (x1+x2)/2;
        this.y = (y1+y2)/2;
        this.color = color;
        isDead = false;
    }

    @Override
    public void update(float deltaTime) {
        float dvx = vx*deltaTime;
        float dvy = vy*deltaTime;
        x1+=dvx;
        y1+=dvy;
        x2+=dvx;
        y2+=dvy;
        this.x+=dvx;
        this.y+=dvy;
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawLine(x1-offsetX,y1-offsetY,x2-offsetX,y2-offsetY,color,1);
        g.drawLine(x1-offsetX,y1-offsetY,x2-offsetX,y2-offsetY,(0x7F000000)|(color&0x00FFFFFF),6);
    }

    @Override
    public boolean hitTestLine(Line line) {
        return lineLine(x1,y1,x2,y2,line.getPoint1().x,line.getPoint1().y,line.getPoint2().x,line.getPoint2().y);
    }

    @Override
    public boolean hitTestCharacter(Character character) {
        if(character.getHp()<=0) return false;//TODO:重要！避免角色死亡后多次回调
        boolean res = lineCir(character.getX(),character.getY(),character.getR());
        if(res){
            //生成特效
            //Effect effect = new SimpleEffect();
            MultSquareEffect effect = new MultSquareEffect();
            effect.initialize((int)x,(int)y,color,6,24,4,4,10);//TODO:此处尝试使用质点代替交点，看看效果
            stage.addEffect(effect);
            //造成伤害
            character.accessHp_Defence(-damage);
            //向父级回调击中的角色
            parent.onHitCharacter(character,this);
            //向被击中角色回调攻击者
            character.onHitByCharacter(parent, this);
            //等待被移除
            isDead = true;
        }
        return res;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    private boolean lineLine(float x1, float y1, float x2, float y2, int x3, int y3, int x4, int y4) {
        float k = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        float r = ((y1 - y3) * (x4 - x3) - (x1 - x3) * (y4 - y3)) / k;
        float s = ((y1 - y3) * (x2 - x1) - (x1 - x3) * (y2 - y1)) / k;
        if (r < 0 || r > 1 || s < 0 || s > 1 || (r == 0 && s != 0)){//不相交或者平行则不发生碰撞
            return false;
        }else {//相交或者共线
            //output[0] = x1+r*(x2-x1);
            //output[1] = y1+r*(y2-y1);
            //向舞台上添加特效
            //Effect effect = new SimpleEffect();
            //effect.initialize((int)x,(int)y);//TODO:此处尝试使用质点代替交点，看看效果
            MultSquareEffect effect = new MultSquareEffect();
            effect.initialize((int)x,(int)y,color,6,24,4,4,10);
            stage.addEffect(effect);
            //等待被移除
            isDead = true;
            return true;
        }
    }

    /**
     * 圆-线段的碰撞检测
     * @param x 圆心X
     * @param y 圆心Y
     * @param r 半径
     */
    private boolean lineCir(float x, float y, int r) {
        //P1到圆心的向量a
        float ax = x - x1;
        float ay = y - y1;
        //计算a在b上的投影
        float u = ax * ux + ay * uy;
        //获得直线上离圆最近的点
        float x0, y0;
        if (u < 0) {
            x0 = x1;
            y0 = y1;
        } else if (u > length) {
            x0 = x2;
            y0 = y2;
        } else {
            x0 = x1 + ux * u;
            y0 = y1 + uy * u;
        }
        x0 -= x;
        y0 -= y;
        return x0 * x0 + y0 * y0 <= r * r;

    }
}
