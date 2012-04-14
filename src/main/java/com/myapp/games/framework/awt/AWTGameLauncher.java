package com.myapp.games.framework.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class AWTGameLauncher {
    
    private JFrame window = null;
    private AWTGame game = null;

    public AWTGameLauncher(AWTGame game) {
        this.game = game;
    }

    public static void launch(final AWTGame game) {
        Runnable swingHook = new Runnable() {
            public void run() {
                AWTGameLauncher launcher = new AWTGameLauncher(game);
                launcher.startAndShowGui();
            }
        };
        
        SwingUtilities.invokeLater(swingHook);
    }

    public void startAndShowGui() {
        window = new JFrame();

        int gameWidth = game.getSurfaceWidth();
        int gameHeight = game.getSurfaceHeight();
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setBounds(new Rectangle(100, 100, gameWidth+50, gameHeight+50));
        window.setMinimumSize(new Dimension(gameWidth+50, gameHeight+50));
        window.setMaximumSize(new Dimension(gameWidth+50, gameHeight+50));
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(game.getUIComponent(), BorderLayout.CENTER);
        
        window.setContentPane(content);
        window.setTitle("Game");
        window.addKeyListener(game.getGameKeyListener());
        game.getUIComponent().addMouseListener(game.getGameMouseListener());
        
        window.setVisible(true);
        game.startGame();
    }
}