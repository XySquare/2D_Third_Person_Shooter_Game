package com.xyy.game.FSM;

import com.xyy.game.ai.Environment;

/**
 * 状态机状态类
 * Created by ${XYY} on ${2016/9/16}.
 */
public interface FSMState {
    /**
     * 进入该状态时调用
     */
    void onEnter();

    /**
     * 每帧都将调用
     */
    void onUpdate(float deltaTime, Environment environment);

    /**
     * 退出当前状态时调用
     */
    void onExit();

    /**
     * 返回当前状态可进行的状态转换
     * @return 状态转换线性表
     */
    FSMTransition[] getTransitions();
}
