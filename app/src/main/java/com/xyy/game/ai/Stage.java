package com.xyy.game.ai;

import android.util.Log;

import com.xyy.game.ai.Attack.Attack;
import com.xyy.game.ai.Character.NPC.NPC;
import com.xyy.game.ai.Effect.Effect;
import com.xyy.game.ai.Character.*;
import com.xyy.game.ai.Character.Character;
import com.xyy.game.ai.Effect.PlayerShowEffect;
import com.xyy.game.ai.Screen.Screen_MainMenu_Repository;
import com.xyy.game.ai.Screen.UserDate;
import com.xyy.game.ai.Weapon.Weapon;
import com.xyy.game.database.WeaponLab;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Pixmap;
import com.xyy.game.util.IntArrayList;
import com.xyy.game.util.Line;
import com.xyy.game.util.iPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ${XYY} on ${2016/5/27}.
 * Last Update on 2016/8/26
 */
public class Stage {

    /**
     * 玩家对象
     */
    private Player player;

    /**
     * 玩家得分
     */
    private int score;

    /**
     * 地图边集
     */
    //private Line[] lines;

    /**
     * 储存“玩家”产生的攻击对象的线性表
     */
    private ArrayList<Attack> playerAtkList = new ArrayList<>();

    /**
     * 缓存一帧中新增的攻击对象
     */
    private ArrayList<Attack> playerAtkListTemp = new ArrayList<>();

    /**
     * 储存“敌方”产生的攻击对象的线性表
     */
    private ArrayList<Attack> hostileAtkList = new ArrayList<>();

    /**
     * 缓存一帧中新增的攻击对象
     */
    private ArrayList<Attack> hostileAtkListTemp = new ArrayList<>();

    /**
     * 储存“敌对”角色的线性表
     */
    private ArrayList<Character> hostileList = new ArrayList<>();

    /**
     * 缓存一帧中新增的“敌对角色”
     */
    private ArrayList<Character> hostileListTemp = new ArrayList<>();

    /**
     * 特效线性表
     */
    private ArrayList<Effect> effectList = new ArrayList<>();

    /**
     * 缓存一帧中新增的特效
     */
    private ArrayList<Effect> effectListTemp = new ArrayList<>();

    /**
     * 追踪列表
     */
    private ArrayList<GameObject> trackList = new ArrayList<>();

    private IntArrayList trackListIco = new IntArrayList();

    /**
     * 传递给bot的地图环境
     */
    private Environment environment;

    /**
     * 根角色
     */
    private Character rootCharacter;

    /**
     * 地图背景
     */
    private Pixmap mapBackGround;

    /**
     * 帧计数器
     */
    private int fps = 0;
    private float frapTimer = 0;


    private GameStateManager gameStateManager;

    //private BackGroundLoader backGroundLoader;


    /**
     * 玩家所持有的武器
     */
    private ArrayList<Weapon> mWeapons;

    /**
     * 玩家当前所使用的武器
     */
    private int mCurrentWeaponIndex;

    /**
     * 当前武器是否在冷却（true = 正在冷却，不可使用）
     */
    private boolean mIsInCD;

    /**
     * 武器冷却时间计时器
     */
    private float mAtkDelayTimer;

    /**
     * Stage在GameScreen中被实例化，
     * 随后被传递给各个GameState
     */
    public Stage(Map map, GameStateManager gameStateManager, Graphics g) {
        this.gameStateManager = gameStateManager;

        //获取根角色
        Class<? extends Character> clazz = map.getRootCharacter();
        try {
            Constructor<? extends Character> constructor = clazz.getConstructor(Stage.class);
            rootCharacter = constructor.newInstance(this);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        }

        //获取地图背景
        mapBackGround = map.getMapBackGround();

        //获取环境
        environment = map.getEnvironment();
        environment.addPlayerAtkList(playerAtkList);
        environment.setHostileList(hostileList);

        //获取地图边
        //lines = world.getLines();

        //创建玩家
        player = new Player(this);

        //设置玩家初始坐标
        iPoint startPoint = map.getPlayerStartPoint();
        player.initialize((NPC) rootCharacter, startPoint.x, startPoint.y);

        //添加玩家出现特效
        Effect e = new PlayerShowEffect();
        e.initialize((int) player.x, (int) player.y);
        addEffect(e);

        //向玩家添加一个无法移动的BUFF，时长与特效时长相同
        player.addBuff(Buff.UNMOVEABLE);

        //玩家武器
        WeaponLab weaponLab = WeaponLab.get();
        mWeapons = new ArrayList<>();
        List<UUID> mCurrentlyEquippedWeapons;
        List<Screen_MainMenu_Repository.WeaponRecord> _mWeapons = weaponLab.getWeapons();
        if(UserDate.mCurrentlyEquippedWeapons == null) {
            mCurrentlyEquippedWeapons = new ArrayList<>();
            mCurrentlyEquippedWeapons.add(_mWeapons.get(0).mUUID);
        }
        else{
            mCurrentlyEquippedWeapons = UserDate.mCurrentlyEquippedWeapons;
        }
        for (int i = 0; i < mCurrentlyEquippedWeapons.size(); i++) {
            Screen_MainMenu_Repository.WeaponRecord weaponRecord = weaponLab.getWeapon(mCurrentlyEquippedWeapons.get(i));
            Weapon weapon = weaponRecord.mWeapon;
            weapon.loadPixmap(g, Weapon.PixmapQuality.LOW);
            mWeapons.add(weapon);
        }

        setCurrentWeaponIndex(0);

        //int indexX = (int)(player.getX()/BLOCK_WIDTH) - 2;
        //int indexY = (int)(player.getY()/BLOCK_HEIGHT) - 2;
        //backGroundLoader = new BackGroundLoader(Assets.map0_Bg,indexX,indexY);

        //new Thread(backGroundLoader).start();

    }

    public void initialize(Map map){
        //释放线程
        int len = hostileList.size();
        for (int i = 0; i < len; i++)
            hostileList.get(i).setHp(0);
        len = hostileListTemp.size();
        for (int i = 0; i < len; i++)
            hostileListTemp.get(i).setHp(0);

        //清空所有攻击对象/角色
        playerAtkList.clear();
        playerAtkListTemp.clear();
        hostileAtkList.clear();
        hostileAtkListTemp.clear();
        hostileList.clear();
        hostileListTemp.clear();
        effectList.clear();
        effectListTemp.clear();

        //获取根角色
        Class<? extends Character> clazz = map.getRootCharacter();
        try {
            Constructor<? extends Character> constructor = clazz.getConstructor(Stage.class);
            rootCharacter = constructor.newInstance(this);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        }

        //获取地图背景
        mapBackGround = map.getMapBackGround();

        //获取环境
        environment = map.getEnvironment();
        environment.addPlayerAtkList(playerAtkList);
        environment.setHostileList(hostileList);

        //获取地图边
        //lines = world.getLines();

        //设置玩家初始坐标
        iPoint startPoint = map.getPlayerStartPoint();
        player.initialize((NPC) rootCharacter, startPoint.x, startPoint.y);
    }

    //private static final int BLOCK_WIDTH = 1280/3 + 1;
    //private static final int BLOCK_HEIGHT = 720/3;

    public void update(float deltaTime) {
        //更新玩家
        player.update(deltaTime, environment);

        if (mIsInCD) {
            mAtkDelayTimer += deltaTime;
            float delay = mWeapons.get(mCurrentWeaponIndex).getAtkDelay();
            if (mAtkDelayTimer >= delay) {
                mAtkDelayTimer -= delay;
                mIsInCD = false;
            }
        }

        //将上一帧新增的攻击对象/角色添加到舞台，随后清空缓存
        playerAtkList.addAll(playerAtkListTemp);
        playerAtkListTemp.clear();
        hostileAtkList.addAll(hostileAtkListTemp);
        hostileAtkListTemp.clear();
        hostileList.addAll(hostileListTemp);
        hostileListTemp.clear();
        effectList.addAll(effectListTemp);
        effectListTemp.clear();

        Line[] blockOfLines;

        //Update the AttackObjects
        //of Player
        int len = playerAtkList.size();
        for (int i = 0; i < len; i++) {
            Attack atk = playerAtkList.get(i);

            atk.update(deltaTime);
            //hitTest with LINE
            blockOfLines = environment.getBlockOfLines(atk.getX(), atk.getY());
            if (blockOfLines == null) {
                atk.remove();
                continue;
            }
            for (Line line : blockOfLines) {
                boolean res = atk.hitTestLine(line);
                if (res) {
                    break;
                }
            }
            //hitTest with CHARACTER
            int l = hostileList.size();
            for (int j = 0; j < l; j++) {
                Character character = hostileList.get(j);
                boolean res = atk.hitTestCharacter(character);
                if (res) {
                    break;
                }
            }
        }

        //Update the AttackObjects
        //of Hostile
        len = hostileAtkList.size();
        for (int i = 0; i < len; i++) {
            Attack atk = hostileAtkList.get(i);

            atk.update(deltaTime);
            //hitTest with LINE
            blockOfLines = environment.getBlockOfLines(atk.getX(), atk.getY());
            if (blockOfLines == null) {
                atk.remove();
                continue;
            }
            for (Line line : blockOfLines) {
                boolean res = atk.hitTestLine(line);
                if (res) {
                    break;
                }
            }
            //hitTest with CHARACTER(Player)
            atk.hitTestCharacter(player);

        }

        /**
         * 更新并（按需）移除特效
         */
        len = effectList.size();
        for (int i = 0; i < len; i++) {
            Effect e = effectList.get(i);
            e.update(deltaTime);
            //移除过期特效
            if (e.isDead()) {
                effectList.remove(i);//TODO: to be optimized
                len--;
                i--;
            }
        }

        /**
         * 按需移除攻击对象
         */
        len = playerAtkList.size();
        for (int i = 0; i < len; i++) {
            Attack atk = playerAtkList.get(i);
            if (atk.isDead()) {
                playerAtkList.remove(i);//TODO: to be optimized
                len--;
                i--;
            }
        }
        len = hostileAtkList.size();
        for (int i = 0; i < len; i++) {
            Attack atk = hostileAtkList.get(i);
            if (atk.isDead()) {
                hostileAtkList.remove(i);//TODO: to be optimized
                len--;
                i--;
            }
        }

        /**
         * 按需移除角色
         */
        len = hostileList.size();
        for (int j = 0; j < len; j++) {
            Character character = hostileList.get(j);
            if (character.getHp() <= 0) {
                hostileList.remove(j);//TODO: to be optimized
                len--;
                j--;
            }
        }

        /**
         * 更新根角色
         */
        rootCharacter.update(deltaTime, environment);

        /**
         * 更新敌人
         */
        len = hostileList.size();
        for (int i = 0; i < len; i++) {
            hostileList.get(i).update(deltaTime, environment);
        }

        /**
         * 帧计数器
         */
        frapTimer += deltaTime;
        fps++;
    }

    /**
     * 以玩家为中心绘制地图
     *
     * @param g 绘图接口
     */
    public void present(Graphics g) {
        final float offsetX = player.getX() - 640;
        final float offsetY = player.getY() - 360;

        present(g, offsetX, offsetY);
    }

    /**
     * 以特定偏移量绘制地图
     *
     * @param g       绘图接口
     * @param offsetX X偏移量
     * @param offsetY Y偏移量
     */
    private void present(Graphics g, float offsetX, float offsetY) {
        g.fill(0xFF000000);


        /**
         * 绘制地图
         */
        /*int indexX = (int)(player.getX()/BLOCK_WIDTH) - 2;
        int indexY = (int)(player.getY()/BLOCK_HEIGHT) - 2;
        backGroundLoader.setIndex(indexX,indexY);
        Bitmap[][] bitmapGroup = backGroundLoader.getBitmapGroup();
        for(int y=0;y<5;y++) {
            for (int x = 0; x < 5; x++) {
                if (bitmapGroup[y][x] != null && !bitmapGroup[y][x].isRecycled()) {
                    int bgX = (int) (-player.getX() + indexX*BLOCK_WIDTH + 1280 / 2)-123+90+x*BLOCK_WIDTH;
                    int bgY = (int) (-player.getY() + indexY*BLOCK_HEIGHT + 720 / 2)-271-30+y*BLOCK_HEIGHT;
                    g.drawBitmap(bitmapGroup[y][x], bgX, bgY);
                }
            }
        }*/
        g.drawPixmap(mapBackGround, -540 - offsetX, -360 - offsetY);
        //g.drawPixmap(Assets.map0_Bg,0,0,(int)(123-90+offsetX), (int) (271+30+offsetY),1280,720);
        /*for (Line line : lines) {
            g.drawLine(line.getPoint1().x - offsetX,
                    line.getPoint1().y - offsetY,
                    line.getPoint2().x - offsetX,
                    line.getPoint2().y - offsetY, 0xFFFFFFFF);
        }*/

        /*byte[] map = environment.getMapForFingPath();
        for(int i=0;i<map.length;i++){
            int x = environment.index2X(i) - 32;
            int y = environment.index2Y(i) - 32;
            g.drawRect((int)(x-offsetX), (int) (y-offsetY),64,64,map[i]<<24 | 0xFF0000);
        }*/

        /**
         * 绘制玩家
         */
        player.present(g, offsetX, offsetY);

        /**
         * 绘制“敌对”角色
         */
        int len = hostileList.size();
        for (int i = 0; i < len; i++) {
            hostileList.get(i).present(g, offsetX, offsetY);
        }

        /**
         * 绘制攻击
         */
        //(玩家)
        len = playerAtkList.size();
        for (int i = 0; i < len; i++) {
            playerAtkList.get(i).present(g, offsetX, offsetY);
        }
        //(敌方)
        len = hostileAtkList.size();
        for (int i = 0; i < len; i++) {
            hostileAtkList.get(i).present(g, offsetX, offsetY);
        }

        /**
         * 绘制特效
         */
        len = effectList.size();
        for (int i = 0; i < len; i++) {
            effectList.get(i).present(g, offsetX, offsetY);
        }

        rootCharacter.present(g, offsetX, offsetY);

        /**
         * 调试代码
         */
        //g.drawText("fps : "+fps,0,20,0xFFFFFFFF,20);
        if (frapTimer >= 1) {
            frapTimer -= 1;
            Log.d("fps", String.valueOf(fps));
            fps = 0;
        }
    }

    /**
     * 玩家执行攻击
     */
    public void PlayerAttack(float dx, float dy) {
        if (!mIsInCD) {
            mWeapons.get(mCurrentWeaponIndex).attack(this, player, dx, dy);
            mIsInCD = true;
        }
    }

    /**
     * 切换武器
     */
    private Weapon setCurrentWeaponIndex(int index) {
        index = (index + mWeapons.size()) % mWeapons.size();
        Weapon weapon = mWeapons.get(index);
        if (index == mCurrentWeaponIndex) return weapon;
        mCurrentWeaponIndex = index;
        mIsInCD = false;
        mAtkDelayTimer = 0;
        player.setWeaponAtk(weapon.getDamage());
        return weapon;
    }

    /**
     * 切换至下一个武器
     */
    public Weapon nextWeapon() {
        return setCurrentWeaponIndex(++mCurrentWeaponIndex);
    }

    /**
     * 切换至上一个武器
     */
    public Weapon prevWeapon() {
        return setCurrentWeaponIndex(--mCurrentWeaponIndex);
    }

    public Weapon getCurrentWeapon() {
        return mWeapons.get(mCurrentWeaponIndex);
    }

    /**
     * 在舞台上添加攻击（玩家）
     */
    public void addAtkPlayer(Attack attack) {
        playerAtkListTemp.add(attack);
    }

    /**
     * 在舞台上添加攻击（敌方）
     */
    public void addAtkHostile(Attack attack) {
        hostileAtkListTemp.add(attack);
    }

    /**
     * 在舞台上添加“敌对”角色
     */
    public void addHostile(Character character) {
        hostileListTemp.add(character);
    }

    /**
     * 在舞台上添加特效
     */
    public void addEffect(Effect effect) {
        effectListTemp.add(effect);
    }

    /**
     * 将游戏对象添加到追踪列表
     *
     * @param gameObject 被添加到追踪列表的对象
     */
    public void addToTrackList(GameObject gameObject) {
        addToTrackList(gameObject, 0);
    }

    /**
     * 将游戏对象添加到追踪列表, 同时指定箭头样式
     *
     * @param gameObject 被添加到追踪列表的对象
     * @param icoIndex   箭头样式的索引
     */
    public void addToTrackList(GameObject gameObject, int icoIndex) {
        trackList.add(gameObject);
        trackListIco.add(icoIndex);
    }

    /**
     * 返回追踪列表，同时移除过期对象
     *
     * @return 追踪列表
     */
    public ArrayList<GameObject> getTrackList() {
        int len = trackList.size();
        for (int j = 0; j < len; j++) {
            GameObject character = trackList.get(j);
            if (character.isDead()) {
                trackList.remove(j);//TODO: to be optimized
                trackListIco.remove(j);
                len--;
                j--;
            }
        }
        return trackList;
    }

    /**
     * @param index 追踪对象的索引
     * @return 对应对象的图标
     */
    public int getTrackIco(int index) {
        return trackListIco.get(index);
    }

    /**
     * @return 玩家对象
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 玩家得分
     *
     * @param score 正为增，负为减
     */
    public void accessScore(int score) {
        int s = this.score;
        if (s + score < 0)
            s = 0;
        else
            s += score;
        this.score = s;
    }

    /**
     * @return 玩家当前得分
     */
    public int getScore() {
        return score;
    }

    /**
     * 设置游戏状态
     *
     * @param stateIndex 游戏状态标志
     */
    public void setState(char stateIndex) {
        gameStateManager.setState(stateIndex);
    }

    /**
     * 切换地图
     * @param mapUid
     */
    public void switchMap(String mapUid){
        gameStateManager.switchMap(mapUid);
    }

    /**
     * 将所以NPC的HP置为0，
     * 结束线程循环，释放资源
     */
    public void dispose() {
        //backGroundLoader.release();
        int len = hostileList.size();
        for (int i = 0; i < len; i++)
            hostileList.get(i).setHp(0);
        len = hostileListTemp.size();
        for (int i = 0; i < len; i++)
            hostileListTemp.get(i).setHp(0);
        Log.i("Stage", "Disposed.");
    }
}
