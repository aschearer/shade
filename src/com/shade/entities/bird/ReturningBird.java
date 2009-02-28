package com.shade.entities.bird;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.CrashLevel;
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
public class ReturningBird implements State {

    private Bird bird;
    private Animation idling;
    private int timer;
    private Body nest;

    public ReturningBird(Bird me) throws SlickException {
        this.bird = me;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/bird/fly.png", 40, 40);
        idling = new Animation(idles, 75);
        idling.setAutoUpdate(false);
        // idling.setPingPong(true);
    }

    public void enter() {
        timer = 0;
        idling.restart();
        if (bird.level != null) {
            Object[] spots = bird.level.getEntitiesByRole(Roles.OBSTACLE
                    .ordinal());
            int pick = (int) (Math.random() * spots.length);
            nest = (Body) spots[pick];
        }
    }

    public int getRole() {
        return Roles.MONSTER.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Bird.States.RETURNING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
            // bird.manager.enter(Bird.States.RETURNING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        idling
                .draw(bird.getX(), bird.getY(), bird.getWidth(), bird
                        .getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        testTimer(delta);
        if (bird.level != null && nest == null)
            bird.manager.enter(Bird.States.RETURNING);
        if (nest != null) {
            float x = bird.getXCenter();
            float y = bird.getYCenter();
            float destx = nest.getXCenter();
            float desty = nest.getYCenter();
            float distx = destx - x;
            float disty = desty - y;
            float radius = (float) Math.sqrt(distx * distx + disty * disty);
            double curve = Math.PI
                    / 3
                    * Math
                            .pow(radius
                                    * Math.max(nest.getWidth(), nest
                                            .getHeight()), 0.02);
            if (radius > Math.min(nest.getWidth(), nest.getHeight())
                    || Math.min(nest.getWidth(), nest.getHeight()) < 30
                    || x < 30 || y < 30
                    || x > game.getContainer().getWidth() - 30
                    || y > game.getContainer().getHeight() - 30)
                curve = Math.PI / 8;
            bird.heading = (float) (Math.atan2(disty, distx) + Math.PI / 2 - curve);
            bird.move(1);
            if (radius < Math.min(nest.getWidth(), nest.getHeight()) / 2) {
                bird.manager.enter(Bird.States.WAITING);
            }
        }
    }

    private void testTimer(int delta) {
        timer += delta;
    }

}
