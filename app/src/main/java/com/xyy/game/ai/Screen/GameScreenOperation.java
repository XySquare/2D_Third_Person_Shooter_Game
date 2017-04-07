package com.xyy.game.ai.Screen;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

/**
 * Created by ${XYY} on ${2016/10/24}.
 */
public interface GameScreenOperation {

    /**
     * 设置游戏状态
     *
     * @param stateIndex 游戏状态标志
     */
    void setState(char stateIndex);

    Input getInput();

    Graphics getGraphics();

    void exit();

    void reLoad();

    void toMapsSelectingScreen();
}
