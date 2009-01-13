package com.shade.entities.treasure;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Roles;

/**
 * A treasure which has been vanquished by sunlight, and returning
 * back into the spawning point
 * 
 * @author Jonathan Jou <thepalatinepoet@gmail.com>
 */
public class ReturningTreasure implements State {

    private Treasure treasure;
    private int timer, failmer;
    private boolean clear;

    public ReturningTreasure(Treasure t) {
        treasure = t;
    }

    public void enter() {
        timer = 0;
        clear = true;
        treasure.detach();
        treasure.scale = Treasure.MAX_SCALE;
    }

    public int getRole() {
        return Roles.SPAWNLING.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Treasure.States.RETURNING;
    }

    public void onCollision(Entity obstacle) {
        clear = false;
    }

    public void render(StateBasedGame game, Graphics g) {
    	//System.out.println("wheee");
    	if (timer < 500 || failmer % 5 > 2) {
    		treasure.draw(g);
        }
    	//g.setColor(new Color(0.7f, 0.7f, 0f));
		//g.fillOval(treasure.getXCenter(), treasure.getYCenter(), 50,50);
		

    }

    public void update(StateBasedGame game, int delta) {
    	//move towards treasure
    	treasure.goHome();
        timer += delta;
        failmer++;
        // it was clear so spawn
        if (timer >2000) {
            treasure.unsize();
            treasure.manager.enter(Treasure.States.NORMAL);
        }
    }

}
