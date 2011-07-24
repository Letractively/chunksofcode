package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the global suffix
 * 
 * @author andre
 * 
 */
public class SuffixNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return (String) getRawValue(f);
    }

    @Override
    public Object getRawValue(IRenamable file) {
        return cfg.getSuffix();
    }

    @Override
    public String getPropertyName() {
        assert false;
        return null;
    }
}
