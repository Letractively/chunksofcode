package com.myapp.games.towerdefense;

import javax.swing.JComponent;

import com.myapp.games.framework.awt.AWTGame;
import com.myapp.games.towerdefense.model.GameModel;
import com.myapp.games.towerdefense.model.Grid;

public class TowerDefenseGame extends AWTGame {

    public static boolean DEBUG_A_STAR = false;

    private static final int
        DEFAULT_GAME_GRID_DIMENSION = 32,
        DEFAULT_GAME_ROWS = 10,
        DEFAULT_GAME_COLS = 10;
    
    
    private TDRenderer view;
    private final GameModel model;


    public TowerDefenseGame(int gridHeight, int rows, int cols) {
        this.model = new GameModel(gridHeight, rows, cols, this);
        this.view = new TDRenderer(this);
    }

    public TowerDefenseGame() {
        this(
           DEFAULT_GAME_GRID_DIMENSION,
           DEFAULT_GAME_ROWS,       
           DEFAULT_GAME_COLS
        );
    }
    
    final GameModel model() {
        return model;
    }

    @Override
    public int getSurfaceHeight() {
        Grid g = model.getGrid();
        return g.tileDim * g.rows;
    }

    @Override
    public int getSurfaceWidth() {
        Grid g = model.getGrid();
        return g.tileDim * g.cols;
    }

    @Override
    public JComponent getUIComponent() {
        return view;
    }

    @Override
    public boolean isExitGame() {
        return model.isGameOver();
    }

    @Override
    protected void executeGameLogic(long gameTimeNow, long gameTimeDelta) {
        model.updateState(gameTimeNow, gameTimeDelta);

    }

}
