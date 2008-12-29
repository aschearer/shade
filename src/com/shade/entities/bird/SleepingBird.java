package com.shade.entities.bird;

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
public class SleepingBird implements State {

    private Bird bird;
    private Animation idling;
    private Animation snores;
    private int timer;

    public SleepingBird(Bird me) throws SlickException {
        this.bird = me;
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
        return o == Bird.States.SLEEPING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
            bird.manager.enter(Bird.States.WAITING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
    	snores.draw(bird.getX()+bird.getWidth()/2, bird.getY()+bird.getHeight()/2, bird.getWidth(), bird.getHeight());
        idling.draw(bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        snores.update(delta);
        bird.wake();
        testTimer(delta);
    }

    private void testTimer(int delta) {
    	timer+=delta;
    }

}
