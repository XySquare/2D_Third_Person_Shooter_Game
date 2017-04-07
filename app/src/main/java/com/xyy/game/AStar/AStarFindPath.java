package com.xyy.game.AStar;

import android.util.Log;

import com.xyy.game.framework.Pool;
import com.xyy.game.util.IntArrayList;
import com.xyy.game.util.Line;

import java.util.ArrayList;

/**
 * A*寻路算法
 */
public class AStarFindPath {

    private static int[] offset;
    private static byte[] map;
    private static Line[] lines;
    private static int blockWidth;
    private static int blockXNum;
    private static Pool<aPoint> pointPool;
    /**
     * 寻路路径缓存
     */
    //private static IntArrayList[][] cache;

    private ArrayList<aPoint> openTable;
    private ArrayList<aPoint> closeTable;

    private IntArrayList pathBuffer;
    private IntArrayList path;

    /**
     * 初始化：
     * 该方法将在WorldBuilder中被调用。
     * @param map   地图
     */
    public static void initialization(byte[] map, Line[] lines, int blockWidth, int blockXNum) {
        AStarFindPath.map = map;
        AStarFindPath.lines = lines;
        AStarFindPath.blockWidth = blockWidth;
        AStarFindPath.blockXNum = blockXNum;
        offset = new int[]{-blockXNum, blockXNum, -1, 1, -blockXNum - 1, -blockXNum + 1, blockXNum - 1, blockXNum + 1};
        //cache = new IntArrayList[map.length][map.length];
        if(pointPool == null) {
            Pool.PoolObjectFactory<aPoint> factory = new Pool.PoolObjectFactory<aPoint>() {
                @Override
                public aPoint createObject() {
                    return new aPoint();
                }
            };
            pointPool = new Pool<>(factory, 1024);
        }
    }

    public AStarFindPath() {
        openTable = new ArrayList<>();
        closeTable = new ArrayList<>();
        pathBuffer = new IntArrayList();
        path = new IntArrayList();
    }

    public static int ifSeeDirectly(int npc, int playerIndex){
        if(npc==playerIndex) return 0;
        //NPC所在地图块左上角（x1,y1）,右下角（x1_,y1_）
        int x1 = npc % blockXNum * blockWidth;
        int y1 = npc / blockXNum * blockWidth;
        int x1_ = x1 + blockWidth;
        int y1_ = y1 + blockWidth;
        //Player所在地图块左上角（x2,y2）,右下角（x2_,y2_）
        int x2 = playerIndex % blockXNum * blockWidth;
        int y2 = playerIndex / blockXNum * blockWidth;
        int x2_ = x2 + blockWidth;
        int y2_ = y2 + blockWidth;

        if((x1<=x2 && y1<=y2) || (x1>x2 && y1>y2)){
            byte b, c;
            b = c = 0;
            for (Line line : lines) {
                int Lx1 = line.getPoint1().x;
                int Ly1 = line.getPoint1().y;
                int Lx2 = line.getPoint2().x;
                int Ly2 = line.getPoint2().y;
                if (b == 0)
                    if (lineLine(x1, y1_, x2, y2_, Lx1, Ly1, Lx2, Ly2))
                        b = 1;
                if (c == 0)
                    if (lineLine(x1_, y1, x2_, y2, Lx1, Ly1, Lx2, Ly2))
                        c = 1;
                if (b + c == 2) break;
            }
            return b+c;
        }
        else{
            byte a,  d;
            a  = d = 0;
            for (Line line : lines) {
                int Lx1 = line.getPoint1().x;
                int Ly1 = line.getPoint1().y;
                int Lx2 = line.getPoint2().x;
                int Ly2 = line.getPoint2().y;
                if (a == 0)
                    if (lineLine(x1, y1, x2, y2, Lx1, Ly1, Lx2, Ly2))
                        a = 1;
                if (d == 0)
                    if (lineLine(x1_, y1_, x2_, y2_, Lx1, Ly1, Lx2, Ly2))
                        d = 1;
                if (a + d == 2) break;
            }
            return a+d;
        }
    }

    /**
     * 线-线碰撞测试
     */
    private static boolean lineLine(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        float k = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        float r = ((y1 - y3) * (x4 - x3) - (x1 - x3) * (y4 - y3)) / k;
        float s = ((y1 - y3) * (x2 - x1) - (x1 - x3) * (y2 - y1)) / k;
        return !(r < 0 || r > 1 || s < 0 || s > 1 || (r == 0 && s != 0));
    }

    public IntArrayList findPath(int start, int end) {
        IntArrayList pathTemp = pathBuffer;
        pathBuffer = path;
        path = pathTemp;
        pathTemp.clear();

        /**
         * 检查缓存中是否存在该路径，存在则直接返回
         */
        //IntArrayList cachePath = cache[start][end];
        //if (cachePath != null) {
            //pathTemp.addAll(cachePath);
            //return pathTemp;
        //}

        // 将当前节点初始化为起点

        aPoint currentPoint = pointPool.newObject();

        currentPoint.initial(start);

        int len;

        boolean flag = true;

        while (true) {

            for (int i = 0; i < 8; i++) {

                int fx = currentPoint.index + offset[i];

                /**
                 * �Ѵ��յ�
                 */

                if (end == fx) {

                    flag = false;

                    break;

                }

                //�����ϰ�
                final byte BLOCKED = (byte)0xFF;
                if (map[fx] == BLOCKED) continue;

                /**
                 * ����ͨ���õ㵽�յ������
                 */

                int G;

                int F;

                G = (int) (currentPoint.G + (i < 4 ? 10 : 14) * (1 + map[fx] / 255f));

                F = G + (Math.abs(fx / blockXNum - end / blockXNum) + Math.abs(fx % blockXNum - end % blockXNum)) * 10;

                /**
                 * ���õ��Ƿ���openTable
                 */

                boolean found = false;

                len = openTable.size();
                for (int j = 0; j < len; j++) {
                    aPoint tempPoint = openTable.get(j);

                    if (tempPoint.equals(fx)) {//contains

                        if (tempPoint.F > F) {

                            tempPoint.parent = currentPoint;

                            tempPoint.F = F;

                            tempPoint.G = G;
                        }

                        found = true;

                        break;
                    }
                }

                if (found) continue;

                /**
                 * ���õ��Ƿ���closeTable
                 */

                int j; //len;

                len = closeTable.size();

                for (j = 0; j < len; j++) {

                    aPoint tempPoint = closeTable.get(j);

                    if (tempPoint.equals(fx)) {//contains

                        if (tempPoint.F > F) {

                            closeTable.remove(j);

                            openTable.add(tempPoint);

                            tempPoint.parent = currentPoint;

                            tempPoint.F = F;

                            tempPoint.G = G;
                        }

                        break;
                    }
                }

                if (j < len) continue;

                /**
                 * ����������ж�û��
                 */

                aPoint newPoint = pointPool.newObject();

                newPoint.initial(fx);

                openTable.add(newPoint);

                newPoint.parent = currentPoint;

                newPoint.G = G;

                newPoint.F = F;

            }// end for

            openTable.remove(currentPoint); //�÷�����һ��ִ��ʱ��openTable�ﲻ����currentPoint

            closeTable.add(currentPoint);

            if (!flag)
                break;// �ҵ�·�������������Ļ��������ڶ����ڵ㣨end.parent�����ᱻ����closeTable����Ȼûʲô�á���

            if (openTable.isEmpty()) return pathTemp;// ��·��

            /**
             * ȡFֵ��С�Ľڵ���Ϊ��һ�������ڵ�
             */

            aPoint min = openTable.get(0);

            len = openTable.size();
            for (int i = 0; i < len; i++) {
                aPoint cur = openTable.get(i);
                if (cur.F < min.F) min = cur;
            }

            currentPoint = min;

        }// end while

        //aPoint node = currentPoint;
        //synchronized (this) {
        pathTemp.add(end);
        while (currentPoint.parent != null) {
            pathTemp.add(currentPoint.index);
            currentPoint = currentPoint.parent;
        }

        len = openTable.size();
        for (int i = 0; i < len; i++) {
            aPoint point = openTable.get(i);
            pointPool.free(point);
        }

        openTable.clear();

        len = closeTable.size();
        for (int i = 0; i < len; i++) {
            aPoint point = closeTable.get(i);
            pointPool.free(point);
        }

        closeTable.clear();

        //}

        /**
         * 将新的路径添加到缓存中
         */
        //if (cache[start][end] == null) {
            //IntArrayList newCache = new IntArrayList();
            //newCache.addAll(pathTemp);
            //cache[start][end] = newCache;
        //}

        return pathTemp;

    }

}


