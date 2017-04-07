package com.xyy.game.ai;


import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.framework.Graphics;

/**
 * 进入游戏画面前渐变过渡，
 * 该状态不在状态列表中，结束后等待垃圾回收
 * Created by ${XYY} on ${2016/10/17}.
 */
public class GameState_FirstIn extends GameState {
    private float tAlpha;

    public GameState_FirstIn(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        tAlpha = 0xFF;
    }

    @Override
    public void enter() {}

    @Override
    public void update(float deltaTime) {
        gameScreen.getInput().getTouchEvents();
        if(deltaTime>1/30f) deltaTime = 1/30f;
        tAlpha -= 0xFF*deltaTime*2;
        if(tAlpha<=0){
            tAlpha = 0;
            gameScreen.setState(GameState.RUNNING);
        }
        //更新舞台
        stage.update(deltaTime);
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = gameScreen.getGraphics();
        /**
         * 绘制舞台
         */
        stage.present(g);
        g.fill((int)tAlpha<<24 & 0xFF000000);
    }

    @Override
    public void onBack() {
        //Do Nothing...
    }
}
