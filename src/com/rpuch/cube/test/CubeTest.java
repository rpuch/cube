package com.rpuch.cube.test;

import com.rpuch.cube.game.Cube;
import com.rpuch.cube.test.framework.Assert;
import com.rpuch.cube.test.framework.AssertionException;

/**
 * @author rpuch
 */
public class CubeTest {
    private Cube cube;

    private static final int[] TEST_COLORS = new int[]{1, 2, 3, 4, 5, 6};

    public void testEmpty() {
        cube = new Cube(3, TEST_COLORS);

        assertCubeEquals(new int[][][]{
                {
                        {1, 1, 1},
                        {1, 1, 1},
                        {1, 1, 1}},
                {
                        {2, 2, 2},
                        {2, 2, 2},
                        {2, 2, 2}},
                {
                        {3, 3, 3},
                        {3, 3, 3},
                        {3, 3, 3}},
                {
                        {4, 4, 4},
                        {4, 4, 4},
                        {4, 4, 4}},
                {
                        {5, 5, 5},
                        {5, 5, 5},
                        {5, 5, 5}},
                {
                        {6, 6, 6},
                        {6, 6, 6},
                        {6, 6, 6}},
        }, cube);

        cube.rotateHoriz(1, false);
    }

    public void testRotateHorizTopToLeft() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateHoriz(1, false); // left

        assertCubeEquals(new int[][][]{
                {
                        {21, 22, 23},
                        {14, 15, 16},
                        {17, 18, 19}},
                {
                        {31, 32, 33},
                        {24, 25, 26},
                        {27, 28, 29}},
                {
                        {41, 42, 43},
                        {34, 35, 36},
                        {37, 38, 39}},
                {
                        {11, 12, 13},
                        {44, 45, 46},
                        {47, 48, 49}},
                {
                        {51, 52, 53},
                        {54, 55, 56},
                        {57, 58, 59}},
                {
                        {67, 64, 61},
                        {68, 65, 62},
                        {69, 66, 63}},
        }, cube);
    }

    public void testRotateHorizBottomToLeft() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateHoriz(-1, false); // left

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 13},
                        {14, 15, 16},
                        {27, 28, 29}},
                {
                        {21, 22, 23},
                        {24, 25, 26},
                        {37, 38, 39}},
                {
                        {31, 32, 33},
                        {34, 35, 36},
                        {47, 48, 49}},
                {
                        {41, 42, 43},
                        {44, 45, 46},
                        {17, 18, 19}},
                {
                        {53, 56, 59},
                        {52, 55, 58},
                        {51, 54, 57}},
                {
                        {61, 62, 63},
                        {64, 65, 66},
                        {67, 68, 69}},
        }, cube);
    }

    public void testRotateHorizTopToRight() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateHoriz(1, true); // right

        assertCubeEquals(new int[][][]{
                {
                        {41, 42, 43},
                        {14, 15, 16},
                        {17, 18, 19}},
                {
                        {11, 12, 13},
                        {24, 25, 26},
                        {27, 28, 29}},
                {
                        {21, 22, 23},
                        {34, 35, 36},
                        {37, 38, 39}},
                {
                        {31, 32, 33},
                        {44, 45, 46},
                        {47, 48, 49}},
                {
                        {51, 52, 53},
                        {54, 55, 56},
                        {57, 58, 59}},
                {
                        {63, 66, 69},
                        {62, 65, 68},
                        {61, 64, 67}},
        }, cube);
    }

    public void testRotateHorizBottomToRight() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateHoriz(-1, true); // right

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 13},
                        {14, 15, 16},
                        {47, 48, 49}},
                {
                        {21, 22, 23},
                        {24, 25, 26},
                        {17, 18, 19}},
                {
                        {31, 32, 33},
                        {34, 35, 36},
                        {27, 28, 29}},
                {
                        {41, 42, 43},
                        {44, 45, 46},
                        {37, 38, 39}},
                {
                        {57, 54, 51},
                        {58, 55, 52},
                        {59, 56, 53}},
                {
                        {61, 62, 63},
                        {64, 65, 66},
                        {67, 68, 69}},
        }, cube);
    }

    public void testRotateVertLeftBackwards() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateVert(1, false); // backwards

        assertCubeEquals(new int[][][]{
                {
                        {13, 16, 19},
                        {12, 15, 18},
                        {11, 14, 17}},
                {
                        {59, 22, 23},
                        {56, 25, 26},
                        {53, 28, 29}},
                {
                        {31, 32, 33},
                        {34, 35, 36},
                        {37, 38, 39}},
                {
                        {41, 42, 67},
                        {44, 45, 64},
                        {47, 48, 61}},
                {
                        {51, 52, 43},
                        {54, 55, 46},
                        {57, 58, 49}},
                {
                        {21, 62, 63},
                        {24, 65, 66},
                        {27, 68, 69}},
        }, cube);

    }

    public void testRotateVertRightBackwards() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateVert(-1, false); // backwards

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 13},
                        {14, 15, 16},
                        {17, 18, 19}},
                {
                        {21, 58, 57},
                        {24, 55, 54},
                        {27, 52, 51}},
                {
                        {37, 34, 31},
                        {38, 35, 32},
                        {39, 36, 33}},
                {
                        {69, 68, 43},
                        {66, 65, 46},
                        {63, 62, 49}},
                {
                        {41, 42, 53},
                        {44, 45, 56},
                        {47, 48, 59}},
                {
                        {61, 22, 23},
                        {64, 25, 26},
                        {67, 28, 29}},
        }, cube);
    }

    public void testRotateVertLeftForward() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateVert(1, true); // forward

        assertCubeEquals(new int[][][]{
                {
                        {17, 14, 11},
                        {18, 15, 12},
                        {19, 16, 13}},
                {
                        {61, 22, 23},
                        {64, 25, 26},
                        {67, 28, 29}},
                {
                        {31, 32, 33},
                        {34, 35, 36},
                        {37, 38, 39}},
                {
                        {41, 42, 53},
                        {44, 45, 56},
                        {47, 48, 59}},
                {
                        {51, 52, 27},
                        {54, 55, 24},
                        {57, 58, 21}},
                {
                        {49, 62, 63},
                        {46, 65, 66},
                        {43, 68, 69}},
        }, cube);

    }

    public void testRotateVertRightForward() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateVert(-1, true); // forward

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 13},
                        {14, 15, 16},
                        {17, 18, 19}},
                {
                        {21, 62, 63},
                        {24, 65, 66},
                        {27, 68, 69}},
                {
                        {33, 36, 39},
                        {32, 35, 38},
                        {31, 34, 37}},
                {
                        {51, 52, 43},
                        {54, 55, 46},
                        {57, 58, 49}},
                {
                        {29, 28, 53},
                        {26, 25, 56},
                        {23, 22, 59}},
                {
                        {61, 48, 47},
                        {64, 45, 44},
                        {67, 42, 41}},
        }, cube);

    }

    public void testRotateSidewaysFrontCounterclockwise() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateSideways(1, false); // counter-clockwise

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 69},
                        {14, 15, 68},
                        {17, 18, 67}},
                {
                        {23, 26, 29},
                        {22, 25, 28},
                        {21, 24, 27}},
                {
                        {57, 32, 33},
                        {58, 35, 36},
                        {59, 38, 39}},
                {
                        {41, 42, 43},
                        {44, 45, 46},
                        {47, 48, 49}},
                {
                        {51, 52, 53},
                        {54, 55, 56},
                        {19, 16, 13}},
                {
                        {61, 62, 63},
                        {64, 65, 66},
                        {31, 34, 37}},
        }, cube);

    }

    public void testRotateSidewaysBackCounterclockwise() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateSideways(-1, false); // counter-clockwise

        assertCubeEquals(new int[][][]{
                {
                        {63, 66, 13},
                        {62, 65, 16},
                        {61, 64, 19}},
                {
                        {21, 22, 23},
                        {24, 25, 26},
                        {27, 28, 29}},
                {
                        {31, 54, 51},
                        {34, 55, 52},
                        {37, 56, 53}},
                {
                        {47, 44, 41},
                        {48, 45, 42},
                        {49, 46, 43}},
                {
                        {17, 14, 11},
                        {18, 15, 12},
                        {57, 58, 59}},
                {
                        {33, 36, 39},
                        {32, 35, 38},
                        {67, 68, 69}},
        }, cube);

    }

    public void testRotateSidewaysFrontClockwise() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateSideways(1, true); // clockwise

        assertCubeEquals(new int[][][]{
                {
                        {11, 12, 59},
                        {14, 15, 58},
                        {17, 18, 57}},
                {
                        {27, 24, 21},
                        {28, 25, 22},
                        {29, 26, 23}},
                {
                        {67, 32, 33},
                        {68, 35, 36},
                        {69, 38, 39}},
                {
                        {41, 42, 43},
                        {44, 45, 46},
                        {47, 48, 49}},
                {
                        {51, 52, 53},
                        {54, 55, 56},
                        {31, 34, 37}},
                {
                        {61, 62, 63},
                        {64, 65, 66},
                        {19, 16, 13}},
        }, cube);

    }

    public void testRotateSidewaysBackClockwise() {
        cube = new Cube(3, TEST_COLORS);
        initCube();

        cube.rotateSideways(-1, true); // clockwise

        assertCubeEquals(new int[][][]{
                {
                        {53, 56, 13},
                        {52, 55, 16},
                        {51, 54, 19}},
                {
                        {21, 22, 23},
                        {24, 25, 26},
                        {27, 28, 29}},
                {
                        {31, 64, 61},
                        {34, 65, 62},
                        {37, 66, 63}},
                {
                        {43, 46, 49},
                        {42, 45, 48},
                        {41, 44, 47}},
                {
                        {33, 36, 39},
                        {32, 35, 38},
                        {57, 58, 59}},
                {
                        {17, 14, 11},
                        {18, 15, 12},
                        {67, 68, 69}},
        }, cube);

    }

    private void initCube() {
        setCube(cube, new int[][][]{
                {
                        {11, 12, 13},
                        {14, 15, 16},
                        {17, 18, 19}},
                {
                        {21, 22, 23},
                        {24, 25, 26},
                        {27, 28, 29}},
                {
                        {31, 32, 33},
                        {34, 35, 36},
                        {37, 38, 39}},
                {
                        {41, 42, 43},
                        {44, 45, 46},
                        {47, 48, 49}},
                {
                        {51, 52, 53},
                        {54, 55, 56},
                        {57, 58, 59}},
                {
                        {61, 62, 63},
                        {64, 65, 66},
                        {67, 68, 69}},
        });
    }

    private void setCube(Cube cube, int[][][] values) {
        for (int face = 0; face < 6; face++) {
            System.arraycopy(values[face], 0, cube.getFace(face), 0, cube.getSize());
        }
    }

    private void assertCubeEquals(int[][][] expected, Cube actual) {
        for (int face = 0; face < 6; face++) {
            Assert.assertEquals(expected[face].length, actual.getFace(face).length);
            for (int row = 0; row < expected[face].length; row++) {
                Assert.assertEquals(expected[face][row].length, actual.getFace(face)[row].length);
                for (int col = 0; col < expected[face][row].length; col++) {
                    try {
                        Assert.assertEquals(expected[face][row][col], actual.getFace(face)[row][col]);
                    } catch (AssertionException e) {
                        throw new AssertionException("Assertion failed for face " + face + " (" + getFaceName(face) + ")", e);
                    }
                }
            }
        }
    }

    private String getFaceName(int face) {
        switch (face) {
            case Cube.LEFT: return "left";
            case Cube.FRONT: return "front";
            case Cube.RIGHT: return "right";
            case Cube.BACK: return "back";
            case Cube.BOTTOM: return "bottom";
            case Cube.TOP: return "top";
            default: throw new IllegalStateException("Unknown face: " + face);
        }
    }
}
