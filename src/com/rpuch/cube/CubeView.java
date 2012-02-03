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
    public CubeView(Context context) {
        super(context);

        setRenderer(new CubeRenderer());
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
        CubeRenderer renderer = Objs.getRenderer();

        if (event.getAction() == MotionEvent.ACTION_DOWN && renderer != null) {
            float glX = ((float) (event.getX() - width/2)) / (width/2);
            float glY = - ((float) (event.getY() - height/2)) / (height/2);
            Game.Facet facet = getGame().getClickedFacet(glX, glY);
            if (facet != null) {
                // TODO:
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
        }
        return true;
    }

    private Game getGame() {
        return Objs.getGame();
    }
}
