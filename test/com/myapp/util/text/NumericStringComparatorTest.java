package com.myapp.util.text;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumericStringComparatorTest {

    @Test
    public void testCompare() {
        NumericStringComparator c = new NumericStringComparator();
        
        int compare = c.compare("1", "11");
        assertTrue(compare < 0);
        
    }

}
