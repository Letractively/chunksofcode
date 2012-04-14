/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt2;

import java.util.BitSet;

/**
 *
 * @author andre
 */
final class ShiftRegister {

private final BitSet bits2;
private final int shiftFlagIndex;
private final int[] feedBackIndexes;

ShiftRegister(int length, int[] feedBackIndexes, int shiftControlBitIndex) {
    bits2 = new BitSet(length);
    clear();
    this.feedBackIndexes = feedBackIndexes;
    this.shiftFlagIndex = shiftControlBitIndex;
}

void clear() {
    bits2.clear();
}

boolean shift(boolean next) {

    boolean actualBitInLoop = get(0);
    for (int i = 1; i < bits2.size(); i++) {
        boolean temp = get(i);
        set(i, actualBitInLoop);
        actualBitInLoop = temp;
    }

    /*save _last_ bit of the register's array*/
    boolean output = actualBitInLoop;
    set(0, next ^ getFeedBack());

    return output;
}

boolean shift() {
    boolean actualBitInLoop = get(0);
    boolean temp;
    int size = bits2.size();

    for (int i = 1; i < size; i++) {
        temp = get(i);
        set(i, actualBitInLoop);
        actualBitInLoop = temp;
    }

    set(0, getFeedBack());
    return actualBitInLoop;
}

private boolean get(int i) {
    return bits2.get(i);
}

private void set(int i, boolean value) {
    bits2.set(i, i, value);
}

boolean getFeedBack() {
    boolean firstLoopStepIsOver = false;
    boolean feedback = true;

    for (int i : feedBackIndexes)
        if (!firstLoopStepIsOver) {
            feedback = bits2.get(i);
            firstLoopStepIsOver = true;
        }
        else
            feedback ^= bits2.get(i);

    return feedback;
}

boolean getShiftFlag() {
    return get(shiftFlagIndex);
}

}
