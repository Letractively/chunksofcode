package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the titel
 * 
 * @author andre
 * 
 */
public class TitelNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return builder.append(cfg.getTitelPrefix())
                      .append(getRawValue(f))
                      .append(cfg.getTitelSuffix())
                      .toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {
        String titel = file.getTitel();
        return titel == null ? cfg.getDefaultTitel() : titel;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean setRawValue(Object value, IRenamable file) {
        assert value != null;
        if (value instanceof String) {
            file.setTitel((String) value);
            return true;
        }
        return false;
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_TITEL;
    }
}
