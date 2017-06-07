package com.xyy.game.ai;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.util.Line;
import com.xyy.game.util.iPoint;

/**
 * Stage将通过该接口获取地图数据
 * Created by ${XYY} on ${2016/9/25}.
 */
public interface Map {

    String getUid();

    Class<? extends Character> getRootCharacter();

    Pixmap getMapBackGround();

    Environment getEnvironment();

    Line[] getLines();

    iPoint getPlayerStartPoint();

}
