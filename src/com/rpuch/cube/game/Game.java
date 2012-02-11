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

    private Facet selectionStart = null;
    private Facet selectionEnd = null;

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
//            System.out.println(String.format("<%f, %f, %f>", intersection.getX(), intersection.getY(), intersection.getZ()));
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

        int faceX = glXToFacetX(x, face);
        int faceY = glYToFacetY(y, face);

        return new Facet(face, faceY, faceX);
    }

    private int glXToFacetX(double x, int face) {
        double fracX;
        int faceX;
        fracX = (x + GeomConstants.CUBE_MAGNITUDE) / (GeomConstants.CUBE_MAGNITUDE * 2);
        faceX = Math.min((int) (fracX * cube.getSize()), cube.getSize() - 1);
        faceX = flipByXOnTurnedFaces(faceX, face);
        return faceX;
    }

    private int flipByXOnTurnedFaces(int faceX, int face) {
        return flipByXOnTurnedFaces(faceX, face, cube.getSize() - 1);
    }

    private int flipByXOnTurnedFaces(int faceX, int face, int edge) {
        switch (face) {
            case Cube.BACK:
            case Cube.RIGHT:
            case Cube.BOTTOM:
                faceX = edge - faceX;
                break;
        }
        return faceX;
    }

    private int glYToFacetY(double y, int face) {
        double fracY;
        int faceY;
        // in <x,y> Y axis goes UP, so negating it
        fracY = 1.0 - (y + GeomConstants.CUBE_MAGNITUDE) / (GeomConstants.CUBE_MAGNITUDE * 2);
        faceY = Math.min((int) (fracY * cube.getSize()), cube.getSize() - 1);
        faceY = flipByYOnTurnedFaces(faceY, face);
        return faceY;
    }

    private int flipByYOnTurnedFaces(int faceY, int face) {
        return flipByYOnTurnedFaces(faceY, face, cube.getSize() - 1);
    }

    private int flipByYOnTurnedFaces(int faceY, int face, int edge) {
        switch (face) {
            case Cube.TOP:
            case Cube.BOTTOM:
                faceY = edge - faceY;
                break;
        }
        return faceY;
    }

    private double facetXToGlX(int faceX, int face) {
        faceX = flipByXOnTurnedFacesFromTheEdge(faceX, face);
        double frac = ((double) faceX) / cube.getSize();
        return frac * (GeomConstants.CUBE_MAGNITUDE * 2) - GeomConstants.CUBE_MAGNITUDE;
    }

    private int flipByXOnTurnedFacesFromTheEdge(int faceX, int face) {
        return flipByXOnTurnedFaces(faceX, face, cube.getSize());
    }

    private int flipByYOnTurnedFacesFromTheEdge(int faceY, int face) {
        return flipByYOnTurnedFaces(faceY, face, cube.getSize());
    }

    private double facetYToGlY(int faceY, int face) {
        faceY = flipByYOnTurnedFacesFromTheEdge(faceY, face);
        double frac = ((double) faceY) / cube.getSize();
        // in GL Y axis goes UP, so negating it
        return ((1.0-frac) * (GeomConstants.CUBE_MAGNITUDE * 2) - GeomConstants.CUBE_MAGNITUDE);
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

    public Facet getSelectionStart() {
        return selectionStart;
    }

    public Facet getSelectionEnd() {
        return selectionEnd;
    }

    public void startSelection(Facet start) {
        selectionStart = start;
        selectionEnd = start;
    }

    public void selectTo(Facet end) {
        selectionEnd = end;
    }

    public void resetSelection() {
        selectionStart = selectionEnd = null;
    }

    public XYZ4 getSelection() {
        Facet start = selectionStart;
        Facet end = selectionEnd;
        if (start == null || end == null) {
            return null;
        }
        return buildSelectionCoords(start, end);
    }

    private XYZ4 buildSelectionCoords(Facet start, Facet end) {
        int minRow = Math.min(start.getRow(), end.getRow());
        int maxRow = Math.max(start.getRow(), end.getRow());
        int minCol = Math.min(start.getCol(), end.getCol());
        int maxCol = Math.max(start.getCol(), end.getCol());
        Geom.XYZ p1 = buildCornerCoords(start.getFace(), minCol, minRow);
        Geom.XYZ p2 = buildCornerCoords(start.getFace(), maxCol + 1, minRow);
        Geom.XYZ p3 = buildCornerCoords(start.getFace(), maxCol + 1, maxRow + 1);
        Geom.XYZ p4 = buildCornerCoords(start.getFace(), minCol, maxRow + 1);
        return new XYZ4(p1, p2, p3, p4);
    }

    private Geom.XYZ buildCornerCoords(int face, int col, int row) {
        double glX = facetXToGlX(col, face);
        double glY = facetYToGlY(row, face);
        switch (face) {
            case Cube.FRONT:
                return new Geom.XYZ(glX, glY, +GeomConstants.CUBE_MAGNITUDE);
            case Cube.BACK:
                return new Geom.XYZ(glX, glY, -GeomConstants.CUBE_MAGNITUDE);
            case Cube.LEFT:
                return new Geom.XYZ(-GeomConstants.CUBE_MAGNITUDE, glY, glX);
            case Cube.RIGHT:
                return new Geom.XYZ(+GeomConstants.CUBE_MAGNITUDE, glY, glX);
            case Cube.TOP:
                return new Geom.XYZ(glX, +GeomConstants.CUBE_MAGNITUDE, glY);
            case Cube.BOTTOM:
                return new Geom.XYZ(glX, -GeomConstants.CUBE_MAGNITUDE, glY);
            default: throw new IllegalArgumentException("Unknown face: " + face);
        }
    }

    public Rotation computeSelectionRotation() {
        Game.Facet start = getSelectionStart();
        Game.Facet end = getSelectionEnd();
        if (start != null && end != null && start.getFace() == end.getFace()) {
            int diffX = end.getCol() - start.getCol();
            int diffY = end.getRow() - start.getRow();
            int modX = Math.abs(diffX);
            int modY = Math.abs(diffY);
            boolean xChanged = diffX != 0;
            boolean yChanged = diffY != 0;
            if ((xChanged || yChanged) && (modX != modY)) {
                boolean byX = modX > modY;
                int parallel = byX ? diffX : diffY;
                int parallelMod = byX ? modX : modY;
                int orthoMod = byX ? modY : modX;
                int parallelFrom = byX ? Math.min(start.getCol(), end.getCol()) : Math.min(start.getRow(), end.getRow());
                int parallelTo = byX ? Math.max(start.getCol(), end.getCol()) : Math.max(start.getRow(), end.getRow());
                int orthoFrom = byX ? Math.min(start.getRow(), end.getRow()) : Math.min(start.getCol(), end.getCol());
                int orthoTo = byX ? Math.max(start.getRow(), end.getRow()) : Math.max(start.getCol(), end.getCol());
                if (orthoFrom == 0 || orthoTo == getCube().getSize() - 1) {
                    int orthoSpan = orthoTo - orthoFrom + 1;
                    int units = orthoFrom == 0 ? orthoSpan : -orthoSpan;
                    switch (start.getFace()) {
                        case Cube.LEFT:
                            if (byX) {
                                return new Rotation(Rotation.Plain.HORIZ, units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.SIDEWAYS, -units, parallel < 0);
                            }
                        case Cube.RIGHT:
                            if (byX) {
                                return new Rotation(Rotation.Plain.HORIZ, units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.SIDEWAYS, units, parallel > 0);
                            }
                        case Cube.FRONT:
                            if (byX) {
                                return new Rotation(Rotation.Plain.HORIZ, units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.VERT, units, parallel > 0);
                            }
                        case Cube.BACK:
                            if (byX) {
                                return new Rotation(Rotation.Plain.HORIZ, units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.VERT, -units, parallel < 0);
                            }
                        case Cube.TOP:
                            if (byX) {
                                return new Rotation(Rotation.Plain.SIDEWAYS, -units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.VERT, units, parallel > 0);
                            }
                        case Cube.BOTTOM:
                            if (byX) {
                                return new Rotation(Rotation.Plain.SIDEWAYS, -units, parallel > 0);
                            } else {
                                return new Rotation(Rotation.Plain.VERT, -units, parallel < 0);
                            }
                        default: throw new IllegalStateException("Unknown face: " + start.getFace());
                    }
                }
            }
        }
        return null;
    }

    public boolean rotateIfNeeded() {
        Rotation rotation = computeSelectionRotation();
        if (rotation != null) {
            System.out.println(String.format("Rotation: %s, %d, %b", rotation.getPlain(), rotation.getUnits(), rotation.isDir()));
            switch (rotation.getPlain()) {
                case HORIZ:
                    cube.rotateHoriz(rotation.getUnits(), rotation.isDir());
                    break;
                case VERT:
                    cube.rotateVert(rotation.getUnits(), rotation.isDir());
                    break;
                case SIDEWAYS:
                    cube.rotateSideways(rotation.getUnits(), rotation.isDir());
                    break;
                default: throw new IllegalStateException("Unknown plain: " + rotation.getPlain());
            }
            return true;
        } else {
            return false;
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

    public static class XYZ2 {
        public final Geom.XYZ p1, p2;

        public XYZ2(Geom.XYZ p1, Geom.XYZ p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    public static class XYZ4 {
        public final Geom.XYZ p1, p2, p3, p4;

        public XYZ4(Geom.XYZ p1, Geom.XYZ p2, Geom.XYZ p3, Geom.XYZ p4) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
        }
    }

    public static class Rotation {
        private Plain plain;
        private int units;
        private boolean dir;

        public Rotation(Plain plain, int units, boolean dir) {
            this.plain = plain;
            this.units = units;
            this.dir = dir;
        }

        public Plain getPlain() {
            return plain;
        }

        public int getUnits() {
            return units;
        }

        public boolean isDir() {
            return dir;
        }

        public static enum Plain { HORIZ, VERT, SIDEWAYS }
    }
}
