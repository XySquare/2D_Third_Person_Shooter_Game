package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;

/**
 * Created by ${XYY} on ${2017/4/14}.
 */

public class M16A4 extends Weapon {
    public M16A4() {
        super(
                Rarity.N,
                "M16A4",//Name
                "",//Description
                1,//Damage
                1,//EnergyCost
                0.2f,//AtkDelay
                new int[]{50,100}
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
                mDamage = 2;
                break;
            case 3:
                mDamage = 1;
                mAtkDelay = 0.1f;
                break;
        }
    }

    @Override
    String getPixmapFileName() {
        return "M16A4";
    }
}
