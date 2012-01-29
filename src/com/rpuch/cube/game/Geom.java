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

        public XYZ normalise() {
            double mag = Math.sqrt(x*x + y*y + z*z);
            if (mag == 0) {
                return this;
            }
            return new XYZ(x/mag, y/mag, z/mag);
        }

        public XYZ vectorProduct(XYZ v) {
            return new XYZ(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
        }

        public XYZ negate() {
            return new XYZ(-x, -y, -z);
        }
    }
}
