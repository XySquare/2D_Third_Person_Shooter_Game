package com.xyy.game.ai;


import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ${XYY} on ${2016/9/7}.
 */
public class BuffManager {
    public final class BuffRecord{
        private Buff buff;
        /**
         * 剩余时间
         */
        private float timer;
        /**
         * 叠加层数
         */
        private int counter;
        /**
         * 剩余时间比
         */
        private float remainTimeRatio;

        private void initialize(Buff buff){
            this.buff = buff;
            timer = buff.duration;
            counter = 1;
        }

        private boolean add(){
            //若该Buff小于最大叠加层数，或叠加无上限...
            if(counter<buff.max || buff.max==0){
                //Buff叠加一层
                counter++;
                //刷新Buff时间
                timer = buff.duration;
                Log.v("BuffMag","["+buff.name+"]叠加 "+counter+"/"+buff.max);
                //需刷新当前属性
                return true;
            }
            //若叠加以达上限，如果Buff可刷新
            else if(buff.refresh){
                //刷新Buff时间
                timer = buff.duration;
                Log.v("BuffMag","["+buff.name+"]已达上限，刷新时间");
            }
            else{
                Log.v("BuffMag","["+buff.name+"]已达上限，且被禁止刷新时间，获得的Buff无效");
            }
            //无需刷新当前属性
            return false;
        }

        private boolean accessTime(float deltaTime){
            if (timer > 0) {
                timer -= deltaTime;
                remainTimeRatio = timer/buff.duration;
                if (timer <= 0)
                    return true;
            }
            return false;
        }

        public float getRemainTimeRatio() {
            return remainTimeRatio;
        }

        public int getCounter() {
            return counter;
        }

        public int getBuffIco(){
            return buff.ico;
        }

        private boolean equals(int uid){
            return buff.equals(uid);
        }
    }

    /**
     * 游戏中出现的全部Buff
     */
    private static Buff[] buffs;
    /**
     * 角色当前持有的Buff
     */
    private ArrayList<BuffRecord> buffList;
    /**
     * 原始属性
     */
    private int[] primitiveProperty;
    /**
     * 叠加Buff后的数值
     */
    private int[] currentProperty;
    /**
     * Buff叠加的属性统计（数值）
     */
    private int[] values;
    /**
     * Buff叠加的属性统计（百分比）
     */
    private float[] percentages;

    /**
     * 初始化Buff
     * @param buffList 从World中获得
     */
    public static void setBuffs(Buff[] buffList){
        BuffManager.buffs = buffList;
    }

    public BuffManager(int[] primitiveProperty, int[] currentProperty){
        buffList = new ArrayList<>(buffs.length);

        values = new int[5];
        percentages = new float[]{1,1,1,1,1};

        this.primitiveProperty = primitiveProperty;
        this.currentProperty = currentProperty;
    }

    /**
     * 通过uid为该角色添加Buff
     * @param uid Buff对应的uid
     */
    public void addBuff(int uid){
        //查找当前是否存在相同的Buff
        int index;
        for(index=0;index<buffList.size();index++){
            if(buffList.get(index).equals(uid)) break;
        }
        //如果不存在，则新增
        if(index==buffList.size()){
            Buff buff = getBuff(uid);
            BuffRecord buffRecord = new BuffRecord();
            buffRecord.initialize(buff);
            buffList.add(buffRecord);
            //则刷新当前属性
            int len = buff.type.length;
            for (short i = 0; i < len; i++) {
                char type = buff.type[i];
                values[type] += buff.value[i];
                percentages[type] += buff.percentage[i];

                currentProperty[type] = (int) (primitiveProperty[type] * percentages[type] + values[type]);
            }
            Log.v("BuffMag","获得["+buff.name+"]");
        }
        //如果存在，则尝试叠加
        else if(buffList.get(index).add()) {
            //如果叠加成功
            Buff buff = getBuff(uid);
            //则刷新当前属性
            int len = buff.type.length;
            for (short i = 0; i < len; i++) {
                char type = buff.type[i];
                values[type] += buff.value[i];
                percentages[type] += buff.percentage[i];

                currentProperty[type] = (int) (primitiveProperty[type] * percentages[type] + values[type]);
            }
        }
    }

    /**
     * 更新Buff剩余时间，移除过期Buff
     */
    public void update(float deltaTime) {
        for(int index=0;index<buffList.size();index++){
            BuffRecord buffRecord = buffList.get(index);
            //若当前Buff剩余时间为0
            if (buffRecord.accessTime(deltaTime)) {
                //刷新当前属性
                Buff buff = buffRecord.buff;
                int count = buffRecord.counter;
                int len = buff.type.length;
                for (short j = 0; j < len; j++) {
                    char type = buff.type[j];
                    values[type] -= (buff.value[j] * count);
                    percentages[type] -= (buff.percentage[j] * count);

                    currentProperty[type] = (int) (primitiveProperty[type] * percentages[type] + values[type]);
                }

                buffList.remove(index);
            }
        }
    }

    public void updatePropertyType(char type){
        currentProperty[type] = (int) (primitiveProperty[type]*percentages[type]+values[type]);
    }

    public ArrayList<BuffRecord> getBuffList() {
        return buffList;
    }

    /**
     * 通过uid获得Buff
     * @param uid Buff对应的uid
     * @return Buff对象
     */
    private Buff getBuff(int uid){
        for (Buff buff:buffs) {
            if(buff.equals(uid)) return buff;
        }
        Log.e("Buff", "无法找到对应 Buff uid = " + uid);
        return nullBuff;
    }

    //一个空Buff
    private Buff nullBuff = new Buff(Buff.NULL, new char[]{}, new int[]{}, new float[]{}, 0, 0, false, -1, "");

}
