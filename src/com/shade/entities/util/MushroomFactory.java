package com.shade.entities.util;

import java.util.LinkedList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.GameContainer;

import com.shade.entities.Mushroom;

public class MushroomFactory {

    /**
     * Corresponds to the Mushroom.Type enum.
     */
    private static final double[] distribution = { 0, .55, .1, .20, .15 };

    /* Minimum number of mushrooms alive at any one time. */
    private int floor;
    private double propensity;
    /* List of existing mushrooms. Mushrooms should remove themselves when
     * removed from the level.
     */
    private LinkedList<Mushroom> mushrooms;

    /**
     * @param floor The baseline number of mushrooms on the field.
     * @param propensity The likelihood to add a mushroom over the baseline.
     */
    public MushroomFactory(int floor, double propensity) {
        this.floor = floor;
        this.propensity = propensity;
        mushrooms = new LinkedList<Mushroom>();
    }

    /**
     * Return true you should create a new mushroom.
     *
     * @return
     */
    public boolean active() {
        if (mushrooms.size() < floor) {
            return true;
        }
        return (Math.random() <= propensity);
    }

    public Mushroom getMushroom(GameContainer c) throws SlickException {
        float x = (float) (c.getWidth() * Math.random());
        float y = (float) (c.getHeight() * Math.random());
        int t = randomType();
        Mushroom m = new Mushroom(x, y, getType(t), this);
        mushrooms.add(m);
        return m;
    }

    public void remove(Mushroom m) {
        mushrooms.remove(m);
    }

    private Mushroom.MushroomType getType(int i) {
        Mushroom.MushroomType[] types = Mushroom.MushroomType.values();
        return types[i];
    }

    private int randomType() {
        double r = Math.random();

        double max = 0;
        for (int i = 0;i < distribution.length;i++) {
            max += distribution[i];
            if (r <= max) return i;
        }
        return 0; //should never reach here
    }
}
