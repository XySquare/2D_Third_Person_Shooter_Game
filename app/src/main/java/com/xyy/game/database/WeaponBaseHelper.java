package com.xyy.game.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xyy.game.ai.Screen.Screen_MainMenu_Repository;
import com.xyy.game.ai.Weapon.IMIDesertEagle;
import com.xyy.game.ai.Weapon.M16A4;
import com.xyy.game.ai.Weapon.RPG;
import com.xyy.game.database.WeaponDbSchema.WeaponTable;

import java.util.UUID;

/**
 * Created by ${XYY} on ${2017/4/22}.
 */

public class WeaponBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weaponBase.db";

    public WeaponBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        WeaponLab.build(db);
        WeaponLab weaponLab = WeaponLab.get();

        db.execSQL("create table " + WeaponTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                WeaponTable.Cols.UUID + ", " +
                WeaponTable.Cols.CLASS_NAME + ", " +
                WeaponTable.Cols.LV + ", " +
                WeaponTable.Cols.EXP +
                ")"
        );
        weaponLab.addWeapon(IMIDesertEagle.class.getName());
        weaponLab.addWeapon(M16A4.class.getName());
        weaponLab.addWeapon(RPG.class.getName());

        Log.i("WeaponBaseHelper","onCreate");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        WeaponLab.build(db);
        Log.i("WeaponBaseHelper","onOpen");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
