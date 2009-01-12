package com.shade.entities.bird;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Roles;

/**
 * A mole who has not yet found any mushrooms but is searching for them.
 *
 * Idle moles: + Have not identified a target mushroom + Have zero mushrooms
 * attached + Are checking for line-of-sight with nearby mushrooms + Return
 * underground after a set amount of time
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class AttackingBird implements State {
	public static final int ATTACK_TIME = 1500;
	public static final int COOLDOWN_TIME = 500;
    private Bird bird;
    private Animation idling;
    private int timer;

    public AttackingBird(Bird me) throws SlickException {
        this.bird = me;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/bird/attack.png", 40, 40);
        idling = new Animation(idles, 600);
        idling.setAutoUpdate(false);
        idling.setPingPong(true);
    }

    public void enter() {
    	bird.attacking = true;
    	if (!Bird.attack.playing()) {
    	    Bird.attack.play();
    	}
        timer = 0;
        idling.restart();
    }

    public int getRole() {
        return Roles.MONSTER.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Bird.States.ATTACKING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
           // bird.manager.enter(Bird.States.RETURNING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        idling.draw(bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        bird.wake();
       testTimer(delta);
    }

    private void testTimer(int delta) {
    	timer+=delta;
    	if(timer>ATTACK_TIME){
    		bird.move(0.3);
    		idling.setSpeed(15);
    		if(timer> ATTACK_TIME+COOLDOWN_TIME){
    		bird.manager.enter(Bird.States.RETURNING);
    		bird.attacking = false;
    		}
    	}
    	else  {
    		bird.move(1.5);
    		idling.setSpeed(1);
    	}
    }

}
