package com.shade.entities;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MockPlayer extends Player {

    public MockPlayer(float x, float y) throws SlickException {
        super(x, y);
    }

    @Override
    public void update(StateBasedGame game, int delta) {
        // do nothing cuz we're faking it
    }

    @Override
    public int getRole() {
        // TODO return a mock player role
        return Roles.PLAYER.ordinal();
    }

}
