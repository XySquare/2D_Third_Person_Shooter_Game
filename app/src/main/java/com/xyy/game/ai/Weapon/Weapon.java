package com.xyy.game.ai.Weapon;

import android.util.Log;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.xyy.game.ai.Weapon.Weapon.PixmapQuality.*;

/**
 * Created by ${XYY} on ${2017/4/13}.
 */

public abstract class Weapon{
    public static enum PixmapQuality {
        LOW, NORMAL
    }

    public static enum Rarity {
        N, R, SR, SSR
    }

    /**
     * 稀有度
     */
    private final Rarity mRarity;

    /**
     * 各等级所需的经验
     */
    private final int[] mLvExpReq;

    private int mCurExp;

    private int mCurLv;

    /**
     * 名称
     */
    private final String mName;

    /**
     * 描述
     */
    private final String mDescription;

    /**
     * 伤害
     */
    protected int mDamage;

    /**
     * 攻击延迟(s)
     */
    protected float mAtkDelay;

    /**
     * 消耗能量
     */
    protected int mEnergyCost;

    /**
     * 武器贴图
     */
    private Pixmap mPixmap;

    /**
     * 贴图质量
     */
    private PixmapQuality mPixmapQuality;

    Weapon(Rarity rarity, String name, String description, int damage, int energyCost, float atkDelay, int[] lvExpReq) {
        mRarity = rarity;
        mName = name;
        mDescription = description;
        mDamage = damage;
        mEnergyCost = energyCost;
        mAtkDelay = atkDelay;
        mLvExpReq = lvExpReq;
        mCurLv = 1;
        mCurExp = 0;
    }

    public void initialize(int lv, int exp){
        mCurLv = lv;
        mCurExp = exp;
        upGrade(lv);
    }

    public abstract void attack(Stage stage, Character src, float dx, float dy);

    abstract void upGrade(int lv);

    public void addExp(int exp){
        int curExp = mCurExp + exp;
        if(curExp >= mLvExpReq[mCurLv-1]){
            curExp = 0;
            upGrade(++mCurLv);
        }
        mCurExp = curExp;
    }

    public void loadPixmap(Graphics g, PixmapQuality pixmapQuality) {
        if (mPixmapQuality != pixmapQuality) {
            if (mPixmap != null)
                mPixmap.dispose();
            if (pixmapQuality == LOW)
                mPixmap = g.newPixmap(getPixmapFileName() + ".low.png", Graphics.PixmapFormat.ARGB4444);
            else
                mPixmap = g.newPixmap(getPixmapFileName() + ".png", Graphics.PixmapFormat.ARGB4444);
            mPixmapQuality = pixmapQuality;
        }
    }

    public Rarity getRarity() {
        return mRarity;
    }

    public int getMaxLv() {
        return mLvExpReq.length+1;
    }

    public int getCurLv() {
        return mCurLv;
    }

    public int getCurExp() {
        return mCurExp;
    }

    public int getNextLvExpReq() {
        return mLvExpReq[mCurLv-1];
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getDamage() {
        return mDamage;
    }

    public final float getAtkDelay() {
        return mAtkDelay;
    }

    public final int getEnergyCost() {
        return mEnergyCost;
    }

    public Pixmap getPixmap() {
        return mPixmap;
    }

    abstract String getPixmapFileName();

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
