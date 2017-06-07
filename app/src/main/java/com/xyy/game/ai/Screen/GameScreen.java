package com.xyy.game.ai.Screen;

import com.xyy.game.ai.Character.NPC.RootCharacter;
import com.xyy.game.ai.GameDataManager;
import com.xyy.game.ai.GameState;
import com.xyy.game.ai.GameStateManager;
import com.xyy.game.ai.GameState_Exit;
import com.xyy.game.ai.GameState_FirstIn;
import com.xyy.game.ai.GameState_GameOver;
import com.xyy.game.ai.GameState_MapSwitching;
import com.xyy.game.ai.GameState_Paused;
import com.xyy.game.ai.GameState_Running;
import com.xyy.game.ai.MapBuilder;
import com.xyy.game.ai.Stage;
import com.xyy.game.ai.Map;
import com.xyy.game.framework.Game;
import com.xyy.game.framework.Graphics;
import com.xyy.game.framework.Input;
import com.xyy.game.framework.Screen;

/**
 * 游戏界面
 * Created by ${XYY} on ${2016/5/27}.
 */
public class GameScreen extends Screen implements GameScreenOperation, GameStateManager {

    private GameState[] gameStates;
    private GameState currentState;

    private String worldUid;

    private Stage stage;

    private MapBuilder mMapBuilder;

    public GameScreen(Game game, Map map) {
        super(game);

        worldUid = map.getUid();

        stage = new Stage(map, this, game.getGraphics());

        gameStates = new GameState[]{new GameState_Running(this, stage),
                new GameState_Paused(this, stage),
                new GameState_Exit(this, stage),
                new GameState_GameOver(this, stage)};

        //设置First_In为初始状态，结束后等待垃圾回收
        currentState = new GameState_FirstIn(this, stage);
    }

    public void initialize(Map map){
        worldUid = map.getUid();

        stage.initialize(map);

        //设置First_In为初始状态，结束后等待垃圾回收
        currentState = new GameState_FirstIn(this, stage);
    }

    /**
     * 设置游戏状态
     *
     * @param stateIndex 游戏状态标志
     */
    @Override
    public void setState(char stateIndex) {
        currentState.exit();
        currentState = gameStates[stateIndex];
        currentState.enter();
    }

    @Override
    public Input getInput() {
        return game.getInput();
    }

    @Override
    public Graphics getGraphics() {
        return game.getGraphics();
    }

    /*public Audio getAudio(){
        return game.getAudio();
    }*/

    @Override
    public void exit() {
        game.exit();
    }

    @Override
    public void reLoad() {
        game.setScreen(new GameLoadingScreen(game, worldUid));
    }

    @Override
    public void toMapsSelectingScreen() {
        game.setScreen(new MapsSelectingScreen(game));
    }

    public void switchMap(String mapUid){
        currentState.exit();
        currentState = new GameState_MapSwitching(this,stage,mapUid);
        //currentState.enter();

        mMapBuilder = new MapBuilder(mapUid,game,false);
        new Thread(mMapBuilder).start();
    }

    @Override
    public boolean isMapBuilt() {
        Map map = mMapBuilder.getMap();
        boolean res = map != null;
        if(res){
            mMapBuilder = null;
            initialize(map);
        }
        return res;
    }

    @Override
    public void update(float deltaTime) {
        currentState.update(deltaTime);
    }

    @Override
    public void present(float deltaTime) {
        currentState.present(deltaTime);
    }

    @Override
    public void pause() {
        GameDataManager.save(game.getFileIO());
        //当活动被暂停时，将游戏暂停
        setState(GameState.PAUSED);
   //     GameDataManager.writeData(game.getFileIO(), "data_blew.dat", new double[][]{RootCharacter.sWeights});
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        currentState.dispose();
    }

    @Override
    public boolean onBack() {
        currentState.onBack();
        return true;
    }


}
