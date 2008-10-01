package com.shade.shadows;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A collection which orders things according to their depth.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class ZBuffer implements Iterable<ShadowEntity> {
    
    private boolean dirty;
    private LinkedList<ShadowEntity> entities;
    
    public ZBuffer() {
        entities = new LinkedList<ShadowEntity>();
    }

    public void add(ShadowEntity s) {
        entities.add(s);
        dirty = true;
    }


    public void remove(ShadowEntity s) {
        entities.remove(s);
    }

    public void clear() {
        entities.clear();
    }
    
    public Iterator<ShadowEntity> iterator() {
        if (dirty) {
            Collections.sort(entities);
        }
        return entities.iterator();
    }
    
}
