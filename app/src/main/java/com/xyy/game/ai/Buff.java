package com.xyy.game.ai;

import android.util.SparseArray;

import com.xyy.game.ai.Character.Character;

/**
 * Created by ${XYY} on ${2016/8/26}.
 */
public class Buff {

    public static final int ATK_UP_I = 0;
    public static final int SPEED_UP = 1;
    public static final int UNMOVEABLE = 2;
    public static final int ATK_UP_II = 3;
    public static final int ATK_UP_III = 4;
    public static final int ATK_UP_IV = 5;
    public static final int ATK_UP_V = 6;
    public static final int ATK_UP_VI = 7;
    public static final int ATK_UP_VII = 8;
    public static final int ATK_UP_VIII = 9;
    public static final int ATK_UP_IX = 10;
    public static final int ATK_UP_X = 11;

    private static final SparseArray<Buff> mBuffSparseArray = new SparseArray<>();
    /**
     * 唯一标识符
     */
    private final int uid;
    /**
     * 属性类型
     */
    final char[] type;
    /**
     * 数值
     */
     final int[] value;
    /**
     * 百分比
     */
     final float[] percentage;
    /**
     * 持续时长（s）
     */
     final int duration;
    /**
     * 最大叠加层数
     */
     final int max;
    /**
     * 叠加到最大层数后，
     * 再叠加时是否刷新时间
     */
     final boolean refresh;
    /**
     * 图标索引
     */
    public final int ico;
    /**
     * Buff名称
     */
    final String name;

    private Buff(int uid, char[] type, int[] value, float[] percentage, int duration, int max, boolean refresh, int ico, String name) {
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

    public boolean equals(int uid) {
        return this.uid == uid;
    }

    public static Buff get(int uid) {
        Buff buff = mBuffSparseArray.get(uid);
        if (buff == null) {
            buff = newInstance(uid);
            mBuffSparseArray.put(uid, buff);
        }
        return buff;
    }

    private static Buff newInstance(int uid) {
        switch (uid) {
            case ATK_UP_I:
                return new Buff(Buff.ATK_UP_I, new char[]{Character.ATK}, new int[]{1}, new float[]{0}, 5, 5, true, 3, "攻击提升I");
            case SPEED_UP:
                return new Buff(Buff.SPEED_UP, new char[]{Character.V}, new int[]{0}, new float[]{0.5f}, 5, 1, true, 14, "速度提升");
            case UNMOVEABLE:
                return new Buff(Buff.UNMOVEABLE, new char[]{Character.V}, new int[]{0}, new float[]{-1}, 2, 1, false, 13, "等待");
            case ATK_UP_II:
                return new Buff(Buff.ATK_UP_II, new char[]{Character.ATK}, new int[]{2}, new float[]{0}, 5, 5, true, 3, "攻击提升II");
            case ATK_UP_III:
                return new Buff(Buff.ATK_UP_III, new char[]{Character.ATK}, new int[]{3}, new float[]{0}, 5, 5, true, 3, "攻击提升III");
            case ATK_UP_IV:
                return new Buff(Buff.ATK_UP_IV, new char[]{Character.ATK}, new int[]{4}, new float[]{0}, 5, 5, true, 3, "攻击提升IV");
            case ATK_UP_V:
                return new Buff(Buff.ATK_UP_V, new char[]{Character.ATK}, new int[]{5}, new float[]{0}, 5, 5, true, 3, "攻击提升V");
            case ATK_UP_VI:
                return new Buff(Buff.ATK_UP_VI, new char[]{Character.ATK}, new int[]{6}, new float[]{0}, 5, 5, true, 3, "攻击提升VI");
            case ATK_UP_VII:
                return new Buff(Buff.ATK_UP_VII, new char[]{Character.ATK}, new int[]{7}, new float[]{0}, 5, 5, true, 3, "攻击提升VII");
            case ATK_UP_VIII:
                return new Buff(Buff.ATK_UP_VIII, new char[]{Character.ATK}, new int[]{8}, new float[]{0}, 5, 5, true, 3, "攻击提升VIII");
            case ATK_UP_IX:
                return new Buff(Buff.ATK_UP_IX, new char[]{Character.ATK}, new int[]{9}, new float[]{0}, 5, 5, true, 3, "攻击提IX");
            case ATK_UP_X:
                return new Buff(Buff.ATK_UP_X, new char[]{Character.ATK}, new int[]{10}, new float[]{0}, 5, 5, true, 3, "攻击提升X");
            default:
                throw new RuntimeException("你请求了一个不存在的Buff。");
        }
    }
}
