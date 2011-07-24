package com.myapp.tools.media.renamer.view;

/**
 * the ui component that represents a list view of the files in the renamer
 * 
 * @author andre
 */
public interface IListView extends IUIComponent {

    /**
     * returns the currently selected elements of the list view
     * 
     * @return the currently selected elements of the list view
     */
    int[] getSelection();

    /**
     * clears the selection. no item will be selected after this call.
     */
    void clearSelection();

    /**
     * sets the selection to the specified range
     * 
     * @param from
     *            the first element in the list that will be in the selection.
     * @param to
     *            the last element in the list that will be in the selection
     */
    void setSelection(int from, int to);
}