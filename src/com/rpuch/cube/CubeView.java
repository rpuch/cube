package com.rpuch.cube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.rpuch.cube.game.Game;
import com.rpuch.cube.tech.Objs;

/**
 * @author rpuch
 */
public class CubeView extends GLSurfaceView {
    private CubeRenderer renderer;

    public CubeView(Context context) {
        super(context);

        setDebugFlags(DEBUG_CHECK_GL_ERROR/* | DEBUG_LOG_GL_CALLS*/);

        setRenderer(renderer = new CubeRenderer());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        System.out.println(String.format("Old %d, %d; new %d, %d", w, h, oldw, oldh));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int width = getWidth();
        int height = getHeight();
        float glX = ((float) (event.getX() - width/2)) / (width/2);
        float glY = - ((float) (event.getY() - height/2)) / (height/2);

        if (event.getAction() == MotionEvent.ACTION_DOWN && renderer != null) {
            Game.Facet facet = getGame().getClickedFacet(glX, glY);
            if (facet != null) {
//                System.out.println(String.format("Face %d, <row,col> is <%d,%d>", facet.getFace(), facet.getRow(), facet.getCol()));
                getGame().startSelection(facet);
            } else {
                boolean left = event.getX() < width / 3;
                boolean right = event.getX() > width * 2 / 3;
                boolean top = event.getY() < height / 3;
                boolean bottom = event.getY() > height * 2 / 3;
                if (left) {
                    getGame().rotateInHorizonPlain(-15f);
                }
                if (right) {
                    getGame().rotateInHorizonPlain(+15f);
                }
                if (top) {
                    getGame().rotateInTerminatorPlain(-15f);
                }
                if (bottom) {
                    getGame().rotateInTerminatorPlain(+15f);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Game.Facet start = getGame().getSelectionStart();
            if (start != null) {
                Game.Facet facet = getGame().getClickedFacet(glX, glY);
                if (facet != null && facet.getFace() == start.getFace()) {
                    getGame().selectTo(facet);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            boolean rotated = getGame().rotateIfNeeded();
            getGame().resetSelection();
            if (rotated) {
                renderer.resetGeometry();
            }
        }

        getGame().getPhysics().nullifySpeed();

        return true;
    }

    private Game getGame() {
        return Objs.getGame();
    }
}
