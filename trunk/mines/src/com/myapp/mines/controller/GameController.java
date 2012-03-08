package com.myapp.mines.controller;

import com.myapp.mines.model.Field;
import com.myapp.mines.model.Game;
import com.myapp.util.log.Log;
import com.myapp.util.timedate.TimeDateUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
the controller logic of the game model and the corresponding view
is implemented in this base class which is designed to inherit view
implementations from.
@author andre
 */
public abstract class GameController
        implements ActionListener,
                   IGameViewCtrl {

    protected int colsSetByUser;
    protected Game game;
    protected int minesSetByUser;
    protected int rowsSetByUser;

    /**
    created a new GameController with the specified parameters.
    @param rows the rows
    @param cols the cols
    @param mines the rows
     */
    protected GameController(int rows, int cols, int mines) {
        rowsSetByUser = rows;
        colsSetByUser = cols;
        minesSetByUser = mines;
        game = new Game(rowsSetByUser, colsSetByUser, minesSetByUser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        Log.logln("(ActionEvent: Command=" + cmd + ")");

        try {
            if (cmd.equals(CMD_ABOUT_GAME))
                showGameInfo();
            else if (cmd.equals(CMD_EXIT_GAME))
                exitGame();
            else if (cmd.equals(CMD_CUSTOM_GAME))
                customGame();
            else if (cmd.equals(CMD_NEW_GAME))
                newGame(rowsSetByUser, colsSetByUser, minesSetByUser);
            else if (cmd.equals(CMD_CHEAT_GAME))
                cheat();
            
        } catch (Exception e1) {
            String title = "Fehler", msg = e1.getMessage();
            
            if (msg != null && ! msg.trim().isEmpty()) 
                title += ": " + msg;
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e1.printStackTrace(ps);
            ps.close();
            String stackTraceString = baos.toString();
            showError(title, msg, stackTraceString);
        }
    }

    protected abstract void showError(String title, String message, String stackTrace);

    /**
    being invoked if the game exits, may be something like System.exit(0);
     */
    protected abstract void exitGame();

    @Override
    public void checkGameState() {
        String newStatus = null;

        if (game.isGameWon()) {
            newStatus = "Game won! Your time was: " + time();
            gameWon();
        }
        else if (game.isGameOver()) {
            newStatus = "Game Lost! Your time was: " + time();
            gameLost();
        }

        if (newStatus != null) {
            setStatusText(newStatus);
            repaintGameView();
        }
    }

    private String time() {
        return TimeDateUtil.formatTime(game.getTime());
    }

    @Override
    public final Game getModel() {
        return game;
    }

    @Override
    public void repaintGameView() {
        for (Field f : game)
            game.getAssociatedView(f).repaintFieldView();
    }

    @Override
    public final void newGame(int rows, int cols, int mines) {
        rowsSetByUser = rows;
        colsSetByUser = cols;
        minesSetByUser = mines;

        game = new Game(rowsSetByUser, colsSetByUser, minesSetByUser);
        newGameStarted();
        repaintGameView();
        setStatusText("new game started! (" +
                      "rows=" + rows + ", " +
                      "cols=" + cols + ", " +
                      "mines=" + mines + ")");
    }

    /**
    the view will re-render fully after a new game was started.
     */
    protected abstract void newGameStarted();

}
