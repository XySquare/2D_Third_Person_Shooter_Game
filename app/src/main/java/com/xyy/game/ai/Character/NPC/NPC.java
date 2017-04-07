package com.xyy.game.ai.Character.NPC;


/**
 * Created by ${XYY} on ${2016/8/24}.
 */
public interface NPC {

    String getName();

    float getX();

    float getY();

    /**
     * @return 该角色ANN神经元的数量
     */
    //int GetNumberOfWeights();

    /**
     * 设置ANN的权重
     * @param weights 各个神经元的权重
     */
    void putWeights(double[] weights);

    /**
     * @return 该角色ANN各个神经元的权重
     */
    double[] getWeights();

    /**
     * @return 该角色的适应度
     */
    float getFitness();

    /**
     * @return 该角色的存活时间（帧）
     */
    int getLiveTime();

    /**
     * 当该NPC的子代被销毁时，
     * 通过该方法向父节点回调
     * @param child 被销毁的子代
     */
    void onChildrenDestroyed(NPC child);

}
