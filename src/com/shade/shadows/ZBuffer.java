package com.shade.shadows;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

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
    
}
