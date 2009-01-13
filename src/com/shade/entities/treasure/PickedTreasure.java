package com.shade.entities.treasure;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.Repelable;
import com.shade.entities.Roles;
import com.shade.entities.treasure.Treasure.States;
import com.shade.util.Geom;

public class PickedTreasure implements State {

    private Treasure treasure;

    public PickedTreasure(Treasure t) {
        treasure = t;
    }

    public void enter() {
    	treasure.open();
    }

    public int getRole() {
        return Roles.TREASURE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Treasure.States.PICKED;
    }

    public void onCollision(Entity obstacle) {
        // TODO determine whether this is desired behavior
        // if (obstacle.getRole() == Roles.MOLE.ordinal()) {
        // shroom.detach();
        // ((Linkable) obstacle).attach(shroom);
        // }
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(treasure);
            
            // check if way too far away, if so break off
            if (Util.overThreshold(treasure, treasure.prev, 12000)) {
                treasure.detach();
                treasure.manager.enter(Treasure.States.NORMAL);
                return;
            }
        }
        if (obstacle.getRole() == Roles.BASKET.ordinal()) {
            Treasure.collected.play();
            treasure.detach();
            treasure.finish();
        }
    }

    public void render(StateBasedGame game, Graphics g) {
    	treasure.draw(g);
    }

    public void update(StateBasedGame game, int delta) {
        if (treasure.prev == null) {
            treasure.manager.enter(Treasure.States.NORMAL);
            return;
        }

        if (treasure.prev.getRole() == Roles.BASKET.ordinal()) {
            treasure.manager.enter(Treasure.States.COLLECTED);
            return;
        }

        if (treasure.tooSmall()) {
            treasure.manager.enter(Treasure.States.RETURNING);
            return;
        }

        // sunny shrink
        if (!treasure.inShadows()) {
            treasure.shrink(delta);
        }

        // too far away, catch up
        if (Util.overThreshold(treasure, treasure.prev, 1200)) {
            followLeader();
            return;
        }
    }

    private void followLeader() {
        float angle = Util.calculateAngle(treasure, treasure.prev);
        Vector2f v = Geom.calculateVector(Treasure.SPEED, angle);
        treasure.nudge(v.x, v.y);
    }

}
