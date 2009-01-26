package com.shade.entities.bird;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Player;
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
public class WaitingBird implements State {

    private Bird bird;
    private Animation idling;
    private int timer;

    public WaitingBird(Bird me) throws SlickException {
        this.bird = me;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/bird/fly.png", 40, 40);
        idling = new Animation(idles, 125);
        idling.setAutoUpdate(false);
        // idling.setPingPong(true);
    }

    public void enter() {
        timer = 0;
        idling.restart();
        // wait.loop();
    }

    public int getRole() {
        return Roles.MOLE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Bird.States.WAITING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
            bird.manager.enter(Bird.States.RETURNING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        idling
                .draw(bird.getX(), bird.getY(), bird.getWidth(), bird
                        .getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        bird.yawn();
        testTimer(delta);
        checkPlayer();
    }

    private void checkPlayer() {
        float x = bird.getXCenter();
        float y = bird.getYCenter();
        Object[] o = bird.level.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (o.length > 0) {
            Player p = (Player) bird.level.getEntitiesByRole(Roles.PLAYER
                    .ordinal())[0];
            float destx = p.getXCenter();
            float desty = p.getYCenter();
            float distx = destx - x;
            float disty = desty - y;
            float radius = (float) Math.sqrt(distx * distx + disty * disty);
            bird.heading = (float) (Math.atan2(disty, distx) + Math.PI / 2);
            if (radius < bird.range * 1.8) {
                idling.setSpeed(Math.min(5, (float) Math.pow(bird.range
                        / radius, 4) * 10));
                if (!Bird.alert.playing()) {
                    Bird.alert.play();
                }
            } else {
                idling.setSpeed(1);
            }
            if (radius < bird.range) {
                // TODO: I think the bird should shriek and turn angry
                // (territorial).
                // this would give the player some time to back off.
                if (!Bird.attack.playing()) {
                    Bird.attack.play();
                }
                bird.manager.enter(Bird.States.ATTACKING);
            }
        }
    }

    private void testTimer(int delta) {
        timer += delta;
        if (timer > 2000) {
            if (Math.random() > 0.2)
                bird.manager.enter(Bird.States.WAITING);
            else {
                bird.manager.enter(Bird.States.RETURNING);
            }
        }
    }

}
