package com.xyy.game.ai.Weapon;

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

public abstract class Weapon {
    public static enum PixmapQuality {
        LOW, NORMAL
    }

    public static enum Rarity {
        N, R, SR, SSR
    }

    private final Rarity mRarity;

    private final String mName;

    private final String mDescription;

    private final int mDamage;
    /**
     * 攻击延迟
     */
    private final float mAtkDelay;

    /**
     * 消耗能量
     */
    protected final int mEnergyCost;

    /**
     * 武器贴图
     */
    private Pixmap mPixmap;

    private PixmapQuality mPixmapQuality;

    private final int mPrice;

    public static Weapon newInstance(String _sClassName) {
        Weapon object = null;
        try {
            Class clazz = Class.forName(_sClassName);
            Constructor<Weapon> constructor = clazz.getConstructor();
            object = constructor.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    Weapon(Rarity rarity, String name, String description, int damage, int energyCost, float atkDelay, int price) {
        mRarity = rarity;
        mName = name;
        mDescription = description;
        mDamage = damage;
        mEnergyCost = energyCost;
        mAtkDelay = atkDelay;
        mPrice = price;
    }

    public abstract void attack(Stage stage, Character src, float dx, float dy);

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

    public Rarity getRarity() {
        return mRarity;
    }

    public int getPrice() {
        return mPrice;
    }

    abstract String getPixmapFileName();
}
