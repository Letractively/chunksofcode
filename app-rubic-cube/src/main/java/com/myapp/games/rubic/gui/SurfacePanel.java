package com.myapp.games.rubic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.myapp.games.rubic.model2.Cube;
import com.myapp.games.rubic.model2.Mosaic;
import com.myapp.games.rubic.model2.Surface;

public class SurfacePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Mosaic[][] mosaics;
    private Surface pov;
    private JLabel title = new JLabel();
    private Cube cube;
    
    private JPanel[][] coloredSquares = new JPanel[3][3];
    private JPanel coloredSquaresContainer = new JPanel(new GridLayout(3, 3, 1, 1));
    
    public SurfacePanel(Surface direction, Cube cube) {
        super(new BorderLayout());
        this.pov = direction;
        this.cube = cube;
        
        for (int i = 0; i < coloredSquares.length; i++)
            for (int j = 0; j < coloredSquares[0].length; j++) {
                JPanel p = new JPanel();
                coloredSquares[i][j] = p;
                coloredSquaresContainer.add(p);

                if (i == 1 && j == 1) setUpMiddle(p); // center of this surface
            }
        
        add(coloredSquaresContainer, BorderLayout.CENTER);
        updateColors();
    }
    
    @SuppressWarnings("deprecation")
    private void updateColors() {
        mosaics = cube.getSquare(pov);
        
        for (int i = 0; i < coloredSquares.length; i++) 
            for (int j = 0; j < coloredSquares[0].length; j++) {
                JPanel p = coloredSquares[i][j];
                Color squareColor = mosaics[i][j].getColor(pov);
                p.setBackground(squareColor);
                
                if (i == 1 && j == 1) { // center of this surface
                    title.setForeground(invert(squareColor));
                }
            }
    }
    
    private static Color invert(Color original) {
        Color c = new Color(255 - original.getRed(),
                            255 - original.getGreen(), 
                            255 - original.getBlue());
        return c;
    }
    
    private void setUpMiddle(JPanel p) {
        p.removeAll();
        p.setLayout(new BorderLayout());
        
        title.setText(pov.name());
        
        p.add(title, BorderLayout.NORTH);
        p.add(getLeftRightArrowsPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel getLeftRightArrowsPanel() {
        final JButton leftArrow = new JButton("<-");
        final JButton rightArrow = new JButton("->");

        ActionListener anonymousCoward = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == leftArrow) {
                    cube.rotateCounterClockwise(pov);
                } else if (e.getSource() == rightArrow) {
                    cube.rotateClockwise(pov);
                } else {
                    throw new RuntimeException("unknown source:"+e.getSource());
                }
                
                updateColors();
            }
        };
        
        leftArrow.addActionListener(anonymousCoward);
        rightArrow.addActionListener(anonymousCoward);

        JPanel leftRightArrows = new JPanel(new GridLayout(1,2));
        leftRightArrows.add(leftArrow);
        leftRightArrows.add(rightArrow);
        return leftRightArrows;
    }

    public Surface getDirection() {
        return pov;
    }
    
    public Cube getCube() {
        return cube;
    }
    
    public void setCube(Cube cube) {
        this.cube = cube;
        updateColors();
    }
}
