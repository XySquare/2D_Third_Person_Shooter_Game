package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralContinuousAttack;
import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;
import com.xyy.game.util.GeneralLine;

public class BlackHole extends Weapon {
    public BlackHole() {
        super(
                Rarity.SSR,
                "Black Hole",
                "",
                2,
                5,
                1f,
                new int[]{50, 100, 200, 400}
        );
    }

    @Override
    public void attack(Stage stage, Character src, float dx, float dy) {
        double alpha = Math.atan2(dy, dx);
        if (src.accessEnergy(-mEnergyCost)) {
            for (int i = 0; i < 5; i++) {
                GeneralLineAttack attack = new GeneralLineAttack(stage);
                double theta = alpha - 0.1 + 0.05 * i;
                attack.initialize(src, src.getX(), src.getY(), (float)Math.cos(theta), (float)Math.sin(theta), 700, src.getAtk(), getDamage(), 30, 0xFF66CCFF);
                stage.addAtkPlayer(attack);
            }
        }
    }

    @Override
    void upGrade(int lv) {
        switch (lv){
            case 2:
                mDamage = 4;
                mAtkDelay = 0.9f;
                break;
            case 3:
                mDamage = 6;
                mAtkDelay = 0.8f;
                break;
            case 4:
                mDamage = 8;
                mAtkDelay = 0.7f;
            case 5:
                mDamage = 10;
                mAtkDelay = 0.6f;
                break;
        }
    }

    @Override
    String getPixmapFileName() {
        return "Black_Hole";
    }
}
