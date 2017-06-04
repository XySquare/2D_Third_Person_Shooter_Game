package com.xyy.game.framework.impl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.xyy.game.ai.GameDataManager;
import com.xyy.game.framework.Audio;
import com.xyy.game.framework.BaseApplication;
import com.xyy.game.framework.FileIO;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;
import com.xyy.game.util.MyLog;

/**
 * AndroidGame抽象类
 * 当前标准分辨率为1280*720
 * 需实现getStartScreen()方法以开始游戏
 * Created by ${XYY} on ${2016/3/5}.
 */
public abstract class AndroidGame extends Activity implements Game {
    private AndroidFastRenderView renderView;
    private Graphics graphics;
    private Audio audio;
    private Input input;
    private FileIO fileIO;
    private Screen screen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化未捕获异常处理
        BaseApplication application = (BaseApplication) getApplication();
        application.init(this);

        //获取系统可用内存
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        Log.i("Android Game", "AvailMen = "+memoryInfo.availMem +"byte");

        //根据设备方向，定义帧缓冲区大小，实例化Bitmap
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 1280 : 720;
        int frameBufferHeight = isLandscape ? 720 : 1280;

        //获取屏幕宽/高
        Point outSize = new Point();
        //Android 4.2, API 17
        //获取真实屏幕大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealSize(outSize);
        } else {
            getWindowManager().getDefaultDisplay().getSize(outSize);
        }

        //计算缩放比例
        float scaleX = (float) frameBufferWidth
                / outSize.x;
        float scaleY = (float) frameBufferHeight
                / outSize.y;

        //文件接口
        fileIO = new AndroidFileIO(getAssets(), getApplicationContext());
        //初始化日志记录
        MyLog.initialize(fileIO);
        //载入游戏设置
        GameDataManager.loadSettings(fileIO);
        //如果可用内存低，将强制开启低品质模式
        if(!GameDataManager.LowQuality && memoryInfo.availMem<150*1024*1024){
            GameDataManager.LowQuality = true;
            new Thread(){
                @Override
                public void run() {
                    GameDataManager.saveSettings(fileIO);
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "内存不足，已自动切换为低品质模式。",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
        }
        //图形接口
        Bitmap frameBuffer;
        if (GameDataManager.LowQuality) {
            frameBuffer = Bitmap.createBitmap(frameBufferWidth >> 1,
                    frameBufferHeight >> 1, Bitmap.Config.RGB_565);
            graphics = new AndroidGraphicsLowQuality(getAssets(), frameBuffer);
        }else {
            frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                    frameBufferHeight, Bitmap.Config.ARGB_8888);
            graphics = new AndroidGraphics(getAssets(), frameBuffer);
        }
        //抗锯齿与位图过滤
        graphics.setAntiAlias(GameDataManager.AntiAlias);
        graphics.setFilterBitmap(GameDataManager.FilterBitmap);
        //SurfaceView
        renderView = new AndroidFastRenderView(this, frameBuffer);
        //二线性过滤
        renderView.setFilterBitmap(GameDataManager.AdvancedFilterBitmap);
        //音频接口
        audio = new AndroidAudio(this);
        //输入接口
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        setContentView(renderView);
        Log.i("Activity", "Created");

    }

    /*
    * 返回键事件处理
    * */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        // 如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (screen.onBack()) return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        /**设置重力感应横屏 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        /** Window flag: as long as this window is visible to the user, keep
         *  the device's screen turned on and bright. */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /**使活动全屏，该Flag可能被OS清除，因此需再次设置 */
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN;
        /**Android 4.4, API 19
         * 沉浸式全屏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(visibility);
        screen.resume();
        renderView.resume();
        Log.i("Activity", "Resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        /** 设置屏幕按感应设置横/竖屏（取消横盘锁定）*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        /**取消保持屏幕常亮 */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        renderView.pause();
        screen.pause();
        Log.i("Activity", "Pause");
        //如果活动将被销毁，则释放screen
        if (isFinishing()) {
            screen.dispose();
            Log.i("Activity", "Finished");
        }
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    /**
     * 设置新Screen
     *
     * @param screen 新Screen实例
     */
    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");
        //暂停并释放当前Screen
        this.screen.pause();
        this.screen.dispose();
        //恢复与更新新的Screen
        screen.resume();
        //screen.update(0);
        this.screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public void setFilterBitmap(boolean enable) {
        renderView.setFilterBitmap(enable);
    }

    @Override
    public void newActivity(Intent intent){
        if(intent==null) return;
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException ignored){
            Log.e("AndroidGame","Activity NOT Found!");
        }
    }

    @Override
    public void exit() {
        MyLog.close();
        screen.pause();
        screen.dispose();
        finish();
        System.exit(0);
    }

}
