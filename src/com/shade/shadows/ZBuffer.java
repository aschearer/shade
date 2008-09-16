package com.shade.shadows;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A collection which orders things according to their depth.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class ZBuffer implements Iterable<ShadowCaster> {
    
    private Queue<ShadowCaster> casters;
    
    public ZBuffer() {
        casters = new LinkedList<ShadowCaster>();
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
    
    public ShadowCaster get(int i) {
        return ((LinkedList<ShadowCaster>) casters).get(i);
    }
    
    public int size() {
        return casters.size();
    }
    
    public Iterator<ShadowCaster> iterator() {
        return casters.iterator();
    }
    
}
