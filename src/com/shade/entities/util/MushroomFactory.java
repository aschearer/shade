package com.shade.entities.util;

import org.newdawn.slick.SlickException;

import com.shade.entities.Mushroom;

public class MushroomFactory {

    /**
     * Corresponds to the Mushroom.Type enum.
     */
    private static final double[] distribution = { 0, .55,.1, .20, .15 };

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
        
        double max = 0;
        for(int i=0;i<distribution.length;i++){
        	max+= distribution[i];
        	if(r<= max) return i;
        }
        return 0; //should never reach here
    }
}
