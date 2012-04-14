package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.myapp.util.soundsorter.wizard.tool.Application;
import com.myapp.util.soundsorter.wizard.tool.INextDirChosenListener;
import com.myapp.util.swing.Util;

public class MainFrame implements INextDirChosenListener 
{
    
    private JFrame frame;
    private JButton nextDirButton;
    private JTextField actualDirTextField;
    
    private Application app;
    
    private MetaDataDisplay unsortedDirsPanel;
    private DestinationPanel destinationButtons;
    private PlayerPanel playControl;

    private boolean shown = false;

    
    private static final long now() {
        return com.myapp.util.format.Util.now();
    }
    
    
    private static void log(String msg, long start) {
        com.myapp.util.format.Util.log(msg, start);
    }
    
    public MainFrame() {
        long totalStart = now();
        String prefix = "MainFrame.MainFrame() ";
        System.out.println(prefix + "initializing...");
        
        long start = now();
        app = new Application();
        log(prefix + "Application created!", start);
        
        start = now();
        createComponents();
        log(prefix + "Components created!", start);
        
        start = now();
        app.start();
        log(prefix + "Application started!", start);
        
        start = now();
        app.loadNextDir();
        log(prefix + "First directory loaded!", start);
        log(prefix + "Mainframe initialized! TOTAL TIME:", totalStart);
    }
    
    private void createComponents() {
        frame = new JFrame("Song Sorter (c) andre");
        frame.setContentPane(new JPanel(new BorderLayout(10, 10)));
        
        unsortedDirsPanel = new MetaDataDisplay();
        playControl = new PlayerPanel(app.getMPD());
        
        actualDirTextField = new JTextField("kein Verzeichnis geladen!");
        actualDirTextField.setBorder(BorderFactory.createTitledBorder("Aktuelles Verzeichnis"));
        actualDirTextField.setEditable(false);
        actualDirTextField.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        
        nextDirButton = new JButton("Weiter");
        nextDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.loadNextDir();
            }
        });
        
        
        JPanel headerCenter = new JPanel(new BorderLayout());
        headerCenter.add(playControl, BorderLayout.WEST);
        playControl.setPreferredSize(new Dimension(380, 150));
        headerCenter.add(unsortedDirsPanel, BorderLayout.CENTER);
        
        JPanel northNorth = new JPanel(new BorderLayout());
        northNorth.add(actualDirTextField, BorderLayout.CENTER);
        northNorth.add(nextDirButton, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(northNorth, BorderLayout.NORTH);
        northContainer.add(headerCenter, BorderLayout.CENTER);
        
        frame.getContentPane().add(northContainer, BorderLayout.NORTH);
        
        destinationButtons = new DestinationPanel(this);

        app.addNextDirChosenListener(unsortedDirsPanel);
        app.addNextDirChosenListener(playControl);
        app.addNextDirChosenListener(this);

        frame.getContentPane().add(destinationButtons, BorderLayout.CENTER);
    }
    
    public void show() {
        if (shown)
            return;

        synchronized (this) {
            if (shown)
                return;
            
            shown = true;
            frame.pack();
            frame.setSize(new Dimension(1200, 750));
            Util.centerFrame(frame);
            Util.quitOnClose(frame);
            frame.setVisible(true);
        }
    }
    
    public Application getApplication() {
        return app;
    }
    
    MetaDataDisplay getUnsortedDirsPanel() {
        return unsortedDirsPanel;
    }
    
    DestinationPanel getDestinationButtons() {
        return destinationButtons;
    }

    @Override
    public void nextDirChosen(Application context) {
        actualDirTextField.setText(context.getCurrentUnsortedDir().getAbsolutePath());
    }
}
