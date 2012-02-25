package com.rpuch.cube.game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
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

    private int samples = 0;
    private double[] gravity = new double[3];

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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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

            if (samples > 10) {
                double mag = accel.magnitude();
                double curMag = curAccel.magnitude();
                boolean small = mag < MAGNITUDE_THRESHOLD;
                boolean curSmall = curMag < MAGNITUDE_THRESHOLD;

                if (!small || !curSmall) {
                    boolean closeAngle = false;
                    if (small || curSmall) {
                        closeAngle = true;
                    }
                    closeAngle |= curAccel.angleWithVectorInDegrees(accel) < ANGLE_THRESHOLD;
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

            samples++;
        }
    }

    private void applyAcceleration(Geom.XYZ curAccel) {
        curVel = curVel.add(curAccel);
    }

    private void applyPhysics() {
        // TODO: rotate in one operation
        game.rotateInHorizonPlain((float) curVel.getX());
        game.rotateInTerminatorPlain((float) curVel.getZ());
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
}
