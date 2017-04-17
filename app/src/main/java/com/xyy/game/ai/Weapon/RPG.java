package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;

/**
 * Created by ${XYY} on ${2017/4/15}.
 */

public class RPG extends Weapon {
    public RPG() {
        super(
                Rarity.R,
                "RPG",
                "",
                10,
                5,
                1,
                5000
        );
    }

    @Override
    public void attack(Stage stage, Character src, float dx, float dy) {
        if (src.accessEnergy(-mEnergyCost)) {
            GeneralLineAttack attackObject = new GeneralLineAttack(stage);
            attackObject.initialize(src, src.getX(), src.getY(), dx, dy, 500, src.getAtk()*10, mEnergyCost, 30, 0xFF66CCFF);
            stage.addAtkPlayer(attackObject);
        }
    }

    @Override
    String getPixmapFileName() {
        return "RPG";
    }
}
