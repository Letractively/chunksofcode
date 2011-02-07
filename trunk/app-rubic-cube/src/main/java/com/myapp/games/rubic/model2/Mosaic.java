package com.myapp.games.rubic.model2;

import static com.myapp.games.rubic.model2.Surface.BOTTOM;
import static com.myapp.games.rubic.model2.Surface.FRONT;
import static com.myapp.games.rubic.model2.Surface.LEFT;
import static com.myapp.games.rubic.model2.Surface.REAR;
import static com.myapp.games.rubic.model2.Surface.RIGHT;
import static com.myapp.games.rubic.model2.Surface.TOP;
import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.orange;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class Mosaic implements ICubeConstants {

    
    private static int counter = 0;
    
    
    private final int id;

    private int x, y, z;
    
    private Map<Surface, Color> colors;
    private Map<Surface, Color> temp = new HashMap<Surface, Color>(3);
    
    
    
    public Mosaic(int x, int y, int z) {
        colors = new HashMap<Surface, Color>(3);
        
        synchronized (Mosaic.class) {
            id = counter;
            counter = id +1;
        }

        this.x = x;
        this.y = y;
        this.z = z;
        
        if (x == 0) colors.put(LEFT, red);
        if (y == 0) colors.put(BOTTOM, yellow);
        if (z == 0) colors.put(FRONT, blue);

        if (x == X_DIM - 1) colors.put(RIGHT, orange);
        if (y == Y_DIM - 1) colors.put(TOP, white);
        if (z == Z_DIM - 1) colors.put(REAR, green);
        
    }

    
    public Color getColor(Surface side)  {
        return colors.get(side);
    }
    
    
    public void rotateX() {
        temp.clear();
        Iterator<Map.Entry<Surface, Color>> iterator;
        
        for (iterator = colors.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Surface, Color> e = iterator.next();
            
            switch(e.getKey()) {
                case BOTTOM:  temp.put(FRONT,  e.getValue()); break;
                case FRONT:   temp.put(TOP,    e.getValue()); break;
                case TOP:     temp.put(REAR,   e.getValue()); break;
                case REAR:    temp.put(BOTTOM, e.getValue()); break;
                case LEFT:    temp.put(LEFT,   e.getValue()); break;
                case RIGHT:   temp.put(RIGHT,  e.getValue()); break;
                default: throw new RuntimeException(e.getKey()+"="+e.getValue());
            }
        }

        switchMaps();
    }

    public void rotateY() {
        temp.clear();
        Iterator<Map.Entry<Surface, Color>> iterator;
        
        for (iterator = colors.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Surface, Color> e = iterator.next();
            
            switch(e.getKey()) {
                case BOTTOM:  temp.put(BOTTOM, e.getValue()); break;
                case FRONT:   temp.put(LEFT,   e.getValue()); break;
                case TOP:     temp.put(TOP,    e.getValue()); break;
                case REAR:    temp.put(RIGHT,  e.getValue()); break;
                case LEFT:    temp.put(REAR,   e.getValue()); break;
                case RIGHT:   temp.put(FRONT,  e.getValue()); break;
                default: throw new RuntimeException(e.getKey()+"="+e.getValue());
            }
        }

        switchMaps();
    }

    public void rotateZ() {
        temp.clear();
        Iterator<Map.Entry<Surface, Color>> iterator;
        
        for (iterator = colors.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Surface, Color> e = iterator.next();
            
            switch(e.getKey()) {
                case BOTTOM:  temp.put(LEFT,   e.getValue()); break;
                case FRONT:   temp.put(FRONT,  e.getValue()); break;
                case TOP:     temp.put(RIGHT,  e.getValue()); break;
                case REAR:    temp.put(REAR,   e.getValue()); break;
                case LEFT:    temp.put(TOP,    e.getValue()); break;
                case RIGHT:   temp.put(BOTTOM, e.getValue()); break;
                default: throw new RuntimeException(e.getKey()+"="+e.getValue());
            }
        }
        
        switchMaps();
    }
    
    private void switchMaps() {
        Map<Surface, Color> foo = colors;
        colors = temp;
        temp = foo;
    }
    
    public String toString(Surface pov) {
        StringBuilder sb = new StringBuilder();
        appendToString(pov, sb);
        return sb.toString();
    }
    
    void appendToString(Surface pov, StringBuilder bui) {
        bui.append("[");
        bui.append(pov.name().substring(0, 3));
        bui.append(" ");
        bui.append(x);
        bui.append(",");
        bui.append(y);
        bui.append(",");
        bui.append(z);
        bui.append(" ");
        Color color = getColor(pov);
        
        if (color == null) {
            bui.append("   ");
        } else {
            bui.append(colorNames.get(color).substring(0, 3));
        }
        bui.append(" ");
        
        if (id < 10) {
            bui.append(" ");
        }
        
        bui.append(id);
        bui.append("] ");
    }


    public void setCoords(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @return a new array containing xyz into the first three positions
     */
    public int[] getCoords() {
        int[] coords = new int[3];
        getCoords(coords);
        return coords;
    }
    
    /**
     * sets xyz into the first three positions of the given array
     * @param coordsBuffer
     */
    public void getCoords(int[] coordsBuffer) {
        coordsBuffer[0] = x;
        coordsBuffer[1] = y;
        coordsBuffer[2] = z;
    }
}
