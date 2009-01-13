package com.shade.entities.treasure;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Linkable;
import com.shade.entities.Roles;

public class NormalTreasure implements State {

    private Treasure treasure;

    public NormalTreasure(Treasure mushroom) {
        treasure = mushroom;
    }

    public void enter() {
        treasure.close();
    }

    public int getRole() {
        return Roles.TREASURE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Treasure.States.NORMAL;
    }

    public void onCollision(Entity obstacle) {
        if (isPicker(obstacle)) {
            ((Linkable) obstacle).attach(treasure);
            if (treasure.prev != null) {
                treasure.manager.enter(Treasure.States.PICKED);
                if (treasure.isPoison()) {
                    Treasure.poisonPicked.play();
                } else {
                    Treasure.picked.play();
                }
            }
        }
        if (obstacle.getRole() == Roles.BASKET.ordinal()) {
            Treasure.collected.play();
            treasure.detach();
            treasure.kill();
        }
    }

    private boolean isPicker(Entity obstacle) {
        return obstacle.getRole() == Roles.PLAYER.ordinal()
                || obstacle.getRole() == Roles.MOLE.ordinal();
    }

    public void render(StateBasedGame game, Graphics g) {
    	treasure.draw(g);
    }

    public void update(StateBasedGame game, int delta) {
        if (treasure.tooSmall()) {
            //shroom.kill();
        }

        // sunny shrink
        if (!treasure.inShadows()) {
           // shroom.shrink();
            return;
        }
        // shady grow
        if (treasure.inShadows() && !treasure.tooBig()) {
            //shroom.grow();
            return;
        }
    }

}
