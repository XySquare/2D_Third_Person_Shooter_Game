package com.xyy.game.ai;

/**
 * Created by ${XYY} on ${2016/8/23}.
 */
public interface AttackGenerator {
    public abstract void generateAttack(float x, float y, float dx, float dy, int atk);
}
