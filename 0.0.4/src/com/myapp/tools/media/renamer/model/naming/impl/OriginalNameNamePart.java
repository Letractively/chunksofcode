package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the original file name
 * 
 * @author andre
 * 
 */
public class OriginalNameNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        builder.append(cfg.getOrigNamePrefix());
        builder.append(getRawValue(f));
        builder.append(cfg.getOrigNameSuffix());

        return builder.toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {
        String oldName = file.getOldName();

        if ( ! cfg.isOrigNameMitSuffix() && oldName.contains("."))
            oldName = oldName.substring(0, oldName.lastIndexOf(".")); //TODO

        return oldName;
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_QUELLDATEI;
    }
}
