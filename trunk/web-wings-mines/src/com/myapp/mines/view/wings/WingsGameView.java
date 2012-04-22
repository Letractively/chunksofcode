package com.myapp.mines.view.wings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.wings.SBorderLayout;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SFont;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SMenu;
import org.wings.SMenuBar;
import org.wings.SMenuItem;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextArea;
import org.wings.STextField;

//import sun.nio.cs.HistoricallyNamedCharset;

import com.myapp.mines.controller.GameController;
import com.myapp.mines.db.HighscoreDB;
import com.myapp.mines.db.HighscoreEntry;
import com.myapp.mines.model.Field;
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


    public WingsGameView(int rows, int cols, int mines) {
        super(rows, cols, mines);
        setupWindow();
    }
    
    public WingsGameView() {
//        this(Game.DEFAULT_ROWS,
//             Game.DEFAULT_COLS,
//             Game.DEFAULT_MINES);
        this(10, 15, 15);
    }

    
    private void setupWindow() {
        frame = new SFrame("Mines Game using WingS");

        statusBar = new STextField("(c) Andre Ragg 2008");
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
                new SGridLayout(rowsSetByUser, colsSetByUser, 1, 1));
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
        Log.logln("NEW GAME STARTED!");
        initComponents();
    }

    @Override
    public void setStatusText(String text) {
        statusBar.setText(text);
    }

    @Override
    public void gameWon() {
        Log.logln("GAME WON!");
        final HighscoreDB db = HighscoreDB.getInstance();
        
        if (! db.isDbAvailable()) {
            SOptionPane.showMessageDialog(
                frame,
                "You won! Your time was: " +
                TimeDateUtil.formatTime(game.getTime()),
                "Congratulations!", 
                SOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        final STextField inputElement = new STextField("Anonymous");
        inputElement.setMaxColumns(10);
        
        ActionListener callback = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playername = String.valueOf(inputElement.getText()).trim();
                System.out.println("lustige action: playername="+playername);
                if (playername.isEmpty()) {
                    playername = "Anonymous";
                }
                HighscoreEntry entry = new HighscoreEntry(playername, game.getTime());
                db.addHighscoreEntry(entry);
            }
        };
        
        SOptionPane.showInputDialog(
            frame, 
            "You won!" +NL+
            "Please enter you name to get on" +NL+
            "the highscore rankings:", 
            "Congratulations!", 
            inputElement,
            callback
        );
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
                "You lost!!!!  Your time was " +
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
        SMenuItem aboutGame = new SMenuItem("info");
        SMenuItem customGame = new SMenuItem("benutzerdefiniert");
        SMenuItem cheatGame = new SMenuItem("cheat");

        newGame.addActionListener(this);
        aboutGame.addActionListener(this);
        customGame.addActionListener(this);
        cheatGame.addActionListener(this);

        newGame.setActionCommand(CMD_NEW_GAME);
        aboutGame.setActionCommand(CMD_ABOUT_GAME);
        customGame.setActionCommand(CMD_CUSTOM_GAME);
        cheatGame.setActionCommand(CMD_CHEAT_GAME);

        SMenu gameMenu = new SMenu("Spiel");
        SFont font = new SFont();
        font.setSize(20);
        gameMenu.setFont(font);
        gameMenu.add(newGame);
//        gameMenu.add(customGame); // not implemented :-P
        gameMenu.add(aboutGame);
        gameMenu.add(cheatGame);
        
        final HighscoreDB db = HighscoreDB.getInstance();
        if (db.isDbAvailable()) {
            SMenuItem viewHighscores = new SMenuItem("highscores");
            viewHighscores.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<HighscoreEntry> scores = db.getHighscoreEntries();
                    String[] values = null;
                    if (scores.isEmpty()) {
                        values = new String[]{"No Highscores yet."};
                    } else {
                        values = new String[scores.size()];
                        for (int i = 0; i < values.length; i++) {
                            HighscoreEntry he = scores.get(i);
                            values[i] = he.getName() + " - " + TimeDateUtil.formatTime(he.getGameTime());
                        }
                    }
                    SList list = new SList(values);
                    SOptionPane.showMessageDialog(frame, list, "Hiscores");
                }
            });
            gameMenu.add(viewHighscores);
        }

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
