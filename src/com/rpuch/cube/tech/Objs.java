package com.rpuch.cube.tech;

import com.rpuch.cube.CubeRenderer;
import com.rpuch.cube.game.Game;

/**
 * @author rpuch
 */
public class Objs {
    private static final Game game = new Game();
    private static CubeRenderer renderer;

    public static Game getGame() {
        return game;
    }

    public static CubeRenderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(CubeRenderer renderer) {
        Objs.renderer = renderer;
    }
}
