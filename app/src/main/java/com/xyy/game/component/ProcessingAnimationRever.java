package com.xyy.game.component;

import com.xyy.game.framework.Graphics;

/**
 * Created by ${XYY} on ${2016/11/9}.
 */
public class ProcessingAnimationRever extends ProcessingAnimation {
    public ProcessingAnimationRever(int x, int y, int width, int height, int color) {
        super(x, y, width, height, color);
    }

    public void present(Graphics g){
        //半透明灰色背景
        g.drawRect(x,y,width,height,0x6F000000);
        //背景半透明进度条
        g.drawRect(x+width-(int)currentWidthBg,y, (int) currentWidthBg,height,BGColor);
        //前景不透明进度条
        g.drawRect(x+width-(int)currentWidth,y, (int) currentWidth,height,color);
        //作端白线
        g.drawLine(x,y,x,y+height,0xFFFFFFFF);
    }
}
