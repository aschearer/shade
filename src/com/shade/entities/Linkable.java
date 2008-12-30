package com.shade.entities;


import com.crash.Body;
import com.shade.lighting.LuminousEntity;

/**
 * Linkables are bodies which form a doubly linked list.
 *
 * This is the pattern used to implement the mushrooms-player following
 * behavior. Mushrooms should follow the player but stay a certain distance
 * away.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public abstract class Linkable extends Body implements LuminousEntity {

    public Linkable prev, next;

    /**
     * Attach the object to the end of this linked list.
     *
     * @param l
     */
    public void attach(Linkable l) {
        if (next == null) {
            next = l;
            l.prev = this;
            return;
        }
        Linkable head = next;
        while (head.next != null) {
            head = head.next;
        }
        head.next = l;
        l.prev = head;
    }
    
    /**
     * Destroy the list!
     */
    public void detachAll(){
    	if(prev!= null) prev.next = null;
    	if(next!=null)next.detachAll();
    	prev = null;
    	next = null;
    }

    /**
     * Remove this object from its linked list.
     */
    public void detach() {
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        prev = null;
        next = null;
    }

    /**
     * Return true if the given linkable is behind the current on in the list.
     * @param l
     * @return
     */
    protected boolean contains(Linkable l) {
        Linkable head = next;
        while (head != null) {
            if (head.equals(l)) {
                return true;
            }
            head = head.next;
        }
        return false;
    }

    /**
     * Checks whether a linkable is over the edge of the screen and wraps it if
     * it is.
     */
    protected void testAndWrap() {
        if (getXCenter() <= 5) {
            shape.setCenterX(795);
        }
        if (getXCenter() > 795) {
            shape.setCenterX(5);
        }
        if (getYCenter() <= 5) {
            shape.setCenterY(595);
        }
        if (getYCenter() > 595) {
            shape.setCenterY(5);
        }
    }

}
