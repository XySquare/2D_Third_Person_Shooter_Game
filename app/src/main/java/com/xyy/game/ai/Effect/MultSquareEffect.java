package com.xyy.game.ai.Effect;

import com.xyy.game.framework.Graphics;

/**
 * 生产若干随机大小/透明度/位置/旋转角度的正方形，
 * 并向四周扩散
 * Created by ${XYY} on ${2016/10/21}.
 */
public class MultSquareEffect extends Effect {
    //矩形颜色
    private int color;
    //生成的方块数量
    private int num;
    //方块透明度
    private float[] alpha;
    //方块旋转角
    private float[] radians;
    //方块大小
    private int[] r;
    //方块坐标
    private float[] dx;
    private float[] dy;
    //alpha下降速度
    private int da;
    //方块旋转速度
    private int dr;
    //方块扩撒速度
    private int dv;

    public void initialize(int x, int y, int color, int num, int size, int da, int dr, int dv){
        super.initialize(x,y);
        this.color = color & 0x00FFFFFF;
        this.num = num;
        this.da = da;
        this.dr = dr;
        this.dv = dv;
        alpha = new float[num];
        radians = new float[num];
        r = new int[num];
        dx = new float[num];
        dy = new float[num];
        float radian = 0;
        for(int i=0;i<num;i++){
            alpha[i] = (float) (0xFF*Math.random());
            radians[i] = (float) (3/*Math.PI*/*Math.random());
            r[i] = (int) (size*Math.random());
            radian += (float)(6f/num*2f*Math.random());
            dx[i] = (float)(r[i]*Math.cos(radian));
            dy[i] = (float)(r[i]*Math.sin(radian));
        }
    }

    @Override
    public void update(float deltaTime) {
        final float da = 0xFF*deltaTime*this.da;
        final float dr = /*3.14f*2*/6*deltaTime*this.dr;
        int deathCounter = 0;
        for(int i=0;i<num;i++){
            alpha[i] -= da;
            if(alpha[i]<0){
                alpha[i] = 0;
                deathCounter++;
            }

            radians[i] += dr;

            dx[i] += dx[i]*deltaTime*dv;
            dy[i] += dy[i]*deltaTime*dv;
        }
        //if(deathCounter==num) isDead = true;
        isDead = deathCounter==num;
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        for(int i=0;i<num;i++)
            g.drawSquareRadians(x+dx[i]-offsetX, y+dy[i]-offsetY,r[i],(int)alpha[i]<<24 | color,radians[i]);
    }
}
