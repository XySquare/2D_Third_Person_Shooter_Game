package com.xyy.game.ai;

import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.List;

/**
 * 游戏结束界面
 * Created by ${XYY} on ${2016/10/2}.
 */
public class GameState_GameOver extends GameState {

    private float timer;

    private CircleButton retryBt, mainmenuBt;

    public GameState_GameOver(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        mainmenuBt = new CircleButton(1280/2-160,720/2+80,80,0xFF30547C,Assets.mainMenuIco);
        retryBt = new CircleButton(1280/2+160,720/2+80,80,0xFFF3318A,Assets.retryIco);
    }

    @Override
    public void enter() {
        timer = 2;
        retryBt.initialize(2.1f);
        mainmenuBt.initialize(2);
    }

    @Override
    public void update(float deltaTime) {
        if(timer>0) {
            timer-=deltaTime;
            stage.update(deltaTime * timer / 2);
        }

        retryBt.update(deltaTime);
        mainmenuBt.update(deltaTime);

        List<Input.Touch> touchEvents = gameScreen.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if(mainmenuBt.isClicked(event)){
                gameScreen.toMapsSelectingScreen();
                break;
            }
            else if(retryBt.isClicked(event)){
                gameScreen.reLoad();
                break;
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = gameScreen.getGraphics();
        stage.present(g);
        g.fill(0x7F00050B);
        g.drawText("MISSION FAILED",1280/2-180,720/2-40,0xFFFFFFFF,50);
        retryBt.present(g);
        mainmenuBt.present(g);
    }

    @Override
    public void onBack() {
    }
}
