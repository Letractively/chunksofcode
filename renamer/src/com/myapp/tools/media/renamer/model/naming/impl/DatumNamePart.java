package com.myapp.tools.media.renamer.model.naming.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the Datum
 * 
 * @author andre
 * 
 */
public class DatumNamePart extends AbstractNamePart {

    private SimpleDateFormat dateFormat = null;

    /**
     * returns the dateformat used by the config
     * 
     * @return the dateformat used by the config
     */
    private Format lazilyGetDateFormat() {
        synchronized (this) {
            /*bugfix for changing fmt in dialog*/
            final String patternFromConfig = cfg.getDatumFormat();
            
            if (dateFormat == null  // TODO: may be cached...
                     || ! dateFormat.toPattern().equals(patternFromConfig)) {
                    dateFormat = new SimpleDateFormat(patternFromConfig);
            }
        }
        return dateFormat;
    }

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        return new StringBuilder()
            .append(cfg.getDatumPrefix())
            .append(lazilyGetDateFormat().format(getRawValue(f)))
            .append(cfg.getDatumSuffix())
            .toString();
    }

    @Override
    public Class<?> getRawType() {
        return Date.class;
    }

    @Override
    public Object getRawValue(IRenamable file) {
        return renamer.getDatum(); // TODO: DATUM FLEXIBEL MACHEN
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_DATUM;
    }
}
