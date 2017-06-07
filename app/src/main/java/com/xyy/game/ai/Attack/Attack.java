package com.xyy.game.ai.Attack;

import com.xyy.game.ai.*;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

/**
 * 攻击对象基类
 * Created by ${XYY} on ${2016/7/30}.
 */
public abstract class Attack extends GameObject implements AtkInfo{
    //产生该对象的父角色
    protected Character parent;

    //同类攻击的唯一标志，垃圾回收标志……现在没用了
    protected final char flag;

    //标记是否将该对象移除(Default = false)
    protected boolean isDead;

    public Attack(char flag, Stage stage){
        super(stage);
        this.flag = flag;
        isDead = false;
    }

    /**
     * Stage将调用该方法
     * 进行本攻击对象与地图边的碰撞检测
     * @param line 参与碰撞检测的边
     * @return 是否产生碰撞（true = 是）
     */
    public abstract boolean hitTestLine(Line line);

    /**
     * Stage将调用该方法
     * 进行此攻击对象与舞台角色（玩家、NPC）的碰撞检测
     * @param character 参与碰撞检测的角色
     * @return 是否产生碰撞（true = 是）
     */
    public abstract boolean hitTestCharacter(Character character);

    /**
     * 在舞台（Stage）更新（update）时，此方法将被同时调用
     * @param deltaTime 两帧之间的时间间隔(s)
     */
    public abstract void update(float deltaTime);

    /**
     * 以特定偏移量绘制攻击
     * @param g 绘图接口
     * @param offsetX X偏移量
     * @param offsetY Y偏移量
     */
    public abstract void present(Graphics g, float offsetX, float offsetY);

    /**
     * 将该攻击对象从舞台上移除，
     * 不建议使用，尽量在攻击对象内部修改标志
     */
    public final void remove(){
        isDead = true;
    }

    /**
     * 返回该对象是否应被移除
     * @return true=该对象将被移除
     */
    public final boolean isDead() {
        return isDead;
    }

    /**
     * 返回该攻击对象类型的标志
     * @return 该攻击对象的唯一标志
     */
    public final char getFlag() {
        return flag;
    }
}
