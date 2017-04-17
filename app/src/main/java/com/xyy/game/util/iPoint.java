package com.xyy.game.util;

/**
 * 点
 * Created by ${XYY} on ${2016/5/27}.
 */
public class iPoint {
    public int x;
    public int y;

    public iPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(iPoint point){
        return (point!=null && x==point.x && y==point.y);
    }

    public boolean equals(int x,int y){
        return (x==this.x && y==this.y);
    }

    //绕(ox,oy)旋转:
    public void rotate(float ox, float oy, double rotation) {
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);
        double xx,yy;

        //"原点平移",平移的向量为-(ox, oy)
        xx = x - ox;
        yy = y - oy;

        //旋转,"复位平移", 平移的向量为(ox, oy)
        x = (int)((xx * cos - yy * sin) + ox);
        y = (int)((xx * sin + yy * cos) + oy);
    }
}
