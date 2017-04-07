package com.xyy.game.component;

import com.xyy.game.framework.Graphics;

/**
 * Windows Metro UI Loading Animation
 * Created by ${XYY} on ${2016/9/30}.
 */
public class LoadingAnimation {
    //半径
    private int r;
    //圆心
    private int x,y;
    //颜色
    private int color;
    //各点偏移量
    private static float[] offset = new float[]{0,0.2f,0.4f,0.6f,0.8f,1.0f};
    //全局计时器
    private float globalTimer;

    public LoadingAnimation(int r, int x, int y, int color){
        this.r = r;
        this.x = x;
        this.y = y;
        this.color = color;
        globalTimer = 0;
    }

    public void  update(float deltaTime){
        globalTimer = (globalTimer+deltaTime)%2;
    }

    public void present(Graphics g){
        for(int i=0;i<6;i++) {
            float radian = getRadian((offset[i]+globalTimer)%2);
            g.drawCircle((float) (x + Math.sin(radian) * r), (float) (y + Math.cos(radian) * r), 8, color);
        }
    }

    /**
     * 时间-弧度函数
     * @param timer （0~2s）
     * @return 弧度
     */
    private static float getRadian(float timer){
        return (float) (timer<1 ? 2*Math.PI-Math.PI*Math.pow(timer,0.5) : Math.PI*Math.pow(2-timer,0.5));
    }
}
