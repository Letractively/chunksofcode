package com.myapp.tools.media.renamer.model;


/**
 * a name part is a property of the file that has a string representation
 * 
 * these objects are responsible for calculating the parts of the new name of
 * the irenamablefiles. their order is defined by getIndex and the comparable
 * interface implementation.
 * 
 * the get/setRawValue methods allow to pass new values to an irenamablefile.
 * 
 * @author andre
 */
public interface INamePart extends IProperty, Comparable<INamePart> {

    /**
     * returns the calculated string for this namePart of the given file.
     * 
     * @param f
     *            the file we want to have the partial string value
     * @return the formatted string value of the given property of the file
     */
    String getFormattedString(IRenamable f);

    /**
     * returns the index of this name part, its order in the list of parts
     * 
     * @return the index of this name part
     */
    int getIndex();

    /**
     * the settings how to represent a property of a file are taken from this
     * renamer instance.
     * @param renamer the renamer to init the namepart with
     */
    void init(IRenamer renamer);
}
