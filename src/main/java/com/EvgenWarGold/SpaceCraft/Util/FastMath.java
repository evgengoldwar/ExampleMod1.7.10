package com.EvgenWarGold.SpaceCraft.Util;

public class FastMath {

    public static final double EPSILON_D = 2.220446049250313E-16;
    public static final float EPSILON_F = 1.1920929E-7f;
    public static final float PI = (float) java.lang.Math.PI;
    public static final float TWO_PI = ((float) java.lang.Math.PI * 2F);
    public static final float HALF_PI = ((float) java.lang.Math.PI / 2F);
    public static final float QUARTER_PI = ((float) java.lang.Math.PI / 4F);
    public static final double TWO_PI_D = 6.283185307179586476925286766559;
    public static final double HALF_PI_D = 1.5707963267948966192313216916398;
    public static final double QUARTER_PI_D = 0.78539816339744830961566084581988;

    /**
     * Java implementation of the fast square root algorithm from Quake III Arena
     *
     * @see <a href=https://stackoverflow.com/a/11513345>https://stackoverflow.com/a/11513345</a>
     */
    public static float invSqrtF(float f) {
        float xHalf = 0.5f * f;
        int i = Float.floatToIntBits(f);
        i = 0x5f3759df - (i >> 1);
        f = Float.intBitsToFloat(i);
        f *= 1.5f - xHalf * f * f;
        return f;
    }

    /**
     * Java implementation of the fast square root algorithm from Quake III Arena
     *
     * @see <a href=https://stackoverflow.com/a/11513345>https://stackoverflow.com/a/11513345</a>
     */
    public static double invSqrtD(double d) {
        double xHalf = 0.5 * d;
        long i = Double.doubleToLongBits(d);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        d = Double.longBitsToDouble(i);
        d *= 1.5 - xHalf * d * d;
        return d;
    }

    public static float tan(float f) {
        if (f == Float.NaN || f == Float.NEGATIVE_INFINITY || f == Float.POSITIVE_INFINITY) return Float.NaN;
        float cos = cos(f);
        return cos == 0.0f ? Float.NaN : sin(f) / cos;
    }

    public static float cot(float f) {
        if (f == Float.NaN || f == Float.NEGATIVE_INFINITY || f == Float.POSITIVE_INFINITY) return Float.NaN;
        float sin = sin(f);
        return sin == 0.0f ? Float.NaN : cos(f) / sin;
    }

    public static float asin(float fValue) {
        if (-1.0f >= fValue) {
            return -FastMath.HALF_PI;
        }
        if (fValue < 1.0f) {
            return (float) java.lang.Math.asin(fValue);
        }
        return FastMath.HALF_PI;
    }

    public static float acos(float fValue) {
        if (-1.0f >= fValue) {
            return FastMath.PI;
        }
        if (fValue < 1.0f) {
            return (float) java.lang.Math.acos(fValue);
        }
        return 0.0f;
    }

    private static float[] a = new float[65536];

    public static float sin(float f) {
        return a[(int) (f * 10430.378F) & '\uffff'];
    }

    public static float cos(float f) {
        return a[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    static {
        for (int i = 0; i < 65536; i++) {
            a[i] = (float) java.lang.Math.sin(java.lang.Math.PI * 2.0D * i / 65536.0D);
        }
    }
}
