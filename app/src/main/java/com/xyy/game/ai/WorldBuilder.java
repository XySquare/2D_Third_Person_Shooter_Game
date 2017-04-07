package com.xyy.game.ai;

import android.util.Log;

import com.xyy.game.AStar.AStarFindPath;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Screen.GameScreen;
import com.xyy.game.framework.FileIO;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.framework.Screen;
import com.xyy.game.util.Line;
import com.xyy.game.util.iPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * 根据世界数据（WorldData）初始化世界，
 * 将在GameLoadingScreen中被执行
 * Created by ${XYY} on ${2016/9/25}.
 */
public class WorldBuilder implements Runnable {
    private WorldData worldData;
    /**
     * 加载完成标准（false=正在加载）
     */
    private boolean built;
    /**
     * 地图边集
     */
    private Line[] lines;
    /**
     * 角色-地图环境接口
     */
    private Environment environment;

    private FileIO fileIO;

    private Game game;

    private Screen gameScreen;

    public WorldBuilder(String worldUid, FileIO fileIO, Game game) {
        switch (worldUid) {
            case "map00":
                worldData = new WorldData_0();
                break;
            default:
                throw new RuntimeException("无法找到对应的地图数据！(uid = " + worldUid + ")");
        }
        this.fileIO = fileIO;
        this.game = game;
        built = false;
    }

    @Override
    public void run() {
        initialization(worldData);

        Log.i("WorldBuilder","initialized.");

        while (AssetsLoader.getState()< AssetsLoader.GAME_LOADED){
            try {
                Log.i("WorldBuilder","Wait while assets loading...");
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        World newWorld = new World() {
            @Override
            public String getUid() {
                return worldData.getUid();
            }

            @Override
            public Character getRootCharacter(Stage stage) {
                return worldData.getRootCharacter(stage);
            }

            @Override
            public Pixmap getMapBackGround() {
                return worldData.getMapBackGround();
            }

            @Override
            public Environment getEnvironment() {
                return environment;
            }

            @Override
            public Line[] getLines() {
                return lines;
            }

            @Override
            public int[] getPlayerStartPoint() {
                return worldData.getPlayerStartPoint();
            }

        };

        gameScreen = new GameScreen(game, newWorld);

        built = true;
    }

    /**
     * 返回世界是否构建完成
     *
     * @return true=已构建完成
     */
    public boolean isBuilt() {
        return built;
    }

    /**
     * 获取构建完成的世界，
     * 请不要在世界未构建完成前调用
     */
    public Screen getGameScreen() {
        return gameScreen;
    }

    private void initialization(WorldData worldData) {

        GameDataManager.load(worldData.getDataToLoad(), fileIO);

        BuffManager.setBuffs(worldData.getBuffList());

        //区块宽度(实际处理时将以3*3个区块为一个判断区域)
        int blockWidth = worldData.getBlockWidth();

        //地图顶点
        iPoint[] points = worldData.getMapPoints();

        //搜索地图边界
        int maxX = 0;
        int maxY = 0;
        int minX = points[0].x;
        int minY = points[0].y;
        for(iPoint p:points){
            maxX = p.x>maxX ? p.x : maxX;
            maxY = p.y>maxY ? p.y : maxY;
            minX = p.x<minX ? p.x : minX;
            minY = p.y<minY ? p.y : minY;
        }

        //计算偏移量
        minX -= blockWidth;
        minY -= blockWidth;

        Log.e("WorldBuilder","X/Y = "+minX+" / "+minY);

        //地图顶点预处理
        for (iPoint p : points) {
            p.x -= minX;
            p.y -= minY;
        }

        maxX -= minX;
        maxY -= minY;

        //区块横向/纵向的数目
        int blockXNum = maxX / blockWidth + 2;
        int blockYNum = maxY / blockWidth + 2;

        //初始化地图边集
        Line[] lines = initializeLines(points, blockWidth, blockXNum, blockYNum);

        //**************TEST****************
        this.lines = lines;
        //**************TEST_END****************

        //碰撞检测分块
        Line[][] blocksList = initializeBlocksList(lines, blockWidth, blockXNum, blockYNum);

        //初始化地图块用于寻路
        int seed = worldData.getPlayerStartPoint()[0] / blockWidth * 2 + worldData.getPlayerStartPoint()[1] / blockWidth * 2 * blockXNum * 2;
        byte[] mapForFingPath = initializeMapForFindPath(lines, blockWidth / 2, blockXNum * 2, blockYNum * 2,seed);

        //构建环境
        this.environment = new Environment(blockWidth, blockXNum, blocksList, blockWidth / 2, blockXNum * 2,mapForFingPath);

        //初始化A*寻路
        AStarFindPath.initialization(mapForFingPath, lines, blockWidth / 2, blockXNum * 2);
    }

    /**
     * 初始化地图向量（Line）
     *
     * @param points 地图点集
     * @return 地图边集
     */
    private static Line[] initializeLines(iPoint[] points, int blockWidth, int blockXNum, int blockYNum) {
        //由点集生成边集
        ArrayList<Line> linesList = new ArrayList<>(points.length/2);
        int len = points.length;
        iPoint head = points[0];
        for (int i = 1; i < len; i++) {
            linesList.add(new Line(points[i - 1], points[i]));
            //当尾节点与头节点相等则封闭，注意防止越界
            if (head.equals(points[i]) && (++i) < points.length) {
                head = points[i];
            }
        }
        //追加边框
        iPoint p0 = new iPoint(blockWidth,blockWidth);
        iPoint p1 = new iPoint(blockWidth*(blockXNum-1),blockWidth);
        iPoint p2 = new iPoint(blockWidth*(blockXNum-1),blockWidth*(blockYNum-1));
        iPoint p3 = new iPoint(blockWidth,blockWidth*(blockYNum-1));
        linesList.add(new Line(p0, p1));
        linesList.add(new Line(p1, p2));
        linesList.add(new Line(p2, p3));
        linesList.add(new Line(p3, p0));
        //转换成数组
        Line[] lines = new Line[linesList.size()];
        linesList.toArray(lines);
        return lines;
    }

    /**
     * 进行碰撞测试的分区
     *
     * @param linesList 地图边集
     * @return 已经被分区的地图边集
     */
    private static Line[][] initializeBlocksList(Line[] linesList, int blockWidth, int blockXNum, int blockYNum) {
        //将边分配到各个区块中
        int totalBlocks;
        int totalLines = 0;
        //以上为统计信息
        //新建线性表用于临时储存每个分区中的边
        LinkedList<Line> linesInBlock = new LinkedList<>();
        //总分区数
        totalBlocks = blockXNum * blockYNum;
        //储存每个分区下的边集
        Line[][] blocksList = new Line[totalBlocks][];
        //遍历每个区块
        for (int k = 0; k < blockYNum; k++) {
            for (int j = 0; j < blockXNum; j++) {
                //遍历所有边
                for (Line line : linesList) {
                    //将当前区块（128*128）向四周拓展成（384*384）大小
                    if (lineSquare(blockWidth * (k - 1), blockWidth * (k + 2), blockWidth * (j - 1), blockWidth * (j + 2), line.getPoint1().x, line.getPoint1().y, line.getPoint2().x, line.getPoint2().y)) {
                        //储存该区块内的边
                        linesInBlock.add(line);
                    }
                }
                //该区块内边的数量
                int s = linesInBlock.size();
                totalLines += s;
                //新建数组储存区块中边
                Line[] linesInBlockAry = new Line[s];
                linesInBlock.toArray(linesInBlockAry);
                //将区块的边集放至对应的区块索引下
                blocksList[k * (blockXNum) + j] = linesInBlockAry;
                //清空区块边缓存
                linesInBlock.clear();
                Log.v("WorldBuilder", "blockList index=" + (k * (blockXNum) + j) + " complicity=" + s);
            }
        }
        Log.i("WorldBuilder", "totally " + linesList.length + "_lines " + totalBlocks + "_blocks AVG = " + totalLines / totalBlocks);

        return blocksList;
    }

    /**
     * 初始化地图块用于寻路
     *
     * @param linesList  边集
     * @param blockWidth 地图块宽度
     * @param blockXNum  地图块X方向上的数量
     * @param blockYNum  地图块Y方向上的数量
     * @return 地图块，其中障碍=255，可通过=0~254
     */
    private static byte[] initializeMapForFindPath(Line[] linesList, int blockWidth, int blockXNum, int blockYNum, int seed) {
        //标记区块中是否存在边
        byte[] map = new byte[blockXNum * blockYNum];
        //遍历每一块
        for (int y = 0; y < blockYNum; y++) {
            for (int x = 0; x < blockXNum; x++) {
                //检查是否有边存在于该块中
                for (Line line : linesList) {
                    if (lineSquare(blockWidth * y, blockWidth * (y + 1), blockWidth * x, blockWidth * (x + 1), line.getPoint1().x, line.getPoint1().y, line.getPoint2().x, line.getPoint2().y)) {
                        //将该块标记为不可达
                        map[y * (blockXNum) + x] = (byte) 0xFF;
                        //只要存在一条边即可
                        break;
                    }
                }
            }
        }
        //从种子（玩家初始坐标）开始执行填充算法，填充为临时占位符0xCC
        seedFillScanLineWithStack(map, seed, blockXNum, (byte) 0xCC, (byte) 0);
        //将剩余的，不可到达区域填充为0xFF
        //将临时占位符0xCC替换为0
        int len = map.length;
        for (int i = 0; i < len; i++){
            if (map[i] == 0) map[i] = (byte) 0xFF;
            else if (map[i] == (byte) 0xCC) map[i] = 0;
        }
        //向障碍周围施加权重
        for (int i = 0; i < map.length; i++) {
            if (map[i] == (byte)0xFF) {
                if (i - blockXNum < map.length && i - blockXNum > 0 && map[i - blockXNum] != (byte)0xFF)
                    map[i - blockXNum] += (byte)0x1F;
                if (i - blockXNum - 1 < map.length && i - blockXNum - 1> 0 && map[i - blockXNum - 1] != (byte)0xFF)
                    map[i - blockXNum - 1] += (byte)0x1F;
                if (i - blockXNum + 1 < map.length && i - blockXNum + 1> 0 && map[i - blockXNum + 1] != (byte)0xFF)
                    map[i - blockXNum + 1] += (byte)0x1F;
                if (i + blockXNum < map.length && map[i + blockXNum] != (byte)0xFF)
                    map[i + blockXNum] += (byte)0x1F;
                if (i + blockXNum - 1 < map.length && map[i + blockXNum - 1] != (byte)0xFF)
                    map[i + blockXNum - 1] += (byte)0x1F;
                if (i + blockXNum + 1 < map.length && map[i + blockXNum + 1] != (byte)0xFF)
                    map[i + blockXNum + 1] += (byte)0x1F;
                if (i - 1 < map.length && i - 1 > 0 && map[i - 1] != (byte)0xFF)
                    map[i - 1] += (byte)0x1F;
                if (i + 1 < map.length && map[i + 1] != (byte)0xFF)
                    map[i + 1] += (byte)0x1F;
            }
        }
        return map;
    }

    //扫描线洪泛填充算法　
    private static void seedFillScanLineWithStack(byte[] map, int seed, int blockXNum, byte newColor, byte oldColor) {
        Stack<Integer> stack = new Stack<>();

        int height = map.length / blockXNum;

        stack.push(seed);//种子点入栈

        while (true) {
            if (stack.isEmpty()) return;
            //取当前种子点
            int pos = stack.pop();
            int x = pos % blockXNum;
            int y1 = pos / blockXNum;
            while (y1 >= 0 && map[x + y1 * blockXNum] == oldColor) y1--; //找到待填充区域顶端
            y1++; //从起始像素点开始填充
            boolean spanLeft, spanRight;
            spanLeft = spanRight = false;
            while (y1 < height && map[x + y1 * blockXNum] == oldColor) {
                map[x + y1 * blockXNum] = newColor;
                //检查相邻左扫描线
                if (!spanLeft && x > 0 && map[x - 1 + y1 * blockXNum] == oldColor) {
                    stack.push(x - 1 + y1 * blockXNum);
                    spanLeft = true;
                } else if (spanLeft && x > 0 && map[x - 1 + y1 * blockXNum] != oldColor) {
                    spanLeft = false;
                }
                //检查相邻右扫描线
                if (!spanRight && x < blockXNum - 1 && map[x + 1 + y1 * blockXNum] == oldColor) {
                    stack.push(x + 1 + y1 * blockXNum);
                    spanRight = true;
                } else if (spanRight && x < blockXNum - 1 && map[x + 1 + y1 * blockXNum] != oldColor) {
                    spanRight = false;
                }
                y1++;
            }
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

    /**
     * 线-矩形碰撞测试
     */
    private static boolean lineSquare(int top, int bottom, int left, int right, int x1, int y1, int x2, int y2) {
        return lineLine(left, top, right, top, x1, y1, x2, y2) ||
                lineLine(right, top, right, bottom, x1, y1, x2, y2) ||
                lineLine(right, bottom, left, bottom, x1, y1, x2, y2) ||
                lineLine(left, bottom, left, top, x1, y1, x2, y2) ||
                ((left <= x1 && x1 <= right && top <= y1 && y1 <= bottom)
                        && (left <= x2 && x2 <= right && top <= y2 && y2 <= bottom));
    }

    /*private static byte[] readCache(FileIO fileIO, String fileName, int checkLength) {
        byte[] data = null;

        DataInputStream in = null;
        try {
            //注意保持读取和写入文件名相同
            in = new DataInputStream(fileIO.readCacheStorage(fileName));
            // 读取数组长度
            int len = in.readInt();
            //校验数组长度
            if (checkLength == len) {
                byte[] dataTemp = new byte[len];
                // 读取数组数据
                int res = in.read(dataTemp);
                Log.i("WorldBuilder", "Cache Read = " + res);
                data = dataTemp;
                // 读取完毕
                Log.i("WorldBuilder", "Cache Size = " + fileIO.getCacheSize());
            } else {
                //数组长度校验失败
                Log.e("WorldBuilder", "checkLength Failed, Found =" + len + " Should be = " + checkLength);
            }
        } catch (IOException ignored) {
            Log.e("WorldBuilder", "readCache Reading Error !");
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
                Log.e("WorldBuilder", "readCache Closing Error !");
            }
        }

        return data;
    }

    private static void writeCache(FileIO files, String fileName, byte[] data) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(files.writeCacheStorage(fileName)));
            // 写入数组长度
            int len = data.length;
            out.writeInt(len);
            // 写入数组数据
            out.write(data);
            // 写入完毕
            Log.e("WorldBuilder", "Cache Size = " + files.getCacheSize());
        } catch (IOException ignored) {
            Log.e("WorldBuilder", "writeCache Writing Error !");
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
                Log.e("WorldBuilder", "writeCache Closing Error !");
            }
        }
    }*/

}
