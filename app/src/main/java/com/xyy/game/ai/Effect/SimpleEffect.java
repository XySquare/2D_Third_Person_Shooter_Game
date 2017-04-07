package com.xyy.game.ai.Effect;

import com.xyy.game.ai.Effect.Effect;
import com.xyy.game.framework.Graphics;

/**
 * 简单的特效
 * Created by ${XYY} on ${2015/11/20}.
 */
@Deprecated
public class SimpleEffect extends Effect {
    private float r = 0;

    @Override
    public void initialize(int x, int y){
        super.initialize(x,y);
        this.r = 0;
    }

    @Override
    public void update(float deltaTime) {
        r+=100*deltaTime;
        if(r>=20){
            isDead = true;
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawCircle(x-offsetX,y-offsetY,r,0x7FFFFFFF);
    }
}
