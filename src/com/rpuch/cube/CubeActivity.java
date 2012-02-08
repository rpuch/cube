package com.rpuch.cube;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.rpuch.cube.game.Game;
import com.rpuch.cube.tech.Objs;

public class CubeActivity extends Activity {
    private CubeView view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new CubeView(this);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
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
}
