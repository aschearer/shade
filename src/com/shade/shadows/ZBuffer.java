package com.shade.shadows;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A collection which orders things according to their depth.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class ZBuffer implements Iterable<ShadowCaster> {
    
    private boolean dirty;
    private LinkedList<ShadowCaster> casters;
    
    public ZBuffer() {
        casters = new LinkedList<ShadowCaster>();
    }

    public void add(ShadowCaster s) {
        casters.add(s);
        dirty = true;
    }


    public void remove(ShadowCaster s) {
        casters.remove(s);
    }

    public void clear() {
        casters.clear();
    }
    
    public Iterator<ShadowCaster> iterator() {
        if (dirty) {
            Collections.sort(casters);
        }
        return casters.iterator();
    }
    
    /**
     * Return all casters with z-index below the ceiling.
     * 
     * This is used to paint the shadowscape over certain casters.
     * @param ceiling
     * @return
     */
    public Iterable<ShadowCaster> under(int ceiling) {
        LinkedList<ShadowCaster> qualified = new LinkedList<ShadowCaster>();
        for (ShadowCaster s : this) {
            if (s.getZIndex() >= ceiling) {
                return qualified;
            }
            qualified.add(s);
        }
        return qualified;
    }
    
    /**
     * Return all casters with z-index at or above the floor.
     * 
     * This is used to paint the shadowscape over certain casters.
     * @param floor
     * @return
     */
    public Iterable<ShadowCaster> over(int floor) {
        LinkedList<ShadowCaster> qualified = new LinkedList<ShadowCaster>();
        for (ShadowCaster s : this) {
            if (s.getZIndex() >= floor) {
                qualified.add(s);
            }
        }
        return qualified;
    }
    
}
