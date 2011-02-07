package com.myapp.game.foxAndBunny.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.myapp.game.foxAndBunny.model.Actor;
import com.myapp.game.foxAndBunny.model.Bunny;
import com.myapp.game.foxAndBunny.model.Field;
import com.myapp.game.foxAndBunny.model.Fox;
import com.myapp.game.foxAndBunny.model.World;

@SuppressWarnings("serial")
final class WorldPanel extends JPanel {

    static final Color FREE_COLOR = Color.GREEN.darker();
    static final Color BUNNY_COLOR = Color.YELLOW;
    static final Color FOX_COLOR = Color.RED;
    
    private World model;
    
    WorldPanel(World model) {
        setWorld(model);
    }
    
    private void setWorld(World model) {
        this.model = model;
        int rows = model.getRowCount(), cols = model.getRowCount();

        setPreferredSize(new Dimension(cols*3, rows*3));
        setMinimumSize(new Dimension(cols, rows));
        setMaximumSize(new Dimension(cols*10, rows*10));
    }

    @Override
    public void paint(Graphics g) {
        drawFields(g);
        drawCrosshair(g);
    }
    
    private void drawFields(Graphics g) {
        int pix = calcPixelsPerField();
        int rows = model.getRowCount();
        int cols = model.getColCount();
        
        // draw each field:
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Field field = model.getField(row, col);
                g.setColor(calcColor(field.getActor()));
                g.fillRect(pix * col, pix * row, pix, pix);
            }
        }
    }
    
    private void drawCrosshair(Graphics g) {
        int pix = calcPixelsPerField();
        int rows = model.getRowCount();
        int cols = model.getColCount();
        int rightX = (pix * cols) - 1;
        int bottomY = (pix * rows) - 1;
        
        // draw a black border around the fields and
        g.setColor(Color.black);
        g.drawRect(0, 0, rightX, bottomY); // border

        // draw a crosshair in the center:
        int halfWidthX = (pix * cols) / 2;
        int halfHeightY = (pix * rows) / 2;
        
        for (int x = 0; x < rightX; x++) {
            g.drawLine(x, halfHeightY, 
                       Math.min(x + 3, rightX), halfHeightY); // horizontal
        }

        for (int y = 0; y < bottomY; y++) {
            g.drawLine(halfWidthX, y, 
                       halfWidthX, Math.min(y + 3, bottomY)); // vertical
        }
    }
    
    private int calcPixelsPerField() {
        // calc biggest rectangle where each field's width is equal to height:
        // the height and width of one field, must be a square:
        return Math.min((getWidth()  - 1) / model.getRowCount(), 
                        (getHeight() - 1) / model.getColCount());
    }
    
    public Color calcColor(Actor actor) {
        if (actor instanceof Bunny)
            return BUNNY_COLOR;
        
        else if (actor instanceof Fox)
            return FOX_COLOR;
        
        return FREE_COLOR;
    }
}
