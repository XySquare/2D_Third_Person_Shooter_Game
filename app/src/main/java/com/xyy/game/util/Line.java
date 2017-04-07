package com.xyy.game.util;

/**
 * 线段
 * Created by ${XYY} on ${2016/5/27}.
 */
public class Line {
    private final iPoint point1;
    private final iPoint point2;
    private final float length;
    private final float[] unit;

    public Line (iPoint p1, iPoint p2){
        point1 = p1;
        point2 = p2;
        //P1到P2的向量b
        float bx = p2.x - p1.x;
        float by = p2.y - p1.y;
        //长度
        length = (float) Math.sqrt(bx*bx + by*by);
        //单位向量
        unit = new float[]{bx/length, by/length};
    }

    public float getLength() {
        return length;
    }

    public float[] getUnit() {
        return unit;
    }

    public iPoint getPoint1() {
        return point1;
    }

    public iPoint getPoint2() {
        return point2;
    }
}
