package com.xyy.game.ai.Attack;
import android.support.annotation.NonNull;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

/**
 * Created by berryice on 2017/6/7.
 */

public class GeneralContinuousAttack extends Attack{

    private Character src;
    private GeneralLineAttack[] mLineAttackArray;
    private float mTime = 0;
    private int mCount = 0;

    private Character parent;
    private float dx, dy;
    private int v;
    private int dam;
    private int eng;
    private int len;
    private int color;

    public GeneralContinuousAttack(Stage stage, int count, Character src) {
        super((char)0, stage);
        mLineAttackArray = new GeneralLineAttack[count];
        for (int i = 0; i < count; i++) {
            mLineAttackArray[i] = new GeneralLineAttack(stage);
        }
        this.src = src;
    }

    public void initialize(@NonNull Character parent, float x, float y, float dx, float dy, int v, int dam, int eng, int len, int color){
//        for (GeneralLineAttack attack : mLineAttackArray) {
//            attack.initialize(parent, x, y, dx, dy, v, dam, eng, len, color);
//        }
        this.parent = parent;
        this.dx = dx;
        this.dy = dy;
        this.v = v;
        this.dam = dam;
        this.eng = eng;
        this.len = len;
        this.color = color;
    }

    public GeneralLineAttack[] getLineAttackArray() {
        return mLineAttackArray;
    }

    @Override
    public boolean hitTestLine(Line line) {
        return false;
    }

    @Override
    public boolean hitTestCharacter(Character character) {
        return false;
    }

    @Override
    public void update(float deltaTime) {
        this.mTime += deltaTime;
        if (mCount < mLineAttackArray.length) {
            if (mTime > 0.1f) {
                mLineAttackArray[mCount].initialize(parent, src.getX(), src.getY(), dx, dy, v, dam, eng, len, color);
                stage.addAtkPlayer(mLineAttackArray[mCount]);
                mCount++;
                mTime -= 0.1f;
            }
        }

        int c = 0;
        for (GeneralLineAttack attack : mLineAttackArray) {
            if (attack == null || attack.isDead) c ++;
        }
        if (c == mLineAttackArray.length) isDead = true;
    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {

    }

    @Override
    public int getEnergy() {
        return 0;
    }
}
