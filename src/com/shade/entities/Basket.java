package com.shade.entities;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.controls.MushroomCounter;
import com.shade.shadows.ShadowEntity;

public class Basket extends Linkable implements ShadowEntity {
    
    private Image sprite;
    private ShadowIntensity shadowStatus;
    private LinkedList<MushroomCounter> counters;
    private Level level;

    public Basket(float x, float y, float w, float h) throws SlickException {
        initShape(x, y, w, h);
        initSprite();
        counters = new LinkedList<MushroomCounter>();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/basket/basket.png");
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public Role getRole() {
        return Role.BASKET;
    }
    
    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.MUSHROOM) {
            Mushroom m = (Mushroom) obstacle;
            m.detach();
            notifyCounters(m);
            level.remove(m);
        }
    }
    
    private void notifyCounters(Mushroom m) {
        for (MushroomCounter c : counters) {
            c.onCollect(m);
        }
    }

    public void add(MushroomCounter counter) {
        counters.add(counter);
    }
    

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

	public void repel(Entity repellee) {
		// TODO Auto-generated method stub
		
	}
	
    public int getZIndex() {
        return 1;
    }

    public int compareTo(ShadowEntity s) {
        return getZIndex() - s.getZIndex();
    }

	public void updateIntensity() {
		// TODO Auto-generated method stub
		
	}

	public float getShadowIntensity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateIntensity(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
