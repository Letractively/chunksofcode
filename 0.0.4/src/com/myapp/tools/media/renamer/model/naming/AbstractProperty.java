package com.myapp.tools.media.renamer.model.naming;

import com.myapp.tools.media.renamer.model.IProperty;
import com.myapp.tools.media.renamer.model.IRenamable;

/**
 * default implementation for defining classes of type IProperty.
 * 
 * @author andre
 * 
 */
public abstract class AbstractProperty implements IProperty {
    private static int counter = 0;
    private String propertyName;

    protected AbstractProperty() {
        propertyName = "Column-" + counter++;
        assert false;
    }

    protected AbstractProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getRawType() {
        return String.class;
    }

    public boolean isEditable() {
        return false;
    }

    public boolean setRawValue(Object value, IRenamable file) {
        assert false : "not (yet) implemented !";
        return false;
    }

}
