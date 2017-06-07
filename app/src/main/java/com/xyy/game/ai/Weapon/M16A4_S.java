package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;

/**
 * Created by ${XYY} on ${2017/6/5}.
 */

public class M16A4_S extends Weapon {
    public M16A4_S() {
        super(
                Rarity.SR,
                "M16A4-S",//Name
                "",//Description
                3,//Damage
                1,//EnergyCost
                0.2f,//AtkDelay
                new int[]{1000,3000,5000}
        );
    }

    @Override
    public void attack(Stage stage, Character src, float dx, float dy) {
        if (src.accessEnergy(-mEnergyCost)) {
            GeneralLineAttack attackObject = new GeneralLineAttack(stage);
            attackObject.initialize(src, src.getX(), src.getY(), dx, dy, 700, src.getAtk(), 1, 30, 0xFF66CCFF);
            stage.addAtkPlayer(attackObject);
        }
    }

    @Override
    void upGrade(int lv) {
        switch (lv){
            case 2:
                mDamage = 4;
                mAtkDelay = 0.15f;
                break;
            case 3:
                mDamage = 5;
                mAtkDelay = 0.1f;
                break;
            case 4:
                mDamage = 4;
                mAtkDelay = 0.05f;
                break;
        }
    }

    @Override
    String getPixmapFileName() {
        return "M16A4";
    }
}
