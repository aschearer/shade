package com.shade.util;

import java.lang.reflect.Constructor;

import org.newdawn.slick.SlickException;

@SuppressWarnings("unchecked")
public class Reflection {

    public static Object getInstance(String name, Object... args)
    throws SlickException {
        try {
            Class c = Class.forName(name);
            Class[] params = extractTypes(args);
            Constructor constructor = c.getConstructor(params);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new SlickException("Couldn't create new object.", e);
        }
    }

    private static Class[] extractTypes(Object[] args) {
        Class[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = getType(args[i].getClass());
        }
        return types;
    }

    private static Class getType(Class c) {
        if (c.equals(Integer.class)) {
            return int.class;
        }
        if (c.equals(Float.class)) {
            return float.class;
        }
        return c;
    }
}
