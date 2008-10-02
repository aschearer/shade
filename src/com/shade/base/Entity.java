package com.shade.base;

/**
 * A game object which can interact with other objects and be removed from the
 * game dynamically.
 * 
 * @author Alex Schearer <aschearer@gmail.com>
 */
public interface Entity extends Animatable {

    /**
     * Roles are used to determine how to respond to a collision.
     */
    public enum Role {
        PLAYER, MUSHROOM, OBSTACLE, BASKET, MOLE, RAY, MONSTER
    };
    

    
    public Role getRole();

    /**
     * Add this entity to the given level, perform any necessary set up.
     * 
     * In many cases this will not need to do anything. In cases where an entity
     * dynamically creates other entities than the level should be stored so
     * that new entities can be added.
     * 
     * @param l
     */
    public void addToLevel(Level l);

    /**
     * Called when this object is to be removed from the level.
     * 
     * In almost all cases being removed from the level will mean being removed
     * from the game. Therefore the entity should clean up any resources and
     * pointers.
     * @param l 
     */
    public void removeFromLevel(Level l);

    /**
     * Called when this objects collides with the obstacle.
     * 
     * @param obstacle
     */
    public void onCollision(Entity obstacle);
    
    /**
     * Called to repel any object which collides with it.
     * Takes the position and velocity, and calculates 
     * where the shape should be put.
     * 
     * @param x the x position of the caller
     * @param y the y position of the caller
     * @param velx the x velocity of the caller
     * @param vely the y velocity of the caller
     */
    public void repel(Entity repellee);
     
}
