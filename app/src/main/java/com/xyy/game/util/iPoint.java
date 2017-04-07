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

}
