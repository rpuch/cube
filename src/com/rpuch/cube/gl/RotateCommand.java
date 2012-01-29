package com.rpuch.cube.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author rpuch
 */
public class RotateCommand implements GLCommand {
    private final float degrees;
    private final float x, y, z;

    public RotateCommand(float degrees, float x, float y, float z) {
        this.degrees = degrees;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void execute(GL10 gl) {
        gl.glRotatef(degrees, x, y, z);
    }
}
