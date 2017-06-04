package com.xyy.game.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.xyy.game.ai.Screen.Screen_MainMenu_Repository.WeaponRecord;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponDbSchema.WeaponTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ${XYY} on ${2017/4/22}.
 */

public class WeaponLab {
    private static WeaponLab sWeaponLab;

    private final SQLiteDatabase mDatabase;

    public static void build(SQLiteDatabase database){
        sWeaponLab = new WeaponLab(database);
    }

    public static WeaponLab get() {
        return sWeaponLab;
    }

    private WeaponLab(SQLiteDatabase database) {
        mDatabase = database;
    }

    public List<WeaponRecord> getWeapons() {
        List<WeaponRecord> crimes = new ArrayList<>();
        WeaponCursorWrapper cursor = queryCrimes(null, null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getWeapon());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public WeaponRecord getWeapon(UUID id) {
        WeaponCursorWrapper cursor = queryCrimes(null, WeaponTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getWeapon();
        } finally {
            cursor.close();
        }
    }

    public void addWeapon(WeaponRecord c) {
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(WeaponTable.NAME, null, values);
    }

    public void addWeapon(String className) {
        //mCrimes.add(c);
        ContentValues values = getContentValues(className);
        mDatabase.insert(WeaponTable.NAME, null, values);
    }

    public void updateWeapon(WeaponRecord crime) {
        String uuidString = crime.mUUID.toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(WeaponTable.NAME, values, WeaponTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void removeWeapon(WeaponRecord obj) {
        mDatabase.delete(WeaponTable.NAME, WeaponTable.Cols.UUID + " = ?", new String[]{obj.mUUID.toString()});
        //getPhotoFile(obj).delete();
    }

    public void removeWeapon(UUID uuid) {
        mDatabase.delete(WeaponTable.NAME, WeaponTable.Cols.UUID + " = ?", new String[]{uuid.toString()});
        //getPhotoFile(obj).delete();
    }

    /*public int count() {
        CrimeCursorWrapper cursor = queryCrimes(new String[]{"COUNT(*)"}, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }*/

    private static ContentValues getContentValues(WeaponRecord weaponRecord) {
        ContentValues values = new ContentValues();
        values.put(WeaponTable.Cols.UUID, weaponRecord.mUUID.toString());
        values.put(WeaponTable.Cols.CLASS_NAME, weaponRecord.mWeapon.getClass().getName());
        values.put(WeaponTable.Cols.LV, weaponRecord.mWeapon.getCurLv());
        values.put(WeaponTable.Cols.EXP, weaponRecord.mWeapon.getCurExp());
        return values;
    }

    private static ContentValues getContentValues(String className) {
        ContentValues values = new ContentValues();
        values.put(WeaponTable.Cols.UUID, UUID.randomUUID().toString());
        values.put(WeaponTable.Cols.CLASS_NAME, className);
        values.put(WeaponTable.Cols.LV, 1);
        values.put(WeaponTable.Cols.EXP, 0);
        return values;
    }

    private WeaponCursorWrapper queryCrimes(String[] columns, String whereClause, String[] whereArgs) {
        return new WeaponCursorWrapper(
                mDatabase.query(WeaponTable.NAME,
                        columns, // Columns - null selects all columns
                        whereClause,
                        whereArgs,
                        null, // groupBy
                        null, // having
                        null  // orderBy
                )
        );
    }
}
