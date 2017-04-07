package com.xyy.game.framework;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Shader;

/**
 * Graphics接口
 * 用于在人工帧缓冲区上绘制图形
 * Created by ${XYY} on ${2016/2/13}.
 */
public interface Graphics {
    //公共静态枚举方法
    //图片格式
    public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    /**
     * 设置抗锯齿
     * @param enable true = 开启
     */
    void setAntiAlias(boolean enable);

    /**
     * 设置位图过滤
     * @param enable true = 开启
     */
    void setFilterBitmap(boolean enable);

    /**
     * 尝试使用特定格式从资源文件中加载位图
     * BitmapFactory可能忽略该格式（原因不明）
     *
     * @param fileName 位图资源文件路径
     * @param format   目标格式
     * @return AndroidBitmap实例
     */
    Pixmap newPixmap(String fileName, PixmapFormat format);

    /**
     * 用特定颜色清除人工帧缓冲区
     *
     * @param color 32位ARGB颜色
     */
    void fill(int color);

    void drawPixel(int x, int y, int color);

    void drawLine(float x, float y, float x2, float y2, int color);

    void drawLine(float x, float y, float x2, float y2, int color, int width);

    void drawRect(int x, int y, int width, int height, int color);

    void drawCircle(float x, float y, float r, int color);

    void drawCircle(float x, float y, float r, Shader shader, float scale);

    /**
     * 绘制圆环
     *
     * @param x          中心X坐标
     * @param y          中心Y坐标
     * @param r          半径
     * @param startAngle 起始角（角度）
     * @param sweepAngle 圆心角（角度）
     * @param color      颜色
     * @param width      圆环宽度（从半径向两边拓展）
     */
    void drawRing(float x, float y, float r, float startAngle, float sweepAngle, int color, int width);

    /**
     * 在人工帧缓冲区中，绘制位图的一部分
     *
     * @param pixmap    Pixmap实例
     * @param x         X坐标
     * @param y         Y坐标
     * @param srcX      目标位图的左上角X坐标
     * @param srcY      目标位图的左上角Y坐标
     * @param srcWidth  目标位图的宽度
     * @param srcHeight 目标位图的高度
     */
    void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
                    int srcWidth, int srcHeight);

    /**
     * 在人工帧缓冲区中，绘制位图的一部分，并制定透明度
     *
     * @param pixmap    Pixmap实例
     * @param x         X坐标
     * @param y         Y坐标
     * @param srcX      目标位图的左上角X坐标
     * @param srcY      目标位图的左上角Y坐标
     * @param srcWidth  目标位图的宽度
     * @param srcHeight 目标位图的高度
     * @param alpha     透明度
     */
    void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
                    int srcWidth, int srcHeight, int alpha);

    /**
     * 在人工帧缓冲区中，在给定的坐标位置位绘制出完整的Pixmap
     *
     * @param pixmap Pixmap实例
     * @param x      X坐标
     * @param y      Y坐标
     */
    void drawPixmap(Pixmap pixmap, float x, float y);

    /**
     * 在人工帧缓冲区中，在给定的坐标位置位绘制出完整的Pixmap，并制定透明度
     *
     * @param pixmap Pixmap实例
     * @param x      X坐标
     * @param y      Y坐标
     * @param alpha  透明度
     */
    void drawPixmapAlpha(Pixmap pixmap, float x, float y, int alpha);

    /**
     * 在人工帧缓冲区中，在给定的坐标<b>为中心</b>绘制出完整的Pixmap,并以该中心按指定角度旋转
     *
     * @param pixmap Pixmap实例
     * @param x      X坐标
     * @param y      Y坐标
     * @param degree 角度
     */
    void drawPixmapDegree(Pixmap pixmap, float x, float y, float degree);

    void drawPixmapScale(Pixmap pixmap, float x, float y, float ScaX, float ScaY, int alpha);

    /**
     * 绘制文字
     *
     * @param text  文本字符串
     * @param x     X坐标
     * @param y     Y坐标
     * @param color 颜色
     * @param size  字号
     */
    void drawText(String text, int x, int y, int color, int size);

    void drawSquareRadians(float x, float y, int r, int color, float radians);

    /**
     * 获取人工帧缓冲区宽度
     *
     * @return 缓冲区宽度
     */
    int getWidth();

    /**
     * 获取人工帧缓冲区高度
     *
     * @return 缓冲区高度
     */
    int getHeight();
}
