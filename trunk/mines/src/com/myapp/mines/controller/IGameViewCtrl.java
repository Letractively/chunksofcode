package com.myapp.mines.controller;

import com.myapp.mines.model.Game;

/**
the controller object which connects a concrete game gui implementation with
a game object. gui objects for the game have to implement the methods to
be able to respond to common game situations and user commands.
@author andre
 */
public interface IGameViewCtrl {

    String CMD_ABOUT_GAME = "CMD_ABOUT_GAME";
    String CMD_CHEAT_GAME = "CMD_CHEAT_GAME";
    String CMD_CUSTOM_GAME = "CMD_CUSTOM_GAME";
    String CMD_EXIT_GAME = "CMD_EXIT_GAME";
    String CMD_NEW_GAME = "CMD_NEW_GAME";

    /**
    extra information of the game state will be displayed to allow
    cheating the game. e.g. a representation of the game grid with
    the mines will be put to stdout, or a tooltiptext is shown on
    the field...
     */
    void cheat();

    /**
    checks if the game state is won or lost after a field was entered.
    if one of that is the case, the controller has to tell the gui to
    display a message.
     */
    void checkGameState();

    /**
    the game view will ask the user for defining dimensions and mine count.
    then, a new game will be started with the specified parameters.
     */
    void customGame();

    /**
    a gameover message will be displayed.
     */
    void gameLost();

    /**
    a game won message will be displayed.
     */
    void gameWon();

    /**
    returns the model of this controller/view.
    @return the model of this controller/view.
     */
    Game getModel();

    /**
    a info message will be displayed.
     */
    void showGameInfo();

    /**
    a new game with the specified parameters will be created and started.
    @param rows the rows
    @param cols the cols
    @param mines the mines count
     */
    void newGame(int rows, int cols, int mines);

    /**
    all field views will be refreshed to display their actual status.
     */
    void repaintGameView();

    /**
    the guis status bar or equivalent will be set to display this text.
    @param text the new text for the status bar of the game view.
     */
    void setStatusText(String text);

}
