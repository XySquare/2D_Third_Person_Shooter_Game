package com.xyy.game.ai.Effect;

import android.graphics.RadialGradient;
import android.graphics.Shader;
import com.xyy.game.framework.Graphics;

/**
 * Created by ${XYY} on ${2016/11/6}.
 */
public class CircleEffect extends Effect {
    //半径
    private int radius;
    //计时器
    private float timer;

    private RadialGradient radialGradient;

    public void initialize(float x, float y, int r, int color){
        radius = r;
        this.x = x;
        this.y = y;
        timer = 0;
        isDead = false;

        //坐标必须为(0,0),半径应与半径一致
        radialGradient = new RadialGradient(0,0,r,0x00FFFFFF&color,0xFF000000|(color), Shader.TileMode.CLAMP);
    }

    @Override
    public void update(float deltaTime) {
        //持续1秒
        timer+=deltaTime;
        if(timer>=1){
            isDead = true;//等待被移除
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawCircle(x-offsetX,y-offsetY,radius*timer,radialGradient,timer);
    }
}
