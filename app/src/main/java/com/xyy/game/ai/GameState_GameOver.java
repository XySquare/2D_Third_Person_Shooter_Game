package com.xyy.game.ai;

import android.graphics.Paint;

import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.ai.Screen.UserDate;
import com.xyy.game.component.SquareButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.List;

/**
 * 游戏结束界面
 * Created by ${XYY} on ${2016/10/2}.
 */
public class GameState_GameOver extends GameState {

    private float timer;

    private SquareButton retryBt, mainmenuBt;

    private String mStringEarnCredit;

    public GameState_GameOver(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen, stage);
        mainmenuBt = new SquareButton(1280/2-10-300,720/2+5+70,300,70,0x7F000000,"MAIN MENU");
        retryBt = new SquareButton(1280/2+10,720/2+5+70,300,70,0x7F000000,"RETRY");
    }

    @Override
    public void enter() {
        timer = 2;
        retryBt.initialize(2.1f);
        mainmenuBt.initialize(2);

        int score = stage.getScore();
        int credit = (int) (score * 0.01);
        mStringEarnCredit = "EARN CREDIT: "+credit;

        //TODO: Save Data
        UserDate.sCurrency += credit;
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
        g.drawText("MISSION FAILED",1280/2,720/2-40-50-20,0xFFFFFFFF,50, Paint.Align.CENTER);
        g.drawText("SCORE: "+stage.getScore(),1280/2,720/2-40,0xFFFFFFFF,50, Paint.Align.CENTER);
        g.drawText(mStringEarnCredit,1280/2,720/2-40+20+50,0xFFFFFFFF,50, Paint.Align.CENTER);
        retryBt.present(g);
        mainmenuBt.present(g);
    }

    @Override
    public void onBack() {
    }
}
