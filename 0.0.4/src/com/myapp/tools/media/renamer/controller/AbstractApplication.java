package com.myapp.tools.media.renamer.controller;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenameProcessListener;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.view.*;

/**
 * implements the basic functionality of the Iapplication interface
 * classes that are extending abstractApplication must instantiate 
 * the components and pass them to this class that will provide getters 
 * for them.
 * 
 * @author andre
 *
 */
public abstract class AbstractApplication implements IApplication,
                                                     IRenameProcessListener {
    
    protected IRenamer renamer;
    private IDialogs dialogs;
    private IController controller;
    private IElementChooserView elementChooser;
    private IListView listView;
    private ISettingsView settingsView;
    private Object uiComponent;
    
    /**
     * sole constructor
     */
    protected AbstractApplication() {}
    
    
    
    /**
     * creates a new application with the given instances to use.
     * 
     * @param renamer
     *            the renamer instance to use
     * @param dialogs
     *            the dialogs instance to use
     * @param controller
     *            the controller instance to use
     * @param elementChooser
     *            the elementChooser instance to use
     * @param listView
     *            the listView instance to use
     * @param settingsView
     *            the settingsView instance to use
     * @param uiComponent
     *            the uiComponent instance to use
     */
    protected AbstractApplication(IRenamer renamer,
                                  IDialogs dialogs,
                                  IController controller,
                                  IElementChooserView elementChooser,
                                  IListView listView,
                                  ISettingsView settingsView,
                                  Object uiComponent) {
        this.renamer = renamer;
        this.dialogs = dialogs;
        this.controller = controller;
        this.elementChooser = elementChooser;
        this.listView = listView;
        this.settingsView = settingsView;
        this.uiComponent = uiComponent;
    }

    /**
     * passes the must-have components of an application to this base class.
     * they can be accessed through the getters.
     * 
     * @param pRenamer
     *            the renamer instance to use
     * @param pDialogs
     *            the dialogs instance to use
     * @param pController
     *            the controller instance to use
     * @param pChooser
     *            the elementChooser instance to use
     * @param pListView
     *            the listView instance to use
     * @param pSettingsView
     *            the settingsView instance to use
     * @param pUiComponent
     *            the uiComponent instance to use
     */
    protected final void initAbstractApplication(IRenamer pRenamer,
                                                 IDialogs pDialogs,
                                                 IController pController,
                                                 IElementChooserView pChooser,
                                                 IListView pListView,
                                                 ISettingsView pSettingsView,
                                                 Object pUiComponent) {
        this.renamer = pRenamer;
        this.dialogs = pDialogs;
        this.controller = pController;
        this.elementChooser = pChooser;
        this.listView = pListView;
        this.settingsView = pSettingsView;
        this.uiComponent = pUiComponent;
    }

    @Override
    public IController getController() {
        return controller;
    }

    @Override
    public IDialogs getDialogs() {
        return dialogs;
    }

    @Override
    public IElementChooserView getElementChooser() {
        return elementChooser;
    }

    @Override
    public IListView getListView() {
        return listView;
    }

    @Override
    public IRenamer getRenamer() {
        return renamer;
    }

    @Override
    public ISettingsView getSettingsView() {
        return settingsView;
    }

    @Override
    public Object getUIComponent() {
        return uiComponent;
    }

    @Override
    public final void persistSettings(IRenamerConfiguration cfg) {
        persistMySettings();
        
        for (IUIComponent component : new IUIComponent[] {getElementChooser(),
                                                          getListView(),
                                                          getSettingsView()}) {
            if (component != null) component.persistSettings(cfg);
        }
    }

    /**
     * called by the persistSettings method. since {@link IApplication} is 
     * extending {@link IUIComponent}, subclasses have to
     * persist its settings by implementing this method.
     */
    protected abstract void persistMySettings();

    @Override
    public void processFailed(Throwable t, IRenamable f) {
        getDialogs().showErrorMessage(
                msg("RenameProcessReport.error").replace("#file#",  
                          f.getOldName() + "' -> '" + f.getNewAbsolutePath()), 
                t);
    }

    @Override
    public void processFinished() {
        getDialogs().showRenameProcessFinished();
    }

    @Override
    public void processStarting(IRenamer irenamer) {}
    
    @Override
    public void processFileStart(IRenamable file) {}

    @Override
    public void processFileSuccess() {}
}
