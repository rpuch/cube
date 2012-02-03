package com.rpuch.cube.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author rpuch
 */
public class U {
    public static <T> T gl(GL10 gl, C<T> cmd) {
        T result = cmd.exec(gl);
        int error = gl.glGetError();
        if (error != 0) {
            processError(gl, error);
        }
        return result;
    }

    private static void processError(GL10 gl, int error) {
        throw new RuntimeException(gl.glGetString(error));
    }

    public static interface C<T> {
        T exec(GL10 gl);
    }

    public static abstract class V implements C<Void> {
        public final Void exec(GL10 gl) {
            vexec(gl);
            return null;
        }

        protected abstract void vexec(GL10 gl);
    }
}
