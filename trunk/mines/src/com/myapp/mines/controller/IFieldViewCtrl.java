package com.myapp.mines.controller;

import com.myapp.mines.model.Field;

/**
defines methods which must be implemented by any gui
implementations to guarantee the ability to serve as a view/controller
in a common way.
@author andre
 */
public interface IFieldViewCtrl {

    /**
    returns the field object associated to this view.
    @return the field object associated to this view.
     */
    Field getModel();

    /**
    refreshes the view object to match its model. this will be invoked when
    something was changed.
     */
    void repaintFieldView();

    /**
    causes the field to solve its neighbors.
    this will invoke the field's solve-method and handle
    the eventually thrown Baaaaaaaam.
     */
    void solveNeighbors();

    /**
    the underlying field will be enterd, and
    the eventually thrown Baaaaaaaam will be handeled.
     */
    void enterField();

    /**
    returns the concrete gui-object for this view. (JPanel, etc...)
    @return the concrete gui-object for this view.
     */
    Object getGuiObject();

}
