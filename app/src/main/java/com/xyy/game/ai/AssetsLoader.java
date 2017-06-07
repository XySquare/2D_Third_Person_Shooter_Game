package com.xyy.game.ai;

import android.util.Log;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;

/**
 * 资源加载器
 * Created by ${XYY} on ${2016/9/17}.
 */
public class AssetsLoader implements Runnable {
    private static final int LOADING = 0;
    public static final int MAINMENU_LOADED = 2;
    public static final int MAPSELECTING_LOADED = 3;
    public static final int SETTING_LOADED = 4;
    public static final int GAME_LOADED = 5;
    //public static final int LOADED = 6;
    /**
     * 图形接口
     */
    private Graphics g;
    /**
     * 加载状态
     */
    private static int state;

    public AssetsLoader(Graphics graphics){
        g = graphics;
        state = LOADING;
    }

    @Override
    public void run() {
        //Assets.crossIco = g.newPixmap("crossIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.tickIco = g.newPixmap("tickIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.start = g.newPixmap("START.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.getMore = g.newPixmap("getMore.png", Graphics.PixmapFormat.ARGB4444);
        Assets.setting = g.newPixmap("setting.png", Graphics.PixmapFormat.ARGB4444);
        Assets.ai = g.newPixmap("title.png", Graphics.PixmapFormat.ARGB4444);
        Assets.back= g.newPixmap("back.png", Graphics.PixmapFormat.RGB565);
        Assets.main_menu_top_bar = g.newPixmap("main_menu_top_bar.png", Graphics.PixmapFormat.RGB565);
        Assets.main_menu_bottom_bar_button = g.newPixmap("main_menu_bottom_bar_button.png", Graphics.PixmapFormat.RGB565);
        Assets.main_menu_bottom_bar_button_red = g.newPixmap("main_menu_bottom_bar_button_red.png", Graphics.PixmapFormat.RGB565);
        Assets.list_item_weapon = g.newPixmap("list_item_weapon.png", Graphics.PixmapFormat.ARGB8888);
        Assets.list_item_weapon_selected = g.newPixmap("list_item_weapon_selected.png", Graphics.PixmapFormat.ARGB4444);
        Assets.list_item_weapon_bronze = g.newPixmap("list_item_weapon_bronze.png", Graphics.PixmapFormat.ARGB4444);
        Assets.list_item_weapon_silver = g.newPixmap("list_item_weapon_silver.png", Graphics.PixmapFormat.ARGB4444);
        Assets.list_item_weapon_gold = g.newPixmap("list_item_weapon_gold.png", Graphics.PixmapFormat.ARGB4444);
        Assets.list_item_supplies = g.newPixmap("list_item_supplies.png", Graphics.PixmapFormat.ARGB4444);
        Assets.list_item_promos = g.newPixmap("list_item_promos.png", Graphics.PixmapFormat.ARGB4444);
        Assets.supply_medkit = g.newPixmap("supply_medkit.png", Graphics.PixmapFormat.ARGB4444);
        Assets.supply_energy = g.newPixmap("supply_energy.png", Graphics.PixmapFormat.ARGB4444);
        Assets.button_details = g.newPixmap("button_details.png", Graphics.PixmapFormat.ARGB4444);
        Assets.button_details_s = g.newPixmap("button_details_s.png", Graphics.PixmapFormat.ARGB4444);
        Assets.button_tab = g.newPixmap("button_tab.png", Graphics.PixmapFormat.ARGB4444);
        Assets.background = g.newPixmap("background.png", Graphics.PixmapFormat.ARGB8888);
        Assets.dialog = g.newPixmap("dialog.png", Graphics.PixmapFormat.ARGB4444);
        Assets.case_elite = g.newPixmap("case_elite.png", Graphics.PixmapFormat.ARGB4444);
        Assets.case_normal = g.newPixmap("case_normal.png", Graphics.PixmapFormat.ARGB4444);
        state = MAINMENU_LOADED;

        Pixmap m1 = g.newPixmap("option1.png", Graphics.PixmapFormat.RGB565);
        Pixmap m2 = g.newPixmap("option2.png", Graphics.PixmapFormat.RGB565);
        Pixmap m3 = g.newPixmap("option3.png", Graphics.PixmapFormat.RGB565);
        Pixmap m4 = g.newPixmap("option4.png", Graphics.PixmapFormat.RGB565);
        Assets.mapThumbs = new Pixmap[]{m1,m2,m3,m4};
        Assets.rightGo = g.newPixmap("rightgo.png", Graphics.PixmapFormat.ARGB4444);
        Assets.leftGo = g.newPixmap("leftgo.png", Graphics.PixmapFormat.ARGB4444);
        state = MAPSELECTING_LOADED;

        //Assets.clear = g.newPixmap("lowquality.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.filter = g.newPixmap("filter.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.highFilter = g.newPixmap("highFilter.png", Graphics.PixmapFormat.ARGB4444);
        state = SETTING_LOADED;

        Assets.padA = g.newPixmap("padA.png", Graphics.PixmapFormat.ARGB4444);
        Assets.pad = g.newPixmap("pad.png", Graphics.PixmapFormat.ARGB4444);
        Assets.ico = g.newPixmap("ico.png", Graphics.PixmapFormat.ARGB4444);
        Assets.pauseIco = g.newPixmap("pauseIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.playIco = g.newPixmap("playIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.mainMenuIco = g.newPixmap("mainMenuIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.exitIco = g.newPixmap("exitIco.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.retryIco = g.newPixmap("retryIco.png", Graphics.PixmapFormat.ARGB4444);


        Pixmap arrow0 = g.newPixmap("arrow.png", Graphics.PixmapFormat.ARGB4444);
        Pixmap arrow1 = g.newPixmap("arrow1.png", Graphics.PixmapFormat.ARGB4444);
        Assets.arrows = new Pixmap[]{arrow0,arrow1};

        Assets.player = g.newPixmap("player.png", Graphics.PixmapFormat.ARGB4444);
        Assets.hostile = g.newPixmap("hostile.png", Graphics.PixmapFormat.ARGB4444);
        Assets.defenceHostile = g.newPixmap("hostile_defence.png", Graphics.PixmapFormat.ARGB4444);
        Assets.NPCProducer = g.newPixmap("NPCProducer.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.map00Bg = g.newPixmap("map00_bg.png", Graphics.PixmapFormat.RGB565);
        //Assets.ranks = g.newPixmap("ranks.png", Graphics.PixmapFormat.ARGB4444);
        Assets.energy = g.newPixmap("energy.png", Graphics.PixmapFormat.ARGB4444);
        Assets.aid= g.newPixmap("aid.png", Graphics.PixmapFormat.ARGB4444);
        Assets.numbers5_26_2 = g.newPixmap("numbers5_26_2.png", Graphics.PixmapFormat.ARGB4444);
        //Assets.map0_Bg = g.newBigPixmap("map00_bg.png");
        Assets.btArrowLeft = g.newPixmap("arrowBt.png", Graphics.PixmapFormat.ARGB4444, 0, 0, 30,60);
        Assets.btArrowRight = g.newPixmap("arrowBt.png", Graphics.PixmapFormat.ARGB4444, 30, 0, 30,60);

        Assets.effect_66ccff_radial_00_ff = g.newPixmap("effect_66ccff_radial_00_ff.png", Graphics.PixmapFormat.ARGB4444);
        Assets.effect_ff0000_radial_00_ff = g.newPixmap("effect_ff0000_radial_00_ff.png", Graphics.PixmapFormat.ARGB4444);
        Assets.effect_ffffff_radial_00_99 = g.newPixmap("effect_ffffff_radial_00_99.png", Graphics.PixmapFormat.ARGB4444);
        state = GAME_LOADED;

        Log.i("AssetsLoader","All Assets Loaded!");
    }

    public static int getState() {
        return state;
    }
}
