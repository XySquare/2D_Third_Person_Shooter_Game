package com.xyy.game.ai.Weapon;

import com.xyy.game.ai.Attack.GeneralLineAttack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;

/**
 * Created by ${XYY} on ${2017/4/13}.
 */

public class IMIDesertEagle extends Weapon {
    public IMIDesertEagle() {
        super(
                Rarity.N,
                "IMI Desert Eagle",
                "",
                1,
                1,
                0.5f,
                1000
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
        return "IMI_Desert_Eagle";
    }
}
