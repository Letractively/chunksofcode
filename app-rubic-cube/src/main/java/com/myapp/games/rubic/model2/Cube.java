package com.myapp.games.rubic.model2;

import static com.myapp.games.rubic.model2.Surface.BOTTOM;
import static com.myapp.games.rubic.model2.Surface.FRONT;
import static com.myapp.games.rubic.model2.Surface.LEFT;
import static com.myapp.games.rubic.model2.Surface.REAR;
import static com.myapp.games.rubic.model2.Surface.RIGHT;
import static com.myapp.games.rubic.model2.Surface.TOP;

import java.util.Arrays;

/**<pre>
                         __________________________
                        /                          \
                        |                           |
                    ___________                     |
                   |           |                    |
               __  |  REAR     | ____               |
              /    |  GREEN    |     \              |             |     |           |      |             |         __________|___________|__________          |        |          |           |          |         |        |  LEFT    |  TOP      |   RIGHT  |         |    __  |  RED     |  WHITE    |   ORANGE |         |  /     |          |           |          |  __     |  |     |__________|___________|__________|    \    |  |                |           |                |   |  |         |      |           |       |        |   |  |         |      |  FRONT    |       |        |   |  |          \ __  |  BLUE     |  ____/         |   |  |                |___________|                |   |  |                |           |                |   |  |                |  BOTTOM   |                |   |  |                |  YELLOW   |                |   |   \_____________  |           |  _____________/    |                   |___________|                    |                                                    |                         |                          |                          \________________________/

                       
                       ____________
                      /    top    /|  <-- 
                     /___________/ | rear
                    |           |  |
               -->  |           | right
      +y      left  |   front   |  |
       |  +z        |           | / 
       |  /         |___________|/
       | /             
       |/_____+x            A     
                    bottom  | 

*/
public final class Cube implements ICubeConstants { 
    
    
    private static final String NL = System.getProperty("line.separator");
    
    private Mosaic[][][] parts;
    
    private final Object sync = new Object(); // TODO
    
    
    
    public Cube() {
        parts = new Mosaic[X_DIM][Y_DIM][Z_DIM];
        
        for (int x = 0; x < X_DIM; x++)
            for (int y = 0; y < Y_DIM; y++)
                for (int z = 0; z < Z_DIM; z++) {
                    parts[x][y][z] = new Mosaic(x, y, z);
                }
    }
    
    
    
    public Mosaic getMosaic(int x, int y, int z) {
        synchronized (sync) {
        return parts[x][y][z];
        }
    }
    
    public void rotateClockwise(Surface surface) {
        synchronized (sync) {
        switch (surface) {
            case TOP:     rotateY(2);                       break;
            case BOTTOM:  rotateY(0);rotateY(0);rotateY(0); break;
            case FRONT:   rotateZ(0);                       break;
            case REAR:    rotateZ(2);rotateZ(2);rotateZ(2); break;
            case LEFT:    rotateX(0);                       break;
            case RIGHT:   rotateX(2);rotateX(2);rotateX(2); break;
            default: throw new RuntimeException(surface+"");
        }
        }
        System.out.println("Cube.rotateClockwise("+surface+") cube=\n"+toString());
    }
    
    
    
    public void rotateCounterClockwise(Surface surface) {
        synchronized (sync) {
        switch (surface) {
            case TOP:     rotateY(2);rotateY(2);rotateY(2); break;
            case BOTTOM:  rotateY(0);                       break;
            case FRONT:   rotateZ(0);rotateZ(0);rotateZ(0); break;
            case REAR:    rotateZ(2);                       break;
            case LEFT:    rotateX(0);rotateX(0);rotateX(0); break;
            case RIGHT:   rotateX(2);                       break;
            default: throw new RuntimeException(surface+"");
        }
        }
        System.out.println("Cube.rotateCounterClockwise("+surface+") cube=\n"+toString());
    }
    

    /**
     * @deprecated use getMosaic() instead
     */
    @Deprecated
    public Mosaic[][] getSquare(Surface surface) {
        synchronized (sync) {
            Mosaic[][] retval = new Mosaic[EDGE_LENGTH][EDGE_LENGTH];
            
            switch (surface) {
            case TOP:
            case BOTTOM: {
                int y = (surface == BOTTOM) ? 0 : 2;
                        
                for (int x = 0; x < EDGE_LENGTH; x++)
                    for (int z = 0; z < EDGE_LENGTH; z++)
                        retval[x][z] = parts[x][y][z];
                break;
            }
            case FRONT:
            case REAR: {
                int z = (surface == FRONT) ? 0 : 2;
                
                for (int x = 0; x < EDGE_LENGTH; x++)
                    for (int y = 0; y < EDGE_LENGTH; y++)
                        retval[x][y] = parts[x][y][z];
                break;
            }
            case LEFT:
            case RIGHT: {
                int x = (surface == LEFT) ? 0 : 2;
                
                for (int y = 0; y < EDGE_LENGTH; y++)
                    for (int z = 0; z < EDGE_LENGTH; z++)
                        retval[y][z] = parts[x][y][z];
                break;
            }
            } // switch (surface)
            
            if (surface == Surface.REAR
                    ||surface == Surface.LEFT
                    ||surface == Surface.BOTTOM) {
                rotateSquareArray(retval);
                rotateSquareArray(retval);
            }
    
            return retval;
        }
    }
    
    
    /**x axis heads from left to right edge*/
    public void rotateX(int... xSlices) {
        synchronized (sync) {
        Mosaic tmp = null;
        final int dim = EDGE_LENGTH;

        for (int i = 0; i < xSlices.length; i++) {
            int x = xSlices[i];

            for (int y = 0, ylimit = dim / 2; y < ylimit; y++) {
                for (int z = 0, zlimit = dim - y - 1; z < zlimit; z++) {
                    tmp                          =  parts [x][y]      [z];
                    parts [x][y]      [z]        =  parts [x][z]      [dim-y-1];
                    parts [x][z]      [dim-y-1]  =  parts [x][dim-y-1][dim-z-1]; 
                    parts [x][dim-y-1][dim-z-1]  =  parts [x][dim-z-1][y];
                    parts [x][dim-z-1][y]        =  tmp;
                }
            }
            
            for (int y = 0; y < dim; y++) {
                for (int z = 0; z < dim; z++) {
                    parts[x][y][z].rotateX();
                    parts[x][y][z].setCoords(x, y, z);
                }
            }
        }
        }
    }

    /**y axis heads from bottom to top edge*/
    public void rotateY(int... ySlices) {
        synchronized (sync) {
        Mosaic tmp = null;
        final int dim = EDGE_LENGTH;

        for (int i = 0; i < ySlices.length; i++) {
            int y = ySlices[i];
            
            for (int x = 0, xlimit = dim / 2; x < xlimit; x++) {
                for (int z = 0, zlimit = dim - x - 1; z < zlimit; z++) {
                    tmp                          =  parts [x]      [y][z];
                    parts [x]      [y][z]        =  parts [z]      [y][dim-x-1];
                    parts [z]      [y][dim-x-1]  =  parts [dim-x-1][y][dim-z-1]; 
                    parts [dim-x-1][y][dim-z-1]  =  parts [dim-z-1][y][x];
                    parts [dim-z-1][y][x]        =  tmp;
                }
            }
            
            for (int x = 0; x < dim; x++) {
                for (int z = 0; z < dim; z++) {
                    parts[x][y][z].rotateY();
                    parts[x][y][z].setCoords(x, y, z);
                }
            }
        }
        }
    }

    /**z axis heads from front to rear edge*/
    public void rotateZ(int... zSlices) {
        synchronized (sync) {
        Mosaic tmp = null;
        final int dim = EDGE_LENGTH;

        for (int i = 0; i < zSlices.length; i++) {
            int z = zSlices[i];

            for (int x = 0, xlimit = dim / 2; x < xlimit; x++) {
                for (int y = 0, ylimit = dim - x - 1; z < ylimit; z++) {
                    tmp                          =  parts [x]      [y]      [z];
                    parts [x]      [y]      [z]  =  parts [y]      [dim-x-1][z];
                    parts [y]      [dim-x-1][z]  =  parts [dim-x-1][dim-z-1][z]; 
                    parts [dim-x-1][dim-y-1][z]  =  parts [dim-y-1][x]      [z];
                    parts [dim-y-1][x]      [z]  =  tmp;
                }
            }
            
            for (int x = 0; x < dim; x++) {
                for (int y = 0; y < dim; y++) {
                    parts[x][y][z].rotateZ();
                    parts[x][y][z].setCoords(x, y, z);
                }
            }
        }
        }
    }    
    
    public static <T> void rotateSquareArray(T[][] arr) {
        int dim = arr.length;
        assert dim == arr[0].length : Arrays.deepToString(arr);
        T tmp = null;
        
        for (int x = 0, xlimit = dim / 2; x < xlimit; x++) {
            for (int y = 0, ylimit = dim - x - 1; y < ylimit; y++) {
               tmp                      =  arr [x]       [y];
               arr [x]       [y]        =  arr [y]       [dim-x-1];
               arr [y]       [dim-x-1]  =  arr [dim-x-1] [dim-y-1]; 
               arr [dim-x-1] [dim-y-1]  =  arr [dim-y-1] [x];
               arr [dim-y-1] [x]        =  tmp;
            }
        }
    }
    
    
    private void toStringImpl(StringBuilder sb) {
        Mosaic m = parts[0][0][0];
        appendMosaicString(m, sb, BOTTOM);
        final int coordsStrLen = sb.length();
        sb.setLength(0);

          
        /* rear */
        Mosaic[][] rear = getSquare(REAR);
          
        for (int i = 0; i < 3; i++) {
            appendGap(3 * coordsStrLen + 2, sb);
            for (int j=0;j<3;j++) appendMosaicString(rear[i][j], sb, REAR);
            sb.append(NL);
        }
        
        sb.append(NL);
        
        
        /* left, top, bottom */
        Mosaic[][] left = getSquare(LEFT);
        Mosaic[][] top = getSquare(Surface.TOP );
        Mosaic[][] right = getSquare(Surface.RIGHT);
        
        for (int i = 0; i < 3; i++) {
            for (int j=0;j<3;j++) appendMosaicString(left[i][j], sb, LEFT);
            sb.append("  ");
            for (int j=0;j<3;j++) appendMosaicString(top[i][j], sb, TOP);
            sb.append("  ");
            for (int j=0;j<3;j++) appendMosaicString(right[i][j], sb, RIGHT);
            sb.append(NL);
        }
        
        sb.append(NL);

        
        /* front */
        Mosaic[][] front = getSquare(FRONT);
        
        for (int i = 0; i < 3; i++) {
            appendGap(3 * coordsStrLen + 2, sb);
            for (int j=0;j<3;j++) appendMosaicString(front[i][j], sb, FRONT);
            sb.append(NL);
        }
        
        sb.append(NL);

        
        /* bottom */
        Mosaic[][] bottom = getSquare(BOTTOM);
        
        for (int i = 0; i < 3; i++) {
            appendGap(3 * coordsStrLen + 2, sb);
            for (int j=0;j<3;j++) appendMosaicString(bottom[i][j], sb, BOTTOM);
            sb.append(NL);
        }
    }
    
    @Override
    public String toString() {
        final int LENGTH = 1600;
        StringBuilder sb = new StringBuilder(LENGTH);
        toStringImpl(sb);
        int len = sb.length();
        
        if (len > LENGTH)
            System.out.println("Cube.toString() bui length = " + len);
        
        return sb.toString();
    }
    
    private static void appendGap(int len, StringBuilder bui) {
        for (int i = 0; i < len; i++) 
            bui.append(' ');
    }
    
    private static void appendMosaicString(Mosaic m, 
                                           StringBuilder bui, 
                                           Surface pov) {
        m.appendToString(pov, bui);
    }
}                        