package com.ppdai.das.client;

import org.junit.Test;

public class MiscTest {
    @Test
    public void testIntegerEquals() {
        Integer i1 = new Integer(0);
        int i2 = 0;
        Integer i3 = 0;
        
        System.out.println("i1 == i2: " + (i1 == i2));
        
        System.out.println("i1 == i3: " + (i1 == i3));
        
        System.out.println("i3 == i2: " + (i3 == i2));
    }
}
