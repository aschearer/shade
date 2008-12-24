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
public class SleepingMonster implements State {

    private Monster monster;
    private Animation idling;
    private Animation snores;
    private int timer;

    public SleepingMonster(Monster mole) throws SlickException {
        this.monster = mole;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/mole/sleep.png", 40, 40);
        SpriteSheet z = new SpriteSheet("entities/mole/z.png",40,40);
        snores = new Animation(z,900);
        idling = new Animation(idles, 600);
        idling.setAutoUpdate(false);
        idling.setPingPong(true);
    }

    public void enter() {
        timer = 0;
        snores.restart();
        idling.restart();
    }

    public int getRole() {
        return Roles.MOLE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Monster.States.SLEEPING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
            monster.manager.enter(Monster.States.WANDERING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
    	snores.draw(monster.getX()+monster.getWidth()/2, monster.getY()+monster.getHeight()/2, monster.getWidth(), monster.getHeight());
        idling.draw(monster.getX(), monster.getY(), monster.getWidth(), monster.getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        snores.update(delta);
        monster.wake();
        testTimer(delta);
    }

    private void testTimer(int delta) {
    	timer+=delta;
    }

}
