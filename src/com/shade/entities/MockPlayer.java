package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MockPlayer extends Player {

    public MockPlayer(int x, int y) throws SlickException {
        super(x, y);
        invincibleTimer = 0;
    }

    @Override
    public void update(StateBasedGame game, int delta) {
        // do nothing cuz we're faking it
    }
    
    @Override
    public void render(StateBasedGame game, Graphics g) {
        normal.drawCentered(getXCenter(), getYCenter());
    }

    @Override
    public int getRole() {
        return Roles.MOCK_PLAYER.ordinal();
    }

}
