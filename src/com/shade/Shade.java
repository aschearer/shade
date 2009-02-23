package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import com.shade.states.MasterState;

public class Shade extends StateBasedGame {

    public static final String TITLE = "Shade";
    private static final String[] ICONS = {
        "icons/icon.16.gif",
        "icons/icon.32.gif",
        "icons/icon.64.gif",
        "icons/icon.128.gif"
    };

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
            c.setIcons(ICONS);
            c.setShowFPS(false);
            c.setTargetFrameRate(60);
            Log.setVerbose(true);
            c.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
