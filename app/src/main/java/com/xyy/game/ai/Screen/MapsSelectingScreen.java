package com.xyy.game.ai.Screen;

import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.component.CircleButton;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.framework.Screen;

import java.util.List;

/**
 * 加载所需资源
 * Created by ${LQ} on ${2016/10/1}.
 */
public class MapsSelectingScreen extends Screen {

    private float scale = 0;//点击放大的参数
    private short ifDir = 0;//右边为1，左边为-1
    private float offset = 0;
    private Pixmap pixArray[] = Assets.mapThumbs;//声明位图数组，方便访问
    private int indexPix = pixArray.length-2;//记录屏幕上三个图片中第一个图片的起始索引
    private final int size = pixArray.length;//位图数组的大小
    private int left;//记录左边位置数组的索引
    private int mid;//记录中间边位置数组的索引
    private int right;//记录右边边位置数组的索引
    private int out = 0;//记录待呈现位置数组的索引
    private float offScale;//下一次图片改变的scale值
    private float offAlpha;//下一次图片改变的alpha值
    private float offRect = 0;//方形区域移动
    //private int offRectAlpha = 0;//方形区域移动的alpha值
    //private CircleButton backBt;//返回按钮
    private  int offline = 0;//背景绘线偏移量

    private int pointer;//记录当前按下的指针
    private byte state;//记录按下时所按的区域
    private float targetScale;//点击放大/缩小的目标值
    private int clicked;//点击(click)的图片索引
    private float tAlpha;

    private boolean mIsNoWeaponEquipped;

    public MapsSelectingScreen(Game game) {
        super(game);
        //backBt = new CircleButton(60, 50, 50, 0x00000000, Assets.back);
        //backBt.initialize(0.2f);
        pointer = -1;
        state = 0;
        targetScale = 0;
        clicked = -1;
        tAlpha = 0;

        left = indexPix;
        mid = (left + 1) % size;
        right = (mid + 1) % size;
        out = (right + 1) % size;
        ifDir = -1;
        indexPix = (indexPix + 1) % size;

        mIsNoWeaponEquipped = false;
    }

    @Override
    public void update(float deltaTime) {
        offline = offline + 1;
        if(offline==80)
            offline = 0;
        //backBt.update(deltaTime);
        offRect += (100 - offRect) * deltaTime * 15;
        //offRectAlpha = (int)(offRect*2.55);
        if (100 - offRect < 0.001) {
            offRect = 100;
        }
        scale += (targetScale - scale) * deltaTime * 10;
        if (clicked != -1) {
            tAlpha += 0xFF*deltaTime*5;
            if (tAlpha >= 0xFF) {
                tAlpha = 0xFF;
                //提示系统,这时是垃圾回收的好时机
                //System.gc();
                switch (clicked) {//TODO: 如有更多可用的地图,请在此添加case语句
                    case 0:
                        game.setScreen(new GameLoadingScreen(game, "map00"));
                        break;
                    case 1:
                        game.setScreen(new Screen_MainMenu_Store(game));
                        break;
                }
            }
        }
        if (ifDir == 1) {
            offset += (400 - offset) * deltaTime * 8 + 1;
            offScale = 0.000625f * offset;
            offAlpha = 0.3175f * offset;
            if (400 - offset < 0.001) {
                ifDir = 0;
                offset = 0;
                offAlpha = 0;
                offScale = 0;

                left = indexPix;
                mid = (left + 1) % size;
                right = (mid + 1) % size;
            }
        } else if (ifDir == -1) {
            offset += (-offset - 400) * deltaTime * 8 - 1;
            offScale = 0.000625f * offset;
            offAlpha = 0.3175f * offset;
            if (offset + 400 < 0.001) {
                ifDir = 0;
                offset = 0;
                offAlpha = 0;
                offScale = 0;

                left = indexPix;
                mid = (left + 1) % size;
                right = (mid + 1) % size;
            }
        }
        List<Input.Touch> inputs = game.getInput().getTouchEvents();
        final int length = inputs.size();
        for (int i = 0; i < length; i++) {
            Input.Touch event = inputs.get(i);
            /*if (backBt.isClicked(event)) {
                game.setScreen(new MainMenuScreen(game));
                break;
            }*/
            final int type = event.type;
            final int eventX = event.x;
            final int eventY = event.y;
            if (type == Input.Touch.TOUCH_DOWN) {
                if(pointer == -1) {
                    if (mIsNoWeaponEquipped) {
                        if (inBounds(event,545, 63 + 472, 189, 74)) {
                            pointer = event.pointer;
                            state = 3;
                        }
                    }
                    else if (this.clicked == -1 && eventY > 180 && eventY < 540) {
                        pointer = event.pointer;
                        if (eventX > 320 && eventX < 960) {
                            targetScale = -0.1f;
                            state = 1;
                        } else if (eventX >= 960) {//右边
                            state = 2;
                        } else /*if (eventX <= 320)*/ {//左边
                            state = 0;
                        }
                    }
                    //SETTING
                    else if(inBounds(event,0,720-84,81,84)){
                        game.setScreen(new SettingScreen(game));
                    }
                    //STORE
                    else if (inBounds(event, 81, 720 - 84, 218, 84)) {
                        game.setScreen(new Screen_MainMenu_Store(game));
                    }
                    //REPOSITORY
                    else if (inBounds(event, 81 + 218, 720 - 84, 218, 84)) {
                        game.setScreen(new Screen_MainMenu_Repository(game));
                    }
                }
            } else if (type == Input.Touch.TOUCH_UP) {
                if (pointer == event.pointer) {
                    pointer = -1;
                    targetScale = 0;
                    if (mIsNoWeaponEquipped) {
                        if (inBounds(event,545, 63 + 472, 189, 74)) {
                            mIsNoWeaponEquipped = false;
                        }
                    }
                    else if (eventY > 180 && eventY < 540) {
                        if (eventX > 320 && eventX < 960 && state == 1) {//中间
                            if(UserDate.mCurrentlyEquippedWeapons == null || UserDate.mCurrentlyEquippedWeapons.size()==0){
                                mIsNoWeaponEquipped = true;
                            }
                            else {
                                final int click = (size + mid - ifDir) % size;
                                if (click <= 1) {//TODO: 如有更多可用的地图,请修改此值
                                    targetScale = 1;
                                    this.clicked = click;
                                }
                            }
                        } else if (eventX >= 960 && state == 2) {//右边
                            offset = 0;
                            offAlpha = 0;
                            offScale = 0;
                            left = indexPix;
                            mid = (left + 1) % size;
                            right = (mid + 1) % size;
                            out = (right + 1) % size;
                            ifDir = -1;
                            indexPix = (indexPix + 1) % size;
                        } else if (eventX < 320 && state == 0) {//左边
                            offset = 0;
                            offAlpha = 0;
                            offScale = 0;
                            left = indexPix;
                            mid = (left + 1) % size;
                            right = (mid + 1) % size;
                            out = (right + 1) % size;
                            ifDir = 1;
                            indexPix = (size + indexPix - 1) % size;
                        }
                    }
                    state = -1;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        /*g.fill(0xff202d42);
        for(int i = 0;i<16;i++){
            g.drawLine(i*80+offline,0,i*80+offline,720,0xff2d415f);
        }
        for(int i = 9;i>0;i--){
            g.drawLine(0,i*80-offline,1280,i*80-offline,0xff2d415f);
        }
        g.drawRect(0, (int) (-100 + offRect), 1280, 100, (offRectAlpha<<24)|0x002d415f);
        backBt.present(g);*/
        g.drawPixmap(Assets.background, 0, 0);

        //顶部条
        g.drawPixmap(Assets.main_menu_top_bar, 0, 0);
        //返回按钮
        g.drawPixmapAlpha(Assets.back, 22, 20, 0x7F);
        //"MainMenu"文字
        g.drawText("MAIN MENU", 77, 28, 0xFF999999, 24);
        //"Store"文字
        g.drawText("[CAMPAIGN]", 77, 53, 0xFFFFFFFF, 24);
        //货币
        g.drawText(String.valueOf(UserDate.sCurrency),1280-200,40,0xFFFFFFFF,30);

        if (ifDir == 1) {//右
            g.drawPixmapScale(pixArray[right], 1040 + offset * 1.2f, 360, 0.6f, 0.6f, 0x80);
            g.drawPixmapScale(pixArray[out], -160 + offset, 360, 0.75f, 0.75f, 0x80);
            g.drawPixmapScale(pixArray[mid], 640 + offset, 360, 1f - offScale, 1f - offScale, (int) (0xFF - offAlpha));
            g.drawPixmapScale(pixArray[left], 240 + offset, 360, 0.75f + offScale + scale, 0.75f + offScale + scale, (int) (0x80 + offAlpha));
        } else if (ifDir == -1) {//左边
            g.drawPixmapScale(pixArray[left], 240 + offset * 1.2f, 360, 0.6f, 0.6f, 0x80);
            g.drawPixmapScale(pixArray[out], 1440 + offset, 360, 0.75f, 0.75f, 0x80);
            g.drawPixmapScale(pixArray[mid], 640 + offset, 360, 1f + offScale, 1f + offScale, (int) (0xFF + offAlpha));
            g.drawPixmapScale(pixArray[right], 1040 + offset, 360, 0.75f - offScale + scale, 0.75f - offScale + scale, (int) (0x80 - offAlpha));
        } else {
            g.drawPixmapScale(pixArray[left], 240, 360, 0.75f, 0.75f, 0x80);
            g.drawPixmapScale(pixArray[right], 1040, 360, 0.75f, 0.75f, 0x80);
            g.drawPixmapScale(pixArray[mid], 640, 360, 1f + scale, 1f + scale, 0xFF);
        }
        g.drawPixmap(Assets.leftGo, 0, 320);
        g.drawPixmap(Assets.rightGo, 1225, 320);

        final int offsetX = 81;
        String[] bottom_bar_text = new String[]{"STORE","REPOSITORY","",""};
        g.drawPixmap(Assets.main_menu_bottom_bar_button,-218+offsetX,720-84,218,0,218,84);
        g.drawPixmap(Assets.setting,18,720-84+20);
        for(int i=0;i<4;i++){
            g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX+i*218,720-84,218,0,218,84);
            g.drawText(bottom_bar_text[i],offsetX+i*218+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
        }
        g.drawPixmap(Assets.main_menu_bottom_bar_button_red,offsetX+4*218,720-84,327,0,327,84);
        g.drawText("CAMPAIGN",offsetX+4*218+164,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);

        g.fill((int)tAlpha<<24);

        if (mIsNoWeaponEquipped) {
            g.fill(0x6F000000);
            g.drawPixmap(Assets.dialog, 231, 63);
            g.drawText("ATTENTION", 640, 63 + 80, 0xFFFF5400, 42, Paint.Align.CENTER);
            g.drawText("PLEASE EQUIP AT LEAST ONE WEAPON.", 640, 360, 0xFFFFFFFF, 30, Paint.Align.CENTER);
            if (state == 3)
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 189, 0, 189, 74);
            else
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 0, 0, 189, 74);
            g.drawText("DONE", 640, 63 + 472 + 24 + 20, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        }
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        //提示系统,这时是垃圾回收的好时机
        System.gc();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        game.setScreen(new MainMenuScreen(game));
        return true;
    }

    private boolean inBounds(Input.Touch event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

}
