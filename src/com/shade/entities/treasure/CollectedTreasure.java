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

public class CollectedTreasure implements State {

    private Treasure treasure;
    private boolean killed;

    public CollectedTreasure(Treasure t) {
        treasure = t;
    }

    public void enter() {
        // don't need to do anything
    }

    public int getRole() {
        return Roles.TREASURE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Treasure.States.COLLECTED;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.BASKET.ordinal()&&!killed) {
            Treasure.collected.play();
            treasure.collect = true;
            //treasure.kill();
            killed = true;
        }
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(treasure);
            // way too far away, break off
            if (Util.overThreshold(treasure, treasure.prev, 120000)) {
                treasure.detach();
                treasure.manager.enter(Treasure.States.NORMAL);
                return;
            }
        }
    }

    public void render(StateBasedGame game, Graphics g) {
    	treasure.draw(g);
    }

    public void update(StateBasedGame game, int delta) {
        if (killed && treasure.prev == null) {
            treasure.detach();
            return;
        }

        if (treasure.tooSmall()) {
            treasure.kill();
            return;
        }

        // sunny shrink
        if (!treasure.inShadows()) {
           // shroom.shrink();
        }

        followLeader();
    }

    private void followLeader() {
        float angle = Util.calculateAngle(treasure, treasure.prev);
        Vector2f v = Geom.calculateVector(Treasure.SPEED, angle);
        treasure.nudge(v.x, v.y);
    }

}
