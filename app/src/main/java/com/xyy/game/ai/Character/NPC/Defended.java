package com.xyy.game.ai.Character.NPC;

/**
 * Created by berryice on 2017/4/16.
 */

public interface Defended {

    /**
     * 标识该对象是否可以被防卫，或者是否接受防卫
     * @return
     */
    boolean canBeDefended();

    /**
     * 防卫者
     * @return
     */
    Defender defender();

    /**
     * 当被defender防卫时
     * @param defender
     */
    void onDefendedBy(Defender defender);

    /**
     * 当defender不再防卫时
     * @param defender
     */
    void onLostDefenceBy(Defender defender);

    /**
     * X坐标
     * @return
     */
    float getX();

    /**
     * Y坐标
     * @return
     */
    float getY();
}
