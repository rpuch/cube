package com.rpuch.cube.game;

/**
 * @author rpuch
 */
public class Game {
    private Cube cube;

    private static Geom.XYZ defaultEyePoint = new Geom.XYZ(0, 0, +5);
    private static Geom.XYZ defaultUpVector = new Geom.XYZ(0, 1, 0);

    private Geom.XYZ eyePoint = defaultEyePoint;
    private Geom.XYZ upVector = defaultUpVector; // up vector

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
        eyePoint = defaultEyePoint;
        upVector = defaultUpVector;
    }

    public Facet getClickedFacet(float plainX, float plainY) {
        Geom.XYZ sightVector = eyePoint.scale(-100);
        Geom.XYZ sideVector = sightVector.vectorProduct(upVector).normalise();
        Geom.XYZ clickPoint = eyePoint.add(sideVector.scale(plainX)).add(upVector.normalise().scale(plainY));
        Geom.XYZ fromPoint = clickPoint.subtract(sightVector);
        Geom.XYZ toPoint = clickPoint.add(sightVector);

        final float mod = GeomConstants.CUBE_MAGNITUDE;
        Plain[] plains = new Plain[]{
                // x=-mod
                new Plain(Cube.LEFT, new Geom.XYZ(-mod, -mod, -mod), new Geom.XYZ(-mod, -mod, +mod), new Geom.XYZ(-mod, +mod, -mod)),
                // x=+mod
                new Plain(Cube.RIGHT, new Geom.XYZ(+mod, -mod, -mod), new Geom.XYZ(+mod, -mod, +mod), new Geom.XYZ(+mod, +mod, -mod)),
                // y=-mod
                new Plain(Cube.BOTTOM, new Geom.XYZ(-mod, -mod, -mod), new Geom.XYZ(-mod, -mod, +mod), new Geom.XYZ(+mod, -mod, -mod)),
                // y=+mod
                new Plain(Cube.TOP, new Geom.XYZ(-mod, +mod, -mod), new Geom.XYZ(-mod, +mod, +mod), new Geom.XYZ(+mod, +mod, -mod)),
                // z=-mod
                new Plain(Cube.FRONT, new Geom.XYZ(-mod, -mod, +mod), new Geom.XYZ(-mod, +mod, +mod), new Geom.XYZ(+mod, -mod, +mod)),
                // z=+mod
                new Plain(Cube.BACK, new Geom.XYZ(-mod, -mod, -mod), new Geom.XYZ(-mod, +mod, -mod), new Geom.XYZ(+mod, -mod, -mod)),
        };

        double minDistance = Double.POSITIVE_INFINITY;
        Plain nearestPlain = null;
        Geom.XYZ intersection = null;
        for (Plain plain : plains) {
            Geom.LineAndPlainIntersect intersect = Geom.computeAndAndPlainIntersection(plain.a, plain.b, plain.c, clickPoint, toPoint);
            if (intersect.getMode() == Geom.LineAndPlain.INTERSECT
                    && isPointInFace(plain, intersect.getIntersection())) {
                double distance = intersect.getIntersection().subtract(fromPoint).magnitude();
                if (nearestPlain == null || distance < minDistance) {
                    minDistance = distance;
                    nearestPlain = plain;
                    intersection = intersect.getIntersection();
                }
            }
        }

        if (nearestPlain != null) {
            // yeah, we found it!
            System.out.println(String.format("<%f, %f, %f>", intersection.getX(), intersection.getY(), intersection.getZ()));
            return createFacet(nearestPlain.face, intersection);
        } else {
            return null;
        }
    }

    private Facet createFacet(int face, Geom.XYZ intersection) {
        double x,y;
        switch (face) {
            case Cube.LEFT:
            case Cube.RIGHT:
                x = intersection.getZ();
                y = intersection.getY();
                break;
            case Cube.TOP:
            case Cube.BOTTOM:
                x = intersection.getX();
                y = intersection.getZ();
                break;
            case Cube.FRONT:
            case Cube.BACK:
                x = intersection.getX();
                y = intersection.getY();
                break;
            default: throw new IllegalArgumentException("Unknown face "  + face);
        }

        double fracX, fracY;
        fracX = (x + GeomConstants.CUBE_MAGNITUDE) / (GeomConstants.CUBE_MAGNITUDE * 2);
        // in <x,y> Y axis goes UP, so negating it
        fracY = 1.0 - (y + GeomConstants.CUBE_MAGNITUDE) / (GeomConstants.CUBE_MAGNITUDE * 2);

        int faceX, faceY;
        faceX = Math.min((int) (fracX * cube.getSize()), cube.getSize() - 1);
        faceY = Math.min((int) (fracY * cube.getSize()), cube.getSize() - 1);

        switch (face) {
            case Cube.BACK:
            case Cube.RIGHT:
            case Cube.BOTTOM:
                faceX = cube.getSize() - 1 - faceX;
                break;
        }

        switch (face) {
            case Cube.TOP:
            case Cube.BOTTOM:
                faceY = cube.getSize() - 1 - faceY;
                break;
        }

        return new Facet(face, faceY, faceX);
    }

    private boolean isPointInFace(Plain plain, Geom.XYZ intersection) {
        switch (plain.face) {
            case Cube.LEFT:
            case Cube.RIGHT:
                return Math.abs(intersection.getY()) <= GeomConstants.CUBE_MAGNITUDE
                        && Math.abs(intersection.getZ()) <= GeomConstants.CUBE_MAGNITUDE;
            case Cube.TOP:
            case Cube.BOTTOM:
                return Math.abs(intersection.getX()) <= GeomConstants.CUBE_MAGNITUDE
                        && Math.abs(intersection.getZ()) <= GeomConstants.CUBE_MAGNITUDE;
            case Cube.FRONT:
            case Cube.BACK:
                return Math.abs(intersection.getX()) <= GeomConstants.CUBE_MAGNITUDE
                        && Math.abs(intersection.getY()) <= GeomConstants.CUBE_MAGNITUDE;
            default: throw new IllegalArgumentException("Unknown face "  + plain.face);
        }
    }

    public static class Facet {
        private final int face;
        private final int row;
        private final int col;

        public Facet(int face, int row, int col) {
            this.face = face;
            this.row = row;
            this.col = col;
        }

        public int getFace() {
            return face;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    private static class Plain {
        private final int face;
        private final Geom.XYZ a,b,c;

        private Plain(int face, Geom.XYZ a, Geom.XYZ b, Geom.XYZ c) {
            this.face = face;
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}
