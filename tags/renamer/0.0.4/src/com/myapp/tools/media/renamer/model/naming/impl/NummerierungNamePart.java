package com.myapp.tools.media.renamer.model.naming.impl;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.naming.AbstractNamePart;

/**
 * the part of the new file name representing the Nummerierung
 * 
 * @author andre
 * 
 */
public class NummerierungNamePart extends AbstractNamePart {

    @Override
    protected String getFormattedStringImpl (IRenamable f) {
        String alter = f.getAlterNummerierung();
        if (alter != null) return alter;
        
        return new StringBuilder()
                      .append(cfg.getNummerierungPrefix())
                      .append(getRawValue(f))
                      .append(cfg.getNummerierungSuffix())
                      .toString();
    }

    @Override
    public Object getRawValue(IRenamable file) {
        String alter = file.getAlterNummerierung();
        if (alter != null) return alter;
        
        return new Integer(
                ( renamer.indexOf(file) * cfg.getNummerierungIncrement() ) 
                + cfg.getNummerierungStart());
    }
    
    @Override
    public String getFormattedString(IRenamable f) {
        return cfg.getIndex(getClass()) >= 0  
                        ? getFormattedStringImpl(f)  
                        : "";
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * <b>if a parseable integer is the value, the prefix and suffix for
     * nummerierung will be applied.</b>
     * 
     * @see AbstractNamePart#setRawValue(Object, IRenamable)
     */
    @Override
    public boolean setRawValue(Object value, IRenamable file) {
        assert value != null;
                
        if (value instanceof String) {
            String str = (String) value;
            try {
                Integer.parseInt(str);
                file.setAlterNummerierung(cfg.getNummerierungPrefix() + 
                                          str + 
                                          cfg.getNummerierungSuffix());
                return true;
            } catch (NumberFormatException nfe) {}
            
            file.setAlterNummerierung((String) value);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public String getPropertyName() {
        return COLUMN_NAME_NUMMER;
    }
}
