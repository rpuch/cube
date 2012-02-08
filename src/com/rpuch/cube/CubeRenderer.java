package com.rpuch.cube;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import com.rpuch.cube.game.Cube;
import com.rpuch.cube.game.Game;
import com.rpuch.cube.game.Geom;
import com.rpuch.cube.game.GeomConstants;
import com.rpuch.cube.gl.GLCommand;
import com.rpuch.cube.tech.Objs;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author rpuch
 */
public class CubeRenderer implements GLSurfaceView.Renderer {
    private FloatBuffer triangleVB;
    private Buffer vertices;
    private int verticesCount;
    private Buffer cubeVertices;
    private int cubeVerticesCount;

    private final Queue<GLCommand> commands = new ConcurrentLinkedQueue<GLCommand>();

    private void initShapes() {

        float triangleCoords[] = {
            // X, Y, Z
//            -0.5f, -0.25f, 0,
//             0.5f, -0.25f, 0,
//             0.0f,  0.559016994f, 0
            -0.5f, -0.25f, -1,
             0.5f, -0.25f, 1,
             0.0f,  0.559016994f, 0
        };

        // initialize vertex Buffer for triangle
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        triangleVB = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        triangleVB.put(triangleCoords);    // add the coordinates to the FloatBuffer
        triangleVB.position(0);            // set the buffer to read the first coordinate

//        final float mod = GeomConstants.CUBE_MAGNITUDE;
//        TrianglesBuilder builder = new TrianglesBuilder()
//                .straightRectZ(-mod, -mod, -mod, 2*mod, 2*mod, 0xff0000)
//                .straightRectZ(mod, -mod, mod, -2*mod, 2*mod, 0xff0000)
//                .straightRectX(-mod, -mod, mod, -2*mod, 2*mod, 0x00ff00)
//                .straightRectX(mod, -mod, mod, -2*mod, 2*mod, 0x00ff00)
//                .straightRectY(-mod, -mod, -mod, 2*mod, 2*mod, 0x0000ff)
//                .straightRectY(-mod, mod, -mod, 2*mod, 2*mod, 0x0000ff);
//        float[] coords = builder.toTriangles();
//        vertices = createTrianglesColoredVerticesBuffer(coords, builder.toTrianglesColors());
//        verticesCount = builder.getTrianglesVerticesCount();

        TrianglesBuilder cubeBuilder = createCubeBuilder();
        cubeVertices = createTrianglesColoredVerticesBuffer(cubeBuilder.toTriangles(), cubeBuilder.toTrianglesColors());
        cubeVerticesCount = cubeBuilder.getTrianglesVerticesCount();
    }

    private Game getGame() {
        return Objs.getGame();
    }

    private Cube getCube() {
        return getGame().getCube();
    }

    private TrianglesBuilder createCubeBuilder() {
        final float mod = GeomConstants.CUBE_MAGNITUDE;
        TrianglesBuilder builder = new TrianglesBuilder();
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.FRONT), -mod, +mod, +mod, +2*mod, -2*mod, 0f);
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.BACK), +mod, +mod, -mod, -2*mod, -2*mod, 0f);
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.LEFT), -mod, +mod, -mod, 0f, -2*mod, +2*mod);
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.RIGHT), +mod, +mod, +mod, 0f, -2*mod, -2*mod);
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.BOTTOM), +mod, -mod, -mod, -2*mod, 0f, +2*mod);
        addFace(builder, getCube().getSize(), getCube().getFace(Cube.TOP), -mod, +mod, -mod, +2*mod, 0f, +2*mod);
        return builder;
    }

    // here, coordinates are in GL space (y axis is bottom-up)
    private void addFace(TrianglesBuilder builder, int dim, int[][] face,
                         float fromX, float fromY, float fromZ,
                         float xdir, float ydir, float zdir) {
        checkDir(xdir, "xdir");
        checkDir(ydir, "ydir");
        checkDir(zdir, "zdir");
        checkDirs(xdir, ydir, zdir);
        float width1 = xdir / dim;
        float height1 = ydir / dim;
        float depth1 = zdir / dim;
        int branch = 0;
        if (xdir == 0f) {
            branch = 0;
        } else if (ydir == 0f) {
            branch = 1;
        } else { // zdir == 0f
            branch = 2;
        }
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                int color = face[row][col];
                switch (branch) {
                    case 0: // zdir == 0f
                        builder.straightRectX(fromX, (fromY + height1 * row), fromZ + depth1 * col, depth1, height1, color);
                        break;
                    case 1: // ydir == 0f
                        builder.straightRectY(fromX + width1 * col, fromY, fromZ + depth1 * row, width1, depth1, color);
                        break;
                    case 2: // zdir == 0f
                        builder.straightRectZ(fromX + width1 * col, (fromY + height1 * row), fromZ, width1, height1, color);
                        break;
                }
            }
        }
    }

    private void checkDirs(float xdir, float ydir, float zdir) {
        int zeros = 0;
        if (xdir == 0) zeros++;
        if (ydir == 0) zeros++;
        if (zdir == 0) zeros++;
        if (zeros != 1) {
            throw new IllegalArgumentException("One and only one of xdir, ydir, zdir must be 0, but they are " + xdir + ", " + ydir + ", " + zdir);
        }
    }

    private void checkDir(float xdir, String name) {
        if (xdir != -1 && xdir != 0 && xdir != 1) {
            throw new IllegalArgumentException("xdir must be -1, 0 or 1 for " + name + ", but it's " + xdir);
        }
    }

    private Buffer createTrianglesColoredVerticesBuffer(float[] coords, int[] colors) {
        if (coords.length % 9 != 0) {
            throw new IllegalArgumentException("Coords length must be divisible by 9 but it's " + coords.length);
        }
        if (coords.length / 3 != colors.length) {
            throw new IllegalArgumentException("Coords array must be 3 times longer than colors array");
        }

        ByteBuffer buf = ByteBuffer.allocateDirect(coords.length * 4 + coords.length / 3 * 4);
        buf.order(ByteOrder.nativeOrder());
//        FloatBuffer result = buf.asFloatBuffer();
//        result.put(coords);
        for (int i = 0; i < coords.length / 3; i++) {
            for (int j = 0; j < 3; j++) {
                buf.putFloat(coords[i * 3 + j]);
            }
            int color = colors[i];
            int t = i / 3;
            buf.put((byte) ((color >> 16) & 0xff));
            buf.put((byte) ((color >> 8) & 0xff));
            buf.put((byte) (color & 0xff));
//            buf.put((byte) (t == 0 ? 255 : t == 1 ? 127 : 0));
//            buf.put((byte) (t == 0 ? 0 : 255));
//            buf.put((byte) 0);
//            buf.put((byte) (255 & (c == 0 ? 255 : 0)));
//            buf.put((byte) (255 & (c == 1 ? 255 : 0)));
//            buf.put((byte) (255 & (c == 2 ? 255 : 0)));
            buf.put((byte) 255);
//            result.put(coords, i * 3, 3);
//            result.put(1f);
//            result.put(0f);
//            result.put(0f);
//            result.put(1f);
        }
//        result.position(0);
//        buf.position(0);

//        return result;
        return buf;
    }

    private ByteBuffer createLineLoopCoordsColoredBuffer(float[] coords, int color) {
        if (coords.length % 3 != 0) {
            throw new IllegalArgumentException("Coords length must be divisible by 3 but it's " + coords.length);
        }

        ByteBuffer buf = ByteBuffer.allocateDirect(coords.length * 4 + coords.length / 3 * 4);
        buf.order(ByteOrder.nativeOrder());
        for (int i = 0; i < coords.length / 3; i++) {
            for (int j = 0; j < 3; j++) {
                buf.putFloat(coords[i * 3 + j]);
            }
            buf.put((byte) ((color >> 16) & 0xff));
            buf.put((byte) ((color >> 8) & 0xff));
            buf.put((byte) (color & 0xff));
            buf.put((byte) 255);
        }
        return buf;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        Objs.setRenderer(this);

        gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        initShapes();

        // Enable use of vertex arrays
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        System.out.println(String.format("Surface %d, %d", width, height));

        // make adjustments for screen ratio
        float ratio = (float) width / height;
//        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
//        gl.glLoadIdentity();                        // reset the matrix to its default state
//        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix

        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state

        System.out.println("Error is " + gl.glGetError());

//        gl.glFrustumf(-1f, +1f, -1f, 1f, 1, 10);  // apply the projection matrix

        gl.glOrthof(-1f, +1f, -1f, 1f, 1, 10);

//        GLU.gluPerspective(gl, 90, 1, );

        System.out.println(GLU.gluErrorString(gl.glGetError()));
        System.out.println("Error is " + gl.glGetError());
    }

    public void onDrawFrame(GL10 gl) {
        executeCommands(gl); // TODO: why here?

//        gl.glEnable(GL10.GL_DEPTH_TEST);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Set GL_MODELVIEW transformation mode
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();   // reset the matrix to its default state
//
//        // When using GL_MODELVIEW, you must set the view point
//        GLU.gluLookAt(gl, 0, 0, 5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Draw the triangle
//        gl.glColor4f(0.63671875f, 0.76953125f, 0.22265625f, 0.0f);
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB);
//        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

//        float angle = (float) (((float) (System.currentTimeMillis() % 1000)) / 4000 * Math.PI);
//        gl.glRotatef(1.0f /*degrees*/, 1f, 1f, 1f);


        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        Game game = getGame();
        // When using GL_MODELVIEW, you must set the view point
//        GLU.gluLookAt(gl, 0, 0, 5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

//        // computing vector to rotate around X axis
//        float rx = 0f;
//        float ry = 1f;
//        float rz = 0f;
//        double cos = Math.cos(Trig.degreesToRadians(-game.getEyeZenith()));
//        double sin = Math.sin(Trig.degreesToRadians(-game.getEyeZenith()));
//        float rrx = rx;
//        float rry = (float) (ry*cos - rz*sin);
//        float rrz = (float) (ry*sin + rz*cos);
//
//        gl.glRotatef(game.getEyeAzimuth(), 0f, 1f, 0f);
////        gl.glRotatef(game.getEyeAzimuth(), rrx, rry, rrz);
//
//        // computing vector to rotate around Y axis
//        rx = 1f;
//        ry = 0f;
//        rz = 0f;
//        cos = Math.cos(Trig.degreesToRadians(-game.getEyeAzimuth()));
//        sin = Math.sin(Trig.degreesToRadians(-game.getEyeAzimuth()));
//        rrx = (float) (rx*cos + rz * sin);
//        rry = ry;
//        rrz = (float) (-rx*sin + rz*cos);
//
//        gl.glRotatef(game.getEyeZenith(), rrx, rry, rrz);
//
//
//        float r = 0.2f;
////        float eyeX = (float) (r * Math.sin(Trig.degreesToRadians(game.getEyeZenith())) * Math.cos(Trig.degreesToRadians(game.getEyeAzimuth())));
////        float eyeY = (float) (r * Math.sin(Trig.degreesToRadians(game.getEyeZenith())) * Math.sin(Trig.degreesToRadians(game.getEyeAzimuth())));
////        float eyeZ = (float) (r * Math.cos(Trig.degreesToRadians(game.getEyeZenith())));
//
//        float eyeX = (float) (r * Math.cos(Trig.degreesToRadians(game.getEyeZenith())) * Math.sin(Trig.degreesToRadians(game.getEyeAzimuth())));
//        float eyeY = (float) (r * Math.sin(Trig.degreesToRadians(game.getEyeZenith())));
//        float eyeZ = (float) (r * Math.cos(Trig.degreesToRadians(game.getEyeZenith())) * Math.cos(Trig.degreesToRadians(game.getEyeAzimuth())));
//
////        GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        Geom.XYZ eye = game.getEyePoint();
        Geom.XYZ up = game.getUpVector();
        GLU.gluLookAt(gl, (float) eye.getX(), (float) eye.getY(), (float) eye.getZ(),
                0f, 0f, 0f,
                (float) up.getX(), (float) up.getY(), (float) up.getZ());


//        gl.glColor4f(0.63671875f, 0.76953125f, 0.22265625f, 0.0f);

//?        drawTrianglesColoredVertexBuffer(gl, vertices, verticesCount);
        drawTrianglesColoredVertexBuffer(gl, cubeVertices, cubeVerticesCount);

        drawSelection(gl, game);
    }

    private void drawSelection(GL10 gl, Game game) {
        Game.XYZ4 selection = game.getSelection();
        if (selection != null) {
            Buffer buf = createLineLoopCoordsColoredBuffer(new float[]{
                    (float) selection.p1.getX(), (float) selection.p1.getY(), (float) selection.p1.getZ(),
                    (float) selection.p2.getX(), (float) selection.p2.getY(), (float) selection.p2.getZ(),
                    (float) selection.p3.getX(), (float) selection.p3.getY(), (float) selection.p3.getZ(),
                    (float) selection.p4.getX(), (float) selection.p4.getY(), (float) selection.p4.getZ(),
            }, 0xc060a0);
//            gl.glColor4f(0.2f, 0.9f, 0.2f, 1.0f);
//            gl.glColor4f(1f, 0f, 0f, 0.0f);
            drawLineLoopColoredBuffer(gl, buf, 4);
        }
    }

    private void executeCommands(GL10 gl) {
        GLCommand command;
        do {
            command = commands.poll();
            if (command != null) {
                executeCommand(gl, command);
            }
        } while (command != null);
    }

    private void executeCommand(GL10 gl, GLCommand command) {
        command.execute(gl);
    }

    private void drawTrianglesColoredVertexBuffer(GL10 gl, Buffer buffer, int verticesCount) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 3*4 + 4, buffer.position(0));
        gl.glColorPointer(3, GL10.GL_UNSIGNED_BYTE, 3*4 + 4, buffer.position(3 * 4));
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, verticesCount);
    }

    private void drawLineLoopColoredBuffer(GL10 gl, Buffer buffer, int verticesCount) {
//        gl.glVertexPointer(3, GL10.GL_FLOAT, /*3*4 + 4*/0, buffer.position(0));
////        gl.glColorPointer(3, GL10.GL_UNSIGNED_BYTE, 3*4 + 4, buffer.position(3 * 4));
//        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, verticesCount);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 3*4 + 4, buffer.position(0));
        gl.glColorPointer(3, GL10.GL_UNSIGNED_BYTE, 3*4 + 4, buffer.position(3 * 4));
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, verticesCount);
    }

    public void resetGeometry() {
        initShapes();
    }

//    public void addRotation(float degrees, float x, float y, float z) {
//        commands.add(new RotateCommand(degrees, x, y, z));
//    }
}
