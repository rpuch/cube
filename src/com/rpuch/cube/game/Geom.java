package com.rpuch.cube.game;

/**
 * @author rpuch
 */
public class Geom {
    private Geom() {}

    /*
       Rotate a point p by angle theta around an arbitrary axis r
       Return the rotated point.
       Positive angles are anticlockwise looking down the axis
       towards the origin.
       Assume right hand coordinate system.
    */
    public static XYZ arbitraryRotate(XYZ p, double theta, XYZ r)
    {
       XYZ q = new XYZ(0, 0, 0);
       double costheta,sintheta;

       r = r.normalise();
       costheta = Math.cos(theta);
       sintheta = Math.sin(theta);

       q.x += (costheta + (1 - costheta) * r.x * r.x) * p.x;
       q.x += ((1 - costheta) * r.x * r.y - r.z * sintheta) * p.y;
       q.x += ((1 - costheta) * r.x * r.z + r.y * sintheta) * p.z;

       q.y += ((1 - costheta) * r.x * r.y + r.z * sintheta) * p.x;
       q.y += (costheta + (1 - costheta) * r.y * r.y) * p.y;
       q.y += ((1 - costheta) * r.y * r.z - r.x * sintheta) * p.z;

       q.z += ((1 - costheta) * r.x * r.z - r.y * sintheta) * p.x;
       q.z += ((1 - costheta) * r.y * r.z + r.x * sintheta) * p.y;
       q.z += (costheta + (1 - costheta) * r.z * r.z) * p.z;

       return(q);
    }

    // a,b,c define plain, x,y define line
    public static LineAndPlainIntersect computeAndAndPlainIntersection(XYZ a, XYZ b, XYZ c, XYZ x, XYZ y) {
        XYZ normal = b.subtract(a).vectorProduct(c.subtract(a)).normalise();
        XYZ lineToPlainVector = a.subtract(x);
        double lineToPlainDistance = normal.dotProduct(lineToPlainVector);
        XYZ lineVector = y.subtract(x);
        double lineDistance = normal.dotProduct(lineVector);
        if (lineDistance != 0) {
            XYZ result = x.add(lineVector.scale(lineToPlainDistance / lineDistance));
            return new LineAndPlainIntersect(LineAndPlain.INTERSECT, result);
        } else if (lineToPlainDistance == 0) {
            return new LineAndPlainIntersect(LineAndPlain.BELONG, null);
        } else {
            return new LineAndPlainIntersect(LineAndPlain.PARALLEL, null);
        }
    }

    public static enum LineAndPlain {
        INTERSECT, BELONG, PARALLEL
    }

    public static class LineAndPlainIntersect {
        private LineAndPlain mode;
        private XYZ intersection;

        private LineAndPlainIntersect(LineAndPlain mode, XYZ intersection) {
            this.mode = mode;
            this.intersection = intersection;
        }

        public LineAndPlain getMode() {
            return mode;
        }

        public XYZ getIntersection() {
            return intersection;
        }
    }

    public static class XYZ {
        double x, y, z;

        public XYZ(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public double magnitude() {
            return Math.sqrt(x*x + y*y + z*z);
        }

        public XYZ normalise() {
            double mag = magnitude();
            if (mag == 0) {
                return this;
            }
            return new XYZ(x/mag, y/mag, z/mag);
        }

        public XYZ vectorProduct(XYZ v) {
            return new XYZ(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
        }

        public XYZ scale(double scalar) {
            return new XYZ(x*scalar, y*scalar, z*scalar);
        }

        public XYZ negate() {
            return scale(-1);
        }

        public XYZ add(XYZ v) {
            return new XYZ(x+v.x, y+v.y, z+v.z);
        }

        public XYZ subtract(XYZ v) {
            return new XYZ(x-v.x, y-v.y, z-v.z);
        }

        public double dotProduct(XYZ v) {
            return x*v.x + y*v.y + z*v.z;
        }

        public double angleWithVectorInDegrees(XYZ v) {
            double cos = this.dotProduct(v)/this.magnitude()/v.magnitude();
            return Trig.radiansToDegrees(Math.acos(cos));
        }

        public String toString() {
            return String.format("%2.2f/%2.2f/%2.2f", x, y, z);
        }
    }
}
