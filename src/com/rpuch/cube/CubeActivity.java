package com.rpuch.cube;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.rpuch.cube.game.Game;
import com.rpuch.cube.tech.Objs;

public class CubeActivity extends Activity implements SensorEventListener {
    private CubeView view;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor orientation;
    private Sensor magneticField;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new CubeView(this);
        setContentView(view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        getGame().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
        sensorManager.unregisterListener(this);
        Log.i("cube", "Uninstalled sensors listener");
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("cube", "Installed sensors listener");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                getGame().getCube().rotateHoriz(1, false); // left
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_2:
                Objs.getGame().getCube().rotateHoriz(1, true); // right
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_3:
                getGame().getCube().rotateVert(1, false); // backwards
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_4:
                Objs.getGame().getCube().rotateVert(1, true); // forward
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_5:
                getGame().getCube().rotateSideways(1, false); // counter-clockwise
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_6:
                Objs.getGame().getCube().rotateSideways(1, true); // clockwise
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_0:
                Objs.getGame().getCube().reset();
                resetGeometry();
                return true;
            case KeyEvent.KEYCODE_ENTER:
                Objs.getGame().resetEye();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void resetGeometry() {
        CubeRenderer renderer = Objs.getRenderer();
        if (renderer != null) {
            renderer.resetGeometry();
        }
    }

    private Game getGame() {
        return Objs.getGame();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        getGame().getPhysics().onSensorEvent(sensorEvent);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
