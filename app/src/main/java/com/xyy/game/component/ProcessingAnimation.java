package com.xyy.game.component;

import com.xyy.game.framework.Graphics;

/**
 * 进度条
 * Created by ${XYY} on ${2016/9/30}.
 */
public class ProcessingAnimation {
    //坐标
    protected int x,y;
    //宽/高
    protected int width, height;
    //颜色/背景颜色
    protected int color, BGColor;
    //进度条当前宽度
    protected float currentWidth;
    //背景进度条当前宽度
    protected float currentWidthBg;

    public ProcessingAnimation(int x, int y, int width, int height, int color){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color|0xFF000000;//忽略Alpha
        this.BGColor = 0x7F000000|(color&0x00FFFFFF);//增加半透明
        currentWidth = 0;
        currentWidthBg = 0;
    }

    public void  update(float deltaTime, float percentage){
        int updatedWidth = (int) (percentage * width);
        //如果血量减少...
        if(updatedWidth<currentWidth){
            //当背景条被覆盖时，延长背景条
            if(currentWidthBg <currentWidth)
                currentWidthBg = currentWidth;
            //缩短前景条
            currentWidth = updatedWidth;
        }
        //前景条增长
        else if(currentWidth<updatedWidth){
            currentWidth += (updatedWidth-currentWidth)*deltaTime*10;
            if(currentWidth>updatedWidth)
                currentWidth = updatedWidth;
        }
        //背景条向前景条收缩
        if(currentWidthBg >currentWidth) {
            currentWidthBg += (currentWidth- currentWidthBg)*deltaTime;
        }
    }

    public void present(Graphics g){
        //半透明灰色背景（非黑色，保证在黑色背景下能看见）
        g.drawRect(x,y,width,height,0x7F333333);
        //背景半透明进度条
        g.drawRect(x,y, (int) currentWidthBg,height,BGColor);
        //前景不透明进度条
        g.drawRect(x,y, (int) currentWidth,height,color);
        //右端白线
        g.drawLine(x+width,y,x+width,y+height,0xFFFFFFFF);
    }
}
