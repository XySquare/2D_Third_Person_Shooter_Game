package com.xyy.game.ai.Screen;

import android.graphics.Paint;

import com.xyy.game.ai.AssetsLoader;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.util.List;

/**
 * 显示制作信息
 * Created by ${LQ} on ${2016/9/28}.
 * Updated by ${XYY} on ${2016/10/17}
 */
public class LogoScreen extends Screen {

    private float alpha;
    private float timer;
    private int pointer;

    public LogoScreen(Game game) {
        super(game);
        alpha = 0x00;
        timer = 0;
        pointer = -1;
    }

    @Override
    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer >= 0.5f) {
            if(timer<0.7f){
                alpha = 0xFF*(timer-0.5f)*5;
            }
            else if(timer<1.7f){
                alpha = 0xFF;
            }
            else if(timer<1.9f){
                alpha = 0xFF*(1.9f-timer)*5;
            }
            else if(AssetsLoader.getState()>=AssetsLoader.MAINMENU_LOADED){
                    game.setScreen(new MainMenuScreen(game));
            }
        }
        List<Input.Touch> inputs = game.getInput().getTouchEvents();
        int length = inputs.size();
        for(int i = 0; i < length; i++) {
            Input.Touch event = inputs.get(i);
            if(event.type == Input.Touch.TOUCH_DOWN){
                 if(pointer==-1)
                     pointer = event.pointer;
            }
            else if(event.type == Input.Touch.TOUCH_UP){
                if(pointer==event.pointer) {
                    pointer = -1;
                    if (AssetsLoader.getState()>=AssetsLoader.MAINMENU_LOADED)
                        game.setScreen(new MainMenuScreen(game));
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.fill(0xFF000000);
        final int color = (int)alpha<<24 | 0xFFFFFF;
        g.drawText("Present by",1280/2,320,color,35, Paint.Align.CENTER);
        g.drawText("Xu YiYang",1280/2,370,color,35, Paint.Align.CENTER);
        g.drawText("Zhou WenGuan",1280/2,410,color,35, Paint.Align.CENTER);
        g.drawText("All Rights Reserved",1280/2,700,color,30, Paint.Align.CENTER);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        return false;
    }
}
