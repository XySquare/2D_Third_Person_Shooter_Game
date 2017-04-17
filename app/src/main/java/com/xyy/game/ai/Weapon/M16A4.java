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
                2500
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
    String getPixmapFileName() {
        return "M16A4";
    }
}
