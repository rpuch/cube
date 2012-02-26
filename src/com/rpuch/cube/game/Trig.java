package com.rpuch.cube.game;

import java.lang.reflect.MalformedParameterizedTypeException;

/**
 * @author rpuch
 */
public class Trig {
    public static float degreesToRadians(float degrees) {
        return (float) (degrees / 180 * Math.PI);
    }

    public static double radiansToDegrees(double radians) {
        return radians * 180 / Math.PI;
    }

    public static float signedCycleDistance(float from, float to, float rangeLeft, float rangeRight) {
        float min = Math.min(from, to);
        float max = Math.max(from, to);
        float innerDistance = max - min;
        float outerDistance = (min - rangeLeft) + (rangeRight - max);
        boolean useInner = innerDistance < outerDistance;
        return useInner ? to - from : from > to ? outerDistance : -outerDistance;
    }
}
