package com.xyy.game.ai.Screen;

import android.graphics.Color;
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

    private CircleButton backBt;//返回按钮
    private CircleButton lowQualityBt;//低品质模式按钮
    private CircleButton filterBt;//开启过滤器
    private CircleButton highFilterBt;//开启高级过滤器
    private String filterSwitch;//过滤器状态
    private String highFilterSwitch;//高级过滤器状态
    private String lowQualitySwitch;//记录缓存大小
    private float offRect = 0;//方形区域移动
    private int offRectAlpha = 0;//方形区域移动的alpha值
    private int offline;//背景绘线偏移量

    public SettingScreen(Game game) {
        super(game);
        backBt = new CircleButton(60, 50, 50, 0x00000000, Assets.back);
        backBt.initialize(0.2f);
        filterBt = new CircleButton(300, 310, 70, 0xff2d415f, Assets.filter);
        filterBt.initialize(0.3f);
        highFilterBt = new CircleButton(300, 465, 70, 0xff2d415f, Assets.highFilter);
        highFilterBt.initialize(0.4f);
        lowQualityBt = new CircleButton(300, 620, 70, 0xff2d415f, Assets.clear);
        lowQualityBt.initialize(0.5f);

        filterSwitch = GameDataManager.FilterBitmap ? "开" : "关";
        highFilterSwitch = GameDataManager.AdvancedFilterBitmap ? "开" : "关";
        lowQualitySwitch = GameDataManager.LowQuality ? "开" : "关";
    }

    @Override
    public void update(float deltaTime) {
        offline = offline + 1;
        if (offline == 80)
            offline = 0;
        offRect += (100 - offRect) * deltaTime * 20;
        offRectAlpha = (int) (offRect * 2.55);
        if (100 - offRect < 0.001) {
            offRect = 100;
        }
        backBt.update(deltaTime);
        lowQualityBt.update(deltaTime);
        filterBt.update(deltaTime);
        highFilterBt.update(deltaTime);

        List<Input.Touch> inputs = game.getInput().getTouchEvents();
        int length = inputs.size();
        for (int i = 0; i < length; i++) {
            Input.Touch event = inputs.get(i);
            if (backBt.isClicked(event)) {
                game.setScreen(new MainMenuScreen(game));
                break;
            } else if (lowQualityBt.isClicked(event)) {
                GameDataManager.LowQuality = !GameDataManager.LowQuality;
                lowQualitySwitch = GameDataManager.LowQuality ? "开" : "关";
                break;
            }  else if (filterBt.isClicked(event)) {
                boolean state = !GameDataManager.FilterBitmap;
                GameDataManager.FilterBitmap = state;
                GameDataManager.AntiAlias = state;
                game.getGraphics().setAntiAlias(state);
                game.getGraphics().setFilterBitmap(state);
                filterSwitch = state ? "开" : "关";
            } else if (highFilterBt.isClicked(event)) {
                boolean state = !GameDataManager.AdvancedFilterBitmap;
                GameDataManager.AdvancedFilterBitmap = state;
                game.setFilterBitmap(state);
                highFilterSwitch = state ? "开" : "关";
            }
        }
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.fill(0xff202d42);
        for (int i = 0; i < 16; i++) {
            g.drawLine(i * 80 + offline, 0, i * 80 + offline, 720, 0xff2d415f);
        }
        for (int i = 9; i > 0; i--) {
            g.drawLine(0, i * 80 - offline, 1280, i * 80 - offline, 0xff2d415f);
        }
        g.drawRect(0, (int) (-100 + offRect), 1280, 100, (offRectAlpha << 24) | 0x002d415f);
        backBt.present(g);
        lowQualityBt.present(g);
        filterBt.present(g);
        highFilterBt.present(g);
        g.drawText("开发人员：LQ XYY", 240, 160, Color.WHITE, 30);
        g.drawText("版本号：V1.0", 240, 205, Color.WHITE, 30);
        g.drawText("抗锯齿，开启可产生平滑的图像，关闭可提高性能", 410, 290, Color.WHITE, 30);
        g.drawText("当前状态:" + filterSwitch, 410, 350, Color.WHITE, 40);
        g.drawText("二线性过滤，开启可提高画面质量，但您可能会感觉到性能下降", 410, 445, Color.WHITE, 30);
        g.drawText("当前状态:" + highFilterSwitch, 410, 505, Color.WHITE, 40);
        g.drawText("开启低品质模式将降低贴图质量，降低内存占用", 410, 600, Color.WHITE, 30);
        g.drawText("当前状态:" + lowQualitySwitch + "（需重启以应用更改）", 410, 660, Color.WHITE, 40);
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
        game.setScreen(new MainMenuScreen(game));
        return true;
    }

}
