package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the global prefix
 * 
 * @author andre
 * 
 */
public class PrefixNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return getRawValue(f).toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {
        return cfg.getPrefix();
    }

    @Override
    public String getPropertyName() {
        assert false;
        return null;
    }
}
