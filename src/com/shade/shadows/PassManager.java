package com.shade.shadows;

import org.newdawn.slick.Graphics;

/**
 * Controls the rendering process between shadows and objects.
 * 
 * Shadows should be rendered below all the shadow casters. Hence they should
 * be rendered first followed by the shadow casters.
 * 
 * @author Alexander Schearer <aschearer@duke.edu>
 */
public class PassManager {

    private Shadowscape shadowscape;
    private ZBuffer buffer;
    
    public PassManager(ZBuffer b, Shadowscape s) {
        buffer = b;
        shadowscape = s;
    }
    
    public void render(Graphics g) {
        shadowscape.render(g); // draw the shadowscape first
        for (ShadowCaster s : buffer) {
            s.render(g); // render each shadow caster, shortest to tallest
        }
    }
}
