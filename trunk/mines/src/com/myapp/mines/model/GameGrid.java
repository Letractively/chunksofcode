package com.myapp.mines.model;

import com.myapp.mines.controller.IFieldViewCtrl;
import com.myapp.util.log.Log;
import java.util.Iterator;
import java.util.Random;

/**
Class to encapsulate the positioning and
initialisation logic from the game class.
@author andre
 */
class GameGrid implements Iterable<Field> {

    private static final Random RANDOM = new Random();

    private Field[][] fieldArray2D;
    private Game game;
    final int rows;
    final int cols;
    final int bombs;
    FieldMapping<IFieldViewCtrl> mapping;

    /**
    creates a new Grid with the specified dimensions and the given bomb count
    for the given game.
    @param rows the rows
    @param cols the cols
    @param bombs the bombs
    @param game the game. fields will notify the game that it is over.
     */
    GameGrid(int rows, int cols, int bombs, Game game) {
        Log.logln("(" + rows + ", " + cols + ", " + bombs + ", game)");
        checkParameters(rows, cols, bombs);

        this.rows = rows;
        this.cols = cols;
        this.bombs = bombs;
        this.game = game;
        mapping = new FieldMapping<IFieldViewCtrl>();

        initNewGrid();
    }

    /**
    returns the field at the specified position.
    @param row the row
    @param col the col
    @return the field at the specified position, or null if position is invalid.
     */
    Field getField(int row, int col) {
        if (row < 0 || row >= rows)
            return null;

        if (col < 0 || col >= cols)
            return null;

        return fieldArray2D[row][col];
    }

    /**
    creates all fields and initializes them.
     */
    private void initNewGrid() {
        Log.logln("()");

        fieldArray2D = new Field[rows][cols];
        for (int r = 0; r < this.rows; r++)
            for (int c = 0; c < this.cols; c++)
                fieldArray2D[r][c] = new Field(r, c, game);


        int counter = 0;
        while (counter < bombs) {
            Field f = fieldArray2D[RANDOM.nextInt(rows)][RANDOM.nextInt(cols)];
            if (f.isBomb())
                continue;
            f.setBomb(true);
            counter++;
        }

        for (Field f : this) {
            f.initNeighbours(this);
        }
    }

    /**
    checks if the dimensions are not too small and not too big.
    the bombcount will be checked also if it is too high or too low.
    @param rows the rows
    @param cols the cols
    @param bombs the bombs
    @throws com.myapp.util.swing.datechooser.images IllegalArgumentException if not legal
     */
    private static void checkParameters(int rows, int cols, int bombs) {
        Log.logln("(" + rows + ", " + cols + ", " + bombs + ")");
        if (rows < 5 || cols < 5)
            throw new IllegalArgumentException(
                    "rows[" + rows + "] < 5 || cols[" + cols + "] < 5");

        if (bombs >= (rows * cols - 5))
            throw new IllegalArgumentException(
                    "bombs[" + bombs + "]" +
                    " >= " +
                    "(rows[" + rows + "] * cols[" + cols + "] - 5)");

        if (rows > 100 || cols > 100)
            throw new IllegalArgumentException("rows>100 || cols>100");
    }

    @Override
    public String toString() {
        return Util.GameGrid.fieldGridToString(this);
    }

    /**
    returns the core array which holds the fields.
    @return the core array which holds the fields.
     */
    final Field[][] getFieldArray2D() {
        return fieldArray2D;
    }

    public Iterator<Field> iterator() {
        Log.logln("()");
        return new Iterator<Field>() {

            private int _row = 0;
            private int _col = 0;

            @Override
            public boolean hasNext() {
                return _row < fieldArray2D.length;
            }

            @Override
            public Field next() {
                /*grab element at current position*/
                Field f = getField(_row, _col);

                /*jump to next element for the next "next()" call*/
                if (_col == fieldArray2D[0].length - 1) {
                    _col = 0;
                    _row++;
                }
                else
                    _col++;

                return f;
            }

            @Override
            public void remove() {
                /*not needed. fieldgrid elements are not designed
                to be modified after construction*/
                throw new UnsupportedOperationException();
            }

        };
    }

}
