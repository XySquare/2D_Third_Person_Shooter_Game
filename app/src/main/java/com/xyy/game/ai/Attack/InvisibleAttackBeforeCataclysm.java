package com.xyy.game.ai.Attack;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Stage;
import com.xyy.game.framework.Graphics;
import com.xyy.game.util.Line;

/**
 * Created by ${XYY} on ${2015/11/20}.
 */
public class InvisibleAttackBeforeCataclysm extends Attack {
    public InvisibleAttackBeforeCataclysm(Stage stage) {
        super((char) 3, stage);
        x = 1280;
        y = 720;
        isDead = true;
    }

    @Override
    public boolean hitTestLine(Line line) {
        return false;
    }

    @Override
    public boolean hitTestCharacter(Character character) {
        if(character.getName().startsWith("NPCProducer")){
            character.setHp(0);
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void present(Graphics g, float offsetX, float offsetY) {

    }

    @Override
    public int getEnergy() {
        return 0;
    }
}
