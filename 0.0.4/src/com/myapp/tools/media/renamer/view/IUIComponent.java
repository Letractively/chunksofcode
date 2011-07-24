package com.myapp.tools.media.renamer.view;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;

/**
 * the basic interface for ui components that are part of the application
 * 
 * @author andre
 */
public interface IUIComponent {

    /**
     * @return the gui component that represents the settings graphically
     */
    Object getUIComponent();

    /**
     * tells the UIComponent to persist its setting in the given configuration
     * 
     * @param cfg
     *            the configuratioin where the settings will be stored in
     */
    void persistSettings(IRenamerConfiguration cfg);
}