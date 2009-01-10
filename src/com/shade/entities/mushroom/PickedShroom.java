package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.Repelable;
import com.shade.entities.Roles;
import com.shade.util.Geom;

public class PickedShroom implements State {

    private Mushroom shroom;

    public PickedShroom(Mushroom mushroom) {
        shroom = mushroom;
    }

    public void enter() {

    }

    public int getRole() {
        return Roles.PICKED_MUSHROOM.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mushroom.States.PICKED;
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
            
            // check if way too far away, if so break off
            if (WrappingUtils.overThreshold(shroom, shroom.prev, 12000)) {
                shroom.detach();
                shroom.manager.enter(Mushroom.States.NORMAL);
                return;
            }
        }
        if (obstacle.getRole() == Roles.BASKET.ordinal()) {
            Mushroom.collected.play();
            shroom.detach();
            shroom.kill();
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        shroom.draw();
    }

    public void update(StateBasedGame game, int delta) {
        if (shroom.prev == null) {
            shroom.manager.enter(Mushroom.States.NORMAL);
            return;
        }

        if (shroom.prev.getRole() == Roles.BASKET.ordinal()) {
            shroom.manager.enter(Mushroom.States.COLLECTED);
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

        // too far away, catch up
        if (WrappingUtils.overThreshold(shroom, shroom.prev, 1200)) {
            followLeader();
            return;
        }
    }

    private void followLeader() {
        float angle = WrappingUtils.calculateAngle(shroom, shroom.prev);
        Vector2f v = Geom.calculateVector(Mushroom.SPEED, angle);
        shroom.nudge(v.x, v.y);
    }

}
