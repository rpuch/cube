package com.rpuch.cube.game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author rpuch
 */
public class Physics {
    private static final double MAGNITUDE_THRESHOLD = 3.0;
    private static final double ANGLE_THRESHOLD = 30; // degrees

    private Game game;

    private Geom.XYZ curAccel = new Geom.XYZ(0, 0, 0);

    private Geom.XYZ curVel = new Geom.XYZ(0, 0, 0);

    private int accelSamples = 0;
    private double[] gravity = new double[3];

    private float[] a_gravity = null;
    private float[] a_geomagn = null;

    private Thread physicsThread = new Thread(new Runnable() {
        public void run() {
            while (!stopped) {
                applyPhysics();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    stopped = true;
                }
            }
        }
    }, "Physics");
    private boolean started = false;
    private boolean stopped = false;

    public Physics(Game game) {
        this.game = game;
    }

    public void onSensorEvent(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometerEvent(event);
                break;
            case Sensor.TYPE_ORIENTATION:
                handleOrientationEvent(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                a_geomagn = event.values;
                break;
        }
    }

    private void handleAccelerometerEvent(SensorEvent event) {
        a_gravity = event.values;
        if (true) return;

        double[] linear_acceleration = new double[3];

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        Geom.XYZ accel = new Geom.XYZ(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
        Log.i("cube", "Accel is " + accel.toString());

        if (accelSamples > 10) {
            processAccel(accel, MAGNITUDE_THRESHOLD, ANGLE_THRESHOLD);
        }

        accelSamples++;
    }

    private void processAccel(Geom.XYZ accel, double magnitudeThreshold, double angleThreahold) {
        double mag = accel.magnitude();
        double curMag = curAccel.magnitude();
        boolean small = mag < magnitudeThreshold;
        boolean curSmall = curMag < magnitudeThreshold;

        if (!small || !curSmall) {
            boolean closeAngle = false;
            if (small || curSmall) {
                closeAngle = true;
            }
            closeAngle |= curAccel.angleWithVectorInDegrees(accel) < angleThreahold;
            if (closeAngle) {
                if (mag > curMag) {
                    curAccel = accel;
                } else {
                    // we've found a peak
                    applyAcceleration(curAccel);
                    curAccel = new Geom.XYZ(0, 0, 0);
                }
            } else {
                // direction has changed drastically
                applyAcceleration(curAccel);
                if (small) {
                    curAccel = new Geom.XYZ(0, 0, 0);
                } else {
                    curAccel = accel;
                }
            }
        }
    }

    private void applyAcceleration(Geom.XYZ curAccel) {
        curVel = curVel.add(curAccel);
    }

    private void handleOrientationEvent(SensorEvent event) {
        synchronized (OrPh.monitor) {
            Log.i("cube", String.format("x %2.2f, y %2.2f, z %2.2f, ts %d", event.values[0], event.values[1], event.values[2], event.timestamp));
            if (OrPh.hasPrev && a_gravity != null && a_geomagn != null) {
                long tsDelta = (event.timestamp - OrPh.prevTimestamp) / 1000000;
                Log.i("cube", "tsDelta = " + tsDelta);
                if (tsDelta > 0) {
                    double azimuthVel = Trig.signedCycleDistance(OrPh.prev[0], event.values[0], 0, 360) / tsDelta;
                    double pitchVel = Trig.signedCycleDistance(OrPh.prev[1], event.values[1], -180, 180) / tsDelta;
                    double rollVel = Trig.signedCycleDistance(OrPh.prev[2], event.values[2], -90, 90) / tsDelta;
                    Geom.XYZ angleVel = new Geom.XYZ(azimuthVel, pitchVel, rollVel);

//                    float[] rotationMatrix = new float[9];
//                    SensorManager.getRotationMatrix(rotationMatrix, null, a_gravity, a_geomagn);
//                    float[] vel = new float[]{(float) azimuthVel, (float) pitchVel, (float) rollVel};
////                    float[] vel = new float[3];
//                    SensorManager.getOrientation(rotationMatrix, vel);
//                    Geom.XYZ fixedVel = new Geom.XYZ(vel[0], vel[1], vel[2]);

                    Log.i("cube", "Angle vel is " + angleVel.toString());
//                    Log.i("cube", "Fixed vel is " + fixedVel.toString());
//                    angleVel = fixedVel;
                    angleVel = angleVel.scale(50.0);
                    processAccel(angleVel, OrPh.MAGNITUDE_VEL_THRESHOLD, OrPh.ANGLE_VEL_THRESHOLD);
                }
            }
            System.arraycopy(event.values, 0, OrPh.prev, 0, event.values.length);
            OrPh.prevTimestamp = event.timestamp;
            OrPh.hasPrev = true;
        }
    }

    private void applyPhysics() {
        // TODO: rotate in one operation
//        game.rotateInHorizonPlain((float) curVel.getX());
//        game.rotateInTerminatorPlain((float) curVel.getZ());

        game.rotateInHorizonPlain((float) curVel.getZ());
    }

    public void start() {
        synchronized (this) {
//            if (started) {
//                throw new IllegalStateException("Already started");
//            }
            if (!started) {
                physicsThread.start();
                started = true;
            }
        }
    }

    public void stop() {
        stopped = true;
    }

    public void nullifySpeed() {
        curVel = new Geom.XYZ(0, 0, 0);
        curAccel = new Geom.XYZ(0, 0, 0);
    }

    private static class OrPh {
        private static final double MAGNITUDE_VEL_THRESHOLD = 1.0;
        private static final double ANGLE_VEL_THRESHOLD = 30;

        private static final Object monitor = new Object();

        private static boolean hasPrev = false;
        private static final float[] prev = new float[3];
        private static long prevTimestamp;
    }
}
