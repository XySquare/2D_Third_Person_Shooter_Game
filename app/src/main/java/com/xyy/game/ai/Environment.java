package com.xyy.game.ai;

import com.xyy.game.ai.Attack.Attack;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.util.Line;

import java.util.ArrayList;

/**
 * Created by ${XYY} on ${2016/8/3}.
 * Last Update on 2016/8/26
 */
public class Environment {

    /**
     * 区块宽度
     */
    private int blockWidth;

    /**
     * 区块横向的数目
     */
    private int blockXNum;

    /**
     * 玩家的坐标
     * 在Player类中被更新
     */
    private int playerX;
    private int playerY;

    /**
     * 储存“玩家”产生的攻击对象的线性表
     */
    public ArrayList<Attack> playerAtkList;

    /**
     * 储存“敌对”角色的线性表
     */
    private ArrayList<Character> hostileList;

    /**
     * 地图边集（分块）
     */
    private Line[][] blocksList;

    private int mapBlockWidth;

    private int mapBlockXNum;

    private byte[] mapForFingPath;

    /**
     * 玩家索引缓存
     */
    private int playerIndex;

    private int playerOldIndex;

    public Environment(int blockWidth, int blockXNum, Line[][] blocksList,
                       int mapBlockWidth, int mapBlockXNum,
                       byte[] mapForFingPath) {
        this.blockWidth = blockWidth;
        this.blockXNum = blockXNum;
        this.blocksList = blocksList;

        this.mapBlockWidth = mapBlockWidth;
        this.mapBlockXNum = mapBlockXNum;

        this.mapForFingPath = mapForFingPath;
    }

    public void addPlayerAtkList(ArrayList<Attack> playerAtkList){
        this.playerAtkList = playerAtkList;
    }

    /**
     * 更新玩家所处的地图索引
     * @param x 玩家所处的X坐标
     * @param y 玩家所处的Y坐标
     */
    public void updatePlayerIndex(float x, float y){
        playerX = (int) x;
        playerY = (int) y;
        int x2 = playerX/ mapBlockWidth;
        int y2 = playerY/ mapBlockWidth;
        //保存旧位置
        playerOldIndex = playerIndex;
        //玩家所在地图索引
        playerIndex = y2*(mapBlockXNum)+x2;
    }

    public boolean isPlayerIndexChanged(){
        return playerIndex != playerOldIndex;
    }

    /**
     * 根据坐标获取碰撞检测区块
     * @param x 对象所处的X坐标
     * @param y 对象所处的Y坐标
     * @return 碰撞检测区块
     */
    public Line[] getBlockOfLines(float x, float y){

        int blockX = (int) (x/ blockWidth);
        int blockY = (int) (y/ blockWidth);

        int index = blockY*(blockXNum)+blockX;
        //越界检查
        if(index<0 || index>=blocksList.length){
            return null;
        }
        //获取所处的区块
        return blocksList[index];
    }

    public int getIndex(float x,float y){
        int x1 = (int) (x/ mapBlockWidth);
        int y1 = (int) (y/ mapBlockWidth);
        //对象所在地图索引
        return  y1*(mapBlockXNum)+x1;
    }

    public boolean isAvailable(float x, float y) {
        int x1 = (int) (x / mapBlockWidth);
        int y1 = (int) (y / mapBlockWidth);
        //所在地图索引
        int pos = y1 * (mapBlockXNum) + x1;
        return pos>=0 && pos < mapForFingPath.length && mapForFingPath[pos] == 0;
    }

    //DebugOnly
    public byte[] getMapForFingPath() {
        return mapForFingPath;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public int index2X(int index){
        return (index%mapBlockXNum)*mapBlockWidth + mapBlockWidth/2;
    }

    public int index2Y(int index){
        return (index/mapBlockXNum)*mapBlockWidth + mapBlockWidth/2;
    }

    public ArrayList<Character> getHostileList() {
        return hostileList;
    }

    public void setHostileList(ArrayList<Character> hostileList) {
        this.hostileList = hostileList;
    }
}
