package com.xyy.game.ai.Screen;

import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponLab;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;
import com.xyy.game.ai.Screen.Screen_MainMenu_Repository.WeaponRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.xyy.game.ai.Weapon.Weapon.Rarity;
import static com.xyy.game.ai.Weapon.Weapon.Rarity.N;
import static com.xyy.game.ai.Weapon.Weapon.Rarity.R;
import static com.xyy.game.ai.Weapon.Weapon.Rarity.SR;
import static com.xyy.game.ai.Weapon.Weapon.Rarity.SSR;

/**
 * Created by ${XYY} on ${2017/4/22}.
 */

public class Screen_MainMenu_Repository_Weapon_Upgrade_Select_Material extends Screen {

    private static final byte BT_UPGRADE = 0;
    private static final byte BT_BACK = 1;

    private WeaponRecord mCurrentlyUpgradingWeapon;

    private final List<WeaponRecord> mWeapons;

    private List<UUID> mCurrentlyEquippedWeapons;

    private boolean[] mSelected;

    private int mNextLvExpReq;

    private int mCurSelectingExp;

    private byte mIdPressing = -1;

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

    public Screen_MainMenu_Repository_Weapon_Upgrade_Select_Material(Game game, UUID uuid) {
        super(game);

        WeaponLab weaponLab = WeaponLab.get();

        /**
         * 玩家所拥有的武器
         */
        mWeapons = weaponLab.getWeapons();

        //移除当前正在强化的武器
        for (int i = 0; i < mWeapons.size(); i++) {
            if (mWeapons.get(i).mUUID.equals(uuid)) {
                mCurrentlyUpgradingWeapon = mWeapons.remove(i);
                break;
            }
        }
        mNextLvExpReq = mCurrentlyUpgradingWeapon.mWeapon.getNextLvExpReq() - mCurrentlyUpgradingWeapon.mWeapon.getCurExp();

        mSelected = new boolean[mWeapons.size()];

        /**
         * 玩家当前装备的武器的索引
         */
        mCurrentlyEquippedWeapons = new ArrayList<>();

        //排序
        Collections.sort(mWeapons, new SortByRarity());

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
                    } else */
                    if (event.y > LIST_Y && event.y < LIST_Y_MAX) {
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
                        if (dragging || relativeIndex > COL_SCREEN * ROW_SCREEN)
                            relativeIndex = COL_SCREEN * ROW_SCREEN;
                    }
                    //TODO: BACK
                    else if(event.x <= 71 && event.y <= 68){
                        pointer = event.pointer;
                        mIdPressing = BT_BACK;
                    }
                    /*//TODO: CANCEL
                    else if (inBounds(event, 190 + 3 * 218, 720 - 84, 218, 84)) {
                        mWeapons.add(mCurrentlyUpgradingWeapon);
                        game.setScreen(new Screen_MainMenu_Repository_Weapon_Upgrade(game,mCurrentlyUpgradingWeapon));
                    }*/
                    //TODO: UPGRADE
                    else if (inBounds(event, 190 + 4 * 218, 720 - 84, 218, 84)) {
                        pointer = event.pointer;
                        mIdPressing = BT_UPGRADE;
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
                                //If current item is selected
                                if (mSelected[index]) {
                                    //Deselect
                                    mSelected[index] = false;
                                    mCurrentlyEquippedWeapons.remove(mWeapons.get(index).mUUID);

                                    mCurSelectingExp -= getExp(mWeapons.get(index).mWeapon.getRarity());
                                }
                                //otherwise, check if current exp is overflow
                                else if(mCurSelectingExp < mNextLvExpReq){
                                    mSelected[index] = true;
                                    mCurrentlyEquippedWeapons.add(mWeapons.get(index).mUUID);

                                    mCurSelectingExp += getExp(mWeapons.get(index).mWeapon.getRarity());
                                }
                            }
                        }
                        //如果列表移动速度大于5px/s，则赋予惯性
                        if (Math.abs((preListX - listX) / deltaTime) > 5)
                            speed = (int) ((listX - preListX) / deltaTime);
                    }
                    else if (inBounds(event, 190 + 4 * 218, 720 - 84, 218, 84)) {
                        if(mIdPressing == BT_UPGRADE){
                            Weapon currentWeapon = mCurrentlyUpgradingWeapon.mWeapon;
                            int prevLevel = currentWeapon.getCurLv();
                            int deltaDamage = currentWeapon.getDamage();
                            int deltaEnergyCost = currentWeapon.getEnergyCost();
                            int deltaRpm = (int)(60/currentWeapon.getAtkDelay());

                            currentWeapon.addExp(mCurSelectingExp);

                            deltaDamage = currentWeapon.getDamage() - deltaDamage;
                            deltaEnergyCost = currentWeapon.getEnergyCost() - deltaEnergyCost;
                            deltaRpm = (int)(60/currentWeapon.getAtkDelay()) - deltaRpm;

                            WeaponLab weaponLab = WeaponLab.get();
                            for(UUID uuid:mCurrentlyEquippedWeapons){
                                weaponLab.removeWeapon(uuid);
                            }
                            weaponLab.updateWeapon(mCurrentlyUpgradingWeapon);

                            Screen_MainMenu_Repository_Weapon_Upgrade screen = new Screen_MainMenu_Repository_Weapon_Upgrade(game, mCurrentlyUpgradingWeapon);
                            screen.setResult(currentWeapon.getCurLv()>prevLevel,deltaDamage, deltaEnergyCost, deltaRpm);
                            game.setScreen(screen);
                        }
                    }
                    else if(event.x <= 71 && event.y <= 68){
                        if(mIdPressing == BT_BACK)
                            game.setScreen(new Screen_MainMenu_Repository_Weapon_Upgrade(game,mCurrentlyUpgradingWeapon));
                    }
                    //重置"当前正在被按下的索引编号"
                    relativeIndex = COL_SCREEN * ROW_SCREEN;

                    mIdPressing = -1;
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
        g.drawPixmap(Assets.back, 22, 20);
        //"MainMenu"文字
        g.drawText("MAIN MENU", 77, 28, 0xFF999999, 24);
        //"Store"文字
        g.drawText("[SELECT MATERIAL]", 77, 53, 0xFFFFFFFF, 24);
        //货币
        //g.drawText(String.valueOf(UserDate.sCurrency),1280-200,40,0xFFFFFFFF,30);

        /*g.drawPixmap(Assets.button_tab, 378, 68, 180, 0, 180, 68);
        g.drawText("WEAPON", 378 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 - 8, 68, 0, 0, 180, 68);
        g.drawText("SUPPLIES", 378 + 180 - 8 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);
        g.drawPixmap(Assets.button_tab, 378 + 180 + 180 - 16, 68, 0, 0, 180, 68);
        g.drawText("PROMOS", 378 + 180 + 180 - 16 + 38, 68 + 24 + 16, 0xFFFFFFFF, 24);*/

        for (int i = 0; i < COL_SCREEN * ROW_SCREEN && startIndex + i < mItemNum; i++) {
            int indexX = i / ROW_SCREEN;
            int indexY = i - indexX * ROW_SCREEN;
            int x = listStartX + indexX * ITEM_WIDTH;
            int y = LIST_Y + indexY * ITEM_HEIGHT;
            item_list_present(g, x, y, startIndex + i);
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

        g.drawRect(0, 720 - 84, 1280, 84, 0x7F000000);

        if(mCurSelectingExp <= mNextLvExpReq)
            g.drawText("[OVERFLOW EXP WOULD BE IGNORED]",16, 720 - 84 + 25 + 30,0x7FFFFFFF,30);
        else
            g.drawText("[OVERFLOW EXP WOULD BE IGNORED]",16, 720 - 84 + 25 + 30,0xFFFB5809,30);
        if(mCurrentlyUpgradingWeapon.mWeapon.getCurLv() < mCurrentlyUpgradingWeapon.mWeapon.getMaxLv()) {
            g.drawText(
                    "EXP " + (mCurrentlyUpgradingWeapon.mWeapon.getCurExp() + mCurSelectingExp) + "/" + mCurrentlyUpgradingWeapon.mWeapon.getNextLvExpReq()
                    , 16 + 440, 720 - 84 + 8 + 25, 0xFFFFFFFF, 25);

            final int width = 200;
            g.drawRect(16 + 440, 720 - 84 + 8 + 25 + 5, width, 36, 0x7FFFFFFF);
            g.drawRect(16 + 440, 720 - 84 + 8 + 25 + 5, (int) (width*((float)mCurrentlyUpgradingWeapon.mWeapon.getCurExp()/mCurrentlyUpgradingWeapon.mWeapon.getNextLvExpReq())), 36, 0xFF00c6fd);
            g.drawRect((int) (16 + 440 + width*((float)mCurrentlyUpgradingWeapon.mWeapon.getCurExp()/mCurrentlyUpgradingWeapon.mWeapon.getNextLvExpReq())), 720 - 84 + 8 + 25 + 5, (int) (width*(((float)Math.min(mCurSelectingExp,mNextLvExpReq)/mCurrentlyUpgradingWeapon.mWeapon.getNextLvExpReq()))), 36, 0xFF69efff);
        }

        if(mIdPressing == BT_UPGRADE)
            g.drawPixmap(Assets.button_details, 1062+15, 720 - 84 + 5, 189 ,0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, 1062+15, 720 - 84 + 5, 0 ,0, 189, 74);
        g.drawText("UPGRADE",1062+15+95, 720 - 84+5+24+20, 0xFFFFFFFF, 24, Paint.Align.CENTER);
    }

    private void item_list_present(Graphics g, int x, int y, int position) {
        Weapon weapon = mWeapons.get(position).mWeapon;

        Weapon.Rarity rarity;
        if (mSelected[position]) {
            g.drawPixmap(Assets.list_item_weapon_selected, x, y);
            g.drawText("SELECTED", x + 234 + 8 + 53, y + 152 + 32 + 4 + 24, 0x7F000000, 24);
        } else if ((rarity = weapon.getRarity()) == N)
            g.drawPixmap(Assets.list_item_weapon, x, y);
        else if (rarity == R)
            g.drawPixmap(Assets.list_item_weapon_bronze, x, y);
        else if (rarity == SR)
            g.drawPixmap(Assets.list_item_weapon_silver, x, y);
        else if (rarity == SSR)
            g.drawPixmap(Assets.list_item_weapon_gold, x, y);

        g.drawPixmap(weapon.getPixmap(), x + 8 + 6, y + 8 + 6);
        g.drawText(weapon.getName(), x + 8 + 6 + 6, y + 152 + 32, 0xFFFFFFFF, 26);
        g.drawText("DAMAGE "+weapon.getDamage(), x + 8 + 6 + 6, y + 152 + 32 + 4 + 24, 0xFFFF5400, 24);
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

    private int getExp(Rarity rarity){
        switch (rarity) {
            case N:
                return 10;
            case R:
                return 50;
            case SR:
                return 500;
            case SSR:
                return 10000;
            default:
                return 0;
        }
    }
}
