package com.xyy.game.ai;

import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Character.NPC.RootCharacter;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.util.iPoint;

/**
 * @see com.xyy.game.ai.WorldData
 * Created by ${XYY} on ${2016/8/26}.
 */
public final class WorldData_0 implements WorldData {
    @Override
    public String getUid() {
        return "map00";
    }

    @Override
    public Character getRootCharacter(Stage stage) {
        return new RootCharacter(stage);
    }

    @Override
    public iPoint[] getMapPoints() {
        return new iPoint[]{
               /*new iPoint(600,0), new iPoint(0,300),new iPoint(300,700),new iPoint(350,900),new iPoint(0,500),
                new iPoint(200,1200),new iPoint(500,800),new iPoint(600,1500),new iPoint(700,100),new iPoint(600,0),
                new iPoint(1000,0),new iPoint(2000,50)*/
                /*new iPoint(179,550),new iPoint(885,194),new iPoint(2009,297),new iPoint(2468,1256),new iPoint(2681,1325),
                new iPoint(2861,1153),new iPoint(2886,389),new iPoint(3272,213),new iPoint(3606,271),new iPoint(3698,855),
                new iPoint(3698,1751),new iPoint(3143,2078),new iPoint(1972,1751),new iPoint(752,2075),new iPoint(205,1663),
                new iPoint(179,550),new iPoint(587,888),new iPoint(976,594),new iPoint(1616,708),new iPoint(1814,1164),
                new iPoint(1017,1527),new iPoint(587,888)*/
                new iPoint(1706,1405),new iPoint(1604,1526),new iPoint(1420,1635),new iPoint(1320,1782),new iPoint(806,2281),new iPoint(754,2383),
                new iPoint(251,2916),new iPoint(576,3172),new iPoint(639,3201),new iPoint(1034,3237),new iPoint(1116,3298),new iPoint(1181,3352),
                new iPoint(1242,3424),new iPoint(1279,3461),new iPoint(1411,3465),new iPoint(1578,3404),new iPoint(1633,3500),new iPoint(1732,3595),
                new iPoint(1748,3680),new iPoint(1717,3695),new iPoint(1745,3808),new iPoint(1810,3892),new iPoint(1875,3997),new iPoint(1973,4122),
                new iPoint(2255,4161),new iPoint(2396,4261),new iPoint(2988,3524),new iPoint(2940,3472),new iPoint(2903,3303),new iPoint(2973,3255),
                new iPoint(3086,3307),new iPoint(3092,3378),new iPoint(3172,3437),new iPoint(3233,3589),new iPoint(3326,3643),new iPoint(3305,3680),
                new iPoint(3350,3684),new iPoint(3565,3548),new iPoint(3613,3589),new iPoint(3719,3600),new iPoint(4812,3211),new iPoint(4955,2886),
                new iPoint(5083,2773),new iPoint(5061,2635),new iPoint(4697,2053),new iPoint(4346,2142),new iPoint(4285,1993),new iPoint(4257,1904),
                new iPoint(4211,1912),new iPoint(4105,1858),new iPoint(4029,1862),new iPoint(3836,1639),new iPoint(3968,1507),new iPoint(3832,1132),
                new iPoint(3717,1047),new iPoint(3812,908),new iPoint(3678,615),new iPoint(3524,427),new iPoint(3244,399),new iPoint(3005,559),
                new iPoint(2793,698),new iPoint(2656,813),new iPoint(2641,897),new iPoint(2342,1030),new iPoint(2220,1041),new iPoint(2212,1125),
                new iPoint(2288,1253),new iPoint(2027,1546),new iPoint(1845,1555),new iPoint(1706,1405),new iPoint(1141,2527),new iPoint(816,2852),
                new iPoint(1011,3041),new iPoint(1104,2959),new iPoint(1405,3260),new iPoint(1761,2897),new iPoint(1544,2691),/*new iPoint(1411,2811),*/
                new iPoint(1410,2577),new iPoint(1670,2319),new iPoint(1481,2147),new iPoint(1141,2527),new iPoint(1839,1917),new iPoint(2062,1670),
                new iPoint(2149,1568),new iPoint(2352,1498),new iPoint(2499,1381),new iPoint(2471,1350),new iPoint(2370,1421),new iPoint(2190,1447),
                new iPoint(1766,1878),new iPoint(1839,1917),new iPoint(2849,1679),new iPoint(2920,1637),new iPoint(2915,1570),new iPoint(2894,1517),
                new iPoint(2987,1448),new iPoint(2992,1400),new iPoint(3053,1368),new iPoint(3229,1528),new iPoint(3261,1499),new iPoint(3423,1557),
                new iPoint(3572,1738),new iPoint(3614,1738),new iPoint(3654,1799),new iPoint(3574,1852),new iPoint(3436,1860),new iPoint(3194,1740),
                new iPoint(2867,1719),new iPoint(2849,1679),new iPoint(2813,2012),new iPoint(2717,1930),new iPoint(2758,1892),new iPoint(2849,1963),
                new iPoint(2813,2012),new iPoint(3436,1225),new iPoint(3439,1292),new iPoint(3571,1286),new iPoint(3568,1228),new iPoint(3436,1225),
                new iPoint(3277,1110),new iPoint(3480,1016),new iPoint(3281,644),new iPoint(3065,739),new iPoint(3277,1110),new iPoint(2204,2069),
                new iPoint(2120,2074),new iPoint(2032,2255),new iPoint(1921,2357),new iPoint(1866,2509),new iPoint(1773,2537),new iPoint(1773,2653),
                new iPoint(1893,2741),new iPoint(1958,2824),new iPoint(2060,2884),new iPoint(2162,2875),new iPoint(2310,2796),new iPoint(2181,2658),
                new iPoint(2144,2565),new iPoint(2153,2412),new iPoint(2213,2347),new iPoint(2287,2329),new iPoint(2297,2280),new iPoint(2150,2213),
                new iPoint(2204,2069),/*new iPoint(2795,2601),new iPoint(2932,2346),new iPoint(2903,2328),new iPoint(2757,2583),new iPoint(2795,2601),*/
                new iPoint(2996,2153),new iPoint(3208,2043),new iPoint(3318,2036),new iPoint(3317,2010),new iPoint(3202,2014),new iPoint(2977,2124),
                new iPoint(2996,2153),new iPoint(3592,2139),new iPoint(3647,2296),new iPoint(3355,2406),new iPoint(3486,2732),new iPoint(3766,2635),
                new iPoint(3779,2673),new iPoint(4207,2499),new iPoint(4050,2076),new iPoint(3834,2156),new iPoint(3787,2042),new iPoint(3592,2139),
                new iPoint(3527,3126),new iPoint(3640,3450),new iPoint(4265,3215),new iPoint(4191,3034),new iPoint(3749,3189),new iPoint(3699,3064),
                new iPoint(3527,3126),new iPoint(4402,2083),new iPoint(4460,2284),new iPoint(4485,2283),new iPoint(4419,2074),new iPoint(4402,2083),
                new iPoint(4513,2487),new iPoint(4513,2487),new iPoint(4542,2475),new iPoint(4651,2798),new iPoint(4623,2805),new iPoint(4513,2485),
                new iPoint(4542,2475),new iPoint(2234,3360),new iPoint(2332,3221),new iPoint(2845,3620),new iPoint(2591,3940),new iPoint(2049,3517),
                new iPoint(2175,3338),new iPoint(2234,3360)
        };
    }

    @Override
    public int getBlockWidth() {
        return 128;
    }

    @Override
    public Pixmap getMapBackGround() {
        return Assets.map00Bg;
    }

    @Override
    public String[] getDataToLoad() {
        return new String[]{"Data.dat", "data_defence.dat"};
    }

    @Override
    public iPoint getPlayerStartPoint() {
        return new iPoint(2825,2706);
    }
}
