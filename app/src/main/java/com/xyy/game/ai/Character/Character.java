package com.xyy.game.ai.Character;

import android.support.annotation.NonNull;
import android.util.Log;

import com.xyy.game.ai.Attack.AtkInfo;
import com.xyy.game.ai.BuffManager;
import com.xyy.game.ai.Character.NPC.Defended;
import com.xyy.game.ai.Character.NPC.Defender;
import com.xyy.game.ai.Character.NPC.NPC;
import com.xyy.game.ai.Environment;
import com.xyy.game.ai.GameObject;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家与怪物的基类
 * Created by ${XYY} on ${2015/3/5}.
 */
public abstract class Character extends GameObject implements Defended {
    //移动速度
    public static final char V = 0;
    //攻击力
    public static final char ATK = 1;
    //防御力
    public static final char DEF = 2;
    //最大生命值
    public static final char XHP = 3;
    //战斗中恢复
    public static final char REC = 4;

    //产生该对象的父角色
    protected NPC parent;
    //碰撞检测半径
    protected int r;
    //名称
    protected String name;
    //移动速度
    //private int v;
    //当前生命值
    private int hp;
    //最大生命值
    //private int maxHp;
    //攻击力
    //private int atk;
    //防御力
    //private int def;
    //额外伤害（无视防御）（未实现，可能放弃该属性，而采用其他方法）
    //protected int extDamage;
    //伤害减免
    protected int damReduce;
    //伤害减免（百分比）
    protected float damReduceByPercentage;
    //战斗中恢复
    //private int recovery;
    //当前能量
    protected int energy;
    //最大能量
    protected int maxEnergy;

    /**
     * 原始属性
     */
    private int[] primitiveProperty;
    /**
     * 叠加Buff后的数值
     */
    private int[] currentProperty;
    /**
     * Buff管理
     */
    private BuffManager buffManager;
    /**
     * 战斗中恢复计时器
     */
    private float timerRecovery;

    private Defender mDefender;

    public Character(Stage stage) {
        super(stage);

        primitiveProperty = new int[5];
        currentProperty = new int[5];

        buffManager = new BuffManager(primitiveProperty,currentProperty);

        timerRecovery = 0;

    }

    public void initialize(@NonNull NPC parent, String name, int x, int y){
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /*
    **Getter
    */

    public int getR() {
        return r;
    }

    public String getName() {
        return name;
    }

    public int getV() {
        return currentProperty[Character.V];
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return currentProperty[Character.XHP];
    }

    public int getAtk() {
        return currentProperty[Character.ATK];
    }

    public int getDef() {
        return currentProperty[Character.DEF];
    }

    /*public int getExtDamage() {
        return extDamage;
    }*/

    public int getDamReduce() {
        return damReduce;
    }

    public float getDamReduceByPercentage() {
        return damReduceByPercentage;
    }

    public int getRecovery() {
        return currentProperty[Character.REC];
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public ArrayList<BuffManager.BuffRecord> getBuffList() {
        return buffManager.getBuffList();
    }

    /**
     * 增/减角色Hp值，受防御减免
     * @param val val值应小于0
     */
    public void accessHp_Defence(int val){
        int hp = this.hp;
        if(hp>0) {
            final float n = 0.1f;
            final int def = getDef();
            hp += (int) (val * (1 - (n * def) / (1 + n * def)));
            if (hp <= 0) {
                if (defender() != null) {
                    defender().onDefendedDestroyed(this);
                }
                hp = onDestroyed();
            }
            this.hp = hp;
        }
    }

    /**
     * 直接增/减角色的Hp值
     * @param val val<0时为减少
     */
    public void accessHp(int val){
        int hp = this.hp;
        if(hp>0) { //避免角色死亡后重复回调
            hp += val;
            if (hp <= 0) {
                if (defender() != null) {
                    defender().onDefendedDestroyed(this);
                }
                hp = onDestroyed();
            } else if (hp > getMaxHp()) {
                hp = getMaxHp();
            }
            this.hp = hp;
        }
    }

    /**
     * 直接增/减角色的能量值
     * @param val val<0时为减少
     */
    public boolean accessEnergy(int val){
        int temp = energy + val;
        if(temp > 0) {
            this.energy = temp > maxEnergy ? maxEnergy : temp;
            return true;
        }
        else
            return false;
    }

    /*
    **Setter
    */

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setHp(int val){
        if(hp>0 && val <= 0) { //避免角色死亡后重复回调
            if (defender() != null) {
                defender().onDefendedDestroyed(this);
            }
            hp = onDestroyed();
        }
        else{
            final int maxHp = getMaxHp();
            hp = val>maxHp ? maxHp : val;
        }
    }

    public void setV(int v) {
        //this.v = v;
        primitiveProperty[Character.V] = v;
        buffManager.updatePropertyType(Character.V);
    }

    public void setAtk(int atk) {
        //this.atk = atk;
        primitiveProperty[Character.ATK] = atk;
        buffManager.updatePropertyType(Character.ATK);
    }

    public void setDef(int def) {
        //this.def = def;
        primitiveProperty[Character.DEF] = def;
        buffManager.updatePropertyType(Character.DEF);
    }

    public void setMaxHp(int maxHp) {
        //this.maxHp = maxHp;
        primitiveProperty[Character.XHP] = maxHp;
        buffManager.updatePropertyType(Character.XHP);
    }

    public void setRecovery(int recovery) {
        //this.recovery = recovery;
        primitiveProperty[Character.REC] = recovery;
        buffManager.updatePropertyType(Character.REC);
    }

    public void setDamReduceByPercentage(float damReduceByPercentage) {
        this.damReduceByPercentage = damReduceByPercentage;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public void addBuff(int uid){
        Log.v(name,name + " 获得BUFF(uid="+uid+")");
        buffManager.addBuff(uid);
    }

    /**
     * 在舞台（Stage）更新（update）时，此方法将被同时调用
     * @param deltaTime 两帧之间的时间间隔(s)
     */
    public final void update(float deltaTime, Environment environment){
        /**
         * 战斗中恢复，每2s
         */
        int rec = getRecovery();
        if(rec>0) {
            timerRecovery += deltaTime;
            if (timerRecovery >= 2) {
                timerRecovery -= 2;
                accessHp(rec);
                if (rec > 0) Log.v(name, name + "Hp恢复" + rec + "点");
            }
        }
        /**
         * 更新Buff
         */
        buffManager.update(deltaTime);
        /**
         * 其他动作
         */
        updateInner(deltaTime, environment);
    }

    protected abstract void updateInner(float deltaTime, Environment environment);

    /**
     * 以特定偏移量绘制攻击
     * @param g 绘图接口
     * @param offsetX X偏移量
     * @param offsetY Y偏移量
     */
    public abstract void present(Graphics g, float offsetX, float offsetY);

    /**
     * 当角色HP<=0时，该方法将被调用
     * @return HP值
     */
    protected abstract int onDestroyed();

    /**
     * 当该角色产生的攻击对象命中其他角色时，
     * 攻击对象通过该方法向其父级角色回调
     * @param character 击中的角色
     */
    public abstract void onHitCharacter(Character character, AtkInfo attack);

    /**
     * 当攻击对象命中该角色时，
     * 攻击对象通过该方法向被击中角色回调
     * @param character 发起攻击的角色
     */
    public abstract void onHitByCharacter(Character character, AtkInfo attack);

    /**
     * 返回该对象是否应被移除
     * @return true=该对象将被移除
     */
    public final boolean isDead() {
        return hp<=0;
    }

    @Override
    public Defender defender() {
        return mDefender;
    }

    @Override
    public void onDefendedBy(Defender defender) {
        mDefender = defender;
    }

    @Override
    public void onLostDefenceBy(Defender defender) {
        if (mDefender == defender) {
            mDefender = null;
        }
    }
}
