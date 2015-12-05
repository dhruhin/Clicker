package com.cse110.clicker;

import junit.framework.TestCase;

/**
 * Created by dhruhin on 12/4/15.
 */
public class CreateSessionActivityTest extends TestCase {

    public void testReturnPercentage() throws Exception {
        CreateSessionActivity act = new CreateSessionActivity();
        float f;
        f = act.returnPercentage(new int[]{1,1,1,1,1}, new int[]{1,1,1,1,1});
        assertEquals(100.00f, f);
        f = act.returnPercentage(new int[]{1,2,3,1,4}, new int[]{1,2,4,1,5});
        assertEquals(60.00f, f);
        f = act.returnPercentage(new int[]{1,1,1,1,1}, new int[]{1,1,1,4,1});
        assertEquals(80.00f, f);
        f = act.returnPercentage(new int[]{1,2,3,4,5}, new int[]{1,1,1,1,1});
        assertEquals(20.00f, f);
        f = act.returnPercentage(new int[]{1,1,1,1,1}, new int[]{1,2,5,2,1});
        assertEquals(40.00f, f);
        f = act.returnPercentage(new int[]{1,1,1,1,1}, new int[]{2,2,2,2,2});
        assertEquals(0.00f, f);
    }

    public void testConvertIntToChar() throws Exception {
        CreateSessionActivity act = new CreateSessionActivity();
        char c;
        c = act.convertIntToChar(1);
        assertEquals(c, 'A');
        c = act.convertIntToChar(2);
        assertEquals(c, 'B');
        c = act.convertIntToChar(3);
        assertEquals(c, 'C');
        c = act.convertIntToChar(4);
        assertEquals(c, 'D');
        c = act.convertIntToChar(5);
        assertEquals(c, 'E');
        c = act.convertIntToChar(0);
        assertEquals(c, '?');
        c = act.convertIntToChar(6);
        assertEquals(c, '?');
    }

}