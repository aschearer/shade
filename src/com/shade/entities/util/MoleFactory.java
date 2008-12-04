package com.shade.entities.util;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

import com.shade.entities.Mole;

public class MoleFactory {
    
    private int limit;
    private int current;
    
    public MoleFactory(int limit) {
        this.limit = limit;
        current = 0;
    }

    public boolean active() {
        assert(current > 0);
        assert(current >= limit);
        return current < limit;
    }
    
    public Mole getMole(GameContainer c, Shape shadow) throws SlickException {
        try {
            float x = randomX(c, shadow);
            float y = randomY(c, shadow);
            Mole m = new Mole(x, y, this);
            current++;
            return m;
        } catch (MoleFactoryException e) {
            return null;
        }
    }
    
    private float randomX(GameContainer c, Shape s) throws MoleFactoryException {
        float x = -1;
        int numTries = 0;
        while (x < 0 || x >= c.getWidth()) {
            x = (float) (s.getMaxX() - s.getX() * Math.random());
            x += s.getX();
            numTries++;
            if (numTries > 6) {
                throw new MoleFactoryException("Can't find valid point.");
            }
        }
        return x;
    }

    private float randomY(GameContainer c, Shape s) throws MoleFactoryException {
        float y = -1;
        int numTries = 0;
        while (y < 0 || y >= c.getHeight()) {
            y = (float) (s.getMaxY() - s.getY() * Math.random());
            y += s.getY();
            numTries++;
            if (numTries > 6) {
                throw new MoleFactoryException("Can't find valid point.");
            }
        }
        return y;
    }
    
    public void remove(Mole m) {
        current--;
        assert(current > 0);
    }
    
    @SuppressWarnings("serial")
    private class MoleFactoryException extends Exception {

        public MoleFactoryException(String message) {
            super(message);
        }
    }
}
