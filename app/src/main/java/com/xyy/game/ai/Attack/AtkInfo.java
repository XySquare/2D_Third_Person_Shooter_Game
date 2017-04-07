package com.xyy.game.ai.Attack;

/**
 * 攻击对象信息，
 * 被命中对象将可以通过该接口获得攻击信息
 * Created by ${XYY} on ${2016/9/16}.
 */
public interface AtkInfo {
    //产生攻击所需消耗的能量
    public int getEnergy();

    public char getFlag();
}
