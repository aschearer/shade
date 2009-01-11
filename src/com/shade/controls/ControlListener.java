package com.shade.controls;

/**
 * Any object which listens for events from ControlSlices.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface ControlListener {

    public void fire(ControlSlice c);
}
