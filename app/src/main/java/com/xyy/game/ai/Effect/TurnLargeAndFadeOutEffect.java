package com.xyy.game.ai.Effect;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;

/**
 * Created by ${XYY} on ${2017/6/4}.
 */

public class TurnLargeAndFadeOutEffect extends Effect {
    //计时器
    private float timer;

    private Pixmap mTexture;

    private float mScale;

    public TurnLargeAndFadeOutEffect initialize(float x, float y, int r, Pixmap texture){
        this.x = x;
        this.y = y;
        mTexture = texture;
        mScale = (float)r*2 / texture.getWidth();
        timer = 0;
        isDead = false;
        return this;
    }

    @Override
    public void update(float deltaTime) {
        //持续1秒
        timer+=deltaTime;
        if(timer>=1){
            isDead = true;//等待被移除
        }
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {
        g.drawPixmapScale(mTexture, x-offsetX, y-offsetY, mScale * timer, mScale * timer, (int) (0xFF*(1-timer)));
    }
}
