package com.xyy.game.ai.Screen;
import android.graphics.Color;

import com.xyy.game.ai.Assets;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.framework.Screen;

import java.util.List;

/**
 * 显示游戏背景及玩法
 * Created by ${LQ} on ${2016/10/13}.
 */
public class InfoScreen extends Screen {

    private CircleButton backBt;//返回按钮
    private float offRect = 0;//方形区域移动
    private int offRectAlpha = 0;//方形区域移动的alpha值
    private int offline;//背景绘线偏移量

    private String[] text;

    public InfoScreen(Game game) {
        super(game);
        backBt = new CircleButton(60,50,60,0x00000000,Assets.back);
        backBt.initialize(0.2f);

        text = new String[]{"某年某月某日，AI突然失控，试图消灭人类、",
                            "取得统治地位，人类与AI的战斗由此开始……",
                            "",
                            "左侧虚拟摇杆：控制人物移动。",
                            "右侧虚拟摇杆：控制人物攻击。"};
    }

    @Override
    public void update(float deltaTime) {
        offline = offline + 1;
        if(offline==80)
            offline = 0;
        offRect += (100 - offRect) * deltaTime * 20;
        offRectAlpha = (int)(offRect*2.55);
        if (100 - offRect < 0.001) {
            offRect = 100;
        }
        backBt.update(deltaTime);
        List<Input.Touch> inputs = game.getInput().getTouchEvents();
        int length = inputs.size();
        for (int i = 0; i < length; i++) {
            Input.Touch event = inputs.get(i);
            if (backBt.isClicked(event)) {
                game.setScreen(new MainMenuScreen(game));
                break;
            }
        }

    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.fill(0xff202d42);
        for(int i = 0;i<16;i++){
            g.drawLine(i*80+offline,0,i*80+offline,720,0xff2d415f);
        }
        for(int i = 9;i>0;i--){
            g.drawLine(0,i*80-offline,1280,i*80-offline,0xff2d415f);
        }
        g.drawRect(0, (int) (-100 + offRect), 1280, 100, (offRectAlpha << 24) | 0x002d415f);
        backBt.present(g);
        g.drawText("游戏背景及玩法:", 240, 265, Color.WHITE, 50);
        for(int i=0;i<text.length;i++)
            g.drawText(text[i],240,335+i*45, Color.WHITE,40);
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
        game.setScreen(new MainMenuScreen(game));
        return true;
    }


}
