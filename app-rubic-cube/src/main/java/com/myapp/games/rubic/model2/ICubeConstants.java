package com.myapp.games.rubic.model2;



import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.orange;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public interface ICubeConstants {

    int EDGE_LENGTH = 3;

    int X_DIM = EDGE_LENGTH;
    int Y_DIM = EDGE_LENGTH;
    int Z_DIM = EDGE_LENGTH;

    
    Map<Color, String> colorNames = Collections.unmodifiableMap(new HashMap<Color, String>(){
        private static final long serialVersionUID = 1L;
        
        {
            put(blue  , "blue");
            put(green , "green");
            put(orange, "orange");
            put(red   , "red");
            put(white , "white");
            put(yellow, "yellow");
        }
    });
}
