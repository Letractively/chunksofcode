package com.myapp.mines.view.wings;

import java.awt.Color;

import org.wings.SBorderLayout;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SMenu;
import org.wings.SMenuBar;
import org.wings.SMenuItem;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextField;

import com.myapp.mines.controller.GameController;
import com.myapp.mines.model.Field;
import com.myapp.mines.model.Game;
import com.myapp.util.log.Log;
import com.myapp.util.timedate.TimeDateUtil;

/**
a concrete view/controller implementation using wings
to interact with the user
@author andre
 */
public class WingsGameView extends GameController {

    
    private static final String NL = System.getProperty("line.separator");
    
    private SFrame frame;
    private STextField statusBar;

    public WingsGameView() {
        this(Game.DEFAULT_ROWS,
             Game.DEFAULT_COLS,
             Game.DEFAULT_MINES);
    }

    public WingsGameView(int rows, int cols, int mines) {
        super(rows, cols, mines);
        setupWindow();
    }

    private void setupWindow() {
        frame = new SFrame("Mines Game using DWR and the WingS framework");

        statusBar = new STextField("(c) Andre Ragg 2009");
        statusBar.setEditable(false);
        statusBar.setPreferredSize(new SDimension("100%", "35"));

        frame.setContentPane(new SPanel(new SBorderLayout()));
        frame.getContentPane().add(statusBar, SBorderLayout.SOUTH);
        frame.getContentPane().add(createMenuBar(), SBorderLayout.NORTH);

        initComponents();
        frame.setVisible(true);
    }

    private void initComponents() {
        SPanel fieldsPanel = new SPanel(
                new SGridLayout(
                rowsSetByUser, colsSetByUser, 1, 1));
        fieldsPanel.setBackground(Color.BLACK);

        for (int r = 0; r < game.getRows(); r++)
            for (int c = 0; c < game.getCols(); c++) {
                final Field f = game.getField(r, c);
                WingsFieldView fg = new WingsFieldView(f, this);
                fieldsPanel.add((SComponent) fg.getGuiObject());
            }

        frame.getContentPane().add(fieldsPanel, SBorderLayout.CENTER);
    }

    @Override
    public void newGameStarted() {
        Log.logln("() NEW GAME STARTED!");
        initComponents();
    }

    @Override
    public void setStatusText(String text) {
        statusBar.setText(text);
    }

    @Override
    public void gameWon() {
        Log.logln("() GAME WON!");
        SOptionPane.showMessageDialog(
                frame,
                "You won! Your time needed was " +
                TimeDateUtil.formatTime(game.getTime()),
                "Game won",
                SOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void cheat() {
        for (Field f : game) {
            if (f.isBomb()) {
                WingsFieldView fieldView;
                fieldView = ((WingsFieldView) game.getAssociatedView(f));
                fieldView.setToolTipText("!! BOMB !!");
            }
        }
    }

    @Override
    public void customGame() {
        throw new UnsupportedOperationException("This Action is not yet implemented!");
    }

    @Override
    public void gameLost() {
        SOptionPane.showMessageDialog(
                frame,
                "You lost!!!!  Your time needed was " +
                TimeDateUtil.formatTime(game.getTime()),
                "Loooooooooooser !!!!",
                SOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showGameInfo() {
        SOptionPane.showMessageDialog(
                frame,
                "(c) Andre Ragg 2008",
                "Mines Game using DWR and the WingS framework",
                SOptionPane.INFORMATION_MESSAGE);
    }

    private SMenuBar createMenuBar() {
        SMenuItem newGame = new SMenuItem("neu");
        SMenuItem exitGame = new SMenuItem("beenden");
        SMenuItem aboutGame = new SMenuItem("info");
        SMenuItem customGame = new SMenuItem("benutzerdefiniert");
        SMenuItem cheatGame = new SMenuItem("cheat");

        newGame.addActionListener(this);
        exitGame.addActionListener(this);
        aboutGame.addActionListener(this);
        customGame.addActionListener(this);
        cheatGame.addActionListener(this);

        newGame.setActionCommand(CMD_NEW_GAME);
        exitGame.setActionCommand(CMD_EXIT_GAME);
        aboutGame.setActionCommand(CMD_ABOUT_GAME);
        customGame.setActionCommand(CMD_CUSTOM_GAME);
        cheatGame.setActionCommand(CMD_CHEAT_GAME);

        SMenu gameMenu = new SMenu("Spiel");
        gameMenu.add(newGame);
        gameMenu.add(customGame);
        gameMenu.add(aboutGame);
        gameMenu.add(cheatGame);
        gameMenu.add(exitGame);

        SMenuBar bar = new SMenuBar();
        bar.add(gameMenu);
        return bar;
    }

    @Override
    protected void exitGame() {
        frame.setVisible(false);
    }

    @Override
    protected void showError(String title, String message, String stackTrace) {
        SOptionPane.showMessageDialog(frame,
                                      "The stacktrace: " + NL + stackTrace,
                                      title,
                                      SOptionPane.ERROR_MESSAGE);
    }

}
