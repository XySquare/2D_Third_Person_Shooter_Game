package com.xyy.game.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.xyy.game.ai.Screen.Screen_MainMenu_Repository.WeaponRecord;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponDbSchema.WeaponTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ${XYY} on ${2015/11/20}.
 */

public class WeaponCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public WeaponCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public WeaponRecord getWeapon() {
        String uuidString = getString(getColumnIndex(WeaponTable.Cols.UUID));
        String class_name = getString(getColumnIndex(WeaponTable.Cols.CLASS_NAME));
        int lv =  getInt(getColumnIndex(WeaponTable.Cols.LV));
        int exp = getInt(getColumnIndex(WeaponTable.Cols.EXP));

        Weapon object = null;
        try {
            Class clazz = Class.forName(class_name);
            Constructor<Weapon> constructor = clazz.getConstructor();
            object = constructor.newInstance();
            object.initialize(lv,exp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new WeaponRecord(UUID.fromString(uuidString),object);
    }
}
