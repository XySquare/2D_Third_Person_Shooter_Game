package com.xyy.game.ai.Screen;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.AssetsLoader;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;
import java.util.List;
/**
 * 主菜单
 * Created by ${LQ} on ${2016/10/1}.
 */
public class MainMenuScreen extends Screen {
    private CircleButton getMoreBt,settingBt;//两个按钮
    private float off = 0;
    private float countTime = 0;
    private Boolean test = false;
    private float scale = 1;
    private boolean backif = false;
    private int offline = 0;//背景绘线偏移量
    private byte req;
    public MainMenuScreen(Game game) {
        super(game);
        getMoreBt = new CircleButton(340,430,100,0xFF31547D, Assets.getMore);
        settingBt = new CircleButton(940,430,100,0xFF31547D,Assets.setting);
        getMoreBt.initialize(0.1f);
        settingBt.initialize(0.1f);
        req = 0;
    }

    @Override
    public void update(float deltaTime) {
        getMoreBt.update(deltaTime);
        settingBt.update(deltaTime);
        offline = offline + 1;
        if(offline==80)
            offline = 0;

        countTime = countTime + deltaTime;
        off = 1040f*countTime - 1200f*countTime*countTime/2;
        if(countTime>=1) {
            off = 440;
            test = true;
        }
       if(test)
        {
            if(!backif){
                scale = scale + deltaTime/5;
                if(scale>=1.15f)
                    backif = true;
            }
            else{
                scale = scale - deltaTime/5;
                if(scale<=1f)
                    backif = false;
            }

        }

        List<Input.Touch> inputs = game.getInput().getTouchEvents();

        if(req == 0){
            int length = inputs.size();
            for (int i = 0; i < length; i++) {
                Input.Touch event = inputs.get(i);
                if (event.type == Input.Touch.TOUCH_UP && (event.x > 490 && event.x < 790 && event.y > 210 && event.y < 510)) {
                    if (AssetsLoader.getState() < AssetsLoader.MAPSELECTING_LOADED)
                        req = 1;
                    else
                        game.setScreen(new MapsSelectingScreen(game));
                    break;
                }
                if (getMoreBt.isClicked(event)) {
                    game.setScreen(new InfoScreen(game));
                    break;
                } else if (settingBt.isClicked(event)) {
                    if (AssetsLoader.getState() < AssetsLoader.SETTING_LOADED)
                        req = 2;
                    else
                        game.setScreen(new SettingScreen(game));
                    break;
                }
            }
        }
        else if(req==1 && AssetsLoader.getState()>=AssetsLoader.MAPSELECTING_LOADED)
            game.setScreen(new MapsSelectingScreen(game));
        else if(req==2 && AssetsLoader.getState()>=AssetsLoader.SETTING_LOADED)
            game.setScreen(new SettingScreen(game));
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.fill(0xff202d42);
        for(int i = 0;i<16;i++){
            g.drawLine(i*80+offline,0,i*80+offline,720,0xff2d415f);
        }
        for(int i = 9;i>0;i--){
            g.drawLine(0,i*80-offline,1280,i*80-offline,0xff2d415f);
        }
        g.drawPixmap(Assets.ai,-670+off*2.32f,0);//380 16 8
        getMoreBt.present(g);
        settingBt.present(g);
        if(!test)
            g.drawPixmap(Assets.start, 640 - 150, 720 - off);
        else
            g.drawPixmapScale(Assets.start,640,430,scale,scale,0xFF);

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
