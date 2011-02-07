package com.myapp.mines.model;

import static com.myapp.mines.model.Field.STATUS_DEFAULT;
import static com.myapp.mines.model.Field.STATUS_DISPLAY_BOMB_COUNT_ENTERED;
import static com.myapp.mines.model.Field.STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED;
import static com.myapp.mines.model.Field.STATUS_ENTERED_NOT_BOMB;
import static com.myapp.mines.model.Field.STATUS_ERROR_ENTERED_MARKED_FIELD;
import static com.myapp.mines.model.Field.STATUS_EXPLOSION;
import static com.myapp.mines.model.Field.STATUS_GUESS_WAS_CORRECT;
import static com.myapp.mines.model.Field.STATUS_GUESS_WAS_INCORRECT;
import static com.myapp.mines.model.Field.STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED;
import static com.myapp.mines.model.Field.STATUS_MARKED;

/**
util collection.
@author andre
 */
final class Util {
    private Util() {}

    static final class Field {
        private Field() {}

        static final String getStatusString(com.myapp.mines.model.Field f) {
            int status = f.getStatusCode();

            switch (status) {
                case STATUS_DEFAULT:
                    return " ";
                case STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED:
                    return "X";
                case STATUS_EXPLOSION:
                    return "#";
                case STATUS_MARKED:
                    return "?";
                case STATUS_GUESS_WAS_INCORRECT:
                    return "!";
                case STATUS_GUESS_WAS_CORRECT:
                    return "x";
                case STATUS_DISPLAY_BOMB_COUNT_ENTERED:
                    return f.bombCountString();
                case STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED:
                    return f.bombCountString();
                case STATUS_ERROR_ENTERED_MARKED_FIELD:
                    return "err: entered marked field";
                case STATUS_ENTERED_NOT_BOMB:
                    return f.bombCountString();
            }

            throw new RuntimeException("wtf ???");
        }

        static final int getStatusCode(com.myapp.mines.model.Field f) {
            boolean gameover = f.getGame().isGameOver();
            boolean entered = f.isEntered();
            boolean marked = f.isMarked();
            boolean bomb = f.isBomb();

            /*this was the bomb which blew you to hell!*/
            if (gameover && entered && !marked && bomb)
                return STATUS_EXPLOSION;

            /*this is an entered field without a bomb*/
            if (entered && !marked && !bomb)
                return STATUS_ENTERED_NOT_BOMB;

            /*NOT POSSIBLE, cannot enter a marked field...*/
            if (entered && marked)
                return STATUS_ERROR_ENTERED_MARKED_FIELD;


            if (!bomb && !marked) {
                /*returning the count of the surrounding fields on
                a entered field which is not the bomb*/
                if (entered && !gameover)
                    return STATUS_DISPLAY_BOMB_COUNT_ENTERED;

                /*show the count of the surrounding bombs on a field after gameover
                which was not marked and not a bomb*/
                if (gameover)
                    return STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED;
            }


            if (gameover && !entered) {
                /*show a small x at the field where the user guessed right*/
                if (marked && bomb)
                    return STATUS_GUESS_WAS_CORRECT;

                /*show an exclamation mark at the field where the user guessed false*/
                if (marked && !bomb)
                    return STATUS_GUESS_WAS_INCORRECT;

                /*gameover, here was a bomb the field was neither marked nor entered*/
                if (!marked && bomb)
                    return STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED;
            }


            if (!gameover && !entered) {
                /*display nothing on a non-touched, non-marked field during gameplay*/
                if (!marked)
                    return STATUS_DEFAULT;

                /*display a question mark on a field*/
                if (marked)
                    return STATUS_MARKED;
            }

            throw new RuntimeException("invalid status, should not happen...");
        }

        static final String toString(com.myapp.mines.model.Field f) {
            StringBuilder bui = new StringBuilder();

            int row = f.row;
            int col = f.col;
            boolean bomb = f.isBomb();
            boolean entered = f.isEntered();
            boolean marked = f.isMarked();

            bui.append("[row=");
            bui.append(row < 10 ? " " + row : row);
            bui.append(", col=");
            bui.append(col < 10 ? " " + col : col);
            bui.append(", ");
            bui.append(bomb ? "B" : "b");
            bui.append(", ");
            bui.append(entered ? "E" : "e");
            bui.append(", ");
            bui.append(marked ? "M" : "m");
            bui.append(']');

            return bui.toString();
        }

    }

    static final class GameGrid {
        private GameGrid() {}

        static final String fieldGridToString(com.myapp.mines.model.GameGrid gg) {
            int rows = gg.rows;
            int cols = gg.cols;
            int bombs = gg.bombs;
            com.myapp.mines.model.Field[][] fieldArray2D = gg.getFieldArray2D();


            StringBuilder bui = new StringBuilder();

            /*first the header:*/
            bui.append("GRID: rows='");
            bui.append(rows);
            bui.append("', cols='");
            bui.append(cols);
            bui.append("', bombs='");
            bui.append(bombs);
            bui.append("'\n");

            /*then a long line:*/
            bui.append("______________");
            for (int i = 0; i < fieldArray2D[0].length; i++)
                bui.append("___");
            bui.append("______________");

            /*then an empty line:*/
            bui.append("\n|              ");
            for (int i = 0; i < fieldArray2D[0].length; i++)
                bui.append("   ");
            bui.append("            |");

            /*then the numbers of the columns:*/
            bui.append("\n| col:        ");
            for (int i = 0; i < cols; i++) {
                if (i < 10)
                    bui.append(' ');
                bui.append(i);
                bui.append(' ');
            }
            bui.append("             |");

            /*then an empty line:*/
            bui.append("\n|              ");
            for (int i = 0; i < fieldArray2D[0].length; i++)
                bui.append("   ");
            bui.append("            |");

            /*each field of each row with beginning and ending row-num:*/
            for (int i = 0; i < fieldArray2D.length; i++) {
                bui.append("\n| row: " + (i < 10 ? " " + i : i) + "    ");
                com.myapp.mines.model.Field[] row = fieldArray2D[i];
                for (int j = 0; j < row.length; j++)
                    bui.append("  " + (row[j].isBomb() ? "X" : "."));
                bui.append("     row: " + (i < 10 ? " " + i : i) + "  |");
            }

            /*then an empty line:*/
            bui.append("\n|              ");
            for (int i = 0; i < fieldArray2D[0].length; i++)
                bui.append("   ");
            bui.append("            |");

            /*then the numbers of the columns again:*/
            bui.append("\n| col:        ");
            for (int i = 0; i < cols; i++) {
                if (i < 10)
                    bui.append(' ');
                bui.append(i);
                bui.append(' ');
            }
            bui.append("             |");

            /*finally a long line:*/
            bui.append("\n|");
            bui.append("______________");
            for (int i = 0; i < fieldArray2D[0].length; i++)
                bui.append("___");
            bui.append("____________");
            bui.append("|\n");

            return bui.toString();
        }
    }
}
