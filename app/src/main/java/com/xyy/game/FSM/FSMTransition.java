package com.xyy.game.FSM;

import com.xyy.game.ai.Environment;

/**
 * 状态转换类
 * Created by ${XYY} on ${2016/9/16}.
 */
public interface FSMTransition {
    /**
     * 是否可转换至该状态
     * @return 可转换至该状态
     */
    boolean isValid(float deltaTime, Environment environment);

    /**
     * 转换后的状态
     * @return 该状态在FSM状态表中的索引
     */
    int getNextState();

    //abstract void onTransition();
}
