package com.xyy.game.ai;

import com.xyy.game.ai.Screen.StartUpScreen;
import com.xyy.game.framework.Screen;
import com.xyy.game.framework.impl.AndroidGame;

/**
 * 主Activity入口
 * Created by ${XYY} on ${2016/5/27}.
 */
public class AIGame extends AndroidGame {
    @Override
    public Screen getStartScreen() {
        return new StartUpScreen(this);
    }
}
