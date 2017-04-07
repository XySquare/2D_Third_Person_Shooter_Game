package com.xyy.game.ai;


/**
 * Created by ${XYY} on ${2016/8/22}.
 */
public abstract class GameObject {
    //对舞台的引用，提供对各类接口的访问
    protected final Stage stage;
    //质点坐标
    protected float x,y;

    public GameObject(Stage stage) {
        this.stage = stage;
    }

    public final float getX() {
        return x;
    }

    public final float getY() {
        return y;
    }

    public abstract boolean isDead();

}
