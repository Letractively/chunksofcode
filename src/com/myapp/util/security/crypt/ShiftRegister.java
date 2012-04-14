/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt;

/**
 *
 * @author andre
 */
final class ShiftRegister {

    private final Bit[] bits;
    private final int shiftControlBitIndex;
    private final int[] feedBackIndexes;

    ShiftRegister(int length, int[] feedBackIndexes, int shiftControlBitIndex) {
        bits = new Bit[length];
        clear();
        this.feedBackIndexes = feedBackIndexes;
        this.shiftControlBitIndex = shiftControlBitIndex;
    }

    void clear() {
        for (int i = 0; i < bits.length; i++)
            bits[i] = new Bit(false);
    }

    Bit shift(Bit next) {

        Bit actualBitInLoop = bits[0];
        for (int i = 1; i < bits.length; i++) {
            Bit temp = bits[i];
            bits[i] = actualBitInLoop;
            actualBitInLoop = temp;
        }

        /*save _last_ bit of the register's array*/
        Bit output = actualBitInLoop;

        bits[0] = next.xor(getFeedback());

        return output;
    }

    Bit shift() {
        Bit actualBitInLoop = bits[0];
        
        for (int i = 1; i < bits.length; i++) {
            Bit temp = bits[i];
            bits[i] = actualBitInLoop;
            actualBitInLoop = temp;
        }

        /*save _last_ bit of the register's array*/
        Bit output = actualBitInLoop;

        bits[0] = getFeedback();

        return output;

    }

    Bit getFeedback() {
        Bit feedback = null;
        for (int i : feedBackIndexes)
            if (feedback == null)
                feedback = bits[i];
            else
                feedback = feedback.xor(bits[i]);
        return feedback;
    }

    boolean isControlFlag() {
        return bits[shiftControlBitIndex].val;
    }
}
