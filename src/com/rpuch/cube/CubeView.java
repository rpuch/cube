package com.rpuch.cube;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * @author rpuch
 */
public class CubeView extends GLSurfaceView {
    public CubeView(Context context) {
        super(context);

        setRenderer(new CubeRenderer());
    }
}
