package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.Repelable;
import com.shade.entities.Player;
import com.shade.entities.Roles;

public class FlyingShroom implements State {
	
	public static final int FLIGHT_TIME = 100;
	public static final double speed = 10;

    private Mushroom shroom;
    private int timer;
    private double angle;

    public FlyingShroom(Mushroom mushroom) {
        shroom = mushroom;
    }

    public void enter() {
    	shroom.detach();
    	 Player p = (Player)shroom.level.getEntitiesByRole(Roles.PLAYER.ordinal())[0];
    	 float x = shroom.getXCenter();
    	 float y = shroom.getYCenter();
         float destx = p.getXCenter();
         float desty = p.getYCenter();
         float distx = destx-x;
         float disty = desty-y;
    	angle = Math.random()*Math.PI+Math.atan2(disty,distx);
    	timer = 0;
    }

    public int getRole() {
        return Roles.MUSHROOM.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mushroom.States.FLYING;
    }

    public void onCollision(Entity obstacle) {
        // TODO determine whether this is desired behavior
        // if (obstacle.getRole() == Roles.MOLE.ordinal()) {
        // shroom.detach();
        // ((Linkable) obstacle).attach(shroom);
        // }
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(shroom);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        shroom.draw();
    }

    public void update(StateBasedGame game, int delta) {

        if (shroom.tooSmall()) {
            shroom.kill();
            return;
        }

        // sunny shrink
        if (!shroom.inShadows()) {
            shroom.shrink();
        }
        testTimer(delta);
        shroom.nudge((float)(speed*Math.cos(angle)), (float)(speed*Math.sin(angle)));

    }
    
    private void testTimer(int delta){
    	timer+=delta;
    	if(timer>FLIGHT_TIME){
    		shroom.manager.enter(Mushroom.States.NORMAL);
    	}
    }


}
