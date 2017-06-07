package com.xyy.game.ai;


import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.framework.Graphics;

/**
 * 切换地图前渐变过渡，
 * 该状态不在状态列表中，结束后等待垃圾回收
 * Created by ${XYY} on ${2017/6/5}.
 */
public class GameState_MapSwitching extends GameState {
    private float tAlpha;

    public GameState_MapSwitching(GameScreenOperation gameScreen, Stage stage, String mapUid) {
        super(gameScreen, stage);
        tAlpha = 0x00;
    }

    @Override
    public void enter() {}

    @Override
    public void update(float deltaTime) {
        gameScreen.getInput().getTouchEvents();
        if(deltaTime>1/30f) deltaTime = 1/30f;
        tAlpha += 0xFF*deltaTime*2;
        if(tAlpha>=0xFF){
            tAlpha = 0xFF;

            gameScreen.isMapBuilt();
        }
        //更新舞台
        //stage.update(deltaTime);
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
