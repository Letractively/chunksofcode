package com.myapp.tools.media.renamer.config;

import java.util.List;

import com.myapp.tools.media.renamer.model.INamePart;
import com.myapp.tools.media.renamer.model.IRenamer;

/**
 * extends the iconfigurable interface for specific mehtods to manage its name
 * parts
 * 
 * @author andre
 * 
 */
public interface IRenamerConfigurable extends IConfigurable {

    /**
     * creates a list of the filename elements in their order to build the file
     * name for the specified renamer.
     * 
     * @param renamer
     *            the renamer for which the list should be generated
     * @return the list of the elements in their order to build the file name
     */
    List<INamePart> getNameElementsList(IRenamer renamer);

    /**
     * sets a name element class to the specified index. this will allow to
     * define their order.
     * 
     * @param c
     *            the class to be moved
     * @param i
     *            the new index of this class
     */
    void setCustomNameElement(Class<? extends INamePart> c, int i);

    /**
     * returns the index of a specific nameelement
     * 
     * @param c
     *            the class of the name element
     * @return the index of those name element
     */
    Integer getIndex(Class<? extends INamePart> c);
}
