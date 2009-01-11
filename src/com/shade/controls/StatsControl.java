package com.shade.controls;

import java.util.HashMap;
import java.util.Map;

public class StatsControl {
    
    private Map<String, Float> stats;
    
    public StatsControl() {
        stats = new HashMap<String, Float>();
    }

    public void add(String name, float amount) {
        float current = 0;
        if (stats.containsKey(name)) {
            current = stats.get(name);
        }
        current += amount;
        stats.put(name, current);
    }
    
    public void replace(String name, float amount) {
        stats.put(name, amount);
    }
    
    public float getStat(String name) {
        return stats.get(name);
    }

    public void reset() {
        stats.clear();
    }
}
