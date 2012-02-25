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
}
