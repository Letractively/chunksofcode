package com.myapp.games.tetris;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockFactory {
    
    private static final boolean[][]  Z_BLOCK = {
        {true , true , false}, 
        {false, true , true },
        {false, false, false}
    };
    private static final boolean[][]  S_BLOCK = {
        {false, true , true },
        {true , true , false},
        {false, false, false}
    };
    private static final boolean[][]  I_BLOCK = {
        {false, true , false, false},
        {false, true , false, false},
        {false, true , false, false},
        {false, true , false, false}
    };
    private static final boolean[][]  L_BLOCK = {
        {false, true , false},
        {false, true , false},
        {false, true , true }
    };
    private static final boolean[][]  L_INVERSE_BLOCK = {
        {false, true , false},
        {false, true , false},
        {true , true , false}
    };
    private static final boolean[][]  SQUARE_BLOCK = {
       {true , true},
       {true , true}
    };
    private static final boolean[][]  TRIANGLE_BLOCK = {
        {true , true , true },
        {false, true , false},
        {false, false, false}
    };

    @SuppressWarnings("unused")
    private static final boolean[][]  TEST_BLOCK = {{true}};
    
    
    
    private static final Random RANDOM = new Random(0L);
    private static final List<boolean[][]> TEMPLATES;
    private static final List<Color> COLORS;
    private static final List<String> NAMES;
    
    static {
        List<Color> c = new ArrayList<Color>();
        List<String> n = new ArrayList<String>();
        List<boolean[][]> t = new ArrayList<boolean[][]>();
        
        t.add(Z_BLOCK        ); c.add(Color.cyan    ); n.add("Z"        );
        t.add(S_BLOCK        ); c.add(Color.green   ); n.add("S"        );
        t.add(I_BLOCK        ); c.add(Color.pink    ); n.add("I"        );
        t.add(L_BLOCK        ); c.add(Color.yellow  ); n.add("L"        );
        t.add(L_INVERSE_BLOCK); c.add(Color.blue    ); n.add("L_INVERSE");
        t.add(SQUARE_BLOCK   ); c.add(Color.red     ); n.add("SQUARE"   );
        t.add(TRIANGLE_BLOCK ); c.add(Color.darkGray); n.add("TRIANGLE" );

        TEMPLATES = Collections.unmodifiableList(t);
        COLORS = Collections.unmodifiableList(c);
        NAMES = Collections.unmodifiableList(n);
    }
    
    private final TetrisGame game;
    
    
    public BlockFactory(TetrisGame game) {
        this.game = game;
    }


    public Block generateRandomBlock() {
        int random = RANDOM.nextInt(TEMPLATES.size());
        boolean[][] template = TEMPLATES.get(random);
        Color color = COLORS.get(random);
        String name = NAMES.get(random);
        
        Block b = new Block(game, color, name, template);
        return b;
    }
}
