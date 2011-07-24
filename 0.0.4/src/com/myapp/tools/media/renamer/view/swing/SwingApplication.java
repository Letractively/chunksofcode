package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;

import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.AbstractApplication;
import com.myapp.tools.media.renamer.controller.LockManager;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.TimeSpan;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.Renamer;
import com.myapp.tools.media.renamer.view.IDialogs;

/**
 * The SwingApplication provides an UI for the renamer using swing components.
 * 
 * @author andre
 * 
 */
class SwingApplication extends AbstractApplication implements ISysConstants {

    private static final String APP_KEY = "jRenamerApplication";
    private static final Logger L = Log.defaultLogger();

    private ActionListener actionListener;
    private TableModel tableModel;
    private ListSelectionListener listSelectionListener;
    private WindowListener windowListener;
    private PropertyChangeListener propertyChangeListener;
    private DropTargetListener dropTargetListener;
    private MenuBar menuBar;

    
    /**
     * creates a new Swing app.
     * a jframe will show up containing the ui.
     */
    SwingApplication() {
        super();
        initLock();
        
        Controller ctrl = new Controller(this);
        actionListener = ctrl;
        listSelectionListener = ctrl;
        windowListener = ctrl;
        propertyChangeListener = ctrl;
        dropTargetListener = ctrl;
        
        renamer = Renamer.getInstance();
        RenamerDelegate delegate = new RenamerDelegate(this);
        renamer = delegate;
        tableModel = delegate;
        
        renamer.addRenameProcessListener(new RenameProcessDisplay());
        renamer.addRenameProcessListener(new Log.LogProcessListener());
        renamer.addRenameProcessListener(this);
        
        IDialogs dialogs = new Dialogs(this);
        

        SettingsView settings = new SettingsView(this);
        ListView listView = new ListView(this);
        ElementChooser chooser = new ElementChooser(this);
        
        JFrame uiComp = new JFrame("JRenamer");

        initAbstractApplication(renamer,
                                dialogs,
                                ctrl,
                                chooser,
                                listView,
                                settings,
                                uiComp);
        setupUI();
    }

    /**
     * looking for another running instance.
     * if running, ask the user if he wants to ignore a running instance.
     */
    private void initLock() {
        if (LockManager.isLocked(APP_KEY)) {
            Date startedAt = LockManager.getLockTime(APP_KEY);
            Date now = new Date();
            TimeSpan ts = new TimeSpan(now, startedAt);

            if (Dialogs.showAnotherInstanceRunning(ts)) {
                L.info(msg("Log.AppFrame.overrideExistingLock"));
                
            } else {
                System.exit(0);
            }
        }
        
        LockManager.lock(APP_KEY);
    }

    /**
     * sets up the ui component
     */
    private void setupUI() {
        JFrame frame = (JFrame) getUIComponent();
        
        Container content = frame.getContentPane();
        content.add((Component) getSettingsView().getUIComponent(), SOUTH);
        content.add((Component) getListView().getUIComponent(),  CENTER);
        
        try {
            String look = renamer.getConfig().getString(LOOK_AND_FEEL);
            
            if (look == null || look.equals(LOOK_N_FEEL_CROSS_PLATFORM))
                UIManager.setLookAndFeel(
                              UIManager.getCrossPlatformLookAndFeelClassName());
            
            else if (look.equals(LOOK_N_FEEL_SYSTEM))
                    UIManager.setLookAndFeel(
                                     UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.updateComponentTreeUI(frame);
            
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        
        new DropTarget(frame, getDropTargetListener());
        
        frame.setJMenuBar(menuBar = new MenuBar(this));
        frame.pack();
        frame.setSize(Utils.getDefaultWindowSize(this));
        frame.setLocation(Utils.getDefaultWindowPosition(this));
        frame.addWindowListener(windowListener);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    protected void persistMySettings() {
        IRenamerConfiguration cfg = getRenamer().getConfig();
        JFrame frame = (JFrame) getUIComponent();
        cfg.setCustomProperty(WINDOW_DEFAULT_HEIGHT, frame.getHeight() + "");
        cfg.setCustomProperty(WINDOW_DEFAULT_WIDTH, frame.getWidth() + "");
        cfg.setCustomProperty(WINDOW_POSITION_X, frame.getLocation().x + "");
        cfg.setCustomProperty(WINDOW_POSITION_Y, frame.getLocation().y + "");
        
        Object o = UIManager.getLookAndFeel();
        if (o == null) return;
        
        String className = o.getClass().getName();
        
        if (className.equals(UIManager.getCrossPlatformLookAndFeelClassName()))
            cfg.setCustomProperty(LOOK_AND_FEEL, LOOK_N_FEEL_CROSS_PLATFORM);
        else
            cfg.setCustomProperty(LOOK_AND_FEEL, LOOK_N_FEEL_SYSTEM);
    }
    
    /**
     * returns the PropertyChangeListener
     * 
     * @return the PropertyChangeListener
     */
    PropertyChangeListener getPropertyChangeListener() {
        return propertyChangeListener;
    }
    
    /**
     * returns the WindowListener
     * 
     * @return the WindowListener
     */
    WindowListener getWindowListener() {
        return windowListener;
    }
    
    /**
     * returns the ActionListener
     * 
     * @return the ActionListener
     */
    ActionListener getActionListener() {
        return actionListener;
    }
    
    /**
     * returns the ActionListener
     * 
     * @return the ActionListener
     */
    ListSelectionListener getListSelectionListener() {
        return listSelectionListener;
    }

    /**
     * returns the TableModel
     * 
     * @return the TableModel
     */
    TableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * returns the DropTargetListener
     * 
     * @return the DropTargetListener
     */
    DropTargetListener getDropTargetListener() {
        return dropTargetListener;
    }
    

    public MenuBar getMenuBar() {
        return menuBar;
    }
    
    @Override
    public final void processFailed(Throwable t, IRenamable f) {
        super.processFailed(t, f);

        ((JFrame) getUIComponent()).setEnabled(true);
    }

    @Override
    public final void processFinished() {
        super.processFinished();
        ((JFrame) getUIComponent()).setEnabled(true);
    }

    @Override
    public final void processStarting(IRenamer irenamer) {
        super.processStarting(irenamer);
        ((JFrame) getUIComponent()).setEnabled(false);
    }
}
