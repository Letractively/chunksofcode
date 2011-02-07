/*
To change this template, choose Tools | Templates
and open the template in the editor.
 */
package com.myapp.mines.controller;

import java.awt.Color;

import static com.myapp.mines.model.Field.*;

/**
util methods.
@author andre
 */
final class Util {

    private Util() {
    }

    public static final class FieldController {

        private FieldController() {
        }

        private static final Color calculateBombCountColor(int bombcount) {
            switch (bombcount) {
                case 1:
                    return Color.blue.brighter();
                case 2:
                    return Color.green;
                case 3:
                    return Color.red;
                case 4:
                    return Color.blue.darker();
                case 5:
                    return Color.cyan;
                case 6:
                case 7:
                case 8:
                    return Color.darkGray;

                default:
                    return Color.black;
            }
        }

        public static final Color calculateForegroundColor(com.myapp.mines.model.Field f) {
            Color foreGround = Color.black;

            switch (f.getStatusCode()) {
                case STATUS_DEFAULT:
                    break;
                case STATUS_DISPLAY_BOMB_COUNT_ENTERED:
                    foreGround = calculateBombCountColor(f.getNeighbourBombs());
                    break;
                case STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED:
                    break;
                case STATUS_EXPLOSION:
                    break;
                case STATUS_GUESS_WAS_CORRECT:
                    break;
                case STATUS_GUESS_WAS_INCORRECT:
                    break;
                case STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED:
                    break;
                case STATUS_MARKED:
                    foreGround = Color.white;
                    break;
                case STATUS_ERROR_ENTERED_MARKED_FIELD:
                    break;
                case STATUS_ENTERED_NOT_BOMB:
                    foreGround = calculateBombCountColor(f.getNeighbourBombs());
                    break;
            }
            return foreGround;
        }

        public static final Color calculateBackgroundColor(com.myapp.mines.model.Field f) {
            Color backGround = null;

            switch (f.getStatusCode()) {
                case STATUS_DEFAULT:
                    backGround = Color.lightGray;
                    break;
                case STATUS_DISPLAY_BOMB_COUNT_ENTERED:
                    backGround = Color.cyan;
                    break;
                case STATUS_DISPLAY_BOMB_COUNT_GAMEOVER_NOT_ENTERED:
                    backGround = Color.lightGray;
                    break;
                case STATUS_EXPLOSION:
                    backGround = Color.red;
                    break;
                case STATUS_GUESS_WAS_CORRECT:
                    backGround = Color.lightGray;
                    break;
                case STATUS_GUESS_WAS_INCORRECT:
                    backGround = Color.orange;
                    break;
                case STATUS_HERE_WAS_BOMB_NOT_ENTERED_NOT_MARKED:
                    backGround = Color.gray;
                    break;
                case STATUS_MARKED:
                    backGround = Color.black;
                    break;
                case STATUS_ERROR_ENTERED_MARKED_FIELD:
                    backGround = Color.cyan;
                    break;
                case STATUS_ENTERED_NOT_BOMB:
                    backGround = Color.white;
                    break;
            }
            return backGround;
        }

    }

}
