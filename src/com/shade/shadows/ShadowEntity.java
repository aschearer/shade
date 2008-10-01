package com.shade.shadows;

import com.shade.base.Entity;

/**
 * An entity which can have a shadow cast on it.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface ShadowEntity extends Entity, Comparable<ShadowEntity> {

    /**
     * Represents the possible intensities of shadow on the entity.
     * 
     * Usually, have a cast shadow on you means you're OK. Sometimes, it will
     * be enough to just be shadowed.
     */
    public enum ShadowIntensity {
        UNSHADOWED, SHADOWED, CASTSHADOWED
    };
    
    public boolean hasIntensity(ShadowIntensity s);
    public void setIntensity(ShadowIntensity s);
    
    /**
     * Used to sort the entities and render them according to their z-index.
     * 
     * TODO have a duck-like interface thing going on here.
     * @return
     */
    public int getZIndex();
}
