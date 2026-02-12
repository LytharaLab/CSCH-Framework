package org.lytharalab.csch.core.common;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Context {
    private final Map<String, Object> data;
    private final Context parent;
    
    public Context() {
        this(null);
    }
    
    public Context(Context parent) {
        this.data = new HashMap<>();
        this.parent = parent;
    }
    
    public void put(String key, Object value) {
        data.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (data.containsKey(key)) {
            return (T) data.get(key);
        }
        if (parent != null) {
            return parent.get(key);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }
    
    public boolean contains(String key) {
        return data.containsKey(key) || (parent != null && parent.contains(key));
    }
    
    public void remove(String key) {
        data.remove(key);
    }
    
    public void clear() {
        data.clear();
    }
    
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        if (parent != null) {
            result.putAll(parent.getAll());
        }
        result.putAll(data);
        return Collections.unmodifiableMap(result);
    }
    
    public Context createChild() {
        return new Context(this);
    }
}
