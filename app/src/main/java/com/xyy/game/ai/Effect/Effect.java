package com.xyy.game.ai.Effect;

import com.xyy.game.framework.Graphics;

/**
 * 特效
 * Created by ${XYY} on ${2016/8/7}.
 */
public abstract class Effect {
    //质点坐标
    protected float x;
    protected float y;
    //标记是否将该对象移除(Default = false)
    protected boolean isDead;

    /**
     * 初始化特效
     * @param x 初始X坐标
     * @param y 初始Y坐标
     */
    public void initialize(int x, int y){
        this.x = x;
        this.y = y;
        isDead = false;
    }

    /**
     * 更新特效
     * @param deltaTime 两帧间时间间隔
     */
    public abstract void update(float deltaTime);

    /**
     * 以特定偏移量绘制攻击
     * @param g 绘图接口
     * @param offsetX X偏移量
     * @param offsetY Y偏移量
     */
    public abstract void present(Graphics g, float offsetX, float offsetY);

    public final boolean isDead() {
        return isDead;
    }
}
