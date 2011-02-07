package com.myapp.games.rubic.model;

import java.util.Arrays;

import com.myapp.games.rubic.model2.ICubeConstants;
import com.myapp.games.rubic.model2.Mosaic;
import com.myapp.games.rubic.model2.Surface;

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
public final class Cube2 implements ICubeConstants { 
    
    
    private Mosaic[][][] parts;
    private final Object sync = new Object(); // TODO
    
    
    
    public Cube2() {
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
    
    public static <T> void rotateSquareArrayWithLogOutput(T[][] arr) {
        int dim = arr.length;
        assert dim == arr[0].length : Arrays.deepToString(arr);
        T tmp = null;
        int steps = 0;
        
        for (int x = 0, xlimit = dim / 2; x < xlimit; x++) {
            for (int y = 0, ylimit = dim - x - 1; y < ylimit; y++) {
               System.out.println("--- step number "+ steps++ +" ---");
                
               tmp                      =  arr [x]       [y];
               System.out.println("tmp                    =  arr[x][y]");
               System.out.println("tmp                    =  arr["+x+"]["+y+"]                   = "+arr[x][y]);
               System.out.println();
               
                                   arr[x][y]                          =  arr[y]      [dim-x-1];
               System.out.println("arr[x][y]       "+         "       =  arr[y]"+   "[dim-x-1];");
               System.out.println("arr["+(x)+"]["+(y)+"]              =  arr["+(y)+"]["+(dim-x-1)+"]                   = "+arr[y][dim-x-1]);
               System.out.println();
               
                                   arr[y][dim-x-1]                     =  arr[dim-x-1][dim-y-1]; 
               System.out.println("arr[y][dim-x-1] "+          "       =  arr[dim-x-1][dim-y-1];");
               System.out.println("arr["+(y)+"]["+(dim-x-1)+"]              =  arr["+(dim-x-1)+"]["+(dim-y-1)+"]                   = "+arr[dim-x-1][dim-y-1]);
               System.out.println();
               
                                   arr[dim-x-1][dim-y-1]                =  arr[dim-y-1][x];
               System.out.println("arr[dim-x-1][dim-y-1] "+           " =  arr[dim-y-1][x];");
               System.out.println("arr["+(dim-x-1)+"]["+(dim-y-1)+"]              =  arr["+(dim-y-1)+"]["+(x)+"]                   = "+arr[dim-y-1][x]);
               System.out.println();
               
                                   arr[dim-y-1] [x]        =  tmp;
               System.out.println("arr[dim-y-1][x] "+          "       =  tmp;");
               System.out.println("arr["+(dim-y-1)+"]["+(x)+"]              =  tmp = "+tmp);
               System.out.println();
            }
        }
    }
    
    public static <T> void rotateSquareArray(T[][] arr, boolean clockwise) {
        int dim = arr.length;
        assert dim == arr[0].length : Arrays.deepToString(arr);
        T tmp = null;
        
        final int xlimit = dim / 2;

        if (clockwise) {
            for (int x = 0; x < xlimit; x++) {
                for (int y = 0, ylimit = dim - x - 1; y < ylimit; y++) {
                   tmp                      =  arr [dim-y-1] [x];
                   arr [dim-y-1] [x]        =  arr [dim-x-1] [dim-y-1];
                   arr [dim-x-1] [dim-y-1]  =  arr [y]       [dim-x-1];
                   arr [y]       [dim-x-1]  =  arr [x]       [y];
                   arr [x]       [y]        =  tmp;
                }
            }
        } else {
            for (int x = 0; x < xlimit; x++) {
                for (int y = 0, ylimit = dim - x - 1; y < ylimit; y++) {
                   tmp                      =  arr [x]       [y];
                   arr [x]       [y]        =  arr [y]       [dim-x-1];
                   arr [y]       [dim-x-1]  =  arr [dim-x-1] [dim-y-1]; 
                   arr [dim-x-1] [dim-y-1]  =  arr [dim-y-1] [x];
                   arr [dim-y-1] [x]        =  tmp;
                }
            }
        }
    }
    
    
    public static void main(String[] args) {
        Integer[][] i = {  {1, 2, 3},
                           {4, 5, 6},
                           {7, 8, 9}  };
        
        System.out.println(arrayToString(i)); 
        rotateSquareArray(i, true);
        System.out.println(arrayToString(i)); 
        rotateSquareArray(i, false);
        System.out.println(arrayToString(i));
        
        Integer[][] j = {  {1,  2,  3,  4 },
                           {5,  6,  7,  8 },
                           {9,  10, 11, 12},
                           {13, 14, 15, 16}  };
        
        System.out.println(arrayToString(j)); 
        rotateSquareArray(j, true);
        System.out.println(arrayToString(j)); 
        rotateSquareArray(j, false);
        System.out.println(arrayToString(j));
    }
    
    public static <T> String arrayToString(T[][] i) {
        StringBuilder bui = new StringBuilder();
        final String newLine = System.getProperty("line.separator");
        
        for (int x = 0; x < i.length; x++) {
            for (int y = 0; y < i[0].length; y++) {
                String element = i[x][y].toString();
                
                if (element.length() < 2)
                    bui.append(' ');
                
                bui.append(element);
                bui.append(" ");
            }
            bui.append(newLine);
        }
        
        return bui.toString();
    }
    
    
}                        