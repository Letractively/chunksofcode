package com.myapp.util.soundsorter.wizard.gui;

interface IInitProgressListener 
{
    /**will be invoked after loading a destination dir object*/
    void notifyDirInitialized(String absolutePath);
}