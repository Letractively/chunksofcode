package com.myapp.mines.model;

import com.myapp.mines.controller.IFieldViewCtrl;
import com.myapp.util.log.Log;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
represents a minesweeper game. it consists of an instance of a FieldGrid
whith the containing Fields, manages the time of the game and calculates
the actual state of the game. 
@author andre
 */
public class Game
        implements Iterable<Field> {

    public static final int DEFAULT_ROWS = 15;
    public static final int DEFAULT_COLS = 20;
    public static final int DEFAULT_MINES = 30;
    /*

     */
    private GameGrid grid;
    private Set<Field> fieldsEntered = new HashSet<Field>();
    private final int bombcount;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private long startTime = Long.MIN_VALUE;
    private long finishTime = Long.MAX_VALUE;

    /**
    creates a new game with a grid of the specified size
    and the specified mines.
    @param rows the row count of the grid
    @param cols the column count of the grid
    @param mines the count of the randomly spreaded mines
     */
    public Game(int rows, int cols, int mines) {
        Log.logln("(" + rows + ", " + cols + ", " + mines + ") Create a new game.");
        bombcount = mines;
        grid = new GameGrid(rows, cols, mines, this);
        Log.logln(System.getProperty("line.separator") + grid);
    }

    /**
    creates a new game with default row count, column count and mine count.
    the mines will be spreaded randomly.
     */
    public Game() {
        this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_MINES);
    }

    /**
    returns the milliseconds the game took.
    @return 0 if the game was not started, the current time of a game in action or the
    duration of the game from start to end.
     */
    public long getTime() {
        if (startTime == Long.MIN_VALUE)
            /*game not started yet*/
            return 0L;
        else {
            if (finishTime == Long.MAX_VALUE)
                /*game not finished yet*/
                return System.currentTimeMillis() - startTime;
            else
                /*time needed to win or lose the game*/
                return finishTime - startTime;
        }
    }

    /**
    invoked by the field to notify the game to stop.
     */
    void gameOver() {
        Log.logln("() GAME OVER.");
        gameOver = true;
        finishTime = System.currentTimeMillis();
    }

    /**
    returns if the game was lost.
    @return if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
    returns if the game was won.
    @return if the game is won yet.
     */
    public boolean isGameWon() {
        return gameWon;
    }

    /**
    returns the number of mines contained in this game.
    @return the number of mines contained in this game.
     */
    public int getTotalMineCount() {
        return bombcount;
    }

    /**
    invoked when a field was entered without an explosion.
    the game needs to be notified that a field was entered without an
    explosion because it is responsible to count thr remaining bombs
    to notice that the game was won. also, the time counter is started
    on entering the first field.
    @param enteredWithoutBaaaaam the field
     */
    void fieldWasEntered(Field enteredWithoutBaaaaam) {
        if (startTime == Long.MIN_VALUE)
            startTime = System.currentTimeMillis();

        fieldsEntered.add(enteredWithoutBaaaaam);

        int fieldsWithoutBombCount = grid.rows;
        fieldsWithoutBombCount *= grid.cols;
        fieldsWithoutBombCount -= bombcount;

        if (fieldsEntered.size() >= fieldsWithoutBombCount) {
            gameWon = true;
            finishTime = System.currentTimeMillis();
        }
    }

    /**
    returns the count of rows in this game.
    @return the count of rows in this game.
     */
    public int getRows() {
        return grid.rows;
    }

    @Override
    public String toString() {
        return grid.toString();
    }

    /**
    returns the count of cols in this game.
    @return the count of cols in this game.
     */
    public int getCols() {
        return grid.cols;
    }

    /**
    you may need to connect fields to specific objects, like gui objects.
    use this method to register fields and key object pairs.
    @param f the field to map
    @param o the corresponding object key
    @throws IllegalStateException if a field OR oject is registered twice.
     */
    public void map(Field f, IFieldViewCtrl o) {
        grid.mapping.map(f, o);
    }

    /**
    returns the view mapped to this field.
    @param key the corresponding field
    @return returns the view mapped to this field, null if it does not exist.
     */
    public IFieldViewCtrl getAssociatedView(Field key) {
        return grid.mapping.getMapping(key);
    }

    /**
    returns the field at the specified position.
    @param row the row
    @param col the col
    @return the field at the specified position.
     */
    public Field getField(int row, int col) {
        return grid.getField(row, col);
    }

    /**
    returns an iterator of all fields contained in this game.
    allows using the game object within a foreach loop accessing the fields.
    the order is row-wise from row 0 to row last, and each row
    iterates over its elements from col 0 to col last.
    @return a iterator of all fields contained in this game.
     */
    public Iterator<Field> iterator() {
        return grid.iterator();
    }

}
