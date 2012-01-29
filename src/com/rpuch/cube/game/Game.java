package com.rpuch.cube.game;

/**
 * @author rpuch
 */
public class Game {
    private Cube cube;
    private Geom.XYZ eyePoint = new Geom.XYZ(0, 0, 0.2);
    private Geom.XYZ upVector = new Geom.XYZ(0, 1, 0); // up vector

    public Game() {
        cube = new Cube(3);
    }

    public Cube getCube() {
        return cube;
    }

    public Geom.XYZ getEyePoint() {
        return eyePoint;
    }

    public Geom.XYZ getUpVector() {
        return upVector;
    }

    public void rotateInTerminatorPlain(float degrees) {
        Geom.XYZ sideVector = eyePoint.negate().vectorProduct(upVector).normalise();
        eyePoint = Geom.arbitraryRotate(eyePoint, Trig.degreesToRadians(degrees), sideVector);
        upVector = Geom.arbitraryRotate(upVector, Trig.degreesToRadians(degrees), sideVector);
    }

    public void rotateInHorizonPlain(float degrees) {
        eyePoint = Geom.arbitraryRotate(eyePoint, Trig.degreesToRadians(degrees), upVector);
    }

    public void resetEye() {
        eyePoint = new Geom.XYZ(0, 0, 5);
        upVector = new Geom.XYZ(0, 1, 0);
    }
}
