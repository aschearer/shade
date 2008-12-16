package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.states.InGameState;
import com.shade.states.TitleState;

public class Shade extends StateBasedGame {

    public static final String TITLE = "Shade";

    public Shade() {
        super(TITLE);
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new TitleState());
        addState(new InGameState());
    }

    public static void main(String[] args) {
        try {
            Shade s = new Shade();
            AppGameContainer c = new AppGameContainer(s, 800, 600, false);
            // container.setTargetFrameRate(60);
            // container.setVSync(true);
            c.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
