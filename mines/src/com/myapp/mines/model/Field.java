package com.myapp.mines.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
represents a field which may contain a mine and
will explode if has a mine and is being entered.
 *
it may be marked to notice the player that this is a bomb potentially.
 *
@author andre
 */
public class Field {

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED = 1;
    public static final int STATUS_EXPLOSION = 2;
    public static final int STATUS_MARKED = 3;
    public static final int STATUS_GUESS_WAS_INCORRECT = 4;
    public static final int STATUS_GUESS_WAS_CORRECT = 5;
    public static final int STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED = 6;
    public static final int STATUS_DISPLAY_BOMB_COUNT_ENTERED = 7;
    public static final int STATUS_ERROR_ENTERED_MARKED_FIELD = 8;
    public static final int STATUS_ENTERED_NOT_BOMB = 9;
    /*

     */
    private Game game;
    private Set<Field> neighbours = new HashSet<Field>(8);
    public final int row;
    public final int col;
    private boolean bomb = false;
    private boolean entered = false;
    private boolean marked = false;
    private int neighbourBombs = -1;

    /**
    creates a new field with default status.
    (no bomb, not entered, not marked, not gameover)
    @param row the row position of the field in the game.
    @param col the column position of the field in the game.
    @param game the game containing this field.
     */
    Field(int row, int col, Game game) {
        this.row = row;
        this.col = col;
        this.game = game;
    }

    /**
    enters this field. if it contains a bomb, the game will be over.
    if the game is over, the field was already entered or the field is marked
    as a bomb, nothing will happen.
    @throws com.myapp.mines.model.Baaaaaam if the field contained a bomb.
     */
    public void enter() throws Baaaaaam {

        if (marked || entered || isGameOver())
            return;

        entered = true;

        if (bomb) {
            game.gameOver();
            throw new Baaaaaam(this);
        }

        game.fieldWasEntered(this);

        if (neighbourBombs == 0)
            for (Field n : neighbours)
                n.enter();
    }

    /**
    returns the game in which this field is used.
    @return the game in which this field is used.
     */
    public Game getGame() {
        return game;
    }

    /**
    a player may mark some mines during playing.
    these marks will be counted, and if the marks are implicitly
    identifying a field clearly to be no bomb, this method will
    enter all neighbour fields.
    in other words if this is invoked on a field with x bombs in
    its neighbourhood and x marks in its neighbourhood, it will enter
    all neighbourfield.
    you cannot solve fields of a unentered field.
    note that it is not ensured that the marks are set on
    the right positions. if the player sets marks on the wrong position,
    and solves the neighbourhood, the game will be over.
    @return if one of the neighbour was entered or not
    @throws Baaaaaam if a field with a bomb was entered
     */
    public boolean solveNeighbourhood() throws Baaaaaam {
        if (!entered)
            return false;

        if (neighbourBombs == 0)
            /*this method would change nothing in this case.*/
            return false;

        /*count the marks in da hood*/
        int marks = 0;
        for (Field n : neighbours)
            if (n.isMarked())
                marks++;

        if (marks != neighbourBombs)
            return false;

        /*enter neighbours...
        really scary, since you have to be sure that flags are set right...*/
        for (Field n : neighbours)
            if (!n.isMarked())
                n.enter();

        return true;
    }

    /**
    returns a collection containing all neighbours of this field.
    @return a collection containing all neighbours of this field.
     */
    public Collection<Field> getNeighbours() {
        return neighbours;
    }

    /**
    is this field marked as bomb?
    @return true if this field is marked as bomb
     */
    public boolean isMarked() {
        return marked;
    }

    /**
    toggles the marking state of this field
     */
    public void toggleMarked() {
        setMarked(!marked);
    }

    /**
    sets the marking state of this field
    if someone try to mark a entered field, nothing will happen.
     *
    @param isMarked the new marking state
     */
    public void setMarked(boolean isMarked) {
        if (entered)
            return;

        marked = isMarked;
    }

    /**
    has this field been entered yet?
    @return if this field has been entered yet
     */
    public boolean isEntered() {
        return entered;
    }

    /**
    is this field a bomb?
    @return if this field is a bomb
     */
    public boolean isBomb() {
        return bomb;
    }

    /**
    sets the bomb state of this field to the specified value.
    @param value the new bomb state
     */
    void setBomb(boolean value) {
        bomb = value;
    }

    /**
    causes the field to calculate its neighbours.
    the parameter is needed because until the fieldgrid constructor (which fills
    the fieldarray2D with new fields) did not finish its work, this will
    throw a nullpointer exception because this' "game" field's grid instance
    will not be assigned earlier.
    @param grid the fieldgrid containing the fields.
     */
    void initNeighbours(GameGrid grid) {

        int totalRows = grid.rows;
        int totalCols = grid.cols;

        int lowerRow = (row + 1) < totalRows ? (row + 1) : totalRows;
        int rightHandCol = (col + 1) < totalCols ? (col + 1) : totalCols;

        int upperRow = (row - 1) >= 0 ? row - 1 : 0;
        int leftHandCol = (col - 1) >= 0 ? col - 1 : 0;

        for (int r = upperRow; r <= lowerRow; r++)
            for (int c = leftHandCol; c <= rightHandCol; c++) {
                Field f = grid.getField(r, c);

                if (f != null && f != this)
                    neighbours.add(f);
            }

        countNeighbourBombs();
    }

    /**
    returns the count of the surrounding bombs.
    @return the count of the surrounding bombs.
     */
    public synchronized int getNeighbourBombs() {
        if (neighbourBombs == -1)
            countNeighbourBombs();

        return neighbourBombs;
    }

    /**
    a string representing the status of this field.
    @return a string representing the status of this field.
     */
    public String getStatus() {
        return Util.Field.getStatusString(this);
    }

    /** causes the field to count its surrounding bombs*/
    private void countNeighbourBombs() {
        if (neighbours.size() == 0)
            throw new IllegalStateException("neighbours not initialized");

        int count = 0;
        for (Field n : neighbours)
            if (n.bomb)
                count++;

        neighbourBombs = count;
    }

    /**
    returns a String with the countof the boms in da hood,
    or a empty string if there are no neighbours
    @return a String with the count of the boms in da hood,
    or a empty string if there are no neighbours*/
    String bombCountString() {
        return neighbourBombs == 0 ? " " : "" + neighbourBombs;
    }

    /**
    returns if the game is over.
    @return true if the game was lost.
     */
    private boolean isGameOver() {
        return game.isGameOver();
    }

    /**
    ths status code of this game will be calculated from its current state.
    @return the status code of this game.
     */
    public int getStatusCode() {
        return Util.Field.getStatusCode(this);
    }

    @Override
    public String toString() {
        return Util.Field.toString(this);
    }

}
