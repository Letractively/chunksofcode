package com.myapp.util.security.crypt;

/**
 * immutable.
 * false == 0, true == 1
 * @author andre
 */
final class Bit {
    
    private static int creationCounter = 0;

    private static final String TRUE = "1";
    private static final String FALSE = "0";

    /**this flag contains all data needed*/
    final boolean val;

    Bit(boolean value) {
        this.val = value;
//        creationCounter++; // for benchmarking...
    }

    Bit xor(Bit other)  {return new Bit((val && (!other.val)) || ((!val) && other.val));}
    Bit and(Bit other)  {return new Bit(val && other.val);}
    Bit or(Bit other)   {return new Bit(val || other.val);}
    Bit not()           {return new Bit(!val);}
    Bit nand(Bit other) {return new Bit(!(val && other.val));}
    Bit nor(Bit other)  {return new Bit(!(val || other.val));}

    @Override public String toString()        {return val ? TRUE : FALSE;}
    @Override public boolean equals(Object o) {return ((Bit) o).val == val;}
    @Override public int hashCode()           {return val ? 1 : 0;}

    public static int getCreationCounter() {
        return creationCounter;
    }

    public static void setCreationCounter(int creationCounter) {
        Bit.creationCounter = creationCounter;
    }
}
