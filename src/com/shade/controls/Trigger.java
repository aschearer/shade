package com.shade.controls;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.*;

/**
 * A trigger contains some game logic which is updated as long as it is
 * active; Usually a trigger is used to set off an event in a game.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface Trigger {

    public boolean isActive();

    public void update(StateBasedGame game, int delta) throws SlickException;

}
