package com.xyy.game.ai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by ${XYY} on ${2016/10/31}.
 */
@Deprecated
public class BackGroundLoader implements Runnable {

    private static final int BLOCK_WIDTH = 1280/3 + 1;
    private static final int BLOCK_HEIGHT = 720/3;

    private volatile Bitmap[][] bitmapGroup;

    private final BitmapRegionDecoder bitmapRegionDecoder;

    private Rect rect = new Rect();

    private boolean running;

    BitmapFactory.Options bigPixmapOptions = new BitmapFactory.Options();

    private int currentIndexX;
    private int currentIndexY;
    private boolean newBitmapReq;


    public BackGroundLoader(BitmapRegionDecoder bitmapRegionDecoder, int indexX, int indexY) {
        this.bitmapRegionDecoder = bitmapRegionDecoder;
        bitmapGroup = new Bitmap[5][5];
        bigPixmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        //初始化
        for (int y = 0; y < 5; y++){
            for (int x = 0; x < 5; x++) {
                int left = indexX*BLOCK_WIDTH+x*BLOCK_WIDTH;
                int top = indexY*BLOCK_HEIGHT+y*BLOCK_HEIGHT;
                int right = left+BLOCK_WIDTH;
                int bottom = top+BLOCK_HEIGHT;
                rect.set(left,top,right,bottom);
                bitmapGroup[y][x] = bitmapRegionDecoder.decodeRegion(rect, bigPixmapOptions);
            }
        }
        currentIndexX = indexX;
        currentIndexY = indexY;
        newBitmapReq = false;
        running = true;
    }

    public void setIndex(int indexX, int indexY){
        if(indexX==currentIndexX && indexY==currentIndexY){
            return;
        }
        synchronized (this) {
            //玩家右移，地图左移
            if (indexX > currentIndexX) {
                int offset = indexX - currentIndexX;
                if (offset > 4) offset = 4;
                for (int y = 0; y < 5; y++) {
                    System.arraycopy(bitmapGroup[y], offset, bitmapGroup[y], 0, 5 - offset);
                    for (int x = 5 - offset; x < 5; x++) {
                        //if (bitmapGroup[y][x] != null) bitmapGroup[y][x].recycle();
                        bitmapGroup[y][x] = null;
                    }
                }
            }
            //玩家左移，地图右移
            else if (indexX < currentIndexX) {
                Log.i("BGLoader","玩家左移，地图右移");
                int offset = currentIndexX - indexX;
                Log.i("BGLoader","offset = "+offset);
                if (offset > 4) offset = 4;
                for (int y = 0; y < 5; y++) {
                    System.arraycopy(bitmapGroup[y], 0, bitmapGroup[y], offset, 5 - offset);
                }
                for (int y = 0; y < 5; y++) {
                    for (int x = 0; x < offset; x++) {
                        //if (bitmapGroup[y][x] != null) bitmapGroup[y][x].recycle();
                        bitmapGroup[y][x] = null;
                        Log.i("BGLoader", "remove y/x = " + y + " / " + x);
                    }
                }
            }
            //玩家下移，地图上移
            if (indexY > currentIndexY) {
                int offset = indexY - currentIndexY;
                if (offset > 4) offset = 4;
                Bitmap[] temp = bitmapGroup[0];
                System.arraycopy(bitmapGroup, offset, bitmapGroup, 0, 5 - offset);
                bitmapGroup[4] = temp;
                for (int x = 0; x < 5; x++) {
                    //if (bitmapGroup[4][x] != null) bitmapGroup[4][x].recycle();
                    bitmapGroup[4][x] = null;
                }
            }
            //玩家上移，地图下移
            else if (indexY < currentIndexY) {
                int offset = currentIndexY - indexY;
                if (offset > 4) offset = 4;
                Bitmap[] temp = bitmapGroup[4];
                System.arraycopy(bitmapGroup, 0, bitmapGroup, offset, 5 - offset);
                bitmapGroup[0] = temp;
                for (int x = 0; x < 5; x++) {
                    //if (bitmapGroup[0][x] != null) bitmapGroup[0][x].recycle();
                    bitmapGroup[0][x] = null;
                }
            }
            currentIndexX = indexX;
            currentIndexY = indexY;
            newBitmapReq = true;
        }
    }

    public Bitmap[][] getBitmapGroup() {
        return bitmapGroup;
    }

    @Override
    public void run() {
        while (running){
            if(newBitmapReq){
                for(int y=0;y<5;y++) {
                    for (int x = 0; x < 5; x++) {
                        if (bitmapGroup[y][x] == null /*|| bitmapGroup[y][x].isRecycled()*/) {
                            int left = (currentIndexX + x) * BLOCK_WIDTH;
                            int top = (currentIndexY + y) * BLOCK_HEIGHT;
                            int right = left + BLOCK_WIDTH;
                            int bottom = top + BLOCK_HEIGHT;
                            rect.set(left, top, right, bottom);
                            bitmapGroup[y][x] = bitmapRegionDecoder.decodeRegion(rect, bigPixmapOptions);
                        }
                    }
                }
                newBitmapReq = false;
            }
        }
    }

    public void release(){
        running = false;
        for(int y=0;y<5;y++) {
            for (int x = 0; x < 5; x++) {
                if (bitmapGroup[y][x] != null) bitmapGroup[y][x].recycle();
            }
        }
    }
}
