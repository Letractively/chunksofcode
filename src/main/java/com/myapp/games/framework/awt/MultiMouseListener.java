package com.myapp.games.framework.awt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

class MultiMouseListener implements MouseListener {
    
    private List<MouseListener> delegates = new ArrayList<MouseListener>();
    
    public void addListener(MouseListener l) {
        if (!delegates.contains(l)) {
            delegates.add(l);
        }
    }
    
    public void removeListener(MouseListener l) {
        while (delegates.contains(l)) {
            delegates.remove(l);
        }
    }

    public void mouseClicked(MouseEvent e) {
        for (MouseListener l : delegates) {
            l.mouseClicked(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        for (MouseListener l : delegates) {
            l.mousePressed(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        for (MouseListener l : delegates) {
            l.mouseReleased(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
        for (MouseListener l : delegates) {
            l.mouseEntered(e);
        }
    }

    public void mouseExited(MouseEvent e) {
        for (MouseListener l : delegates) {
            l.mouseExited(e);
        }
    }
}