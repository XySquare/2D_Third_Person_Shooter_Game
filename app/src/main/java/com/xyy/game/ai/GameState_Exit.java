package com.xyy.game.ai;

import android.graphics.Paint;

import com.xyy.game.ai.Screen.GameScreen;
import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.component.SquareButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.List;

/**
 * 退出确认界面
 * Created by ${XYY} on ${2016/10/1}.
 */
public class GameState_Exit extends GameState {

    private SquareButton okBt, cancelBt;

    public GameState_Exit(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        okBt = new SquareButton(1280/2+10,720/2+5,300,70,0x7F000000,"OK");
        cancelBt = new SquareButton(1280/2-10-300,720/2+5,300,70,0x7F000000,"CANCEL");
    }

    @Override
    public void enter() {
        okBt.initialize(0.1f);
        cancelBt.initialize(0);
    }

    @Override
    public void update(float deltaTime) {
        okBt.update(deltaTime);
        cancelBt.update(deltaTime);

        List<Input.Touch> touchEvents = gameScreen.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if(cancelBt.isClicked(event)){
                gameScreen.setState(GameState.PAUSED);
                break;
            }
            else if(okBt.isClicked(event)){
                gameScreen.exit();
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = gameScreen.getGraphics();
        stage.present(g);
        g.fill(0x7F00050B);
        g.drawText("Are you sure to quit?",1280/2,720/2-40,0xFFFFFFFF,50, Paint.Align.CENTER);
        okBt.present(g);
        cancelBt.present(g);
    }

    @Override
    public void onBack() {
        gameScreen.setState(GameState.PAUSED);
    }
}
