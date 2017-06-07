package com.xyy.game.ai.Character.NPC;

import android.support.annotation.NonNull;

import com.xyy.game.ANN.GenPool;
import com.xyy.game.ANN.NeuralNet;
import com.xyy.game.ai.*;
import com.xyy.game.ai.Attack.AtkInfo;
import com.xyy.game.ai.Attack.CataclysmAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.framework.Graphics;

/**
 * Created by ${XYY} on ${2016/8/24}.
 */
public final class NPCProducer extends Character implements NPC {
    private static final String TAG = "NPCProducer";

    /**
     * 基因池
     */
    private static GenPool genPool = null;
    private static GenPool genPool_Defence = null;
    private static GenPool genPool_Assist = null;
    private static GenPool genPool_Blew = null;

    private int childrenCounter;

    private float timer;

    private double[] weights;

    public NPCProducer(Stage stage) {
        super(stage,TAG);

        //初始化基因池
        genPool = GameDataManager.getGenPool("Data.dat");
        genPool_Defence = GameDataManager.getGenPool("data_defence.dat");
        genPool_Assist = GameDataManager.getGenPool("data_assist.dat");
        genPool_Blew = GameDataManager.getGenPool("data_blew.dat");

        this.r = 100;
        setMaxHp(500);
    }

    public void initialize(@NonNull NPC parent, String name, int x, int y) {
        this.parent = parent;
        //this.name = name;
        this.x = x;
        this.y = y;
        setHp(500);
        childrenCounter = 0;
        timer = 0;
    }

    @Override
    protected int onDestroyed() {
        if (timer < 0.2) return getMaxHp();
        CataclysmAttack cataclysmAttack = new CataclysmAttack(stage);
        cataclysmAttack.initialize(this, x, y);
        stage.addAtkPlayer(cataclysmAttack);
        stage.accessScore(50);
        parent.onChildrenDestroyed(this);
        return 0;
    }

    @Override
    public void onHitCharacter(com.xyy.game.ai.Character.Character character, AtkInfo attack) {

    }

    @Override
    public void onHitByCharacter(Character character, AtkInfo attack) {
        if (getHp() <= 0) {
            character.addBuff(1);
        }
        //character.accessEnergy(attack.getEnergy()*2);//返还2倍能量
    }

    @Override
    public void putWeights(double[] weights) {

    }

    @Override
    public double[] getWeights() {
        return new double[0];
    }

    @Override
    public float getFitness() {
        return 0;
    }

    @Override
    public int getLiveTime() {
        return 0;
    }

    @Override
    public void updateInner(float deltaTime, Environment environment) {
        if (timer < 0.2)
            timer += deltaTime;
        //随机生成敌人（调试）
        if (Math.random() > 0.99 && childrenCounter < 3) {
            double rand = Math.random();
            if (rand > 0.75) {
                SimpleCharacter simpleCharacter = new SimpleCharacter(stage);//暂时没使用回收池
                simpleCharacter.initialize(this, (int) x, (int) y);
                double[] gen = genPool.get();
                simpleCharacter.putWeights(gen);
                stage.addHostile(simpleCharacter);
                stage.addToTrackList(simpleCharacter);
            } else if (rand > 0.5){
                DefenceCharacter defenceCharacter = new DefenceCharacter(stage);
                defenceCharacter.initialize(this, (int) x, (int) y);
                defenceCharacter.putWeights(genPool_Defence.get());
       //         defenceCharacter.putWeights(weights);
                stage.addHostile(defenceCharacter);
                stage.addToTrackList(defenceCharacter);
            } else if (rand > 0.25){
                AssistCharacter assistCharacter = new AssistCharacter(stage);
                assistCharacter.initialize(this, (int)x, (int)y);
                assistCharacter.putWeights(genPool_Assist.get());
                stage.addHostile(assistCharacter);
                stage.addToTrackList(assistCharacter);
            } else {
                BlewCharacter blewCharacter = new BlewCharacter(stage);
                blewCharacter.initialize(this, (int)x, (int)y);
                blewCharacter.putWeights(genPool_Blew.get());
                stage.addHostile(blewCharacter);
                stage.addToTrackList(blewCharacter);
            }
            childrenCounter++;
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        //g.drawCircle(x-offsetX,y-offsetY,100,0xFF66CCFF);
        g.drawPixmap(Assets.NPCProducer, x - offsetX - 100, y - offsetY - 100);
    }

    @Override
    public void onChildrenDestroyed(NPC child) {

        childrenCounter--;
        if (child.getName().startsWith("SimpleCharacter")) {
            genPool.inster(child.getWeights(), child.getFitness(), child.getLiveTime());
        } else if (child.getName().startsWith("DefenceCharacter")) {
            genPool_Defence.inster(child.getWeights(), child.getFitness(), child.getLiveTime());
        }
        parent.onChildrenDestroyed(child);
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    @Override
    public boolean canBeDefended() {
        return false;
    }

}
