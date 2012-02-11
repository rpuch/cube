package com.rpuch.cube.game;

import java.io.PrintStream;

/**
 * @author rpuch
 */
public class Cube {
    private int[][][] cells;
    private int size;

    private static final int[] DEFAULT_COLORS = {
            0x00ff00,   // green
            0xffffff,   // white
            0x0000ff,   // blue
            0xff0000,   // red
            0xffff00,   // yellow
            0x000000    // black
    };
    public static final int LEFT = 0;
    public static final int FRONT = 1;
    public static final int RIGHT = 2;
    public static final int BACK = 3;
    public static final int BOTTOM = 4;
    public static final int TOP = 5;

    public Cube(int size) {
        this(size, DEFAULT_COLORS);
    }

    public Cube(int size, int[] colors) {
        this.size = size;

        reset(colors);

//        rotateHoriz(1, true);
    }

    private void reset(int[] colors) {
        cells = new int[6][][];
        for (int face = 0; face < 6; face++) {
            int color = colors[face];
            cells[face] = new int[size][];
            for (int row = 0; row < size; row++) {
                cells[face][row] = new int[size];
                for (int col = 0; col < size; col++) {
                    cells[face][row][col] = color;
                }
            }
        }
    }

    public void reset() {
        reset(DEFAULT_COLORS);
    }

    public int getSize() {
        return size;
    }

    public int[][] getFace(int face) {
        return cells[face];
    }

    public void print(PrintStream out) {
        for (int row = 0; row < size; row++) {
            printSpaces(out, size * 3 + 2);
            printRow(out, TOP, row, true);
        }
        out.println();
        for (int row = 0; row < size; row++) {
            printRow(out, LEFT, row, false);
            out.print("  ");
            printRow(out, FRONT, row, false);
            out.print("  ");
            printRow(out, RIGHT, row, false);
            out.print("  ");
            printRow(out, BACK, row, true);
        }
        out.println();
        for (int row = 0; row < size; row++) {
            printSpaces(out, size * 3 + 2);
            printRow(out, BOTTOM, row, true);
        }
        out.println();
    }

    private void printSpaces(PrintStream out, int count) {
        for (int i = 0; i < count; i++) {
            out.print(" ");
        }
    }

    private void printRow(PrintStream out, int face, int row, boolean endline) {
        for (int col = 0; col < size; col++) {
            out.print(String.format("%3d", getFace(face)[row][col]));
        }
        if (endline) {
            out.println();
        }
    }

    public void rotateHoriz(int rows, boolean right) {
        int from, to, border, delta;
        if (rows > 0) {
            from = 0;
            to = rows - 1;
            border = to + 1;
            delta = 1;
        } else {
            from = size - 1;
            to = size - 1 + rows + 1;
            border = to - 1;
            delta = -1;
        }
        for (int row = from; row != border; row += delta) {
            if (!right) {
                shiftRows(new int[]{FRONT, RIGHT, BACK, LEFT}, row);
            } else {
                shiftRows(new int[]{FRONT, LEFT, BACK, RIGHT}, row);
            }
        }
        if (rows > 0) {
            rotateFace(getFace(TOP), !right);
        } else {
            rotateFace(getFace(BOTTOM), right);
        }
    }

    public void rotateVert(int cols, boolean forward) {
        int from, to, border, delta;
        if (cols > 0) {
            from = 0;
            to = cols - 1;
            border = to + 1;
            delta = 1;
        } else {
            from = size - 1;
            to = size - 1 + cols + 1;
            border = to - 1;
            delta = -1;
        }
        for (int col = from; col != border; col += delta) {
            if (!forward) {
                shiftCols(new int[]{FRONT, BOTTOM, BACK, TOP}, col);
            } else {
                shiftCols(new int[]{FRONT, TOP, BACK, BOTTOM}, col);
            }
        }
        if (cols > 0) {
            rotateFace(getFace(LEFT), forward);
        } else {
            rotateFace(getFace(RIGHT), !forward);
        }
    }

    public void rotateSideways(int plains, boolean clockwise) {
        int from, to, border, delta;
        if (plains > 0) {
            from = 0;
            to = plains - 1;
            border = to + 1;
            delta = 1;
        } else {
            from = size - 1;
            to = size - 1 + plains + 1;
            border = to - 1;
            delta = -1;
        }
        for (int plain = from; plain != border; plain += delta) {
            if (!clockwise) {
                shiftPlains(new int[]{TOP, RIGHT, BOTTOM, LEFT}, plain);
            } else {
                shiftPlains(new int[]{TOP, LEFT, BOTTOM, RIGHT}, plain);
            }
        }
        if (plains > 0) {
            rotateFace(getFace(FRONT), clockwise);
        } else {
            rotateFace(getFace(BACK), !clockwise);
        }
    }

    private void shiftRows(int[] order, int rowIndex) {
        int[] shiftedRow = getFace(order[0])[rowIndex];
        for (int i = 0; i < order.length - 1; i++) {
            getFace(order[i])[rowIndex] = getFace(order[i+1])[rowIndex];
        }
        getFace(order[order.length-1])[rowIndex] = shiftedRow;
    }

    private boolean getFaceVertDir(int face) {
        return (face == FRONT || face == TOP || face == RIGHT);
    }

    private void shiftCols(int[] order, int colIndex) {
        // first face is ALWAYS positively directed (dir==true)
        int[][] firstFace = getFace(order[0]);
        int[] shiftedCol = new int[size];
        for (int i = 0; i < size; i++) {
            shiftedCol[i] = firstFace[i][colIndex];
        }
        for (int i = 0; i < order.length - 1; i++) {
            int face1 = order[i];
            int face2 = order[i+1];
            boolean dir1 = getFaceVertDir(face1);
            boolean dir2 = getFaceVertDir(face2);
            int colIndex1 = dir1 ? colIndex : size-1-colIndex;
            int colIndex2 = dir2 ? colIndex : size-1-colIndex;
            for (int j = 0; j < size; j++) {
                int rowIndex1 = dir1 ? j : size-1-j;
                int rowIndex2 = dir2 ? j : size-1-j;
                getFace(face1)[rowIndex1][colIndex1] = getFace(face2)[rowIndex2][colIndex2];
            }
        }
        // and the last face...
        boolean lastDir = getFaceVertDir(order[order.length-1]);
        int lastColIndex = lastDir ? colIndex : size-1-colIndex;
        for (int i = 0; i < size; i++) {
            int lastRowIndex = lastDir ? i : size-1-i;
            getFace(order[order.length-1])[lastRowIndex][lastColIndex] = shiftedCol[i];
        }
    }

    private void shiftPlains(int[] order, int plainIndex) {
        // first face is ALWAYS TOP
        int[][] firstFace = getFace(order[0]);
        int[] shiftedRow = new int[size];
        for (int i = 0; i < size; i++) {
            shiftedRow[i] = firstFace[size-1-plainIndex][i];
        }
        boolean destIsRow = true;
        for (int i = 0; i < order.length - 1; i++) {
            int face1 = order[i];
            int face2 = order[i+1];
            boolean dir1 = getFaceSidewaysDir(face1);
            boolean dir2 = getFaceSidewaysDir(face2);
            int statIndex1 = (dir1 && !destIsRow) ? plainIndex : size-1-plainIndex;
            int statIndex2 = (dir2 && destIsRow) ? plainIndex : size-1-plainIndex;
            for (int j = 0; j < size; j++) {
                int dynIndex1 = dir1 ? j : size-1-j;
                int dynIndex2 = dir2 ? j : size-1-j;
                if (destIsRow) {
                    getFace(face1)[statIndex1][dynIndex1] = getFace(face2)[dynIndex2][statIndex2];
                } else {
                    getFace(face1)[dynIndex1][statIndex1] = getFace(face2)[statIndex2][dynIndex2];
                }
            }
            destIsRow = !destIsRow;
        }
        // and the last face...
        boolean lastDir = getFaceSidewaysDir(order[order.length - 1]);
        int lastStatIndex = lastDir ? plainIndex : size-1-plainIndex;
        for (int i = 0; i < size; i++) {
            int lastDynIndex = lastDir ? i : size-1-i;
            getFace(order[order.length-1])[lastDynIndex][lastStatIndex] = shiftedRow[i];
        }
    }

    private boolean getFaceSidewaysDir(int face) {
        return (face == TOP || face == BOTTOM || face == RIGHT);
    }

    private void rotateFace(int[][] face, boolean clockwise) {
        int[] shifted2 = new int[size];
        for (int i = 0; i < size / 2; i++) {
            int from = i;
            int to = size - 1 - i;
            if (!clockwise) {
                for (int j = from; j <= to-1; j++) {
                    shifted2[j] = face[from][j];
                }

                for (int j = from; j <= to-1; j++) {
                    face[from][j] = face[j][to];
                }

                for (int j = from; j <= to-1; j++) {
                    face[j][to] = face[to][to-j+from];
                }

                for (int j = from+1; j <= to; j++) {
                    face[to][j] = face[j][from];
                }

                for (int j = from; j <= to-1; j++) {
                    face[from-j+to][from] = shifted2[j];
                }
            } else {
                for (int j = from+1; j <= to; j++) {
                    shifted2[j] = face[from][j];
                }

                for (int j = to; j >= from+1; j--) {
                    face[from][j] = face[to-j+from][from];
                }

                for (int j = from; j <= to-1; j++) {
                    face[j][from] = face[to][j];
                }

                for (int j = from; j <= to-1; j++) {
                    face[to][j] = face[to-j+from][to];
                }

                for (int j = from+1; j <= to; j++) {
                    face[j][to] = shifted2[j];
                }
            }
        }
    }
}
