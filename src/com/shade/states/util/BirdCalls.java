package com.shade.states.util;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class BirdCalls {
    
    private int lastPlayed;
    private Sound[] birds;
    
    public BirdCalls() throws SlickException {
        birds = new Sound[4];
        birds[0] = new Sound("states/common/birds/bird1.ogg");
        birds[1] = new Sound("states/common/birds/bird2.ogg");
        birds[2] = new Sound("states/common/birds/bird3.ogg");
        birds[3] = new Sound("states/common/birds/bird4.ogg");
    }

    public void play() {
        int i = (int) Math.floor(Math.random() * birds.length);
        birds[i].play();
        lastPlayed = i;
    }
    
    public boolean playing() {
        return birds[lastPlayed].playing();
    }
}
