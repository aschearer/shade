package com.shade.entities.mole;

import com.shade.base.Entity;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.Roles;
import com.shade.lighting.LuminousEntity;

/**
 * Some utility routines which are used across mole states.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class Util {

    /** Return first mushroom you can see which isn't already attached. */
    public static boolean foundTarget(Mole mole) {
        LuminousEntity[] entities = mole.level.nearbyEntities(mole, 200);

        int i = findValidTarget(entities, mole);

        if (i > 0) {
            mole.target = (Mushroom) entities[i];
            return true;
        }
        return false;
    }

    private static int findValidTarget(LuminousEntity[] entities, Mole mole) {
        boolean lineOfSight = false;
        int i = 0;
        while (!lineOfSight && i < entities.length) {
            if (((Entity) entities[i]).getRole() == Roles.MUSHROOM.ordinal()) {
                lineOfSight = mole.level.lineOfSight(mole, entities[i], mole);
            }
            i++;
        }
        i--;
        return (lineOfSight) ? i : -1;
    }
}
