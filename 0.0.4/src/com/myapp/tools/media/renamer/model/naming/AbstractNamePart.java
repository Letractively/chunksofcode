package com.myapp.tools.media.renamer.model.naming;

import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.model.INamePart;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.naming.impl.ExtensionNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.PrefixNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.SuffixNamePart;

/**
 * base class for quickly implementing a inamepart. implementation of the
 * index/comparison algorithms and also providing default values for the
 * setRawValue, getRawType and isEditable methods. <br />
 * programmers have to call the init method to provide a renamer instance to
 * this name part, which is needed to get the user settings.
 * 
 * @author andre
 * 
 */
public abstract class AbstractNamePart implements INamePart, IConstants.ISysConstants {

    protected IRenamerConfiguration cfg;
    protected IRenamer renamer;
    protected final StringBuilder builder = new StringBuilder();

    /**
     * sole default constructor. programmers must call the init mehtod to
     * register the renamer to the namepart obj.
     */
    protected AbstractNamePart() {}

    /**
     * creates a namepart using the given renamer for calculating the file
     * representing string
     * 
     * @param renamer
     *            the renamer to get the settings from
     */
    protected AbstractNamePart(IRenamer renamer) {}

    /**
     * calculates the string which represents the given file for this name part
     * implementation.
     * 
     * @param f
     *            the file we want to have the string representation
     * @return the string representation for the file of this namepart
     */
    protected abstract String getFormattedStringImpl(IRenamable f);


    @Override
    public final void init(IRenamer irenamer) {      
        this.renamer = irenamer;
        cfg = irenamer.getConfig();
        assert irenamer != null;
        assert cfg != null;
    }


    @Override
    public final int getIndex() {
        Integer number = cfg.getIndex(getClass());
        if (number == null)
            throw new RuntimeException("i dont know key " + getClass());

        return number;
    }

    @Override
    public synchronized String getFormattedString(IRenamable f) {
        Class<? extends INamePart> c = getClass();
        builder.setLength(0);

        if (  // these elements cannot be suppressed:
                   c == PrefixNamePart.class 
                || c == SuffixNamePart.class 
                || c == ExtensionNamePart.class ) {
            return getFormattedStringImpl(f);
        } 

        return cfg.getIndex(c) >= 0 ? getFormattedStringImpl(f) : "";
    }

    @Override
    public final int compareTo(INamePart another) {
        Integer thisIndex = getIndex();
        Integer thatInxex = another.getIndex();
        return thisIndex.compareTo(thatInxex);
    }

    @Override
    public final String toString() {
        String s = getClass().getSimpleName();
        String suffix = "NameElement";
        if (s.endsWith(suffix)) 
            s = s.replace(suffix, "");

        return s;
    }

    @Override
    public Class<?> getRawType() {
        return String.class;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean setRawValue(Object value, IRenamable file) {
        assert false : "not implemented for " + getClass();
        return false;
    }
}
