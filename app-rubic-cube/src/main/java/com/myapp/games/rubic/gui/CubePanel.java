package com.myapp.games.rubic.gui;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import com.myapp.games.rubic.model2.Cube;
import com.myapp.games.rubic.model2.Surface;

public class CubePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Cube cube;

    private Map<Surface, SurfacePanel> surfaces = new HashMap<Surface, SurfacePanel>();

    public CubePanel(Cube pcube) {
        super(new GridLayout(4, 3));
        this.cube = pcube;
        
        add(new JPanel());
        
        SurfacePanel p = new SurfacePanel(Surface.REAR, cube);
        surfaces.put(Surface.REAR, p);
        add(p);
        
        add(new JPanel());
        
        p = new SurfacePanel(Surface.LEFT, cube);
        surfaces.put(Surface.LEFT, p);
        add(p);
        
        p = new SurfacePanel(Surface.TOP, cube);
        surfaces.put(Surface.TOP, p);
        add(p);
        
        p = new SurfacePanel(Surface.RIGHT, cube);
        surfaces.put(Surface.RIGHT, p);
        add(p);
        
        add(new JPanel());
        
        p = new SurfacePanel(Surface.FRONT, cube);
        surfaces.put(Surface.FRONT, p);
        add(p);
        
        add(new JPanel());
        add(new JPanel());
        
        p = new SurfacePanel(Surface.BOTTOM, cube);
        surfaces.put(Surface.BOTTOM, p);
        add(p);

        add(new JPanel());
    }
    
    public void setCube(Cube cube) {
        this.cube = cube;
        Iterator<Map.Entry<Surface, SurfacePanel>> i;
        
        for (i = surfaces.entrySet().iterator(); 
             i.hasNext();
             i.next().getValue().setCube(this.cube));
    }
}
