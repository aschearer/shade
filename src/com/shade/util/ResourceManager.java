package com.shade.util;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Manager the various assets so the rest of the app doesn't have to.
 * 
 * There are two goals here:
 *   1. Consolidate loading of resources in one place to avoid duplication.
 *   2. Provide mechanism to stream content on the fly instead of locking.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class ResourceManager {
    
    private HashMap<String, Image> resources;
    
    public ResourceManager() {
        resources = new HashMap<String, Image>();
    }

    public boolean register(String name, String resource) throws SlickException {
        if (resources.containsKey(name)) {
            return false;
        }
        resources.put(name, new Image(resource));
        return true;
    }
    
    public Image get(String resource) {
        return resources.get(resource);
    }
}
