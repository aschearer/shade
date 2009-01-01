package com.shade.entities.monster;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.StateManager;
import com.shade.controls.DayPhaseTimer;
import com.shade.crash.CrashLevel;
import com.shade.crash.Repelable;
import com.shade.entities.Basket;
import com.shade.entities.Player;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.levels.Model;
import com.shade.lighting.LuminousEntity;

/**
 * The real deal; this mole is the sum of different mole states.
 *
 * No I haven't heard of encapsulation.
 *
 * @author Jonathan Jou <j.j@duke.edu>
 */
public final class Monster extends Body implements LuminousEntity{

    protected enum States {
        PROWLING, SLEEPING, LOST, CHASING, WANDERING, SNIFFING
    }

    protected CrashLevel level;
    protected StateManager manager;
    protected Mushroom target;
    protected float heading;
    protected float range;
    protected float speed;
    
    private float luminosity;

    public Monster(int x, int y, int range, float speed) throws SlickException {
        heading = (float) (Math.PI);
        this.range = range;
        this.speed = speed;
        initShape(x, y);
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 18f);
    }

    private void initStates() throws SlickException {
        manager = new StateManager();
        manager.add(new ProwlingMonster(this));
        manager.add(new ChasingMonster(this));
        manager.add(new SleepingMonster(this));
        manager.add(new LostMonster(this));
        manager.add(new SniffingMonster(this));
        manager.add(new WanderingMonster(this));
    }

    protected void kill() {
        level.remove(this);
    }
    
    public void move(double rate){
    	float x = (float)(Math.cos(heading-Math.PI/2)*speed*rate);
    	float y = (float)(Math.sin(heading-Math.PI/2)*speed*rate);
    	nudge(x,y);
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level < ? > l) {
        level = (CrashLevel) l;
    }


    public int getRole() {
        return Roles.MONSTER.ordinal();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(this);
        }
    }
    
    public boolean canChase(){
    	return playerInSight() && playerInRange();
    }
    
    public boolean playerInSight(){
    	Player p = (Player)level.getEntitiesByRole(Roles.PLAYER.ordinal())[0];
    	return level.lineOfSight(this,p, this,(Basket)level.getEntitiesByRole(Roles.BASKET.ordinal())[0]) && p.getLuminosity()>0.6;
    }
    
    public boolean playerInRange(){
    	Player p = (Player)level.getEntitiesByRole(Roles.PLAYER.ordinal())[0];
    	float distx = p.getXCenter()-getXCenter();
    	float disty = p.getYCenter()-getYCenter();
    	return Math.sqrt(distx*distx+disty*disty)<range;
    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getXCenter(), getYCenter(), (float) Math.toDegrees(heading));
        manager.render(game, g);
        g.resetTransform();
    }
    
    public void yawn(){
    	Model mode = (Model)level;
    	if(mode.getTimer().getDaylightStatus()==DayPhaseTimer.DayLightStatus.NIGHT){
        	manager.enter(States.SLEEPING);
        }
    }
    
    public void wake(){
    	Model mode = (Model)level;
    	if(mode.getTimer().getDaylightStatus()==DayPhaseTimer.DayLightStatus.DAWN){
        	manager.enter(States.PROWLING);
        }
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();
        
    }
    

    /**
     * Checks whether a linkable is over the edge of the screen and wraps it if
     * it is.
     */
    protected void testAndWrap() {
        if (getXCenter() <= 5) {
            shape.setCenterX(795);
        }
        if (getXCenter() > 795) {
            shape.setCenterX(5);
        }
        if (getYCenter() <= 5) {
            shape.setCenterY(595);
        }
        if (getYCenter() > 595) {
            shape.setCenterY(5);
        }
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public int getZIndex() {
        return 2;
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }


	public void removeFromLevel(Level<?> l) {
		level.remove(this);
		
	}
}
