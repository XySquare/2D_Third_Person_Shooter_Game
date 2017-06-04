package com.xyy.game.ai.Screen;

import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Weapon.IMIDesertEagle;
import com.xyy.game.ai.Weapon.M16A4;
import com.xyy.game.ai.Weapon.RPG;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponLab;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by ${XYY} on ${2017/4/20}.
 */

public class Screen_MainMenu_Repository extends Screen {
    public static class WeaponRecord{
        public UUID mUUID;
        public Weapon mWeapon;
        //public int mExp;

        public WeaponRecord(UUID uuid, Weapon weapon/*, int exp*/){
            mUUID = uuid;
            mWeapon = weapon;
            //mExp = exp;
        }
    }

    private static final int EQUIP_LIMIT = 3;

    private final List<WeaponRecord> mWeapons;

    private List<UUID> mCurrentlyEquippedWeapons;

    private boolean[] mSelected;

    //private Weapon mPurchasedWeapon = null;
    private boolean mUpgradeButtonPressed = false;

    //列表项的宽度度
    private final int ITEM_WIDTH = 437;
    private final int ITEM_HEIGHT = 242;
    //屏幕中最大显示的行/列数
    private final int ROW_SCREEN = 2;
    private final int COL_SCREEN = 1280 / ITEM_WIDTH + 2;
    //列表纵坐标
    private final int LIST_Y = 136;
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

    public Screen_MainMenu_Repository(Game game) {
        super(game);

        WeaponLab weaponLab = WeaponLab.get();

        /**
         * 玩家所拥有的武器
         */
        /*if(UserDate.mWeapons == null) {
            mWeapons = new ArrayList<>();
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new M16A4(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new RPG(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new IMIDesertEagle(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new RPG(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new RPG(), 0));
            mWeapons.add(new WeaponRecord(UUID.randomUUID(), new RPG(), 0));
            UserDate.mWeapons = mWeapons;
        }
        else {
            mWeapons = UserDate.mWeapons;
        }*/
        mWeapons = weaponLab.getWeapons();

        mSelected = new boolean[mWeapons.size()];

        /**
         * TODO: 玩家当前装备的武器的索引
         */
        if(UserDate.mCurrentlyEquippedWeapons == null) {
            mCurrentlyEquippedWeapons = new ArrayList<>();
            mCurrentlyEquippedWeapons.add(mWeapons.get(0).mUUID);
        }
        else{
            mCurrentlyEquippedWeapons = UserDate.mCurrentlyEquippedWeapons;
        }

        //排序
        Collections.sort(mWeapons, new SortByRarity());

        //将装备的武器移动至最前
        int index = 0;
        for(UUID uuid: mCurrentlyEquippedWeapons){
            for(int i=0;i<mWeapons.size();i++){
                if(mWeapons.get(i).mUUID.equals(uuid)){
                    mWeapons.add(index,mWeapons.remove(i));
                    mSelected[index] = true;
                    index++;
                    break;
                }
            }
        }

        for (WeaponRecord weapon : mWeapons) {
            weapon.mWeapon.loadPixmap(game.getGraphics(), Weapon.PixmapQuality.NORMAL);
        }

        mItemNum = mWeapons.size();
        mRowNum = 2;
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

        /*if (!dragAble) {
            relativeIndex = COL_SCREEN * ROW_SCREEN;
            //listX = listStartX = (1280 - mColumnNum * ITEM_WIDTH) / 2;
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
                    /*if (mPurchasedWeapon != null) {
                        if (inBounds(event,545, 63 + 472, 189, 74)) {
                            pointer = event.pointer;
                            mDialogButtonPressed = true;
                        }
                    } else */if (event.y > LIST_Y && event.y < LIST_Y_MAX) {
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
                        else {
                            int indexX = (event.x - listStartX) / ITEM_WIDTH;
                            int indexY = (event.y - LIST_Y) / ITEM_HEIGHT;
                            int dx = event.x - (listStartX + indexX * ITEM_WIDTH);
                            int dy = event.y - (LIST_Y + indexY * ITEM_HEIGHT);
                            //UPGRADE
                            if (dx > 234 && dx < 234 + 189 && dy > 152 && dy < 152 + 74) {
                                //game.setScreen(new Screen_MainMenu_Store_Weapon_Detail(game, mWeapons.get(index)));
                                mUpgradeButtonPressed = true;
                            }
                        }
                    }
                    //SETTING
                    else if(inBounds(event,0,720-84,81,84)){
                        game.setScreen(new SettingScreen(game));
                    }
                    //STORE
                    else if(inBounds(event,81,720-84,218,84)){
                        game.setScreen(new Screen_MainMenu_Store(game));
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
                    mUpgradeButtonPressed = false;
                    /*if (mPurchasedWeapon != null) {
                        mDialogButtonPressed = false;
                        if (inBounds(event, 545, 63 + 472, 189, 74)) {
                            mPurchasedWeapon = null;
                        }
                    }
                    else*/
                    //当触摸区域在列表范围
                    if (event.y > LIST_Y && event.y < LIST_Y_MAX) {
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
                                int dx = event.x - (listX + indexX * ITEM_WIDTH);
                                int dy = event.y - (LIST_Y + indexY * ITEM_HEIGHT);
                                //UPGRADE
                                if(dx>234 && dx<234+189 && dy>152 && dy<152+74){
                                    game.setScreen(new Screen_MainMenu_Repository_Weapon_Upgrade(game,mWeapons.get(index)));
                                }
                                //SELECT
                                else {
                                    if(mSelected[index]){
                                        mSelected[index] = false;
                                        mCurrentlyEquippedWeapons.remove(mWeapons.get(index).mUUID);
                                    }
                                    else if(mCurrentlyEquippedWeapons.size() < EQUIP_LIMIT){
                                        mSelected[index] = true;
                                        mCurrentlyEquippedWeapons.add(mWeapons.get(index).mUUID);
                                    }
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
        g.drawText("[REPOSITORY]", 77, 53, 0xFFFFFFFF, 24);
        //货币
        g.drawText(String.valueOf(UserDate.sCurrency),1280-200,40,0xFFFFFFFF,30);

        g.drawText("EQUIPPED "+mCurrentlyEquippedWeapons.size()+"/3", 8, 68 + 24 + 16, 0xFFFFFFFF, 24);
        /*g.drawPixmap(Assets.button_tab, 378, 68, 180, 0, 180, 68);
        g.drawText("WEAPON", 378 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 - 8, 68, 0, 0, 180, 68);
        g.drawText("SUPPLIES", 378 + 180 - 8 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 + 180 - 16, 68, 0, 0, 180, 68);
        g.drawText("PROMOS", 378 + 180 + 180 - 16 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);*/

        for (int i = 0; i < COL_SCREEN * ROW_SCREEN && startIndex+i < mItemNum; i++) {
            int indexX = i / ROW_SCREEN;
            int indexY = i - indexX * ROW_SCREEN;
            int x = listStartX + indexX * ITEM_WIDTH;
            int y = LIST_Y + indexY * ITEM_HEIGHT;
            item_list_present(g, x, y, startIndex + i, i == relativeIndex && mUpgradeButtonPressed);
        }

        /*if (mPurchasedWeapon != null) {
            g.fill(0x6F000000);
            g.drawPixmap(Assets.dialog, 231, 63);
            g.drawText("PURCHASE SUCCEED", 640, 63 + 80, 0xFFFF5400, 42, Paint.Align.CENTER);
            g.drawText(mPurchasedWeapon.getName(), 640, 63 + 140, 0xFFFFFFFF, 30, Paint.Align.CENTER);
            g.drawPixmap(mPurchasedWeapon.getPixmap(), 432.5f, 292);
            if (mDialogButtonPressed)
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 189, 0, 189, 74);
            else
                g.drawPixmap(Assets.button_details, 546, 63 + 472, 0, 0, 189, 74);
            g.drawText("DONE", 640, 63 + 472 + 24 + 20, 0xFFFFFFFF, 24, Paint.Align.CENTER);
        }*/


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

        g.drawPixmap(Assets.main_menu_bottom_bar_button,offsetX+218,720-84,0,0,218,84);
        g.drawText(bottom_bar_text[1],offsetX+218+109,720-84+36+19,0xFFFFFFFF,26, Paint.Align.CENTER);

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
        Weapon weapon = mWeapons.get(position).mWeapon;

        Weapon.Rarity rarity;
        if(mSelected[position]){
            g.drawPixmap(Assets.list_item_weapon_selected, x, y);
            g.drawText("EQUIPPED", x + 234 + 94, y + 150 - 62 + 24 + 35, 0x7F000000, 24, Paint.Align.CENTER);
        }
        else if ((rarity = weapon.getRarity()) == Weapon.Rarity.N)
            g.drawPixmap(Assets.list_item_weapon, x, y);
        else if (rarity == Weapon.Rarity.R)
            g.drawPixmap(Assets.list_item_weapon_bronze, x, y);
        else if (rarity == Weapon.Rarity.SR)
            g.drawPixmap(Assets.list_item_weapon_silver, x, y);
        else if (rarity == Weapon.Rarity.SSR)
            g.drawPixmap(Assets.list_item_weapon_gold, x, y);
        //g.drawRect(x + 234 + 8, y + 150 - 62 + 8, 175, 62, 0x7F000000);

        if (pressed)
            g.drawPixmap(Assets.button_details, x + 234, y + 152, 189, 0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, x + 234, y + 152, 0, 0, 189, 74);
        g.drawText("UPGRADE", x + 234 + 94, y + 152 + 24 + 22, 0xFFFFFFFF, 24, Paint.Align.CENTER);

        g.drawPixmap(weapon.getPixmap(), x + 8 + 6, y + 8 + 6);
        g.drawText(weapon.getName(), x + 8 + 6 + 6, y + 152 + 32, 0xFFFFFFFF, 26);
        g.drawText("DAMAGE "+weapon.getDamage(), x + 8 + 6 + 6, y + 152 + 32 + 4 + 24, 0xFFFF5400, 24);
        //g.drawText(String.valueOf(), x + 8 + 6 + 6 + 110, y + 152 + 32 + 4 + 24, 0xFFFF5400, 24);
    }

    /*void setResult(Weapon purchasedWeapon){
        mPurchasedWeapon = purchasedWeapon;
    }*/

    @Override
    public void pause() {
        //TODO: SAVE DATE
        UserDate.mCurrentlyEquippedWeapons = mCurrentlyEquippedWeapons;
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

    /**
     * 稀有度降序
     */
    private class SortByRarity implements Comparator<WeaponRecord> {
        @Override
        public int compare(WeaponRecord o1, WeaponRecord o2) {
            int rarity = o2.mWeapon.getRarity().ordinal() - o1.mWeapon.getRarity().ordinal();
            return rarity == 0 ? o1.mWeapon.getName().compareTo(o2.mWeapon.getName()) : rarity;
        }
    }
}
