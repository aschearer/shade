package com.shade.entities.util;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;

import com.shade.base.Entity;

public class StateManager {
    
    private LinkedList<State> states;
    private State currentState;
    
    public StateManager() {
        states = new LinkedList<State>();
        currentState = null;
    }

    public void add(State s) {
        states.add(s);
        if (currentState == null) {
            currentState = s;
        }
    }
    
    /**
     * Attempts to enter the target state. Fails silently!!
     * 
     * TODO have this fail politely but loudly.
     * @param o
     */
    public void enter(Object o) {
        for (State s : states) {
            if (s.equals(o)) {
                currentState = s;
            }
        }
    }
    
    public void update(int delta) {
        currentState.update(delta);
    }
    
    public void onCollision(Entity obstacle) {
        currentState.onCollision(obstacle);
    }

    public void render(Graphics g) {
        currentState.render(g);
    }

}
