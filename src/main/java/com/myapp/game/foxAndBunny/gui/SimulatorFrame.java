package com.myapp.game.foxAndBunny.gui;

import static com.myapp.util.swing.Util.centerFrame;
import static com.myapp.util.swing.Util.quitOnClose;
import static com.myapp.util.swing.Util.title;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.YES_NO_OPTION;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.myapp.game.foxAndBunny.Logger;
import com.myapp.game.foxAndBunny.model.World;

@SuppressWarnings("serial")
public final class SimulatorFrame extends JFrame {

    // SimulatorFrame.draw() w='359', h='519'
    private static final Dimension preferredSize = new Dimension(359, 519);
    
    private WorldPanel worldPanel;
    private final PopulationPanel populationPanel;
    
    public SimulatorFrame(World model) {
        super("Füchse und Hasen Simulator");
        quitOnClose(this);
        
        populationPanel = new PopulationPanel(model);
        worldPanel = new WorldPanel(model);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        
        if (Logger.IS_DEBUG) {
            contentPane.add(
                new JPanel(new BorderLayout()) {{ // wrap in titled panel
                    this.add(worldPanel, BorderLayout.CENTER);
                    title(this, 
                          SimulatorFrame.class.getSimpleName()+".worldPanel");
                }}
            );
        } else {
            contentPane.add(worldPanel, BorderLayout.CENTER);
        }
        
        contentPane.add(populationPanel, BorderLayout.SOUTH);
        setContentPane(contentPane);

        if (Logger.IS_DEBUG)
            title(contentPane, "SimulatorFrame.contentPane");
        
        pack();
        setSize(Logger.IS_DEBUG ? new Dimension(379, 644) : preferredSize);
        centerFrame(this);
        draw();
        
        if ( ! isVisible()) {
            super.setVisible(true);
        }
    }
    
    public void draw() {
        worldPanel.repaint();
        populationPanel.repaint();
//        System.out.println("SimulatorFrame.draw() " +
//        		             "w='"+getWidth()+"', h='"+getHeight()+"'");
    }
    
    /** ask the user if restart or quit after one breed has died
    @return true means the user want to restart, false to quit */
    public boolean askUserIfRestartOrExit(String extinctBreed) {
        return OK_OPTION == JOptionPane.showOptionDialog(
            this,
            "All "+extinctBreed+" died. Do you want to restart or quit?",
            "Game over!",
            YES_NO_OPTION,
            INFORMATION_MESSAGE,
            null,
            new String[]{"restart", "quit"},
            "restart" 
        );
    }
}
