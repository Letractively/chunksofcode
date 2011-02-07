/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.Border;


/**
 * 
 * @author andre
 */
public class Util {

    public static Point calculateScreenCenter(JFrame frame) {
        Dimension sc = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension fr = frame.getSize();

        int x = sc.width / 2;
        int y = sc.height / 2;

        x -= (fr.width / 2);
        y -= (fr.height / 2);

        return new Point(x, y);
    }


    public static void centerFrame(JFrame frame) {
        frame.setLocation(calculateScreenCenter(frame));
    }


    public static void quitOnClose(JFrame appFrame) {
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void title(JComponent comp, String title) {
        Border border = BorderFactory.createTitledBorder(title);
        comp.setBorder(border);
    }
}
