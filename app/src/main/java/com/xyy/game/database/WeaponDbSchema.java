package com.xyy.game.database;

/**
 * Created by ${XYY} on ${2017/4/22}.
 */

public class WeaponDbSchema {
    public static final class WeaponTable {
        public static final String NAME = "weapons";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String CLASS_NAME = "class_name";
            public static final String LV = "level";
            public static final String EXP = "experience";
        }
    }
}
