package com.xyy.game.ai.Screen;

import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.AssetsLoader;
import com.xyy.game.component.LoadingAnimation;
import com.xyy.game.ai.WorldBuilder;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Screen;

/**
 * 加载所需资源
 * Created by ${XYY} on ${2016/5/27}.
 */
public class GameLoadingScreen extends Screen {

    private WorldBuilder worldBuilder;

    private LoadingAnimation loadingAnimation;

    private Thread loaderThread;

    public GameLoadingScreen(Game game, String worldUid) {
        super(game);
        /**
         * 加载动画
         */
        loadingAnimation = new LoadingAnimation(50, 1280/2-100, 720/2, 0xFF66CCFF);
        /**
         * 创建新线程构建世界
         */
        worldBuilder = new WorldBuilder(worldUid, game.getFileIO(),game);
        loaderThread = new Thread(worldBuilder);

    }

    @Override
    public void update(float deltaTime) {

        loadingAnimation.update(deltaTime);

        if(worldBuilder.isBuilt()) {
            Screen screen = worldBuilder.getGameScreen();
            //载入完成，跳转到游戏界面
            game.setScreen(screen);
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        //黑色背景
        g.fill(0xFF000000);
        //Loading字样
        g.drawText("Loading...",1280/2,720/2+16,0xFFFFFFFF,35);
        //绘制加载动画
        loadingAnimation.present(g);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        loaderThread.start();
        Log.i("GameLoadingScreen","LoaderThread Started!");
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        return true;
    }
}
