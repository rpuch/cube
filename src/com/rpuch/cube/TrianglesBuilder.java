package com.rpuch.cube;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rpuch
 */
public class TrianglesBuilder {
    private List<Triangle> triangles = new ArrayList<Triangle>();

    public TrianglesBuilder triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int color) {
        triangles.add(new Triangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, color));
        return this;
    }

//    // points 1, 2, 3 are in correct order!
//    public TrianglesBuilder rect(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
//        float x4, y4, z4;
//
//    }

    public TrianglesBuilder straightRectZ(float x, float y, float z, float width, float height, int color) {
        triangle(
                x, y, z,
                x + width, y, z,
                x + width, y + height, z,
                color
        );
        triangle(
                x + width, y + height, z,
                x, y + height, z,
                x, y, z,
                color
        );
        return this;
    }

    public TrianglesBuilder straightRectX(float x, float y, float z, float depth, float height, int color) {
        triangle(
                x, y, z,
                x, y, z + depth,
                x, y + height, z + depth,
                color
        );
        triangle(
                x, y + height, z + depth,
                x, y + height, z,
                x, y, z,
                color
        );
        return this;
    }

    public TrianglesBuilder straightRectY(float x, float y, float z, float width, float depth, int color) {
        triangle(
                x, y, z,
                x + width, y, z,
                x + width, y, z + depth,
                color
        );
        triangle(
                x + width, y, z + depth,
                x, y, z + depth,
                x, y, z,
                color
        );
        return this;
    }

    public float[] toTriangles() {
        float[] result = new float[triangles.size() * 9];
        int index = 0;
        for (Triangle triangle : triangles) {
            result[index++] = triangle.x1;
            result[index++] = triangle.y1;
            result[index++] = triangle.z1;
            result[index++] = triangle.x2;
            result[index++] = triangle.y2;
            result[index++] = triangle.z2;
            result[index++] = triangle.x3;
            result[index++] = triangle.y3;
            result[index++] = triangle.z3;
        }
        return result;
    }

    public int[] toTrianglesColors() {
        int[] result = new int[triangles.size() * 3];
        int index = 0;
        for (Triangle triangle : triangles) {
            result[index++] = triangle.color;
            result[index++] = triangle.color;
            result[index++] = triangle.color;
        }
        return result;
    }

    public int getTrianglesVerticesCount() {
        return triangles.size() * 3;
    }

    private static class Triangle {
        public float x1, y1, z1, x2, y2, z2, x3, y3, z3;
        public int color;

        private Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int color) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.x3 = x3;
            this.y3 = y3;
            this.z3 = z3;
            this.color = color;
        }
    }
}
