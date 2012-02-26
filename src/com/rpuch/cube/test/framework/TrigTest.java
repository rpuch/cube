package com.rpuch.cube.test.framework;

import com.rpuch.cube.game.Trig;

/**
 * @author rpuch
 */
public class TrigTest {
    public void testSignedCycleDistance() {
        Assert.assertEquals(+10.0f, Trig.signedCycleDistance(10, 20, 0, 360));
        Assert.assertEquals(-20.0f, Trig.signedCycleDistance(360, 340, 0, 360));
        Assert.assertEquals(+10.0f, Trig.signedCycleDistance(350, 0, 0, 360));
        Assert.assertEquals(-1.0f, Trig.signedCycleDistance(0, 359, 0, 360));
    }
}
