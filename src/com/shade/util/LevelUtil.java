package com.shade.util;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

public class LevelUtil {

    public static Vector2f randomPoint(GameContainer c) {
        Vector2f p = new Vector2f();
        p.x = (float) Math.random() * (c.getWidth() - 20) + 10;
        p.y = (float) Math.random() * (c.getHeight() - 20) + 10;
        return p;
    }
}
