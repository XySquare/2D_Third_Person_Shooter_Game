package com.xyy.game.FSM;

import com.xyy.game.ai.Environment;


/**
 * 有限状态机
 * Created by ${XYY} on ${2016/9/16}.
 */
public class FiniteStateMachine {
    /**
     * 状态线性表
     */
    private FSMState[] states;
    /**
     * 当前活动状态
     */
    private FSMState activeState;

    /**
     * 初始化有限状态机，
     * 以状态列表中的首项为初始状态
     * @param states 状态列表
     */
    public FiniteStateMachine(FSMState[] states){
        this.states = states;
        activeState = states[0];
        activeState.onEnter();
    }

    public void update(float deltaTime, Environment environment){
        //检查是否进行状态转换
        FSMTransition[] transitions = activeState.getTransitions();
        for (FSMTransition transition : transitions) {
            if (transition.isValid(deltaTime,environment)) {
                activeState.onExit();
                activeState = states[transition.getNextState()];
                activeState.onEnter();
                break;
            }
        }
        //刷新活动状态
        activeState.onUpdate(deltaTime,environment);
    }
}
