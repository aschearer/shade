package com.shade.controls;

import com.shade.base.Animatable;
import com.shade.entities.mushroom.Mushroom;

public interface MushroomCounter extends Animatable {

    /**
     * Called when the player collects a shroomie.
     *
     * @param shroomie
     */
    public void onCollect(Mushroom shroomie);
}
