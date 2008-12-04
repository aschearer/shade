package com.shade.entities.util;

import java.util.LinkedList;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

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

    public Mushroom getMushroom(GameContainer c, Shape shadow) throws SlickException {
        try {
            float x = randomX(c, shadow);
            float y = randomY(c, shadow);
            int t = randomType();
            Mushroom m = new Mushroom(x, y, getType(t), this);
            mushrooms.add(m);
            return m;
        } catch (MushroomFactoryException e) {
            return null;
        }
    }

    private float randomX(GameContainer c, Shape s) throws MushroomFactoryException {
        float x = -1;
        int numTries = 0;
        while (x < 0 || x >= c.getWidth()) {
            x = (float) (s.getMaxX() - s.getX() * Math.random());
            x += s.getX();
            numTries++;
            if (numTries > 6) {
                throw new MushroomFactoryException("Can't find valid point.");
            }
        }
        return x;
    }

    private float randomY(GameContainer c, Shape s) throws MushroomFactoryException {
        float y = -1;
        int numTries = 0;
        while (y < 0 || y >= c.getHeight()) {
            y = (float) (s.getMaxY() - s.getY() * Math.random());
            y += s.getY();
            numTries++;
            if (numTries > 6) {
                throw new MushroomFactoryException("Can't find valid point.");
            }
        }
        return y;
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

    @SuppressWarnings("serial")
    private class MushroomFactoryException extends Exception {

        public MushroomFactoryException(String message) {
            super(message);
        }
    }
}
