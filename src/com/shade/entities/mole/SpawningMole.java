package com.shade.entities.mole;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Roles;

/**
 * A mole who is underground and is determining whether to appear above ground.
 *
 * Spawning moles:
 *   + Are not rendered on screen
 *   + Should play the "spawnling" role
 *   + Only surface on clear ground
 *   + Wait to surface a set amount of time
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class SpawningMole implements State {

    private Mole mole;
    private int timer;
    private boolean clear;

    public SpawningMole(Mole mole) {
        this.mole = mole;
    }

    public void enter() {
        timer = 0;
        clear = true;
    }

    public int getRole() {
        return Roles.SPAWNLING.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mole.States.SPAWNING;
    }

    public void onCollision(Entity obstacle) {
        clear = false;
    }

    public void render(StateBasedGame game, Graphics g) {
        // don't render this state
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        // it was clear for 2 seconds so spawn
        if (timer > 2000 && clear) {
            mole.manager.enter(Mole.States.IDLE);
        }
        // it was not clear, you waited, so respawn
        if (timer > 5000 && !clear) {
            mole.kill();
        }
    }

}
