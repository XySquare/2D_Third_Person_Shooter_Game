package com.xyy.game.ai;

import android.graphics.Paint;
import android.util.Log;

import com.xyy.game.ai.Character.Player;
import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.ai.Screen.UserDate;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.component.CircleButton;
import com.xyy.game.component.ProcessingAnimation;
import com.xyy.game.component.SquareImageButton;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${XYY} on ${2015/11/20}.
 */
public class GameState_Running extends GameState {
    private abstract class Item{
        final Pixmap mIco;
        private final int mCooldown;

        private int mCounter;
        private float mCooldownTimer;

        private Item(Pixmap ico, int counter, int cooldown) {
            mIco = ico;
            mCounter = counter;
            mCooldown = cooldown;
            mCooldownTimer = cooldown;
        }

        void update(float deltaTime){
            if(mCooldownTimer < mCooldown){
                mCooldownTimer += deltaTime;
            }
        }

        void use(){
            if(mCounter > 0 && mCooldownTimer >= mCooldown){
                mCounter--;
                mCooldownTimer = 0;
                usage();
            }
        }

        abstract void usage();

        int getCounter() {
            return mCounter;
        }

        float getColldownProgress(){
            return mCooldownTimer/mCooldown;
        }

    }

    private class ItemCircleButton extends CircleButton{
        private Item mItem;

        public ItemCircleButton(int x, int y, Item item) {
            super(x, y, 45, 0x6F000000, item.mIco);
            mItem = item;
            initialize(0);
        }

        @Override
        public void update(float deltaTime) {
            super.update(deltaTime);
            mItem.update(deltaTime);
        }

        @Override
        public void present(Graphics g) {
            super.present(g);
            //绘制数字
            g.drawText(String.valueOf(mItem.getCounter()),x + 32,y+30+18,0xFFFF0000,24);
            //绘制环形进度条背景
            g.drawRing(x, y, currentR-10, 0, 360, 0x7F000000,6);
            //绘制环形进度条
            g.drawRing(x, y, currentR-10, 360 - 360*mItem.getColldownProgress() -90, 360*mItem.getColldownProgress(), 0xFFFC560C,6);
        }

        @Override
        public boolean isClicked(Input.Touch event) {
            boolean isclicked;
            if(isclicked = super.isClicked(event))
                mItem.use();
            return isclicked;
        }
    }

    private final SquareImageButton prevWeaponBt;
    private final SquareImageButton nextWeaponBt;
    private final ItemCircleButton mItemMedkit;
    private final ItemCircleButton mItemEngkit;
    //左摇杆
    private Controller controller;
    //右摇杆
    private Controller atkCtrl;
    //HP,能量进度条
    private ProcessingAnimation hpBar, energyBar;
    //暂停按钮
    private SquareImageButton pauseBt;
    //玩家得分
    private int score;

    //private byte[] numbers;

    private Weapon mCurrentWeapon;

    private String mWeaponCostString;

    public GameState_Running(GameScreenOperation gameScreen, final Stage stage) {
        super(gameScreen,stage);
        //加载图片资源（由于整个GameScreen都是在单独线程中加载的，而GameState又在GameScreen中加载，所以没关系）
        final Pixmap ico_medkit = gameScreen.getGraphics().newPixmap("ico_medkit.png", Graphics.PixmapFormat.ARGB8888);
        final Pixmap ico_engkit = gameScreen.getGraphics().newPixmap("ico_engkit.png", Graphics.PixmapFormat.ARGB8888);

        controller = new Controller(150,570,100);
        atkCtrl = new Controller(1280-150,570,100);

        hpBar = new ProcessingAnimation(77,10,320,39,0xFFFFFFFF);
        energyBar = new ProcessingAnimation(77,51,320,17,0xFF66CCFF);

        pauseBt = new SquareImageButton(12,9,60,60,0x6F000000,Assets.pauseIco);

        prevWeaponBt = new SquareImageButton(1280-280,9,50,80,0/*0x6F000000*/,Assets.btArrowLeft);
        nextWeaponBt = new SquareImageButton(1280-50,9,50,80,0/*0x6F000000*/,Assets.btArrowRight);

        //score = 0;

        //numbers = new byte[10];

        mCurrentWeapon = stage.getCurrentWeapon();
        mWeaponCostString = String.valueOf(mCurrentWeapon.getEnergyCost());

        // initialize Supply Items
        Item[] items = new Item[]{
                new Item(ico_medkit, UserDate.sMedkit, 15) {
                    @Override
                    void usage() {
                        Player player = stage.getPlayer();
                        player.accessHp((int) (player.getMaxHp() * 0.2));
                    }
                },
                new Item(ico_engkit, UserDate.sEngkit, 15) {
                    @Override
                    void usage() {
                        Player player = stage.getPlayer();
                        player.accessEnergy((int) (player.getMaxEnergy() * 0.2));
                    }
                }
        };
        mItemMedkit = new ItemCircleButton(300, 650, items[0]);
        mItemEngkit = new ItemCircleButton(410, 650, items[1]);
    }

    @Override
    public void enter() {
        pauseBt.initialize(0);
        prevWeaponBt.initialize(0);
        nextWeaponBt.initialize(0);
    }

    @Override
    public void exit() {
        Log.i("Running","controller.reset()");
        controller.reset();
        atkCtrl.reset();
        Player player = stage.getPlayer();
        player.setDirection(0, 0);
    }

    @Override
    public void update(float deltaTime) {
        final int RCtrlX = 1280-150;
        final int RCtrlY = 570;

        Player player = stage.getPlayer();

        //更新暂停按钮
        pauseBt.update(deltaTime);

        prevWeaponBt.update(deltaTime);
        nextWeaponBt.update(deltaTime);

        mItemMedkit.update(deltaTime);
        mItemEngkit.update(deltaTime);

        //处理输入事件
        List<Input.Touch> touchEvents = gameScreen.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            //暂停按钮是否被点击
            if(pauseBt.isClicked(event)){
                gameScreen.setState(GameState.PAUSED);
                break;
            }
            else if(mItemMedkit.isClicked(event)){

            }
            else if(mItemEngkit.isClicked(event)){

            }
            //切换武器按钮
            else if(nextWeaponBt.isClicked(event)){
                Log.i("GS_R","Next Weapon.");
                mCurrentWeapon = stage.nextWeapon();
                mWeaponCostString = String.valueOf(mCurrentWeapon.getEnergyCost());
            }
            else if(prevWeaponBt.isClicked(event)){
                Log.i("GS_R","Prev Weapon.");
                mCurrentWeapon = stage.prevWeapon();
                mWeaponCostString = String.valueOf(mCurrentWeapon.getEnergyCost());
            }
            controller.update(event);
            atkCtrl.update(event);
        }

        //玩家操控左摇杆移动
        player.setDirection(controller.getLX(), controller.getLY());

        //玩家操控右摇杆攻击
        if(atkCtrl.isPressed()) {
            stage.PlayerAttack(atkCtrl.getLX(), atkCtrl.getLY());
        }

        //更新舞台
        stage.update(deltaTime);

        //更新血条
        hpBar.update(deltaTime,(float)player.getHp()/player.getMaxHp());

        //更新能量条
        energyBar.update(deltaTime,(float)player.getEnergy()/player.getMaxEnergy());

        int s = stage.getScore();
        if(s != score) {
            int ds = (int) ((s - score) * deltaTime * 10);
            this.score += ds==0 ? 1 : ds;
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = gameScreen.getGraphics();

        Player player = stage.getPlayer();
        /**
         * 绘制舞台
         */
        stage.present(g);
        /**
         * 绘制玩家的HP条
         */
        hpBar.present(g);
        /**
         * 绘制玩家的能量条
         */
        energyBar.present(g);
        /**
         * 绘制Buff
         */
        final int BUFF_X = 405;
        ArrayList<BuffManager.BuffRecord> buffList = player.getBuffList();
        int len = buffList.size();
        for(int i=0;i<len;i++){
            BuffManager.BuffRecord buffRecord = buffList.get(i);
            //绘制环形进度条的半透明背景
            //g.drawRing(BUFF_X+30+i*80, 40, 33, 0, 3600, 0x7FFFFFFF,6);
            //绘制半透明背景
            g.drawCircle(BUFF_X+30+i*80, 39, 30, 0x6F000000);
            //绘制图标
            g.drawPixmap(Assets.ico,BUFF_X+i*80,9,60*buffRecord.getBuffIco(),0,60,60);
            //当叠加层数大于一时才绘制数字
            int counter = buffRecord.getCounter();
            if(counter>1)
                g.drawText(String.valueOf(counter),BUFF_X + 35+i*80,60,0xFFFFFFFF,24);
            //绘制环形进度条
            g.drawRing(BUFF_X + 30+i*80, 39, 25, 360 - 360*buffRecord.getRemainTimeRatio() -90, 360*buffRecord.getRemainTimeRatio(), 0xFFFFFFFF,4);
        }
        /**
         * 绘制追踪箭头
         */
        ArrayList<GameObject> trackList = stage.getTrackList();
        len = trackList.size();
        for(int i=0;i<len;i++){
            GameObject object = trackList.get(i);
            //指向目标的向量
            float XComponentToTarget = object.getX() -player.getX();
            float YComponentToTarget = object.getY() - player.getY();
            //忽略在视线内的对象
            if(XComponentToTarget<640 && XComponentToTarget>-640 && YComponentToTarget<360 && YComponentToTarget>-360) continue;
            //到目标的距离
            float distanceToTarget = (float) Math.sqrt
                    (XComponentToTarget*XComponentToTarget + YComponentToTarget*YComponentToTarget);
            //指向目标的单位向量
            float XUnitToTarget = XComponentToTarget/distanceToTarget;
            float YUnitToTarget = YComponentToTarget/distanceToTarget;
            //
            float x = XUnitToTarget * 256;
            float y = YUnitToTarget * 256;
            //
            float degree = (float) (Math.atan2(YComponentToTarget,XComponentToTarget)/Math.PI*180) + 90;

            g.drawPixmapDegree(Assets.arrows[stage.getTrackIco(i)],x+640,y+360,degree);
        }
        /*
         * 绘制摇杆
         */
        final int LCtrlX = 150;
        final int LCtrlY = 570;
        g.drawPixmap(Assets.padA, LCtrlX-100,LCtrlY-100);
        g.drawPixmap(Assets.pad, LCtrlX-75+controller.getPadX(),LCtrlY-75+controller.getPadY());
        final int RCtrlX = 1280-150;
        final int RCtrlY = 570;
        g.drawPixmap(Assets.padA, RCtrlX-100,RCtrlY-100);
        g.drawPixmap(Assets.pad, RCtrlX-75+atkCtrl.getPadX(),RCtrlY-75+atkCtrl.getPadY());
        /**
         * 绘制暂停按钮
         */
        pauseBt.present(g);

        /**
         * 绘制武器/切换按钮
         */
        g.drawRect(1050,9,180,80,0x6F000000);
        g.drawPixmap(mCurrentWeapon.getPixmap(),1280-280+50,9);
        g.drawText("ENG",1055,9+75,0xFFFFFFFF,16);
        g.drawText(mWeaponCostString,1085,9+75,0xFFFFFFFF,24);
        nextWeaponBt.present(g);
        prevWeaponBt.present(g);

        /**
         * 绘制Supply Item
         */
        mItemMedkit.present(g);
        mItemEngkit.present(g);

        //g.drawText(String.valueOf(score),1280-400,40,0xFFFFFFFF,35);

        /*String line = String.valueOf(score);
        len = line.length();
        int x = 1280-400;
        int srcWidth = 26;
        for (int i = 0; i < len; i++) {
            char character = line.charAt(i);

            int srcX = (character - '0') * 26;

            g.drawPixmap(Assets.numbers5_26_2, x, 10, srcX, 0, srcWidth, 33);
            x += srcWidth;
        }*/

        /**
         * 绘制得分
         */
        /*g.drawText("score",1280-402,73,0xFFFFFFFF,16);

        int x = score;
        for(int i=9;i>=0;i--){
            int num = x % 10;
            x/=10;
            g.drawPixmap(Assets.numbers5_26_2, 1280-400 + 40 + i*26, 40, num*26, 0, 26, 33);
        }*/
        g.drawText("score",1280/2,26,0xFFFFFFFF,16, Paint.Align.CENTER);
        g.drawText(String.valueOf(score), 1280/2, 26+30+4,0xFFFFFFFF,28, Paint.Align.CENTER);
    }

    @Override
    public void onBack() {
        gameScreen.setState(GameState.PAUSED);
    }

    private static boolean inBounds(Input.Touch event, int x, int y, int r) {
        int dx = event.x - x;
        int dy = event.y - y;
        return dx*dx + dy*dy <= r*r;
    }
}
