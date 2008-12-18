package com.shade.controls;

import com.shade.base.Animatable;

/**
 * A small piece of control logic which fits into a larger controller.
 * 
 * In most cases a control slice will be a normal animatable object. In some
 * cases a control slice will not need to render. In those cases the render
 * method is there for completeness.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface ControlSlice extends Animatable {

    public void register(ControlListener c);
}
