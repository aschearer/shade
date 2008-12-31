package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Linkable;
import com.shade.entities.Roles;

public class NormalShroom implements State {

    private Mushroom shroom;

    public NormalShroom(Mushroom mushroom) {
        shroom = mushroom;
    }

    public void enter() {
        // do nothing
    }

    public int getRole() {
        return Roles.MUSHROOM.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mushroom.States.NORMAL;
    }

    public void onCollision(Entity obstacle) {
        if (isPicker(obstacle)) {
            ((Linkable) obstacle).attach(shroom);
            if (shroom.prev != null) {
                shroom.manager.enter(Mushroom.States.PICKED);
                if (shroom.isPoison()) {
                    Mushroom.poisonPicked.play();
                } else {
                    Mushroom.picked.play();
                }
            }
        }
        if (obstacle.getRole() == Roles.BASKET.ordinal()) {
            Mushroom.collected.play();
            shroom.detach();
            shroom.kill();
        }
    }

    private boolean isPicker(Entity obstacle) {
        return obstacle.getRole() == Roles.PLAYER.ordinal()
                || obstacle.getRole() == Roles.MOLE.ordinal();
    }

    public void render(StateBasedGame game, Graphics g) {
        shroom.draw();
    }

    public void update(StateBasedGame game, int delta) {
        if (shroom.tooSmall()) {
            shroom.kill();
        }

        // sunny shrink
        if (!shroom.inShadows()) {
            shroom.shrink();
            return;
        }
        // shady grow
        if (shroom.inShadows() && !shroom.tooBig()) {
            shroom.grow();
            return;
        }
    }

}
