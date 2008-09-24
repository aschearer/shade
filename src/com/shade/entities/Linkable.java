package com.shade.entities;

import com.shade.crash.Body;

/**
 * Linkables are bodies which form a doubly linked list.
 * 
 * This is the pattern used to implement the mushrooms-player following
 * behavior. Mushrooms should follow the player but stay a certain distance
 * away.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public abstract class Linkable extends Body {

    public Linkable prev, next;
    
}
