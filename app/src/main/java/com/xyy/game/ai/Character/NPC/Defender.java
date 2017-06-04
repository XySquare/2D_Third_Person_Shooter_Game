package com.xyy.game.ai.Character.NPC;

/**
 * Created by berryice on 2017/4/16.
 */

public interface Defender {

    /**
     * 获得防卫的对象
     * @return defended;
     */
    Defended defended();

    /**
     * 设置防卫的对象
     * @param defended
     */
    void defend(Defended defended);

    /**
     * 解除对defended的防卫
     * @param defended
     */
    void unDefend(Defended defended);

    /**
     * 当防卫的对象被摧毁时
     * @param defended
     */
    void onDefendedDestroyed(Defended defended);
}
