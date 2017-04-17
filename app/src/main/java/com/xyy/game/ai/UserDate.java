package com.xyy.game.ai;

import com.xyy.game.ai.Weapon.IMIDesertEagle;
import com.xyy.game.ai.Weapon.M16A4;
import com.xyy.game.ai.Weapon.RPG;
import com.xyy.game.ai.Weapon.Weapon;

/**
 * Created by ${XYY} on ${2017/4/15}.
 */

public class UserDate {
    public static Weapon[] sEquippedWeapons = new Weapon[]{
            new IMIDesertEagle(),
            new M16A4(),
            new RPG()
    };
}
