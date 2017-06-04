package com.xyy.game.util;

/**
 * Created by berryice on 2017/4/20.
 * line: Ax + By + C = 0;
 */

public class GeneralLine {
    public final float A;
    public final float B;
    public final float C;

    public GeneralLine(float a, float b, float c) {
        this.A = a;
        this.B = b;
        this.C = c;
    }

    public static GeneralLine CreateLine(float x1, float y1, float x2, float y2){
        return new GeneralLine(y2 - y1, x1 - x2, x2 * y1 - x1 * y2);
    }

    public float k(){
        return - A / B;
    }

    public float side(float x, float y) {
        return A * x + B * y + C;
    }

    public float offset(float x, float y) {
        return (A * x + B * y + C) / (float)Math.sqrt(A * A + B * B);
    }

    public boolean kIsNull() {
        return B == 0;
    }

    public GeneralLine perpendicular(float x, float y) {
        if (kIsNull()) {
            return new GeneralLine(0,A,- A * y);
        }

        float a = B;
        float b = - A;
        float c = A * y - B * x;

        return new GeneralLine(a,b,c);
    }
}
