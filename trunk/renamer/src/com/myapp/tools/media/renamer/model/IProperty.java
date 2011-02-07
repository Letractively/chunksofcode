package com.myapp.tools.media.renamer.model;


/**
 * an iproperty is a property for a irenamablefile. it represents a value for a
 * irenamablefile that has a type, a name, and a value, some iproperties may be
 * editable.
 * 
 * @author andre
 * 
 */
public interface IProperty {

    /**
     * returns the type of the raw value for this namepart. the value the getter
     * returns and the value argument for the getter should be instances of this
     * class.
     * 
     * @return returns the type of the raw value for this namepart
     */
    Class<?> getRawType();

    /**
     * sets the new value for this namepart. this must not be called if the
     * namepart is not editable. if editable, the file will be updated with the
     * new value.
     * 
     * @param value
     *            the new value for this file
     * @param file
     *            the file we want to set the value
     * @return if the value could be set
     */
    boolean setRawValue(Object value, IRenamable file);

    /**
     * returns the raw value for the given file. this object should be instance
     * of the class as defined in getRawType.
     * 
     * @param file
     *            the file we want to know the value for this namepart
     * @return the value for this file for this namepart
     */
    Object getRawValue(IRenamable file);

    /**
     * if the value of this namepart may be set
     * 
     * @return true if the namepart is editable
     */
    boolean isEditable();

    /**
     * the name of this property
     * 
     * @return the name of the property this namepart represents
     */
    String getPropertyName();
}
