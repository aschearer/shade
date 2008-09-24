package com.shade.controls;

import com.shade.entities.Mushroom;

public interface MushroomCounter {

    /**
     * Called when the player collects a shroomie.
     * 
     * @param shroomie
     */
    public void onCollect(Mushroom shroomie);
}
