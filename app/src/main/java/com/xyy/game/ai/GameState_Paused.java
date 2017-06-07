package com.xyy.game.ai;

import android.graphics.Paint;

import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.component.SquareButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.List;

/**
 * 游戏暂停界面
 * Created by ${XYY} on ${2016/9/30}.
 */
public class GameState_Paused extends GameState {

    private SquareButton resumeBt, exitBt, mainMenuBt;

    public GameState_Paused(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        resumeBt = new SquareButton(1280/2 - 150,720/2 - 35,300,70,0x7F000000,"RESUME");
        exitBt = new SquareButton(1280/2 - 150,720/2+75 - 35,300,70,0x7F000000,"QUIT");
        mainMenuBt = new SquareButton(1280/2 - 150,720/2-75 - 35,300,70,0x7F000000,"MAIN MENU");
    }

    @Override
    public void enter() {
        mainMenuBt.initialize(0);
        resumeBt.initialize(0.1f);
        exitBt.initialize(0.2f);
    }

    @Override
    public void update(float deltaTime) {
        resumeBt.update(deltaTime);
        exitBt.update(deltaTime);
        mainMenuBt.update(deltaTime);

        List<Input.Touch> touchEvents = gameScreen.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if(resumeBt.isClicked(event)){
                gameScreen.setState(GameState.RUNNING);
                break;
            }
            else if(exitBt.isClicked(event)){
                gameScreen.setState(GameState.EXIT);
                break;
            }
            else if(mainMenuBt.isClicked(event)){
                //stage.release();
                gameScreen.toMapsSelectingScreen();
                //ReStart
                //gameScreen.reLoad();
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = gameScreen.getGraphics();
        stage.present(g);
        g.fill(0x7F00050B);
        //g.drawText("PAUSED",1280/2 - 150,720/2-75,0xFFFFFFFF,65, Paint.Align.CENTER);;
        resumeBt.present(g);
        exitBt.present(g);
        mainMenuBt.present(g);
    }

    @Override
    public void onBack() {
        gameScreen.setState(GameState.RUNNING);
    }
}
