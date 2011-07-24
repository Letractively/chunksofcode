package com.myapp.tools.media.renamer.controller;

import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.view.IDialogs;
import com.myapp.tools.media.renamer.view.IElementChooserView;
import com.myapp.tools.media.renamer.view.IListView;
import com.myapp.tools.media.renamer.view.ISettingsView;
import com.myapp.tools.media.renamer.view.IUIComponent;

/**
 * provides a context where the different actors can be referenced with.
 * programmers will be able to access the model, the view(s) and the controller
 * by using that context.
 * 
 * @author andre
 * 
 */
public interface IApplication extends IUIComponent {

    /**
     * returns the Controller
     * 
     * @return the Controller
     */
    public abstract IController getController();

    /**
     * returns the Table
     * 
     * @return the Table
     */
    public abstract IListView getListView();

    /**
     * returns the IDialogs
     * 
     * @return the IDialogs
     */
    public abstract IDialogs getDialogs();

    /**
     * returns the ControlPanel
     * 
     * @return the ControlPanel
     */
    public abstract ISettingsView getSettingsView();

    /**
     * returns the IRenamer
     * 
     * @return the IRenamer
     */
    public abstract IRenamer getRenamer();

    /**
     * returns the FileChooser
     * 
     * @return the FileChooser
     */
    public IElementChooserView getElementChooser();
}