package com.xyy.game.ai.Screen;

import com.xyy.game.ai.AssetsLoader;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载所需资源
 * Created by ${XYY} on ${2016/5/27}.
 */
public class StartUpScreen extends Screen {
    public StartUpScreen(Game game) {
        super(game);
        /**
         * 创建新线程加载资源
         */
        Runnable assetsLoader = new AssetsLoader(game.getGraphics()/*, game.getFileIO()*/);
        Thread loaderThread = new Thread(assetsLoader);
        loaderThread.start();
    }

    @Override
    public void update(float deltaTime) {
        ArrayList<File> crashReports = new ArrayList<>(1);
        File externalStorage = game.getFileIO().getExternalStorage();
        File[] fileList = externalStorage.listFiles();
        if(fileList != null) {
            for (File file : fileList) {
                if (!file.isDirectory() && file.getName().endsWith(".crash.txt")) {
                    crashReports.add(file);
                }
            }
        }
        if(crashReports.size()==0)
            game.setScreen(new LogoScreen(game));
        else {
            while(AssetsLoader.getState()<AssetsLoader.MAINMENU_LOADED){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            game.setScreen(new CrashReportScreen(game, crashReports));
        }
    }

    @Override
    public void present(float deltaTime) {

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
