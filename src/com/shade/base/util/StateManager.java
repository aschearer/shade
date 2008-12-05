package com.shade.base.util;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.base.Entity;

/**
 * A utility which manages a set of states and facilitates state transitions.
 *
 * Any entity which is split into a set of states should use a manager to govern
 * the transition between states. The manager acts like a proxy for each state.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class StateManager implements Animatable {

    private LinkedList<State> states;
    private State currentState;

    public StateManager() {
        states = new LinkedList<State>();
        currentState = null;
    }

    /**
     * Add the state to the manager; assign the first state to be the current
     * state.
     *
     * @param s
     */
    public void add(State s) {
        states.add(s);
        if (currentState == null) {
            currentState = s;
            currentState.enter();
        }
    }

    /**
     * Attempts to enter the target state.
     *
     * @param o
     * @return
     */
    public boolean enter(Object o) {
        for (State s : states) {
            if (s.isNamed(o)) {
                currentState = s;
                currentState.enter();
                return true;
            }
        }
        return false;
    }

    /**
     * Useful if you want ot know hat state you are currently in; this isn't a
     * hidden markov model!
     *
     * @return
     */
    public State currentState() {
        return currentState;
    }

    public void update(StateBasedGame game, int delta) {
        currentState.update(game, delta);
    }

    public void onCollision(Entity obstacle) {
        currentState.onCollision(obstacle);
    }

    public void render(StateBasedGame game, Graphics g) {
        currentState.render(game, g);
    }

    public int getRole() {
        return currentState.getRole();
    }

}
