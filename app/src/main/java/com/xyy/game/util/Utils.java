package com.xyy.game.util;

import java.util.Random;

/**
 * Created by ${XYY} on ${2016/5/15}.
 */
public class Utils {
    private static final Random r = new Random();

    //----------------------------------------------------------------------------
    //	some random number functions.
    //----------------------------------------------------------------------------

    //returns a random integer between x and y
    public static int RandInt(int x, int y) {
        return r.nextInt(y-x+1)+x;
    }

    //returns a random floating point number between zero and 1
    public static double RandFloat() {
        return r.nextDouble();
    }

    //returns a random bool
    static boolean RandBool(){
        return RandInt(0, 1) == 1;
    }

    //returns a random float in the range -1 < n < 1
    public static double RandomClamped() {
        return RandFloat() - RandFloat();
    }

    //-----------------------------------------------------------------------
    //	some handy little functions
    //-----------------------------------------------------------------------

    //clamps the first argument between the second two
    double Clamp(double arg, float min, float max)
    {
        if (arg < min)
        {
            return min;
        }
        else if (arg > max)
        {
            return max;
        }
        return arg;
    }
}