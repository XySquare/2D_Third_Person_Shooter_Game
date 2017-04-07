package com.xyy.game.framework.impl;

import android.graphics.Bitmap;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;


/**
 * Created by ${XYY} on ${2015/3/5}.
 */
public final class AndroidPixmap implements Pixmap {
    //公共类成员bitmap，外部方法可通过改变量获取Bitmap实例
    public Bitmap bitmap;
    private Graphics.PixmapFormat format;
    public AndroidPixmap(Bitmap bitmap, Graphics.PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }
    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public Graphics.PixmapFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }
}
