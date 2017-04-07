package com.xyy.game.ai;

import com.xyy.game.ai.Character.Player;
import com.xyy.game.ai.Screen.GameScreenOperation;
import com.xyy.game.component.CircleButton;
import com.xyy.game.component.ProcessingAnimation;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${XYY} on ${2015/11/20}.
 */
public class GameState_Running extends GameState {
    //左摇杆
    private Controller controller;
    //右摇杆
    private Controller atkCtrl;
    //攻击延迟
    private float atkDelay;
    //攻击延迟计时器
    private float delayTimer;
    //HP,能量进度条
    private ProcessingAnimation hpBar, energyBar;
    //暂停按钮
    private CircleButton pauseBt;
    //玩家得分
    private int score;

    //private byte[] numbers;

    public GameState_Running(GameScreenOperation gameScreen, Stage stage) {
        super(gameScreen,stage);
        controller = new Controller(150,570,100);
        atkCtrl = new Controller(1280-150,570,100);
        atkDelay = 0.1f;
        delayTimer = 0;

        hpBar = new ProcessingAnimation(10,10,320,40,0xFFFFFFFF);
        energyBar = new ProcessingAnimation(10,52,320,12,0xFF66CCFF);

        pauseBt = new CircleButton(1280-40,40,36,0x7F31547C,Assets.pauseIco);

        score = 0;

        //numbers = new byte[10];
    }

    @Override
    public void enter() {
        pauseBt.initialize(0);
    }

    @Override
    public void update(float deltaTime) {
        Player player = stage.getPlayer();

        //更新暂停按钮
        pauseBt.update(deltaTime);

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
            controller.update(event);
            atkCtrl.update(event);
        }

        //玩家操控左摇杆移动
        player.setDirection(controller.getLX(), controller.getLY());

        //玩家操控右摇杆攻击
        if(atkCtrl.getLX()!=0 && atkCtrl.getLY()!=0) {
            delayTimer += deltaTime;
            if (delayTimer >= atkDelay) {
                delayTimer -= atkDelay;
                //将AttackObject(Player)置于舞台，现在暂时没有使用贴图
                stage.PlayerAttack(atkCtrl.getLX(), atkCtrl.getLY());
            }
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
        ArrayList<BuffManager.BuffRecord> buffList = stage.getPlayer().getBuffList();
        int len = buffList.size();
        for(int i=0;i<len;i++){
            BuffManager.BuffRecord buffRecord = buffList.get(i);
            //绘制图标
            g.drawPixmap(Assets.ico,400+i*80,10,60*buffRecord.getBuffIco(),0,60,60);
            //当叠加层数大于一时才绘制数字
            int counter = buffRecord.getCounter();
            if(counter>1)
                g.drawText(String.valueOf(counter),430+i*78,60,0xFFFFFFFF,24);
            //绘制环形进度条的半透明背景
            g.drawRing(430+i*80, 40, 33, 0, 3600, 0x7FFFFFFF,6);
            //绘制环形进度条
            g.drawRing(430+i*80, 40, 33, 360 - 360*buffRecord.getRemainTimeRatio() -90, 360*buffRecord.getRemainTimeRatio(), 0xFFFFFFFF,6);
        }
        /**
         * 绘制追踪箭头
         */
        ArrayList<GameObject> trackList = stage.getTrackList();
        len = trackList.size();
        for(int i=0;i<len;i++){
            GameObject object = trackList.get(i);
            //指向目标的向量
            float XComponentToTarget = object.getX() - stage.getPlayer().getX();
            float YComponentToTarget = object.getY() - stage.getPlayer().getY();
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

        g.drawText("score",1280-402,73,0xFFFFFFFF,16);

        int x = score;
        for(int i=9;i>=0;i--){
            int num = x % 10;
            x/=10;
            g.drawPixmap(Assets.numbers5_26_2, 1280-400 + 40 + i*26, 40, num*26, 0, 26, 33);
        }
    }

    @Override
    public void onBack() {
        gameScreen.setState(GameState.PAUSED);
    }

}
