package com.myapp.mines.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.myapp.mines.controller.GameController;
import com.myapp.mines.model.Field;
import com.myapp.mines.model.Game;
import com.myapp.util.swing.Util;
import com.myapp.util.timedate.TimeDateUtil;
import com.myapp.util.log.Log;

/**
a concrete view/controller implementation using a jframe
to interact with the user
@author andre
 */
public class SwingGameView extends GameController {

    private JPanel fieldsPanel;
    private JTextField statusBar;
    private JFrame window;

    /**
    creates a new default game view.
     */
    public SwingGameView() {
        this(Game.DEFAULT_ROWS,
             Game.DEFAULT_COLS,
             Game.DEFAULT_MINES);
    }

    /**
    creates a new game view with a new game using the given parameters.
    @param rows the rows
    @param cols the cols
    @param mines the mines
     */
    public SwingGameView(int rows, int cols, int mines) {
        super(rows, cols, mines);
        setupWindow();
    }

    /**
    creates all field views needed in the new game and sets them up.
     */
    private void initFields() {
        Log.logln("()");
        if (Arrays.asList(
                window.getContentPane().getComponents()).contains(fieldsPanel))
            window.getContentPane().remove(fieldsPanel);

        fieldsPanel = new JPanel(
                new GridLayout(rowsSetByUser, colsSetByUser, 1, 1));

        for (int r = 0; r < game.getRows(); r++)
            for (int c = 0; c < game.getCols(); c++) {
                final Field f = game.getField(r, c);
                SwingFieldView fg = new SwingFieldView(f, this);
                fieldsPanel.add((Component) fg.getGuiObject());
            }

        window.getContentPane().add(fieldsPanel, BorderLayout.CENTER);
    }

    /**
    sets up the components for the first time playing
     */
    private void setupWindow() {
        Log.logln("()");
        window = new JFrame("JMines");
        window.setContentPane(new JPanel(new BorderLayout()));

        initFields();

        statusBar = new JTextField("(c) andre");
        statusBar.setEditable(false);

        window.getContentPane().add(fieldsPanel, BorderLayout.CENTER);
        window.getContentPane().add(statusBar, BorderLayout.SOUTH);

        JMenuItem newGame = new JMenuItem("neu");
        JMenuItem exitGame = new JMenuItem("beenden");
        JMenuItem aboutGame = new JMenuItem("info");
        JMenuItem customGame = new JMenuItem("benutzerdefiniert");
        JMenuItem cheatGame = new JMenuItem("cheat");

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


        JMenu gameMenu = new JMenu("Spiel");
        gameMenu.add(newGame);
        gameMenu.add(customGame);
        gameMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
        gameMenu.add(aboutGame);
        gameMenu.add(cheatGame);
        gameMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
        gameMenu.add(exitGame);


        JMenuBar bar = new JMenuBar();
        bar.add(gameMenu);

        window.setJMenuBar(bar);
        window.pack();
        window.setLocation(Util.calculateScreenCenter(window));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    @Override
    public void showGameInfo() {
        Log.logln("()");
        JOptionPane.showMessageDialog(
                window,
                "author: andre 2009",
                "java_mines",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void customGame() {
        Log.logln("()");
        final JSpinner rows;
        final JSpinner cols;
        final JSpinner mines;

        SpinnerNumberModel rowsSNM, colsSNM, minesSNM;
        rowsSNM = new SpinnerNumberModel(game.getRows(), 5, 50, 1);
        colsSNM = new SpinnerNumberModel(game.getRows(), 5, 50, 1);
        minesSNM = new SpinnerNumberModel(game.getTotalMineCount(), 1, 50, 1);
        rows = new JSpinner(rowsSNM);
        cols = new JSpinner(colsSNM);
        mines = new JSpinner(minesSNM);

        rows.setBorder(BorderFactory.createTitledBorder("Zeilen"));
        cols.setBorder(BorderFactory.createTitledBorder("Spalten"));
        mines.setBorder(BorderFactory.createTitledBorder("Minen"));

        JPanel gridJPanel = new JPanel(new GridLayout(0, 1));
        gridJPanel.add(new JLabel("Definieren Sie die Parameter:"));
        gridJPanel.add(rows);
        gridJPanel.add(cols);
        gridJPanel.add(mines);
        gridJPanel.add(new JLabel("Wollen Sie ein neues Spiel starten?"));

        int answer = JOptionPane.showConfirmDialog(
                window,
                gridJPanel,
                "Custom game",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer != JOptionPane.YES_OPTION)
            return;

        newGame((Integer) rows.getValue(),
                (Integer) cols.getValue(),
                (Integer) mines.getValue());
    }

    @Override
    public void cheat() {
        Log.logln("()");
        for (Field f : game) {
            SwingFieldView fg = (SwingFieldView) game.getAssociatedView(f);

            if (f.isBomb())
                ((JComponent) fg.getGuiObject()).setToolTipText("!! BOMB !!");
        }

        String gameString = game.toString();
        Log.logln("\n" + gameString);
    }

    @Override
    public void setStatusText(String text) {
        Log.logln("(" + text + ")");
        statusBar.setText(text);
    }

    @Override
    public void gameWon() {
        Log.logln("()");
        JOptionPane.showMessageDialog(
                window,
                "You won! Your time needed was " + TimeDateUtil.formatTime(game.getTime()),
                "Game won",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void gameLost() {
        Log.logln("()");
        JOptionPane.showMessageDialog(
                window,
                "You lost!!!!  Your time needed was " + TimeDateUtil.formatTime(game.getTime()),
                "Loooooooooooser !!!!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void newGameStarted() {
        Log.logln("()");
        Point currentLocation2 = window.getLocation();
        initFields();
        window.pack();
        window.setLocation(currentLocation2);
    }

    @Override
    protected void exitGame() {
        Log.logln("()");
        System.exit(0);
    }

    private static final String NL = System.getProperty("line.separator");
    
    @Override
    protected void showError(String title, String message, String stackTrace) {
        Log.logln("("+title + "," + message +")");
        JOptionPane.showMessageDialog(window,
                                      "Technical Information: " + NL + stackTrace,
                                      title,
                                      JOptionPane.ERROR_MESSAGE);
    }

}
