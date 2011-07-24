package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;


/**
 * the part of the new file name representing the thema
 * 
 * @author andre
 * 
 */
public class ThemaNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return builder.append(cfg.getThemaPrefix())
                      .append(getRawValue(f))
                      .append(cfg.getThemaSuffix())
                      .toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {
        String thema = file.getThema();
        return (thema == null) ? cfg.getDefaultThema() : thema;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean setRawValue(Object value, IRenamable file) {
        assert value != null;
        if (value instanceof String) {
            file.setThema((String) value);
            return true;
        }
        return false;
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_THEMA;
    }
}
