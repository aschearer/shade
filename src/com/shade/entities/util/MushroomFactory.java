package com.shade.entities.util;

import org.newdawn.slick.SlickException;

import com.shade.entities.Mushroom;

public class MushroomFactory {

    /**
     * Corresponds to the Mushroom.Type enum.
     */
    private static final double[] distribution = { 0, .75, .20, .05 };

    /**
     * Take care of assigning the mushroom a type.
     * 
     * @param x
     * @param y
     * @return
     * @throws SlickException 
     */
    public static Mushroom makeMushroom(float x, float y) throws SlickException {
        return new Mushroom(x, y, getType(randomType()));
    }

    private static Mushroom.MushroomType getType(int i) {
        Mushroom.MushroomType[] types = Mushroom.MushroomType.values();
        return types[i];
    }

    private static int randomType() {
        double r = Math.random();
        
        double max = distribution[0];
        if (r <= max) {
            return 0;
        }
        
        max += distribution[1];
        if (r <= max) {
            return 1;
        }

        max += distribution[2];
        if (r <= max) {
            return 2;
        }
        
        max += distribution[3];
        return 3;
    }
}
