package com.rpuch.cube.game;

/**
 * @author rpuch
 */
public class Game {
    private Cube cube;
    // the following are relative to (0,0,0) which is the center of the model
    private float eyeZenith = 0f; // degrees
    private float eyeAzimuth = 0f; // degrees

    public Game() {
        cube = new Cube(3);
    }

    public Cube getCube() {
        return cube;
    }

    public float getEyeZenith() {
        return eyeZenith;
    }

    public float getEyeAzimuth() {
        return eyeAzimuth;
    }

    public void addToEyeZenith(float delta) {
        eyeZenith += delta;
    }

    public void addToEyeAzimuth(float delta) {
        eyeAzimuth += delta;
    }

    public void resetEye() {
        eyeZenith = 0f;
        eyeAzimuth = 0f;
    }
}
