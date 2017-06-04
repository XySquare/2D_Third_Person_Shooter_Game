package com.xyy.game.ai.Screen;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.AssetsLoader;
import com.xyy.game.ai.GameDataManager;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.util.List;

/**
 * 加载所需资源
 * Created by ${LQ} on ${2016/10/13}.
 * Updated by ${XYY} on ${2016/10/17}.
 */
public class SettingScreen extends Screen {

    //private CircleButton backBt;//返回按钮
    private CircleButton lowQualityBt;//低品质模式按钮
    private CircleButton filterBt;//开启过滤器
    private CircleButton highFilterBt;//开启高级过滤器
    private String filterSwitch;//过滤器状态
    private String highFilterSwitch;//高级过滤器状态
    private String lowQualitySwitch;//记录缓存大小
    private float offRect = 0;//方形区域移动
    private int offRectAlpha = 0;//方形区域移动的alpha值
    private int offline;//背景绘线偏移量

    private int mPointer = -1;
    private int mPressedIndex = -1;

    private final int y = -30;

    public SettingScreen(Game game) {
        super(game);
        //backBt = new CircleButton(60, 50, 50, 0x00000000, Assets.back);
        //backBt.initialize(0.2f);
        filterBt = new CircleButton(300, 310, 70, 0xff2d415f, Assets.filter);
        filterBt.initialize(0.3f);
        highFilterBt = new CircleButton(300, 465, 70, 0xff2d415f, Assets.highFilter);
        highFilterBt.initialize(0.4f);
        lowQualityBt = new CircleButton(300, 620, 70, 0xff2d415f, Assets.clear);
        lowQualityBt.initialize(0.5f);

        filterSwitch = GameDataManager.FilterBitmap ? "ON" : "OFF";
        highFilterSwitch = GameDataManager.AdvancedFilterBitmap ? "ON" : "OFF";
        lowQualitySwitch = GameDataManager.LowQuality ? "LOW" : "NORMAL";
    }

    @Override
    public void update(float deltaTime) {
        /*offline = offline + 1;
        if (offline == 80)
            offline = 0;
        offRect += (100 - offRect) * deltaTime * 20;
        offRectAlpha = (int) (offRect * 2.55);
        if (100 - offRect < 0.001) {
            offRect = 100;
        }*/

        lowQualityBt.update(deltaTime);
        filterBt.update(deltaTime);
        highFilterBt.update(deltaTime);

        List<Input.Touch> inputs = game.getInput().getTouchEvents();
        int length = inputs.size();
        for (int i = 0; i < length; i++) {
            Input.Touch event = inputs.get(i);
            if(event.type == Input.Touch.TOUCH_DOWN){
                if(mPointer == -1){
                    if(inBounds(event, 200, y + 290-74, 189, 74)){
                        mPointer = event.pointer;
                        mPressedIndex = 0;
                    }
                    else if(inBounds(event, 200, y + 425-74, 189, 74)){
                        mPointer = event.pointer;
                        mPressedIndex = 1;
                    }
                    else if(inBounds(event, 200, y + 560-74, 189, 74)){
                        mPointer = event.pointer;
                        mPressedIndex = 2;
                    }
                    //STORE
                    else if(inBounds(event,81,720-84,218,84)){
                        game.setScreen(new Screen_MainMenu_Store(game));
                    }
                    //REPOSITORY
                    else if(inBounds(event,81+218,720-84,218,84)){
                        game.setScreen(new Screen_MainMenu_Repository(game));
                    }
                    //CAMPAIGN
                    else if(event.x>81+4*218 && event.y>720-84){
                        game.setScreen(new MapsSelectingScreen(game));
                    }
                }
            }
            else if(event.type == Input.Touch.TOUCH_UP){
                if(mPointer == event.pointer){
                    mPointer = -1;
                    if(mPressedIndex==0 && inBounds(event, 200, y + 290-74, 189, 74)){
                        boolean state = !GameDataManager.FilterBitmap;
                        GameDataManager.FilterBitmap = state;
                        GameDataManager.AntiAlias = state;
                        game.getGraphics().setAntiAlias(state);
                        game.getGraphics().setFilterBitmap(state);
                        filterSwitch = state ? "ON" : "OFF";
                    }
                    else if(mPressedIndex==1 && inBounds(event, 200, y + 425-74, 189, 74)){
                        boolean state = !GameDataManager.AdvancedFilterBitmap;
                        GameDataManager.AdvancedFilterBitmap = state;
                        game.setFilterBitmap(state);
                        highFilterSwitch = state ? "NO" : "OFF";
                    }
                    else if(mPressedIndex==2 && inBounds(event, 200, y + 560-74, 189, 74)){
                        GameDataManager.LowQuality = !GameDataManager.LowQuality;
                        lowQualitySwitch = GameDataManager.LowQuality ? "LOW" : "NORMAL";
                    }
                    mPressedIndex = -1;
                }
            }
        }
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.background, 0, 0);

        //顶部条
        g.drawPixmap(Assets.main_menu_top_bar, 0, 0);
        //返回按钮
        g.drawPixmapAlpha(Assets.back, 22, 20, 0x7F);
        //"MainMenu"文字
        g.drawText("SETTING", 77, 28, 0xFFFFFFFF, 24);
        //"Store"文字
        //g.drawText("[STORE]", 77, 53, 0xFFFFFFFF, 24);

        /*for (int i = 0; i < 16; i++) {
            g.drawLine(i * 80 + offline, 0, i * 80 + offline, 720, 0xff2d415f);
        }
        for (int i = 9; i > 0; i--) {
            g.drawLine(0, i * 80 - offline, 1280, i * 80 - offline, 0xff2d415f);
        }
        g.drawRect(0, (int) (-100 + offRect), 1280, 100, (offRectAlpha << 24) | 0x002d415f);*/
        /*backBt.present(g);
        lowQualityBt.present(g);
        filterBt.present(g);
        highFilterBt.present(g);*/

        g.drawText("ANTIALIAS - ON TO PRODUCE A SMOOTH IMAGE, TURN OFF TO IMPROVE PERFORMANCE.", 410, y + 263, Color.WHITE, 24);
        if (mPressedIndex == 0)
            g.drawPixmap(Assets.button_details, 200, y + 290-74, 189, 0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, 200, y + 290-74, 0, 0, 189, 74);
        g.drawText(filterSwitch, 200+94, y + 263, Color.WHITE, 24, Paint.Align.CENTER);

        g.drawText("BILINEAR SAMPLING - TURN ON TO IMPROVE PICTURE QUALITY,", 410, y + 373, Color.WHITE, 24);
        g.drawText("BUT YOU MAY FEEL PERFORMANCE DEGRADATION.", 410, y + 397, Color.WHITE, 24);
        if (mPressedIndex == 1)
            g.drawPixmap(Assets.button_details, 200, y + 415-74, 189, 0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, 200, y + 415-74, 0, 0, 189, 74);
        g.drawText(highFilterSwitch, 200+94, y + 388, Color.WHITE, 24, Paint.Align.CENTER);

        g.drawText("TEXTURE QUALITY (RESTART TO APPLY THE CHANGE)", 410, y + 513, Color.WHITE, 24);
        if (mPressedIndex == 2)
            g.drawPixmap(Assets.button_details, 200, y + 540-74, 189, 0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, 200, y + 540-74, 0, 0, 189, 74);
        g.drawText(lowQualitySwitch, 200+94, y + 513, Color.WHITE, 24, Paint.Align.CENTER);

        g.drawText("DEVELOPERS: XU YIYANG, LUO QING(V1.0), ZHOU WENGUAN(V2.0)", 1280/2, y + 603, Color.WHITE, 24, Paint.Align.CENTER);
        g.drawText("VERSION: V2.0", 1280/2, y + 627, Color.WHITE, 24, Paint.Align.CENTER);

        final int offsetX = 81;
        String[] bottom_bar_text = new String[]{"STORE","REPOSITORY","","",""};
        g.drawPixmap(Assets.main_menu_bottom_bar_button,-218+offsetX,720-84,0,0,218,84);
        g.drawPixmap(Assets.setting,18,720-84+20);
        for(int i=0;i<4;i++){
            g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX+i*218,720-84,218,0,218,84);
            g.drawText(bottom_bar_text[i],offsetX+i*218+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
        }
        g.drawPixmap(Assets.main_menu_bottom_bar_button_red,offsetX+4*218,720-84);
        g.drawText("CAMPAIGN",offsetX+4*218+164,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
    }


    @Override
    public void pause() {
        GameDataManager.saveSettings(game.getFileIO());
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        return true;
    }

    private boolean inBounds(Input.Touch event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

}
