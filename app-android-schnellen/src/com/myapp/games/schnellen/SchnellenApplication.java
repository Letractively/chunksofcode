package com.myapp.games.schnellen;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class SchnellenApplication extends Application {
    
    static final String GAME_CONTEXT;
    static final String GAME_FRONTEND;

    static {
        String prefix = SchnellenApplication.class.getName();
        GAME_CONTEXT  = prefix + ".GAME_CONTEXT";
        GAME_FRONTEND = prefix + ".GAME_FRONTEND";
    }
    
    public Map<String, Object> attributes = new HashMap<String, Object>();

    public void clear() {
        attributes.clear();
    }

    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    public Object setAttribute(String key, Object value) {
        return attributes.put(key, value);
    }

    public Object removeAttribute(Object key) {
        return attributes.remove(key);
    }
}
