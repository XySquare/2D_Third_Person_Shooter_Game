package com.xyy.game.ai;

import com.xyy.game.ai.Screen.GameScreenOperation;

/**
 * 游戏状态基类
 * Created by ${XYY} on ${2016/4/3}.
 */
public abstract class GameState {
    public static final char RUNNING = 0;
    public static final char PAUSED = 1;
    public static final char EXIT = 2;
    public static final char GAMEOVER = 3;

    protected final GameScreenOperation gameScreen;
    protected final Stage stage;

    public GameState(GameScreenOperation gameScreen, Stage stage){
        this.gameScreen = gameScreen;
        this.stage = stage;
    }

    public abstract void enter();

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void onBack();

    public void exit(){

    }

    public final void dispose() {
        stage.dispose();
    }
}
