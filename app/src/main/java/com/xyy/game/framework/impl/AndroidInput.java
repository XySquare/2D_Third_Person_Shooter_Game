package com.xyy.game.framework.impl;

import android.content.Context;
import android.view.View;

import com.xyy.game.framework.Input;

import java.util.List;

/**
 * Created by ${XYY} on ${2016/3/5}.
 */
public final class AndroidInput implements Input {
    MultiTouchHandler touchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    @Override
    public List<Touch> getTouchEvents() { return touchHandler.getTouchEvents(); }
}
