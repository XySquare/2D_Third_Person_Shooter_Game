package com.xyy.game.ai.Screen;

import android.graphics.Paint;

import com.xyy.game.ai.Assets;
import com.xyy.game.ai.Weapon.IMIDesertEagle;
import com.xyy.game.ai.Weapon.M16A4;
import com.xyy.game.ai.Weapon.RPG;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

import java.util.List;

/**
 * Created by ${XYY} on ${2017/4/16}.
 */

public class Screen_MainMenu_Store_Weapon_Detail extends Screen {
    private final Weapon mWeapon;

    private int pointer;
    private boolean pressed;


    public Screen_MainMenu_Store_Weapon_Detail(Game game, Weapon weapon) {
        super(game);

        pointer = -1;
        pressed = false;

        weapon.loadPixmap(game.getGraphics(), Weapon.PixmapQuality.NORMAL);
        mWeapon = weapon;
    }

    @Override
    public void update(float deltaTime) {
        List<Input.Touch> touchEvents = game.getInput().getTouchEvents();

        final int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.Touch event = touchEvents.get(i);
            if (event.type == Input.Touch.TOUCH_DOWN) {
                if(inBounds(event, 900, 420, 189, 74)) {
                    pointer = event.pointer;
                    pressed = true;
                }
                else if(event.x <= 71 && event.y <= 68){
                    pointer = event.pointer;
                }
            }
            else if(event.type == Input.Touch.TOUCH_UP){
                if(pointer == event.pointer) {
                    pointer = -1;
                    pressed = false;
                    if(inBounds(event, 900, 420, 189, 74)){

                    }
                    else if(event.x <= 71 && event.y <= 68){
                        game.setScreen(new Screen_MainMenu_Store(game));
                    }
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.background,0,0);

        //顶部条
        g.drawPixmap(Assets.main_menu_top_bar,0,0);
        //返回按钮
        g.drawPixmap(Assets.back,22,20);
        //"MainMenu"文字
        g.drawText("STORE",77,28,0xFF999999,24);
        //"Store"文字
        g.drawText("[WEAPON DETAIL]",77,53,0xFFFFFFFF,24);


        Weapon weapon = mWeapon;

        g.drawRect(98,89,385,70,0x7F666666);
        Weapon.Rarity rarity = weapon.getRarity();
        if(rarity == Weapon.Rarity.N)
            g.drawText("RARITY N",110,123,0xFFFFFFFF,30);
        else if(rarity == Weapon.Rarity.R)
            g.drawText("RARITY R",110,123,0xFFFFFFFF,30);
        else if(rarity == Weapon.Rarity.SR)
            g.drawText("RARITY SR",110,123,0xFFFFFFFF,30);
        else if(rarity == Weapon.Rarity.SSR)
            g.drawText("RARITY SSR",110,123,0xFFFFFFFF,30);

        g.drawRect(98,89+70+8,385,70,0x7F666666);
        g.drawText("DAMAGE",110,201,0xFFFFFFFF,30);
        g.drawText(String.valueOf(weapon.getDamage()),110+220,201+24,0xFFFFFFFF,42);

        g.drawRect(98,89+70+8+70+8,385,70,0x7F666666);
        g.drawText("ENERGY COST",110,279,0xFFFFFFFF,30);
        g.drawText(String.valueOf(weapon.getEnergyCost()),110+220,277+24,0xFFFFFFFF,42);

        g.drawRect(98,89+70+8+70+8+70+8,385,70,0x7F666666);
        g.drawText("RATE OF FIRE",110,357,0xFFFFFFFF,30);
        String num = String.valueOf((int)(60/weapon.getAtkDelay()));
        g.drawText(num,110+220,353+22,0xFFFFFFFF,42);
        g.drawText("RPM",110+220+num.length()*25,353+24,0xFFFFFFFF,28);

        g.drawRect(575, 410, 525, 210, 0x7F000000);
        if(pressed)
            g.drawPixmap(Assets.button_details, 900, 420, 189 ,0, 189, 74);
        else
            g.drawPixmap(Assets.button_details, 900, 420, 0 ,0, 189, 74);
        g.drawText(String.valueOf(weapon.getPrice()),900+56, 420+24+20, 0xFFFFFFFF, 24);

        g.drawPixmap(weapon.getPixmap(),630,200);
        String name = weapon.getName();
        g.drawText(name,1100,130,0xFFFFFFFF,35, Paint.Align.RIGHT);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBack() {
        game.setScreen(new Screen_MainMenu_Store(game));
        return true;
    }

    private boolean inBounds(Input.Touch event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

}
