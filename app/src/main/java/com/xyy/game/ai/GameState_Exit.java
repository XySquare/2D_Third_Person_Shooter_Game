package com.xyy.game.ai;

import com.xyy.game.ai.Screen.GameScreen;
import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.List;

/**
 * 退出确认界面
 * Created by ${XYY} on ${2016/10/1}.
 */
public class GameState_Exit extends GameState {

    private CircleButton okBt, cancelBt;

    public GameState_Exit(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        okBt = new CircleButton(1280/2+160,720/2+80,80,0xFFF3318A,Assets.tickIco);
        cancelBt = new CircleButton(1280/2-160,720/2+80,80,0xFF30547C,Assets.crossIco);
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
        g.drawText("您确定要退出么？",1280/2-180,720/2-40,0xFFFFFFFF,50);
        okBt.present(g);
        cancelBt.present(g);
    }

    @Override
    public void onBack() {
        gameScreen.setState(GameState.PAUSED);
    }
}
