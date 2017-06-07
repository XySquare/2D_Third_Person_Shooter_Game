package com.xyy.game.ai;

import com.xyy.game.framework.Input;

/**
 * 虚拟摇杆
 * Created by ${XYY} on ${2016/2/15}.
 * Updated by  ${XYY} on ${2016/5/27}.
 */
public class Controller {
    //摇杆的指针ID, 未被触摸则为-1
    private int Pointer = -1;
    //摇杆的标准化向量
    private float x = 0;
    private float y = 0;
    //摇杆的坐标
    private int PadX = 0;
    private int PadY = 0;
    //摇杆属性
    private final int CtrlX;// = 150;
    private final int CtrlY;// = 570;
    private final int CtrlR;// = 100;

    public Controller(int x,int y,int r){
        this.CtrlX = x;
        this.CtrlY = y;
        this.CtrlR = r;
    }

    public void update(Input.Touch event){
        int dx = event.x- CtrlX;
        int dy = event.y- CtrlY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if(event.type == Input.Touch.TOUCH_DOWN) {
            if(length < CtrlR && Pointer ==-1){
                Pointer = event.pointer;
                if(length == 0) return;
                x = (float)dx/length;
                y = (float)dy/length;
                PadX = dx;
                PadY = dy;
            }
        }else if(event.type == Input.Touch.TOUCH_DRAGGED) {
            if(event.pointer == Pointer){
                if(length == 0) return;
                x = (float)dx/length;
                y = (float)dy/length;
                if(length <= CtrlR) {
                    PadX = dx;
                    PadY = dy;
                }
                else{
                    PadX = (int) (x * CtrlR);
                    PadY = (int) (y * CtrlR);
                }
            }
        }else if(event.type == Input.Touch.TOUCH_UP) {
            if(event.pointer == Pointer) {
                Pointer = -1;
                x = 0;
                y = 0;
                PadX = 0;
                PadY = 0;
            }
        }
    }
    public float getLX(){
        return x;
    }
    public float getLY(){
        return y;
    }
    public int getPadX(){
        return PadX;
    }
    public int getPadY(){
        return PadY;
    }
    public boolean isPressed(){
        return Pointer != -1;
    }
    public void reset(){
        Pointer = -1;
        //摇杆的标准化向量
          x = 0;
          y = 0;
        //摇杆的坐标
          PadX = 0;
          PadY = 0;
    }
}
