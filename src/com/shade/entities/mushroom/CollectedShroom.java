package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.Repelable;
import com.shade.entities.Roles;
import com.shade.util.Geom;

public class CollectedShroom implements State {

    private Mushroom shroom;
    private boolean killed;
    private boolean blocked;

    public CollectedShroom(Mushroom mushroom) {
        shroom = mushroom;
    }

    public void enter() {
        // don't need to do anything
    }

    public int getRole() {
        return Roles.PICKED_MUSHROOM.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mushroom.States.COLLECTED;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.BASKET.ordinal()) {
            Mushroom.collected.play();
            killed = true;
        }
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(shroom);
            // blocked or way too far away, break off
            if (blocked || WrappingUtils.overThreshold(shroom, shroom.prev, 120000)) {
                shroom.detach();
                shroom.manager.enter(Mushroom.States.NORMAL);
                return;
            }
            blocked = true;
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        shroom.draw();
    }

    public void update(StateBasedGame game, int delta) {
        if (killed && shroom.prev == null) {
            shroom.kill();
            shroom.detach();
            return;
        }

        if (shroom.tooSmall()) {
            shroom.kill();
            return;
        }

        // sunny shrink
        if (!shroom.inShadows()) {
            shroom.shrink();
        }

        followLeader();
    }

    private void followLeader() {
        float angle = WrappingUtils.calculateAngle(shroom, shroom.prev);
        Vector2f v = Geom.calculateVector(Mushroom.SPEED, angle);
        shroom.nudge(v.x, v.y);
    }

}
