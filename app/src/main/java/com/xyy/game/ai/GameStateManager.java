package com.xyy.game.ai;

/**
 * Created by ${XYY} on ${2016/10/24}.
 */
public interface GameStateManager {

    void setState(char stateIndex);

    public void switchMap(String mapUid);

}
