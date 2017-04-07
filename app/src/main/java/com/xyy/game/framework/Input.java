package com.xyy.game.framework;

import java.util.List;

/**
 * Input接口
 * Created by ${XYY} on ${2015/3/5}.
 */
public interface Input {

    public static class Touch {
        public static final int TOUCH_DOWN = 0;
        public static final int TOUCH_UP = 1;
        public static final int TOUCH_DRAGGED = 2;

        public int type;
        public int x, y;
        public int pointer;

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (type == TOUCH_DOWN)
                builder.append("touch down, ");
            else if (type == TOUCH_DRAGGED)
                builder.append("touch dragged, ");
            else
                builder.append("touch up, ");
            builder.append(pointer);
            builder.append(",");
            builder.append(x);
            builder.append(",");
            builder.append(y);
            return builder.toString();
        }
    }

    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<Touch> getTouchEvents();
}
