package com.xyy.game.framework.impl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;

import java.io.IOException;
import java.io.InputStream;

/**
 * AndroidGraphics类实现了Graphics接口
 * @see com.xyy.game.framework.Graphics
 * Created by ${XYY} on ${2016/3/5}.
 */
public final class AndroidGraphics implements Graphics {
    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    RectF dstRect = new RectF();
    Matrix matrix = new Matrix();
    Path path = new Path();
    Typeface font;
    int drawFilter;

    BitmapFactory.Options bigPixmapOptions = new BitmapFactory.Options();


    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
        //载入字体
        //this.font = Typeface.createFromAsset(assets, "msyh.ttc");
        this.font = Typeface.create("DroidSansFallback",Typeface.NORMAL);
        //设置文本字体与对齐方式
        paint.setTypeface(font);
        paint.setTextAlign(Paint.Align.LEFT);
        //抗锯齿
        //this.canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        drawFilter = 0;

        bigPixmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

    }

    @Override
    public void setAntiAlias(boolean enable){
        if(enable)
            drawFilter |= Paint.ANTI_ALIAS_FLAG;
        else
            drawFilter &= ~Paint.ANTI_ALIAS_FLAG;
        this.canvas.setDrawFilter(new PaintFlagsDrawFilter(0, drawFilter));
    }

    @Override
    public void setFilterBitmap(boolean enable){
        if(enable)
            drawFilter |= Paint.FILTER_BITMAP_FLAG;
        else
            drawFilter &= ~Paint.FILTER_BITMAP_FLAG;
        this.canvas.setDrawFilter(new PaintFlagsDrawFilter(0, drawFilter));
    }

    @Override
    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Bitmap.Config config;
        //将PixmapFormat转换为Bitmap.Config常量
        if (format == PixmapFormat.RGB565)
            config = Bitmap.Config.RGB_565;
        else if (format == PixmapFormat.ARGB4444)
            config = Bitmap.Config.ARGB_4444;
        else
            config = Bitmap.Config.ARGB_8888;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;
        try {
            //加载位图
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in,null,options);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        //重新检测图片格式
        //bitmap不会为NULL，在加载时就会抛出异常
        if (bitmap.getConfig() == Bitmap.Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
                format = PixmapFormat.ARGB8888;

        return new AndroidPixmap(bitmap, format);
    }

    @Override
    public void fill(int color) {
        canvas.drawColor(color);
    }

    @Override
    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    @Override
    public void drawLine(float x, float y, float x2, float y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    @Override
    public void drawLine(float x, float y, float x2, float y2, int color, int width) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
        canvas.drawLine(x, y, x2, y2, paint);
        paint.setStrokeWidth(1);
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    @Override
    public void drawCircle(float x, float y, float r, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, r, paint);
    }

    @Override
    public void drawCircle(float x, float y, float r, Shader shader, float scale) {
        matrix.setTranslate(x,y);
        matrix.postScale(scale, scale,x,y);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, r, paint);
        paint.setShader(null);
    }

    @Override
    public void drawRing(float x, float y, float r, float startAngle, float sweepAngle, int color, int width){
        float offset = (r + 1 + width/2);
        dstRect.left = x - offset;
        dstRect.top = y - offset;
        dstRect.right = x + offset;
        dstRect.bottom = y + offset;

        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);

        canvas.drawArc(dstRect, startAngle, sweepAngle, false, paint);

        paint.setStrokeWidth(1);
    }

    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
            srcRect.left = srcX;
            srcRect.top = srcY;
            srcRect.right = srcX + srcWidth;
            srcRect.bottom = srcY + srcHeight;

            dstRect.left = x;
            dstRect.top = y;
            dstRect.right = x + srcWidth;
            dstRect.bottom = y + srcHeight;

            canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect,
                    null);
    }

    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight, int alpha) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        paint.setAlpha(alpha);

        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect,
                paint);

        paint.setAlpha(0xFF);
    }

    @Override
    public void drawPixmap(Pixmap pixmap, float x, float y) {
        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
    }

    @Override
    public void drawPixmapAlpha(Pixmap pixmap, float x, float y, int alpha) {
        paint.setAlpha(alpha);

        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, paint);

        paint.setAlpha(0xFF);
    }

    @Override
    public void drawPixmapDegree(Pixmap pixmap, float x, float y, float degree) {
        Bitmap bmp = ((AndroidPixmap) pixmap).bitmap;
        matrix.setTranslate(x-bmp.getWidth()/2, y-bmp.getHeight()/2);
        matrix.postRotate(degree, x, y);
        canvas.drawBitmap(bmp, matrix, null);
    }

    @Override
    public void drawPixmapScale(Pixmap pixmap, float x, float y, float ScaX, float ScaY, int alpha) {
        Bitmap bmp = ((AndroidPixmap) pixmap).bitmap;
        matrix.setTranslate(x-bmp.getWidth()/2, y-bmp.getHeight()/2);
        matrix.postScale(ScaX, ScaY,x,y);
        paint.setAlpha(alpha);
        canvas.drawBitmap(bmp, matrix, paint);

        paint.setAlpha(0xFF);
    }

    @Override
    public void drawText(String text, int x, int y, int color, int size) {
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, x, y,paint);
    }

    @Override
    public void drawSquareRadians(float x, float y, int r, int color, float radians){
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        float dx = (float) (r*Math.cos(radians));
        float dy = (float) (r*Math.sin(radians));
        path.rewind();//快速重用
        path.moveTo(x+dx,y+dy);//起点  
        path.lineTo(x+dy,y-dx);
        path.lineTo(x-dx,y-dy);
        path.lineTo(x-dy,y+dx);
        path.close();//封闭
        canvas.drawPath(path,paint);
    }

    @Override
    public int getWidth() {
        return frameBuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return frameBuffer.getHeight();
    }
}
