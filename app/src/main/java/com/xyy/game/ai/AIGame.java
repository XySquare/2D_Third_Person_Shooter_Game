package com.xyy.game.ai;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.xyy.game.ai.Screen.StartUpScreen;
import com.xyy.game.database.WeaponBaseHelper;
import com.xyy.game.framework.Screen;
import com.xyy.game.framework.impl.AndroidGame;

/**
 * 主Activity入口
 * Created by ${XYY} on ${2016/5/27}.
 */
public class AIGame extends AndroidGame {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new WeaponBaseHelper(getApplicationContext()).getWritableDatabase();
    }

    //Rename File Name
    @Override
    public Screen getStartScreen() {
        return new StartUpScreen(this);
    }
}