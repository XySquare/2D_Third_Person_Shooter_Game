package com.xyy.game.ai.Screen;

import android.graphics.Paint;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Weapon.*;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponLab;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.framework.Screen;

import java.util.List;
import java.util.UUID;

/**
 * Created by ${XYY} on ${2017/4/16}.
 */

public class Screen_MainMenu_Store_Promos extends Screen {
    private class ListItem {
        final Pixmap mIco;
        final String mName;
        final String[] mDescription;
        final int mPrice;

        private ListItem(Pixmap ico, String name, String[] description, int price) {
            mIco = ico;
            mName = name;
            mDescription = description;
            mPrice = price;
        }
    }

    class CaseItem{
        final Class<? extends Weapon> mName;
        final int mWeight;

        CaseItem(Class<? extends Weapon> name, int weight) {
            mName = name;
            mWeight = weight;
        }
    }

    private final CaseItem[] mCaseItems = new CaseItem[]{
            new CaseItem(IMIDesertEagle.class,50),
            new CaseItem(M16A4.class,50),
            new CaseItem(RPG.class,10),
            new CaseItem(RPD.class, 10)
    };

    private final CaseItem[] mEliteCaseItems = new CaseItem[]{
            new CaseItem(IMIDesertEagle.class,50),
            new CaseItem(M16A4.class,50),
            new CaseItem(RPG.class,120),
            new CaseItem(BlackHole.class, 30),
            new CaseItem(RPD.class, 100)
    };

    private final ListItem[] mListItems;

    private Weapon mPurchasedWeapon = null;
    private boolean mDialogButtonPressed = false;

    //列表项的宽度度
    private final int ITEM_WIDTH = 460;
    private final int ITEM_HEIGHT = 278;
    //屏幕中最大显示的行/列数
    private final int ROW_SCREEN = 1;
    private final int COL_SCREEN = 2;
    //列表纵坐标
    private final int LIST_Y = 240;
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
    private int listX;
    //按下时，指针到列表左侧的横坐标的偏移量
    private int offsetX;
    //屏幕左侧的索引编号
    private int startIndex;
    //屏幕左侧的列表左端的横坐标
    private int listStartX;
    //按下时指针的横坐标
    private int perEventX;
    //惯性速度
    private int speed;
    //是否正在被拖拽
    private boolean dragging;
    //是否可拖拽
    private boolean dragAble;

    public Screen_MainMenu_Store_Promos(Game game) {
        super(game);

        mItemNum = 2;
        mRowNum = 1;
        if (mItemNum % mRowNum == 0)
            mColumnNum = mItemNum / mRowNum;
        else
            mColumnNum = mItemNum / mRowNum + 1;
        pointer = -1;
        relativeIndex = COL_SCREEN * ROW_SCREEN;
        listX = startIndex = listStartX = 0;
        dragging = false;
        dragAble = !(mColumnNum <= (1280 / ITEM_WIDTH));
        speed = 0;

        if (!dragAble) {
            relativeIndex = COL_SCREEN * ROW_SCREEN;
            listX = listStartX = (1280 - mColumnNum * ITEM_WIDTH) / 2;
        }

        /**
         * 商店所出售的武器
         */
        mListItems = new ListItem[]{
                new ListItem(Assets.case_normal,"MYSTERY CASE",new String[]{"YOU COULD WIN ","ONE OF THOSE","COOL ITEMS"},100),
                new ListItem(Assets.case_elite,"ELITE SPEC MYSTERY CASE",new String[]{"RARE ITEMS","WINNING RATE INCREASE","BY 12 TIMES"},1000)
        };

        /*for (Item item : mItems) {
            item.mWeapon.loadPixmap(game.getGraphics(), Weapon.PixmapQuality.NORMAL);
        }*/
    }

    @Override
    public void update(float deltaTime) {
        List<Input.Touch> touchEvents = game.getInput().getTouchEvents();
        /**
         * 处理惯性运动
         */
        //if(len==0 && dragAble) {
        if (speed != 0) {
            //列表移动
            listX += speed * deltaTime;
            //减速
            speed *= 1 - 2 * deltaTime;
            //边界处理
            if (listX > 0) {
                listX = 0;
                speed = 0;
                dragging = false;
            } else if (listX < 1280 - mColumnNum * ITEM_WIDTH) {
                listX = 1280 - mColumnNum * ITEM_WIDTH;
                speed = 0;
                dragging = false;
            }
            //更新列表起始索引编号与起始坐标
            int startIndexX = (-listX / ITEM_WIDTH);
            startIndex = startIndexX * ROW_SCREEN;
            listStartX = listX + startIndexX * ITEM_WIDTH;
        }
        /**
         * 处理输入
         */
        final int LIST_Y_MAX = LIST_Y + ITEM_HEIGHT * ROW_SCREEN;
        //暂存移动前列表横坐标
        final int preListX = listX;
        //事件线性表长度缓存
        final int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if (event.type == Input.Touch.TOUCH_DOWN) {
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
                            relativeIndex = COL_SCREEN * ROW_SCREEN;
                    }
                    //SETTING
                    else if(inBounds(event,0,720-84,81,84)){
                        game.setScreen(new SettingScreen(game));
                    }
                    //REPOSITORY
                    else if(inBounds(event,81+218,720-84,218,84)){
                        game.setScreen(new Screen_MainMenu_Repository(game));
                    }
                    //WEAPON
                    else if(inBounds(event,378, 68, 180, 68)){
                        game.setScreen(new Screen_MainMenu_Store(game));
                    }
                    //SUPPLIES
                    else if(inBounds(event,378 + 180 - 8, 68, 180, 68)){
                        game.setScreen(new Screen_MainMenu_Store_Supplies(game));
                    }
                    //CAMPAIGN
                    else if(event.x>81+4*218 && event.y>720-84){
                        game.setScreen(new MapsSelectingScreen(game));
                    }
                }
            } else if (event.type == Input.Touch.TOUCH_DRAGGED) {
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
            } else if (event.type == Input.Touch.TOUCH_UP) {
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
                            */
                        //非拖拽中
                        if (!dragging && event.x >= listX) {
                            //获取"索引编号"
                            int indexX = (event.x - listX) / ITEM_WIDTH;
                            int indexY = (event.y - LIST_Y) / ITEM_HEIGHT;
                            int index = indexX * ROW_SCREEN + indexY;
                            //按下与抬起的索引编号应一致，且不应为边界
                            if (index == this.relativeIndex + startIndex && index < mItemNum) {
                                switch (index){
                                    case 0:
                                        if(UserDate.sCurrency >= 100){
                                            UserDate.sCurrency -= 100;
                                            WeaponLab weaponLab = WeaponLab.get();
                                            Class<? extends Weapon> _randomCaseItem = randomCaseItem(mCaseItems);
                                            weaponLab.addWeapon(_randomCaseItem.getName());
                                            game.setScreen(new Screen_MainMenu_Store_Promos_Rolling(game, mCaseItems, _randomCaseItem));
                                        }
                                        break;
                                    case 1:
                                        if(UserDate.sCurrency >= 1000){
                                            UserDate.sCurrency -= 1000;
                                            WeaponLab weaponLab = WeaponLab.get();
                                            Class<? extends Weapon> _randomCaseItem = randomCaseItem(mEliteCaseItems);
                                            weaponLab.addWeapon(_randomCaseItem.getName());
                                            game.setScreen(new Screen_MainMenu_Store_Promos_Rolling(game, mEliteCaseItems, _randomCaseItem));
                                        }
                                        break;
                                }

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
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.background, 0, 0);

        //顶部条
        g.drawPixmap(Assets.main_menu_top_bar, 0, 0);
        //返回按钮
        g.drawPixmapAlpha(Assets.back, 22, 20, 0x7F);
        //"MainMenu"文字
        g.drawText("MAIN MENU", 77, 28, 0xFF999999, 24);
        //"Store"文字
        g.drawText("[STORE]", 77, 53, 0xFFFFFFFF, 24);
        //货币
        g.drawText("CREDIT",1280-200-8,40,0xFFFFF200,30, Paint.Align.RIGHT);
        g.drawText(String.valueOf(UserDate.sCurrency),1280-200,40,0xFFFFFFFF,30);

        g.drawPixmap(Assets.button_tab, 378, 68, 0, 0, 180, 68);
        g.drawText("WEAPON", 378 + 90, 68 + 24 + 18, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        g.drawPixmap(Assets.button_tab, 378 + 180 - 8, 68, 0, 0, 180, 68);
        g.drawText("SUPPLIES", 378 + 180 - 8 + 90, 68 + 24 + 18, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        g.drawPixmap(Assets.button_tab, 378 + 180 + 180 - 16, 68, 180, 0, 180, 68);
        g.drawText("PROMOS", 378 + 180 + 180 - 16 + 90, 68 + 24 + 18, 0xFFFFFFFF, 24, Paint.Align.CENTER);

        for (int i = 0; i < COL_SCREEN * ROW_SCREEN && startIndex+i < mItemNum; i++) {
            int indexX = i / ROW_SCREEN;
            int indexY = i - indexX * ROW_SCREEN;
            int x = listStartX + indexX * ITEM_WIDTH;
            int y = LIST_Y + indexY * ITEM_HEIGHT;
            item_list_present(g, x, y, startIndex + i, i == relativeIndex);
        }

        final int offsetX = 81;
        String[] bottom_bar_text = new String[]{"STORE","REPOSITORY","","",""};
        g.drawPixmap(Assets.main_menu_bottom_bar_button,-218+offsetX,720-84,218,0,218,84);
        g.drawPixmap(Assets.setting,18,720-84+20);
        for(int i=0;i<4;i++){
            g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX+i*218,720-84,218,0,218,84);
            g.drawText(bottom_bar_text[i],offsetX+i*218+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);
        }
        g.drawPixmap(Assets.main_menu_bottom_bar_button_red,offsetX+4*218,720-84);
        g.drawText("CAMPAIGN",offsetX+4*218+164,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);

        g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX,720-84,0,0,218,84);
        g.drawText(bottom_bar_text[0],offsetX+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);

        if (mPurchasedWeapon != null) {
            g.fill(0x6F000000);
            g.drawPixmap(Assets.dialog, 231, 63);
            g.drawText("NEW WEAPON", 640, 63 + 80, 0xFFFF5400, 42, Paint.Align.CENTER);
            g.drawText(mPurchasedWeapon.getName(), 640, 63 + 140, 0xFFFFFFFF, 30, Paint.Align.CENTER);
            g.drawPixmap(mPurchasedWeapon.getPixmap(), 432.5f, 292);
            if (mDialogButtonPressed)
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 189, 0, 189, 74);
            else
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 0, 0, 189, 74);
            g.drawText("DONE", 640, 63 + 472 + 24 + 20, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        }
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

    private void item_list_present(Graphics g, int x, int y, int position, boolean pressed) {
        ListItem listItem = mListItems[position];

        g.drawPixmap(Assets.list_item_promos, x, y);
        //g.drawRect(x + 234 + 8, y + 150 - 62 + 8, 175, 62, 0x7F000000);
        //g.drawText(String.valueOf(item.mPrice), x + 234 + 8 + 55, y + 150 - 62 + 24 + 25, 0xFFFFFFFF, 24);
        if(listItem.mPrice > UserDate.sCurrency){
            g.drawPixmap(Assets.button_details_s, x + 295, y + 200, 292, 0, 146, 66);
        }
        else if (pressed)
            g.drawPixmap(Assets.button_details_s, x + 295, y + 200, 146, 0, 146, 66);
        else
            g.drawPixmap(Assets.button_details_s, x + 295, y + 200, 0, 0, 146, 66);
        g.drawText(String.valueOf(listItem.mPrice), x + 295 + 73, y + 200 + 24 + 16, 0xFFFFFFFF, 24, Paint.Align.CENTER);

        g.drawPixmap(listItem.mIco, x + 16, y + 50);
        g.drawText(listItem.mName, x + 310, y + 50, 0xFFFF5400, 26, Paint.Align.CENTER);
        g.drawText(listItem.mDescription[0], x + 310, y + 100, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        g.drawText(listItem.mDescription[1], x + 310, y + 130, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        g.drawText(listItem.mDescription[2], x + 310, y + 160, 0xFFFFFFFF, 24, Paint.Align.CENTER);
    }

    void setResult(Weapon purchasedWeapon){
        mPurchasedWeapon = purchasedWeapon;
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
        return false;
    }

    private boolean inBounds(Input.Touch event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

    private Class<? extends Weapon> randomCaseItem(CaseItem[] caseItems){
        int totalWeight = 0;
        //统计权重
        for (CaseItem caseItem:
             caseItems) {
            totalWeight += caseItem.mWeight;
        }
        //生产随机数
        double ramNum = Math.random();
        //赌轮盘算法
        double currentFence = 0;
        for (CaseItem caseItem:
                caseItems) {
            currentFence += (double)caseItem.mWeight / (double)totalWeight;
            if(ramNum <currentFence){
                return caseItem.mName;
            }
        }
        return null;
    }
}
