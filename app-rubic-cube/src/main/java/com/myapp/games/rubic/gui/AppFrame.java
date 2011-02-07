package com.myapp.games.rubic.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.myapp.games.rubic.model2.Cube;
import com.myapp.util.swing.Util;

public class AppFrame extends JFrame {

    
    private static final long serialVersionUID = 1L;
    private static AppFrame instance = null;
    
    
    public static AppFrame getSingleton() {
        if (instance == null)
            synchronized (AppFrame.class) {
                if (instance == null)
                    instance = new AppFrame();
            }
        
        return instance;
    }
    
    
    
    
    private CubePanel cubePanel;
    private Cube cube = new Cube();
    private JPanel contentPane = new JPanel(new BorderLayout());
    
    
    
    
    private AppFrame() {
        super("rubic cube demo app");
        cubePanel = new CubePanel(cube);
        System.out.println("AppFrame.AppFrame() cube:\n" + cube);
        setContentPane(contentPane);
        contentPane.add(cubePanel, BorderLayout.CENTER);
        super.setPreferredSize(new Dimension(520, 720));
        super.pack();
        Util.centerFrame(this);
        Util.quitOnClose(this);
        super.setVisible(true);
    }
    
    
    public void setCube(Cube cube) {
        this.cube = cube;
        cubePanel.setCube(cube);
    }
    
    public Cube getCube() {
        return cube;
    }
    
}
