package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Roles;
import com.shade.util.Geom;

public class CollectedShroom implements State {

    private Mushroom shroom;
    private boolean killed;

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
            shroom.kill();
            killed = true;
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        shroom.draw();
    }

    public void update(StateBasedGame game, int delta) {
        if (killed && shroom.prev == null) {
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

        // way too far away, break off
        if (Util.overThreshold(shroom, shroom.prev, 120000)) {
            shroom.detach();
            shroom.manager.enter(Mushroom.States.NORMAL);
            return;
        }

        followLeader();
    }

    private void followLeader() {
        float angle = Util.calculateAngle(shroom, shroom.prev);
        Vector2f v = Geom.calculateVector(Mushroom.SPEED, angle);
        shroom.nudge(v.x, v.y);
    }

}
