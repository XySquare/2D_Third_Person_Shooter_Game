package com.xyy.game.ai.Screen;

import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Weapon.IMIDesertEagle;
import com.xyy.game.ai.Weapon.M16A4;
import com.xyy.game.ai.Weapon.RPG;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.framework.Screen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by ${XYY} on ${2017/4/16}.
 */

public class Screen_MainMenu_Store_Promos_Rolling extends Screen {
    private final Weapon[] mItems;

    private Weapon mPurchasedWeapon = null;
    //private boolean mDialogButtonPressed = false;

    private boolean mDone = false;

    //列表项的宽度度
    private final int ITEM_WIDTH = 437;
    private final int ITEM_HEIGHT = 242;
    //屏幕中最大显示的行/列数
    private final int ROW_SCREEN = 1;
    private final int COL_SCREEN = 1280 / ITEM_WIDTH + 2;
    //列表纵坐标
    private final int LIST_Y = (720-ITEM_HEIGHT)/2;
    //列表总项数
    private int mItemNum;
    //列表总列数
    private int mColumnNum;
    //列表总行数
    private int mRowNum;
    //当前被按下的指针ID，-1为未被按下
    private int pointer;
    //相对于屏幕左上方的索引偏移量
    private int relativeIndex;
    //列表左侧的横坐标
    private float listX;
    //按下时，指针到列表左侧的横坐标的偏移量
    private int offsetX;
    //屏幕左侧的索引编号
    private int startIndex;
    //屏幕左侧的列表左端的横坐标
    private float listStartX;
    //按下时指针的横坐标
    private int perEventX;
    //惯性速度
    //private float speed;

    //private final float speed0;

    private int dram;

    private Pixmap background, effect_0, effect_1, mask_0, mask_1, rolling_back,rolling_cover;

    private float mDegree = 0;

    public Screen_MainMenu_Store_Promos_Rolling(Game game, Screen_MainMenu_Store_Promos.CaseItem[] caseItems, Class<? extends Weapon> awardItem) {
        super(game);

        Graphics g = game.getGraphics();

        background = g.newPixmap("background_promos.png", Graphics.PixmapFormat.ARGB8888);
        effect_0 = g.newPixmap("promos_effect_0.png", Graphics.PixmapFormat.ARGB8888);
        effect_1 = g.newPixmap("promos_effect_1.png", Graphics.PixmapFormat.ARGB8888);
        mask_0 = g.newPixmap("promos_mask_0.png", Graphics.PixmapFormat.ARGB8888);
        mask_1 = g.newPixmap("promos_mask_1.png", Graphics.PixmapFormat.ARGB8888);
        rolling_back = g.newPixmap("promos_rolling_back.png", Graphics.PixmapFormat.ARGB8888);
        rolling_cover = g.newPixmap("promos_rolling_cover.png", Graphics.PixmapFormat.ARGB8888);

        mItemNum = 30;//TODO:?
        mRowNum = 1;
        if (mItemNum % mRowNum == 0)
            mColumnNum = mItemNum / mRowNum;
        else
            mColumnNum = mItemNum / mRowNum + 1;
        pointer = -1;
        relativeIndex = COL_SCREEN * ROW_SCREEN;
        listX = listStartX = 0;
        startIndex = 0;
        //speed = 0;
        //speed = -4500f;

        //speed0 = (float) (((mItemNum - 2)*ITEM_WIDTH - 1280/2 + 280)*(-Math.log(0.5)));
        //speed0 = (float) (((mItemNum - 1)*ITEM_WIDTH - 1280/2 + 280)*(-Math.log(0.5)));
        int dmin = ( - (mItemNum - 3)*ITEM_WIDTH + 1280/2 - 8);
        int dmax = ( - (mItemNum - 2)*ITEM_WIDTH + 1280/2 + 8);
        dram = (int) (Math.random()*(dmax - dmin) + dmin);

        /**
         * 奖品前的武器
         */
        mItems = new Weapon[mItemNum];
        for(int i=0;i<mItems.length;i++){
            int ramIndex = (int)(Math.random()*caseItems.length);
            Weapon object = null;
            Class<? extends Weapon> aClass = caseItems[ramIndex].mName;
            try {
                Constructor<? extends Weapon> constructor = aClass.getConstructor();
                object = constructor.newInstance();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            mItems[i] = object;
        }

        try {
            mPurchasedWeapon = mItems[mItemNum-3] = awardItem.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Weapon item : mItems) {
            item.loadPixmap(game.getGraphics(), Weapon.PixmapQuality.NORMAL);
        }
    }

    private float timer = 0;

    @Override
    public void update(float deltaTime) {
        List<Input.Touch> touchEvents = game.getInput().getTouchEvents();
        /**
         * 处理惯性运动
         */
        //if(len==0 && dragAble) {
        //if (speed != 0) {
        //speed = (float) (-speed0 * (1/Math.pow(2,timer)));
            //列表移动
            //listX += speed * deltaTime;

        mDegree += 90 * deltaTime;

        float dx = dram - listX;
        if(Math.abs(dx) > 4) {
            float v = (dx) * 0.5f * deltaTime;
            listX += v;
            //更新列表起始索引编号与起始坐标
            int startIndexX = (int) (-listX / ITEM_WIDTH);
            startIndex = startIndexX * ROW_SCREEN;
            listStartX = listX + startIndexX * ITEM_WIDTH;
        }
        else{
            mDone = true;
            timer += deltaTime;
        }
        //Log.i("Rolling","dram = "+dram+" listX = "+listX+" v = "+v);
            //减速
            //speed *= 1 - 2 * deltaTime;
        //speed *= 1 - 0.5 * deltaTime;

            //边界处理
            /*if (listX > 0) {
                listX = 0;
                speed = 0;
                dragging = false;
            } else if (listX < 1280 - mColumnNum * ITEM_WIDTH) {
                listX = 1280 - mColumnNum * ITEM_WIDTH;
                speed = 0;
                dragging = false;
            }*/

        //}
        /**
         * 处理输入
         */
        final int LIST_Y_MAX = LIST_Y + ITEM_HEIGHT * ROW_SCREEN;
        //暂存移动前列表横坐标
        //final float preListX = listX;
        //事件线性表长度缓存
        final int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if (event.type == Input.Touch.TOUCH_UP) {
                if(timer>1) {
                    Screen_MainMenu_Store_Promos screen = new Screen_MainMenu_Store_Promos(game);
                    screen.setResult(mPurchasedWeapon);
                    game.setScreen(screen);
                }
            }
            /*if (event.type == Input.Touch.TOUCH_DOWN) {
                //只允许单一输入
                if (pointer == -1) {
                    if (mPurchasedWeapon != null) {
                        if (inBounds(event,545, 63 + 472, 189, 74)) {
                            pointer = event.pointer;
                            mDialogButtonPressed = true;
                        }
                    } else if (event.y > LIST_Y && event.y < LIST_Y_MAX) {
                        pointer = event.pointer;
                        //获取按下时相对于列表左侧的横坐标偏移量
                        offsetX = listX - event.x;
                        //保存"按下时的横坐标"
                        perEventX = event.x;
                        //按下时，当列表并未移动，则重置"是否拖拽中"为否
                        //当列表正在移动，则使列表停止
                        if (speed == 0)
                            dragging = false;
                        else
                            speed = 0;
                        //获取按下时相对于"列表起始索引编号"的"当前正在被按下的索引编号"，
                        //并进行边界处理
                        int relativeIndexX = (event.x - listStartX) / ITEM_WIDTH;
                        int relativeIndexY = (event.y - LIST_Y) / ITEM_HEIGHT;
                        relativeIndex = relativeIndexX * ROW_SCREEN + relativeIndexY;
                        if (dragging || event.x < listStartX || relativeIndex > COL_SCREEN * ROW_SCREEN)
                            //relativeIndex = COL_SCREEN * ROW_SCREEN;
                    }
                }
            } *//*else if (event.type == Input.Touch.TOUCH_DRAGGED) {
                // 只有当触摸区域在列表范围，且列表可拖拽（长度小于屏幕高度），
                // 并且，到"按下时的纵坐标"距离大于5时，或已经在拖拽中，才执行拖拽
                if (dragAble
                        && (dragging || Math.abs(perEventX - event.x) > 5)
                        && event.y > LIST_Y && event.y < LIST_Y_MAX
                        && pointer == event.pointer) {
                    //设置"是否拖拽中"标志为是
                    dragging = true;
                    //更新列表横坐标
                    listX = event.x + offsetX;
                    //重置"当前正在被按下的索引编号"
                    relativeIndex = COL_SCREEN * ROW_SCREEN;
                    //边界处理
                    if (listX > 0)
                        listX = 0;
                    else if (listX < 1280 - mColumnNum * ITEM_WIDTH)
                        listX = 1280 - mColumnNum * ITEM_WIDTH;
                    //更新列表起始索引编号与起始坐标
                    int startIndexX = (-listX / ITEM_WIDTH);
                    startIndex = startIndexX * ROW_SCREEN;
                    listStartX = listX + startIndexX * ITEM_WIDTH;
                }
            }*/ /*else if (event.type == Input.Touch.TOUCH_UP) {
                //抬起的指针必须为初始按下的指针
                if (pointer == event.pointer) {
                    pointer = -1;
                    if (mPurchasedWeapon != null) {
                        mDialogButtonPressed = false;
                        if (inBounds(event, 545, 63 + 472, 189, 74)) {
                            mPurchasedWeapon = null;
                        }
                    }
                    //当触摸区域在列表范围
                    else if (event.y > LIST_Y && event.y < LIST_Y_MAX) {
                            /* event.y>=listY: 处理无法拖拽时的情况，
                            * 在ITEMHEIGHT<event.y<listY时，
                            * index也会=0
                            *
                        //非拖拽中
                        if (!dragging && event.x >= listX) {
                            //获取"索引编号"
                            int indexX = (event.x - listX) / ITEM_WIDTH;
                            int indexY = (event.y - LIST_Y) / ITEM_HEIGHT;
                            int index = indexX * ROW_SCREEN + indexY;
                            //按下与抬起的索引编号应一致，且不应为边界
                            if (index == this.relativeIndex + startIndex && index < mItemNum) {
                                //设置新楼层数
                                //stage.setFloor(index);
                                //开始传送
                                //gameScreen.setState(TRANSITION);
                                //计算成就
                                //stage.achieve(Achievements.TELEPORT);
                                //game.setScreen(new Screen_MainMenu_Store_Weapon_Detail(game, mItems[index % mItems.length]));
                            }
                        }
                        //如果列表移动速度大于5px/s，则赋予惯性
                        if ((preListX - listX) / deltaTime > 5
                                || (preListX - listX) / deltaTime < -5)
                            speed = (int) ((listX - preListX) / deltaTime);
                    }
                    //重置"当前正在被按下的索引编号"
                    //this.relativeIndex = (COL_SCREEN * ROW_SCREEN < mColumnNum*mRowNum) ? COL_SCREEN* ROW_SCREEN: mColumnNum*mRowNum;
                    relativeIndex = COL_SCREEN * ROW_SCREEN;
                    break;
                }
            }*/
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        //g.drawPixmap(Assets.background, 0, 0);
        g.fill(0xFF000000);

        /*//顶部条
        g.drawPixmap(Assets.main_menu_top_bar, 0, 0);
        //返回按钮
        g.drawPixmap(Assets.back, 22, 20);
        //"MainMenu"文字
        g.drawText("MAIN MENU", 77, 28, 0xFF999999, 24);
        //"Store"文字
        g.drawText("[STORE]", 77, 53, 0xFFFFFFFF, 24);
        //货币
        g.drawText(String.valueOf(UserDate.sCurrency),1280-200,40,0xFFFFFFFF,30);*/

        /*g.drawPixmap(Assets.button_tab, 378, 68, 180, 0, 180, 68);
        g.drawText("WEAPON", 378 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 - 8, 68, 0, 0, 180, 68);
        g.drawText("SUPPLIES", 378 + 180 - 8 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 + 180 - 16, 68, 0, 0, 180, 68);
        g.drawText("PROMOS", 378 + 180 + 180 - 16 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);*/

        if(timer<0.5) {
            //g.drawRect(0,(720-ITEM_HEIGHT-30)/2,1280,ITEM_HEIGHT+30,0xFF121619);
            g.drawPixmap(rolling_back,0,(720-312)/2);
            for (int i = 0; i < COL_SCREEN * ROW_SCREEN && startIndex + i < mItemNum; i++) {
                int indexX = i / ROW_SCREEN;
                int indexY = i - indexX * ROW_SCREEN;
                float x = listStartX + indexX * ITEM_WIDTH;
                int y = LIST_Y + indexY * ITEM_HEIGHT;
                item_list_present(g, x, y, startIndex + i, i == relativeIndex);
            }
            g.drawPixmap(rolling_cover,0,(720-312)/2);
        }

        if (mDone) {
            if(timer<0.5){
                g.drawPixmap(mask_0, -1280+timer*2*1280, 0);
                g.drawPixmap(mask_1, 1280-timer*2*1280, 0);
            }
            else {
                g.drawPixmap(background, 0, 0);
                g.drawRect(0, 0, 1280, 124, 0xFF787878);
                g.drawRect(0, 720 - 124, 1280, 124, 0xFF787878);
                //g.drawText("PURCHASE SUCCEED", 640, 63 + 80, 0xFFFF5400, 42, Paint.Align.CENTER);
                g.drawPixmapDegree(effect_0, 1280 / 2, 720 / 2, -mDegree / 2);
                g.drawPixmapDegree(effect_1, 1280 / 2, 720 / 2, mDegree);
                g.drawText(mPurchasedWeapon.getName(), 640, 580, 0xFFFFFFFF, 60, Paint.Align.CENTER);
                g.drawPixmapScale(mPurchasedWeapon.getPixmap(), 1280 / 2, 720 / 2, 1.3f, 1.3f, 0xFF);
                if(timer<1){
                    g.fill((int)(0xFF*(1-timer)*2)<<24 | 0xFFFFFF);
                }
            }
            /*if (mDialogButtonPressed)
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 189, 0, 189, 74);
            else
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 0, 0, 189, 74);*/
            //g.drawText("DONE", 640, 63 + 472 + 24 + 20, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        }

        /*final int offsetX = 190;
        String[] bottom_bar_text = new String[]{"STORE","REPOSITORY","","",""};
        for(int i=0;i<5;i++){
            g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX+i*218,720-84,218,0,218,84);
            g.drawText(bottom_bar_text[i],offsetX+i*218+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
        }
        g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX,720-84,0,0,218,84);
        g.drawText(bottom_bar_text[0],offsetX+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
        g.drawPixmap(Assets.main_menu_bottom_bar_button,-218+offsetX,720-84,218,0,218,84);*/
        /**
         * 首先，先绘制未被按下的按钮，直至被按下的按钮之前
         * 接着，绘制被按下的按钮，
         * 最后，绘制剩下的未被按下的按钮
         */
        //if(dragAble) {
        /*for (i = 0; i < relativeIndex; i++) {
            int indexX = i / ROW_SCREEN;
            int indexY = i - indexX * ROW_SCREEN;
            int x = listStartX + indexX * ITEM_WIDTH;
            int y = LIST_Y + indexY * ITEM_HEIGHT;
            item_list_present(g, x, y);
            //g.drawRect(listStartX + indexX * ITEM_WIDTH, LIST_Y + indexY * ITEM_HEIGHT, ITEM_WIDTH, ITEM_HEIGHT, 0x6F000000);
            //g.drawText("AAAA" + (startIndex + i), listStartX + indexX * ITEM_WIDTH, LIST_Y + indexY * ITEM_HEIGHT, 0xFF000000, 32);
        }
        if (i < COL_SCREEN * ROW_SCREEN) {
            int indexX = i / ROW_SCREEN;
            int indexY = i - indexX * ROW_SCREEN;
            int x = listStartX + indexX * ITEM_WIDTH;
            int y = LIST_Y + indexY * ITEM_HEIGHT;
            item_list_present(g, x, y);
            i++;
            for (; i < COL_SCREEN * ROW_SCREEN; i++) {
                indexX = i / ROW_SCREEN;
                indexY = i - indexX * ROW_SCREEN;
                x = listStartX + indexX * ITEM_WIDTH;
                y = LIST_Y + indexY * ITEM_HEIGHT;
                item_list_present(g, x, y);
            }
        }*/
        //}
        /*else {
            for (i = 0; i < relativeIndexX; i++) {
                g.drawPixmap(Assets.itemListBg2, LIST_Y, listStartX + i * ITEM_WIDTH,0,0,600,120);
                gameScreen.drawNum5_52(String.valueOf(i + 1), TXT_X, listStartX + i * ITEM_WIDTH + 28);
                g.drawPixmap(Assets.txt_F, TXT_X+100, listStartX + i * ITEM_WIDTH + 28);
            }
            if(i < mColumnNum) {
                g.drawPixmap(Assets.itemListBg2, LIST_Y, listStartX + i * ITEM_WIDTH,600,0,600,120);
                gameScreen.drawNum5_52(String.valueOf(i + 1), TXT_X, listStartX + i * ITEM_WIDTH + 28);
                g.drawPixmap(Assets.txt_F, TXT_X+100, listStartX + i * ITEM_WIDTH + 28);
                i++;
                for (; i < mColumnNum; i++) {
                    g.drawPixmap(Assets.itemListBg2, LIST_Y, listStartX + i * ITEM_WIDTH,0,0,600,120);
                    gameScreen.drawNum5_52(String.valueOf(i + 1), TXT_X, listStartX + i * ITEM_WIDTH + 28);
                    g.drawPixmap(Assets.txt_F, TXT_X+100, listStartX + i * ITEM_WIDTH + 28);
                }
            }
        }*/
    }

    private void item_list_present(Graphics g, float x, int y, int position, boolean pressed) {
        Weapon item = mItems[position % mItems.length];

        Weapon.Rarity rarity = item.getRarity();
        if (rarity == Weapon.Rarity.N)
            g.drawPixmap(Assets.list_item_weapon, x, y);
        else if (rarity == Weapon.Rarity.R)
            g.drawPixmap(Assets.list_item_weapon_bronze, x, y);
        else if (rarity == Weapon.Rarity.SR)
            g.drawPixmap(Assets.list_item_weapon_silver, x, y);
        else if (rarity == Weapon.Rarity.SSR)
            g.drawPixmap(Assets.list_item_weapon_gold, x, y);
        //g.drawRect(x + 234 + 8, y + 150 - 62 + 8, 175, 62, 0x7F000000);
        //g.drawText(String.valueOf(position), (int) (x + 234 + 8 + 55), y + 150 - 62 + 24 + 25, 0xFFFFFFFF, 24);
        /*if (pressed)
            g.drawPixmap(Assets.button_details, x + 234, y + 152, 189, 0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, x + 234, y + 152, 0, 0, 189, 74);
        g.drawText("DETAIL", x + 234 + 46, y + 152 + 24 + 20, 0xFFFFFFFF, 24);*/

        g.drawPixmap(item.getPixmap(), x + 8 + 6, y + 8 + 6);
        g.drawText(item.getName(), (x + 8 + 6 + 6), y + 152 + 32, 0xFFFFFFFF, 26);
        g.drawText("DAMAGE "+item.getDamage(), (x + 8 + 6 + 6), y + 152 + 32 + 4 + 24, 0xFFFF5400, 24);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        return true;
    }

    private boolean inBounds(Input.Touch event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

}
