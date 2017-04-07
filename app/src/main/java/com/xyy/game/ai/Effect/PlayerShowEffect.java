package com.xyy.game.ai.Effect;

import com.xyy.game.framework.Graphics;

/**
 * 玩家出现特效
 * Created by ${XYY} on ${2016/10/8}.
 */
public class PlayerShowEffect extends Effect {
    //各点偏移量
    private static float[] offset = new float[]{0,-0.2f,-0.4f,-0.6f};

    private float globalTimer;

    @Override
    public void update(float deltaTime) {
        globalTimer += deltaTime;
        if(globalTimer>2) isDead = true;
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        for(int i=0;i<4;i++) {
            float timer = globalTimer+offset[i];
            if(timer>0 && timer<0.8)
            g.drawRing(x-offsetX,y-offsetY, (float) (280*Math.pow(0.8-timer,0.5)),0,360,0xFFFFFF|((int) (0xff*(timer/0.8)) << 24), (int) (100*Math.pow(0.9-timer,0.5)));
        }
    }
}
