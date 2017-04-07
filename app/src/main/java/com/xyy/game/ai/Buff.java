package com.xyy.game.ai;

import android.util.Log;

/**
 * Created by ${XYY} on ${2016/8/26}.
 */
public class Buff {

    public static final int NULL = -1;
    public static final int ATK_UP = 0;
    public static final int SPEED_UP = 1;
    public static final int UNMOVEABLE = 2;

    /**
     * 唯一标识符
     */
    private final int uid;
    /**
     * 属性类型
     */
    public final char[] type;
    /**
     * 数值
     */
    public final int[] value;
    /**
     * 百分比
     */
    public final float[] percentage;
    /**
     * 持续时长（s）
     */
    public final int duration;
    /**
     * 最大叠加层数
     */
    public final int max;
    /**
     * 叠加到最大层数后，
     * 再叠加时是否刷新时间
     */
    public final boolean refresh;
    /**
     * 图标索引
     */
    public final int ico;
    /**
     * Buff名称
     */
    public final String name;

    public Buff(int uid, char[] type, int[] value, float[] percentage, int duration, int max, boolean refresh, int ico, String name){
        this.uid = uid;
        this.type = type;
        this.value = value;
        this.percentage = percentage;
        this.duration = duration;
        this.max = max;
        this.refresh = refresh;
        this.ico = ico;
        this.name = name;
    }

    public boolean equals(int uid){
        return this.uid == uid;
    }

}