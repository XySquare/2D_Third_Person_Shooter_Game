package com.xyy.game.component;

import android.util.Log;

import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Pixmap;

/**
 * 圆形按钮，带图标，
 * 带有渐变出现效果，
 * 带有点击缩放效果。
 * Created by ${XYY} on ${2016/10/1}.
 */
public class CircleButton {
    //坐标
    protected final int x;
    protected final int y;
    //宽/高
    private final int r;
    //颜色
    private final int color;
    //图标
    private final Pixmap ico;
    //当前半径
    protected float currentR;
    //当前缩放
    private float currentScale;
    //目标半径
    private float targetR;
    //目标缩放
    private float targetScale;
    //当前aplha值
    private float currentAlpha;
    private float currentAlphaPixmap;
    //目标alpha值
    private int targetAlpha;
    //当前指针（-1为空）
    private int pointer;
    //出现延迟
    private float delay;
    //计时器
    private float timer;

    public CircleButton(int x, int y, int r, int color, Pixmap ico){
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
        this.ico = ico;
    }

    /**
     * 初始化按钮
     * @param delay 出现延迟时间
     */
    public void initialize(float delay){
        currentR = r*0.8f;
        currentScale = 0.8f;
        targetR = r;
        targetScale = 1;
        pointer = -1;
        timer = 0;
        currentAlpha = currentAlphaPixmap = 0;
        targetAlpha = (color>>24)&0xFF;
        this.delay = delay;
    }

    /**
     * 返回按钮是否被点击（同一指针按下、抬起都在按钮内部）
     * @return true=被点击
     */
    public boolean isClicked(Input.Touch event){
        //若未到出现时间，直接返回
        if(timer<delay)
            return false;
        //如果未被按下
        if(pointer == -1) {
            //如果在按钮内部被按下
            if (event.type == Input.Touch.TOUCH_DOWN && inBounds(event, x, y, r)) {
                //缩小
                targetR = r * 0.85f;
                targetScale = 0.85f;
                //记录指针
                pointer = event.pointer;
            }
        }
        //如果已被按下，检查同一指针的抬起状态
        else if(event.pointer == pointer && event.type == Input.Touch.TOUCH_UP){
            //缩放复原
            targetR = r;
            targetScale = 1;
            //清空指针
            pointer = -1;
            //如果在按钮内部
            if (inBounds(event, x, y, r)) {
                return true;
            }
        }
        return false;
    }

    public void  update(float deltaTime){
        //计时，直到延迟时间到达，才进行下一步操作
        if(timer<delay) {
            timer += deltaTime;
        }else {
            //缩放
            //if (currentR != targetR) {
                currentR += (targetR - currentR) * deltaTime * 10;
                currentScale += (targetScale - currentScale) * deltaTime * 10;
            //}
            if(currentAlpha != targetAlpha || currentAlphaPixmap!= 0xFF){
                currentAlpha += (1 + targetAlpha - currentAlpha)  * deltaTime * 20;
                if(currentAlpha>targetAlpha)
                    currentAlpha = targetAlpha;
                currentAlphaPixmap += (1 + 0xFF - currentAlphaPixmap) * deltaTime *20;
                if(currentAlphaPixmap>0xFF)
                    currentAlphaPixmap = 0xFF;
            }
        }
    }

    public void present(Graphics g){
        //绘制圆形背景
        g.drawCircle(x,y,currentR,(color & 0x00FFFFFF)|((int) currentAlpha << 24));
        //绘制图标
        g.drawPixmapScale(ico,x,y,currentScale,currentScale, (int) (currentAlphaPixmap));
    }

    private static boolean inBounds(Input.Touch event, int x, int y, int r) {
        int dx = event.x - x;
        int dy = event.y - y;
        return dx*dx + dy*dy <= r*r;
    }
}
