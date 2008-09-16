package com.shade.shadows;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * A collection which orders things according to their depth.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class ZBuffer implements Iterable<ShadowCaster> {
    
    private PriorityQueue<ShadowCaster> casters;
    
    public ZBuffer() {
        casters = new PriorityQueue<ShadowCaster>();
    }

    public void add(ShadowCaster s) {
        casters.add(s);
    }


    public void remove(ShadowCaster s) {
        casters.remove(s);
    }

    public void clear() {
        casters.clear();
    }
    
    public Iterator<ShadowCaster> iterator() {
        return casters.iterator();
    }
    
}
