package com.cse110.clicker;

import junit.framework.TestCase;

/**
 * Created by dhruhin on 12/4/15.
 */
public class DashboardActivityTest extends TestCase {

    public void testRandomSessionID() throws Exception {
        DashboardActivity act = new DashboardActivity();
        int i;
        i = act.randomSessionID();
        assertTrue(i<100000 && i >=0);
        i = act.randomSessionID();
        assertTrue(i<100000 && i >=0);
        i = act.randomSessionID();
        assertTrue(i<100000 && i >=0);
        i = act.randomSessionID();
        assertTrue(i<100000 && i >=0);
        i = act.randomSessionID();
        assertTrue(i<100000 && i >=0);
    }
}