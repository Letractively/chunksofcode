package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.view.IUIComponent;

/**
 * gui elements to configure the insertion options like inclusive hidden files,
 * recursive or excludeduplicates
 * 
 * @author andre
 * 
 */
@SuppressWarnings("serial")
class InsertOptionsPanel extends JPanel implements IUIComponent,
                                                   IConstants.ISysConstants {

    /** used when firing action events by the show_all_checkbox */
    static final String SHOW_ALL_CMD = "SHOW_ALL_CMD";

    private JCheckBox showAllChbx;
    private JCheckBox recursiveChBx;
    private JCheckBox exclDuplChBx;

    /**
     * sole default constructor.
     * 
     * @param app
     *            the app to init to
     */
    InsertOptionsPanel(IApplication app) {
        super(new GridLayout(0, 1));

        showAllChbx = new JCheckBox(msg("FileChooser.inclusiveHiddenFiles"));
        recursiveChBx = new JCheckBox(msg("FileChooser.inclusiveSubDirs"));
        exclDuplChBx = new JCheckBox(msg("FileChooser.excludeDuplicates"));

        refresh(app);
        showAllChbx.setActionCommand(SHOW_ALL_CMD);

        add(showAllChbx);
        add(recursiveChBx);
        add(exclDuplChBx);
    }

    /**
     * adapts the control to an applications state.
     * 
     * @param app
     *            the app to adapt to
     */
    void refresh(IApplication app) {
        IRenamerConfiguration cfg = app.getRenamer().getConfig();

        showAllChbx.setSelected(cfg.getBoolean(SHOW_HIDDEN_FILES));
        recursiveChBx.setSelected(cfg.getBoolean(RECURSE_INTO_DIRECTORIES));
        exclDuplChBx.setSelected(cfg.getBoolean(EXCLUDE_DUPLICATE_FILES));
    }

    /**
     * adds an actionlistener to the show-all-checkbox. actions from this
     * checkbox will have the command SHOW_ALL_CMD
     * 
     * @param l
     *            the listener
     * @see javax.swing.AbstractButton#addActionListener(ActionListener)
     */
    void addActionListener(ActionListener l) {
        showAllChbx.addActionListener(l);
    }

    /**
     * returns if the recursive checkbox is selected
     * 
     * @return if the recursive checkbox is selected
     * @see javax.swing.AbstractButton#isSelected()
     */
    boolean isRecursiveSelected() {
        return recursiveChBx.isSelected();
    }

    /**
     * returns if the showAllChbx checkbox is selected
     * 
     * @return if the showAllChbx is selected
     * @see javax.swing.AbstractButton#isSelected()
     */
    boolean isShowHiddenSelected() {
        return showAllChbx.isSelected();
    }

    /**
     * returns if the exclDuplChBx checkbox is selected
     * 
     * @return if the exclDuplChBx is selected
     * @see javax.swing.AbstractButton#isSelected()
     */
    boolean isExcludeDuplicateSelected() {
        return exclDuplChBx.isSelected();
    }

    @Override
    public Object getUIComponent() {
        return this;
    }

    @Override
    public void persistSettings(IRenamerConfiguration cfg) {
        cfg.setCustomProperty(SHOW_HIDDEN_FILES, 
                              Boolean.toString(showAllChbx.isSelected()));
        cfg.setCustomProperty(EXCLUDE_DUPLICATE_FILES, 
                              Boolean.toString(exclDuplChBx.isSelected()));
        cfg.setCustomProperty(RECURSE_INTO_DIRECTORIES, 
                              Boolean.toString(recursiveChBx.isSelected()));
    }
}
