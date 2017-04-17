package com.xyy.game.ai.Attack;

import android.support.annotation.NonNull;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Effect.MultSquareEffect;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;
import com.xyy.game.util.iPoint;

/**
 * Created by berryice on 2017/4/13.
 */

public class GeneralPolygonAttack extends Attack{

    private float vx;
    private float vy;
    private double rotation;
    //伤害
    private int damage;
    //端点x坐标数组
    private float[] xArray;
    //端点y坐标数组
    private float[] yArray;

    private Line[] mLines;
    //产生攻击所需的能量
    private int energy;

    private int color;

    public GeneralPolygonAttack(Stage stage) {
        super((char)2, stage);
    }

    public void initialize(@NonNull Character parent, float[] xArray, float[] yArray, float vx, float vy, double rotation, int damage, int energy, int color) {
        this.parent = parent;
        this.xArray = xArray;
        this.yArray = yArray;
        this.vx = vx;
        this.vy = vy;
        this.rotation = rotation;
        this.damage = damage;
        this.energy = energy;
        this.color = color;

        updateCenter();

        for (int i = 0; i < xArray.length; i++) {
            iPoint point = new iPoint((int)xArray[i], (int)yArray[i]);
            point.rotate(x, y, rotation);
            xArray[i] = point.x;
            yArray[i] = point.y;
        }

        updateProperty();
    }

    private void updateCenter(){
        for (int i = 0; i < xArray.length; i++) {
            this.x += xArray[i];
            this.y += yArray[i];
        }
        this.x = x / xArray.length;
        this.y = y / yArray.length;
    }

    private void updateProperty() {
        mLines = new Line[xArray.length];

        int last = mLines.length - 1;

        for (int i = 0; i < last; i ++) {
            mLines[i] = new Line(new iPoint((int)xArray[i], (int)yArray[i]),
                    new iPoint((int)xArray[i+1], (int)yArray[i+1]));

        }

        mLines[last] = new Line(new iPoint((int)xArray[last], (int)yArray[last]),
                new iPoint((int)xArray[0], (int)yArray[0]));
    }

    @Override
    public boolean hitTestLine(Line line) {
       for (Line mLine : mLines) {
            if (hitTestLine2Line(mLine.getPoint1().x,mLine.getPoint1().y,mLine.getPoint2().x,mLine.getPoint2().y,
                    line.getPoint1().x,line.getPoint1().y,line.getPoint2().x,line.getPoint2().y)){
                MultSquareEffect effect = new MultSquareEffect();
                effect.initialize((int)x,(int)y,color,6,24,4,4,10);
                stage.addEffect(effect);
                //等待被移除
                isDead = true;

                return true;
            }
        }
        return false;
    }

    private boolean hitTestLine2Line(float x1, float y1, float x2, float y2, int x3, int y3, int x4, int y4) {
        float A1 = y2 - y1;
        float B1 = x1 - x2;
        float C1 = x2 * y1 - x1 * y2;

        float A2 = y4 - y3;
        float B2 = x3 - x4;
        float C2 = x4 * y3 - x3 * y4;

        float result1 = A1 * x3 + B1 * y3 + C1;
        float result2 = A1 * x4 + B1 * y4 + C1;
        float result3 = A2 * x1 + B2 * y1 + C2;
        float result4 = A2 * x2 + B2 * y2 + C2;

        boolean bool1;
        boolean bool2;

        if (result1 < 0 ) {
            bool1 = result2 > 0;
        } else {
            bool1 = result2 < 0;
        }

        if (result3 < 0 ) {
            bool2 = result4 > 0;
        } else {
            bool2 = result4 < 0;
        }

        return bool1 && bool2;
    }

    @Override
    public boolean hitTestCharacter(Character character) {
        if(character.getHp()<=0) return false;//TODO:重要！避免角色死亡后多次回调

        boolean res = hitTestPolygon2Circle(mLines, character.getX(), character.getY(), character.getR());
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

    private boolean hitTestPolygon2Circle(Line[] lines, float cx, float cy, float r) {
        for (Line line : lines) {
            if (hitTestLine2Circle(line.getPoint1().x, line.getPoint1().y, line.getPoint2().x, line.getPoint2().y, cx, cy, r)){
                return true;
            }
        }

        return false;
    }

    private boolean hitTestLine2Circle(float x1, float y1, float x2, float y2, float cx, float cy, float r){
        float A = y2 - y1;
        float B = x1 - x2;
        float C = x2*y1 - x1*y2;

        float temp = A*cx + B*cy + C;
        float rSquare = r * r;

        if (temp * temp > (A*A + B*B) * rSquare) {
            return false;
        }

        if ((cx - x1) * (y2 - y1) + (x2 - x1) * (cy - y1) < 0) {
            float d = (cx - x1) * (cx - x1) + (cy - y1) * (cy - y1);
            return d < rSquare;
        }

        if ((cx - x2) *(y1 - y2) + (x1 - x2) * (cy - y2) < 0) {
            float d = (cx - x2) * (cx - x2) + (cy - y2) * (cy - y2);
            return d < rSquare;
        }

        return true;
    }

    @Override
    public void update(float deltaTime) {
        float dvx = vx*deltaTime;
        float dvy = vy*deltaTime;

        for (int i = 0; i < xArray.length; i++) {
            xArray[i] += dvx;
            yArray[i] += dvy;
        }

        x += dvx;
        y += dvy;

        updateProperty();
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        for (Line line : mLines) {
            g.drawLine(line.getPoint1().x-offsetX, line.getPoint1().y-offsetY, line.getPoint2().x-offsetX, line.getPoint2().y-offsetY, color, 1);
            g.drawLine(line.getPoint1().x-offsetX, line.getPoint1().y-offsetY, line.getPoint2().x-offsetX, line.getPoint2().y-offsetY,(0x7F000000)|(color&0x00FFFFFF),6);
        }
    }

    @Override
    public int getEnergy() {
        return energy;
    }
}
