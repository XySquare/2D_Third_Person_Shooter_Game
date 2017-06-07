package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralContinuousAttack;
import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;

public class RPD extends Weapon {
    public RPD() {
        super(
                Rarity.SR,
                "RPD",
                "",
                2,
                3,
                0.9f,
                new int[]{50, 100, 200}
        );
    }

    @Override
    public void attack(Stage stage, Character src, float dx, float dy) {
        if (src.accessEnergy(-mEnergyCost)) {
            GeneralContinuousAttack attack = new GeneralContinuousAttack(stage, 3, src);
            attack.initialize(src, src.getX(), src.getY(), dx, dy, 700, src.getAtk(), getDamage(), 30, 0xFF66CCFF);
            stage.addAtkPlayer(attack);
        }
    }

    @Override
    void upGrade(int lv) {
        switch (lv){
            case 2:
                mDamage = 4;
                mAtkDelay = 0.8f;
                break;
            case 3:
                mDamage = 6;
                mAtkDelay = 0.7f;
            case 4:
                mDamage = 8;
                mAtkDelay = 0.6f;
                break;
        }
    }

    @Override
    String getPixmapFileName() {
        return "RPD";
    }
}
