package com.myapp.util.soundsorter.wizard.tool;

/**
 * will be notified when the next directory was chosen in the application
 * 
 * @author andre
 * 
 */
public 
interface INextDirChosenListener 
{
    /**
     * notifies the implementor that the next dir was chosen by the
     * application
     * 
     * @param context
     *            the application instance which fired the event
     */
    void nextDirChosen(Application context);
}