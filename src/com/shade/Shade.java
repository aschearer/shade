package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.states.MasterState;

public class Shade extends StateBasedGame {

    public static final String TITLE = "Shade";

    public Shade() {
        super(TITLE);
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MasterState());
    }

    public static void main(String[] args) {
        try {
            Shade s = new Shade();
            AppGameContainer c = new AppGameContainer(s, 800, 600, false);
            c.setShowFPS(false);
            //c.setTargetFrameRate(60);
            c.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
