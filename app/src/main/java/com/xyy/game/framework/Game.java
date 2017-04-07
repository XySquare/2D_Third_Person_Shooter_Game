package com.xyy.game.framework;

import android.content.Intent;

/**
 * Game接口
 * Created by ${XYY} on ${2015/11/20}.
 */
public interface Game {
    Input getInput();

    FileIO getFileIO();

    Graphics getGraphics();

    Audio getAudio();

    void setScreen(Screen screen);

    Screen getCurrentScreen();

    Screen getStartScreen();

    void setFilterBitmap(boolean enable);

    void newActivity(Intent intent);

    void exit();
}
