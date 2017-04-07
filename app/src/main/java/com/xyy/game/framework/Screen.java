package com.xyy.game.framework;

/**
 * Screen抽象类
 * 所有屏幕将拓展，并实现该类
 * Created by ${XYY} on ${2016/2/13}.
 */
public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();

    public abstract boolean onBack();
}
