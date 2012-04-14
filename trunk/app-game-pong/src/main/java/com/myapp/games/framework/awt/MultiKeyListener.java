package com.myapp.games.framework.awt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

class MultiKeyListener implements KeyListener {
    
    private List<KeyListener> delegates = new ArrayList<KeyListener>();
    
    public void addListener(KeyListener l) {
        if (!delegates.contains(l)) {
            delegates.add(l);
        }
    }
    
    public void removeListener(KeyListener l) {
        while (delegates.contains(l)) {
            delegates.remove(l);
        }
    }

    public void keyPressed(KeyEvent evt) { 
        for (KeyListener l : delegates) {
            l.keyPressed(evt);
        }
    }

    public void keyReleased(KeyEvent evt) {
        for (KeyListener l : delegates) {
            l.keyReleased(evt);
        }
    }

    public void keyTyped(KeyEvent evt) {
        for (KeyListener l : delegates) {
            l.keyReleased(evt);
        }
    }
}