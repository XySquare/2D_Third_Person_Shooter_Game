package com.xyy.game.framework;

import static com.xyy.game.framework.Graphics.PixmapFormat;

/**
 * Pixmap接口
 * Created by ${XYY} on ${2015/11/20}.
 */
public interface Pixmap {

    public int getWidth();

    public int getHeight();

    /**
     * 获取位图格式
     * @return Graphics.PixmapFormat的枚举常量
     */
    public PixmapFormat getFormat();

    /**
     * 释放位图
     */
    public void dispose();
}
