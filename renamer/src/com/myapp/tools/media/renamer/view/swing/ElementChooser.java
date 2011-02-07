package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static javax.swing.JFileChooser.SELECTED_FILES_CHANGED_PROPERTY;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Util;
import com.myapp.tools.media.renamer.view.IElementChooserView;

/**
 * encapsulates the file-choosing dialog
 * 
 * @author andre
 */
@SuppressWarnings("serial")
class ElementChooser extends JPanel implements ISysConstants,
                                                  ActionListener,
                                                  PropertyChangeListener,
                                                  IElementChooserView {

    /**
     * runnable that will wait for the user to proceed the file choosing dialog
     * 
     * @author andre
     * 
     */
private final class UserResponseCallback implements Runnable {

    boolean loop = false;
    boolean userClickedOk = false;

    @Override
    public void run() {
        loop = true;
        L.info(msg("FileChooser.Waiter.start")); 

        try {
            while (loop) { //sleep until user clicks ok button
                try {
                    Thread.sleep(50);
                    
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (userClickedOk) okWasClicked();

        } finally {
            dialogEnd();
        }
    }

    /**
     * this call will end the file choosing dialog
     * 
     * @param okClick
     */
    void end(boolean okClick) {
        L.info(msg("FileChooser.Waiter.end").replace("#ok#", "" + okClick)); 
        this.userClickedOk = okClick;
        loop = false;
    }
}



    //**************** FIELDS ***********************************

    private static final Logger L = Log.defaultLogger();
    private static File[] EMPTY = new File[] {};

    private final IApplication app;
    private UserResponseCallback waiter;

    // components we need a reference to:
    private JFileChooser chooser;
    private InsertOptionsPanel options;
    private IndexControlPanel indexControl;
    
    private JFrame frame;
    private File[] currentSelection = EMPTY;



    //**************** INITIALISATION ***********************************



    /**
     * creates a new FileChooser for a given app instance.
     * 
     * @param appFrame
     *            the appframe this filechooser belongs to
     */
ElementChooser(IApplication appFrame) {
    super(new BorderLayout());
    this.app = appFrame;

    initComponents();
    initSettings();
    initRest();
}


    /**
     * creates needed components for this file chooser
     */
private void initComponents() {     
//the components of the dialog:
    chooser = new JFileChooser();
    chooser.addPropertyChangeListener(this);

    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    controlPanel.add(indexControl = new IndexControlPanel(app));
    controlPanel.add(options = new InsertOptionsPanel(app));
    controlPanel.add(buildButtonsPanel());
    controlPanel.setBorder(BorderFactory.createTitledBorder(
                               msg("FileChooser.controlPanelDescription")));

    super.add(chooser, BorderLayout.CENTER);
    super.add(controlPanel, BorderLayout.SOUTH);

//the frame containing the dialog:
    frame = new JFrame(msg("FileChooser.dialogTitle"));
    frame.setContentPane(this);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(((SwingApplication)app).getWindowListener());
}


    /**
     * sets up the components with their settings.
     */
private void initSettings() {
//hardcoded settings:
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
    chooser.setMultiSelectionEnabled(true);
    chooser.setControlButtonsAreShown(false);

    
//configured settings:
    IRenamerConfiguration cfg = app.getRenamer().getConfig();

    chooser.setFileHidingEnabled( ! cfg.getBoolean(SHOW_HIDDEN_FILES));

    frame.setPreferredSize(new Dimension(
                                   cfg.getInt(FILECHOOSER_DEFAULT_WIDTH),
                                   cfg.getInt(FILECHOOSER_DEFAULT_HEIGHT)));

    final String lastAccessed = cfg.getLastAccessedPath();
    
    if (lastAccessed != null && lastAccessed.trim().length() > 0) {
        File lastDir = new File(lastAccessed);
        if (lastDir.exists())
            chooser.setCurrentDirectory(lastDir);
    }

    String filters = cfg.getString(ISysConstants.FILE_FILTERS);
    setFileFilter(Util.createFileFilterFromCommaSepList(filters));
}


    /**
     * finish the construction of the filechooser and starts the finish-waiting
     * thread
     */
private void initRest() {
    frame.pack();
    frame.setLocation(Utils.getCenteredPosition(frame));
    waiter = new UserResponseCallback();
}





    //**************** BUSINESS METHODS ***********************************



@Override
public void dialogShow() {
    IRenamerConfiguration cfg = app.getRenamer().getConfig();
    ((Component) app.getUIComponent()).setEnabled(false);     
    currentSelection = EMPTY;

    // this will wait for user response:
    new Thread(waiter).start();
    
    // set frame to its location
    int x = cfg.getInt(FILECHOOSER_POSITION_X);
    int y = cfg.getInt(FILECHOOSER_POSITION_Y);
    
    frame.setLocation(  (x < 0 || y < 0) 
                            ? Utils.getCenteredPosition(frame)
                            : new Point(x, y));

    SwingUtilities.updateComponentTreeUI(frame);

        // hack to set details view as default, look for a abstractButt
        // in the jfc's subcomponents with the same icon as detailsbutt
        // of the JFilechooser. when found, click it programmatically.
    clickOnDetailViewButton(chooser);
    
    options.refresh(app);
    indexControl.refresh();
    
    frame.setEnabled(true);
    frame.setVisible(true);
}


    /**
     * being called after the user clicked ok.
     */
private void okWasClicked() {
    frame.getContentPane().setEnabled(false);

    List<File> lst = Util.getFileList(currentSelection,
                                      options.isRecursiveSelected(),
                                      options.isShowHiddenSelected());

    app.getController().filesWereSelected(
                                indexControl.getSelectedIndex(),
                                options.isExcludeDuplicateSelected(),
                                lst);

    options.persistSettings(app.getRenamer().getConfig());
}


    /**
     * hides the dialog, activates the appframe and saves the settings of the
     * dialog.
     */
private void dialogEnd() {
    frame.setVisible(false);
    frame.setEnabled(false);
    ((Component) app.getUIComponent()).setEnabled(true);

    persistSettings(app.getRenamer().getConfig());
    currentSelection = EMPTY;
}


//**************** IMPLEMENTATION OF VIEW ********************************


@Override
public void actionPerformed(ActionEvent e) { 
    if (InsertOptionsPanel.SHOW_ALL_CMD.equals(e.getActionCommand())) {
        chooser.setFileHidingEnabled( ! options.isShowHiddenSelected());

    } else if (e.getActionCommand().equals("ok")) {
        dialogEnd(true);

    } else if (e.getActionCommand().equals("cancel")) {
        dialogEnd(false);
    }
}


@Override
public void propertyChange(PropertyChangeEvent evt) {
    final String propertyName = evt.getPropertyName();

    if (propertyName.equals(SELECTED_FILES_CHANGED_PROPERTY)) {
        Object value = evt.getNewValue();

        if (value == null) return;
        
        assert value.getClass() == File[].class : value.getClass();
        currentSelection = (File[]) value;
    }
}

@Override
public void dialogEnd(boolean userClickedOk) {
    waiter.end(userClickedOk);
}

@Override
public void setFileFilter(FileFilter filter) {
    chooser.setFileFilter(filter);
}

@Override
public FileFilter getFileFilter() {
    return chooser.getFileFilter();
}

@Override
public JFrame getUIComponent() {
    return frame;
}

@Override
public void persistSettings(IRenamerConfiguration cfg) {
    cfg.setCustomProperty(FILECHOOSER_DEFAULT_HEIGHT, frame.getHeight() + "");
    cfg.setCustomProperty(FILECHOOSER_DEFAULT_WIDTH, frame.getWidth() + "");
    
    cfg.setCustomProperty(FILECHOOSER_POSITION_X, frame.getLocation().x + "");
    cfg.setCustomProperty(FILECHOOSER_POSITION_Y, frame.getLocation().y + "");
    
    cfg.setCustomProperty(LAST_ACCESSED_FILE_PATH, 
                           chooser.getCurrentDirectory().getAbsolutePath());
}


    /**
     * returns the filechooser swing object used by this elementchooser
     * 
     * @return the filechooser swing object used by this elementchooser
     */
JFileChooser getJFileChooser() {
    return chooser;
}


    //***************** COMPONENTS FACTORY ************************



    /**
     * returns the panel containing the ok and cancel buttons for the file
     * chooser component
     * 
     * @return the panel containing the ok and cancel buttons
     */
private JPanel buildButtonsPanel() {
    JPanel buttons = new JPanel(new GridLayout(0, 1, 2, 2));

    JButton filter = new JButton("Filter...");
    filter.setActionCommand(IActionCommands.EDIT_FILTER);
    filter.addActionListener(((SwingApplication)app).getActionListener());
    
    JButton ok = new JButton("Fertig");
    ok.setActionCommand("ok");
    ok.addActionListener(this);

    JButton cancel = new JButton("Abbrechen");
    cancel.setActionCommand("cancel");
    cancel.addActionListener(this);
    

    buttons.add(ok);
    buttons.add(cancel);
    buttons.add(filter);

    return buttons;
}


//***************** HELPER METHODS ************************

    /**
     * sets the jfilechooser to "details view" (quick and dirty)
     * 
     * @param chooser
     *            the filechooser to be set to detailsview
     */
private static void clickOnDetailViewButton(JFileChooser chooser) {
    Component detailViewBtn = null;
    try {
    	Icon detailsBtnIcon = UIManager.getIcon("FileChooser.detailsViewIcon");

        // System.out.println("detailsBtnIcon: _________________" +
        // detailsBtnIcon);
        // System.out.println("chooser: ________________________" +
        // chooser);

        detailViewBtn = Utils.findSubComponent(AbstractButton.class,
                                               chooser,
                                               "getIcon",
                                               detailsBtnIcon);

        // System.out.println("found: __________________________" +
        // detailViewBtn);

        ((AbstractButton)detailViewBtn).doClick();
    } catch (Exception e) {
        e.printStackTrace();
        assert false : "caught " + e;
    }
}



}