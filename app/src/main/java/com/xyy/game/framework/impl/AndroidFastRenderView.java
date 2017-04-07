package com.xyy.game.framework.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xyy.game.framework.Screen;

/**
 * Created by ${XYY} on ${2016/3/5}.
 */
public final class AndroidFastRenderView extends SurfaceView implements Runnable{
    private AndroidGame game;
    private Bitmap framebuffer;
    private Thread renderThread = null;
    private SurfaceHolder holder;
    public volatile boolean running = false;
    private Paint paint;

    public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer) {
        super(game);
        this.game = game;
        this.framebuffer = framebuffer;
        this.holder = getHolder();
        paint = new Paint();
    }

    public void setFilterBitmap(boolean enable){
        paint.setFilterBitmap(enable);
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void run() {
        //Paint paint = new Paint();
        //paint.setFilterBitmap(FilterBitmapEnabled);
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while(running) {
            if(!holder.getSurface().isValid())
                continue;

            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            Screen currentScreen = game.getCurrentScreen();
            currentScreen.update(deltaTime);
            currentScreen.present(deltaTime);

            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }
}
