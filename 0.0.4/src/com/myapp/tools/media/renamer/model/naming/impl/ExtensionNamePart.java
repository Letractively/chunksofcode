package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the extension of the original file
 * 
 * @author andre
 * 
 */
public class ExtensionNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return getRawValue(f).toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {        
        String on = file.getOldName();
        int pos = on.lastIndexOf(".");

        if (pos == -1) return "";

        return on.substring(pos);
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_TYP;
    }
}
