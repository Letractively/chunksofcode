package com.myapp.games.rubic.model2;


public enum Surface {
    TOP, BOTTOM, FRONT, REAR, LEFT, RIGHT;
    
    public String toString() {
        return name().toLowerCase();
    }
}
