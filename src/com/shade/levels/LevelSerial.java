package com.shade.levels;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.xml.XMLElement;
import org.newdawn.slick.util.xml.XMLElementList;
import org.newdawn.slick.util.xml.XMLParser;

import com.shade.lighting.LuminousEntity;
import com.shade.util.Reflection;

public class LevelSerial {

    private HashMap<String, String> mappings;

    public LevelSerial() {
        mappings = new HashMap<String, String>();
        mappings.put("Block", "com.shade.entities.Block");
        mappings.put("Dome", "com.shade.entities.Dome");
        mappings.put("Basket", "com.shade.entities.Basket");
        mappings.put("Player", "com.shade.entities.Player");
        mappings.put("MockPlayer", "com.shade.entities.MockPlayer");
        mappings.put("Fence", "com.shade.entities.Fence");
        mappings.put("Door", "com.shade.entities.Door");
        mappings.put("Monster", "com.shade.entities.monster.Monster");
    }

    public LuminousEntity[] deserialize(String level) throws SlickException {
        XMLParser xml = new XMLParser();
        XMLElement root = xml.parse(level);
        XMLElementList children = root.getChildren();
        ArrayList<LuminousEntity> entities = new ArrayList<LuminousEntity>();

        for (int i = 0; i < children.size(); i++) {
            entities.add(getEntityFrom(children.get(i)));
        }

        return entities.toArray(new LuminousEntity[0]);
    }

    private LuminousEntity getEntityFrom(XMLElement element)
            throws SlickException {
        String c = mappings.get(element.getName());
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(element.getIntAttribute("x"));
        args.add(element.getIntAttribute("y"));
        if (!element.getAttribute("z", "NULL").equals("NULL")) {
            args.add(element.getIntAttribute("z"));
        }
        if (!element.getAttribute("d", "NULL").equals("NULL")) {
            args.add(element.getIntAttribute("d"));
        }
        if (!element.getAttribute("r", "NULL").equals("NULL")) {
            args.add(element.getIntAttribute("r"));
        }
        if (!element.getAttribute("range", "NULL").equals("NULL")) {
            args.add(element.getIntAttribute("range"));
        }
        if (!element.getAttribute("speed", "NULL").equals("NULL")) {
            args.add(element.getIntAttribute("speed"));
        }

        return (LuminousEntity) Reflection.getInstance(c, args.toArray());
    }
}
