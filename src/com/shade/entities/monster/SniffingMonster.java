package com.shade.entities.monster;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
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
public class SniffingMonster implements State {

	private Monster monster;
	private Animation idling;
	private int timer;

	public SniffingMonster(Monster mole) throws SlickException {
		this.monster = mole;
		initResources();
	}

	private void initResources() throws SlickException {
		SpriteSheet idles = new SpriteSheet("entities/mole/sniff.png", 40, 40);
		idling = new Animation(idles, 300);
		idling.setAutoUpdate(false);
		idling.setPingPong(true);
	}

	public void enter() {
		timer = 0;
		idling.restart();
	}

	public int getRole() {
		return Roles.MOLE.ordinal();
	}

	public boolean isNamed(Object o) {
		return o == Monster.States.SNIFFING;
	}

	public void onCollision(Entity obstacle) {
		if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
			monster.manager.enter(Monster.States.WANDERING);
		}
	}

	public void render(StateBasedGame game, Graphics g) {
		idling.draw(monster.getX(), monster.getY(), monster.getWidth(), monster
				.getHeight());
	}

	public void update(StateBasedGame game, int delta) {
		idling.update(delta);
		hunt();
		monster.yawn();
		testTimer(delta);
	}

	private void hunt() {
		if (monster.canChase()) {
			monster.manager.enter(Monster.States.CHASING);
		}
	}

	private void testTimer(int delta) {
		timer += delta;
		if (timer > 1500) {
			if (Math.random() > 0.4) {
				monster.heading += (float) ((Math.random() - 0.5) * 3/2 * Math.PI);
				monster.manager.enter(Monster.States.SNIFFING);
			} else
				monster.manager.enter(Monster.States.PROWLING);
		}
	}

}
