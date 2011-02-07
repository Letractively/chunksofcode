package com.myapp.mines.model;

import java.util.HashMap;
import java.util.Map;

/**
a class where field objects are being mapped to objects.
useful when you want to get a fieldview object from a specific model.
the field object should not refer to a gui object, therefore this is
needed. class Game uses an instance of this to delegate the methods.
@author andre
 */
class FieldMapping<T> {

    private Map<Field, T> fieldsToObjects = new HashMap<Field, T>();

    /**
    you may need to connect fields to specific objects, like gui objects.
    @param f the field to map
    @param o the corresponding object key
    @throws IllegalStateException if a field OR oject is registered twice.
     */
    synchronized void map(Field f, T o) {
        if (fieldsToObjects.containsKey(f))
            throw new IllegalArgumentException("key '" + f +
                                               "' is already mapped to value " +
                                               fieldsToObjects.get(f));
        fieldsToObjects.put(f, o);
    }

    /**
    returns the object mapped to this field.
    @param fieldKey the corresponding field
    @return returns the object mapped to this field, null if it does not exist.
     */
    T getMapping(Field fieldKey) {
        return fieldsToObjects.get(fieldKey);
    }

}
