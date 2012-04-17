package com.myapp.tools.media.renamer.view.swing;

import java.util.*;
import javax.swing.*;
import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static com.myapp.tools.media.renamer.config.IConstants.ISysConstants.*;
import static com.myapp.tools.media.renamer.view.swing.IActionCommands.*;


/**
 * the component used as menubar of the renamer swing ui.
 * 
 * @author andre
 */
@SuppressWarnings("serial")
class MenuBar extends JMenuBar {
    
    private final SwingApplication app;
    
    /**the status of these menu items depend on the state of the model.*/
    private Set<JMenuItem> selectionMenuItems = new HashSet<JMenuItem>();

    /**the status of this menu item depends on the state of the model.*/
    private JMenuItem startRenaming;

    /**
     * creates a new menubar for the given appframe
     * 
     * @param appFrame
     */
    MenuBar(SwingApplication appFrame) {
        this.app = appFrame;
        add(createFileMenu());
        add(createEditMenu());
        refreshDisplay();
    }



    /**
     * creates and inserts the file menu
     */
    private JMenu createFileMenu() {
        JMenuItem add, rmAll, resetAll, copyright, exit;
//         add             = new JMenuItem(msg("MenuBar.AddFiles"));
//         rmAll           = new JMenuItem(msg("MenuBar.RemoveAllFiles"));
//         resetAll        = new JMenuItem(msg("MenuBar.discardAllChanges"));
//         startRenaming   = new JMenuItem(msg("MenuBar.renameStartbtn"));
//         copyright       = new JMenuItem(msg("MenuBar.showCopyRight"));
//         exit            = new JMenuItem(msg("MenuBar.ExitApplication"));
//        add             .addActionListener(app.getActionListener());
//        rmAll           .addActionListener(app.getActionListener());
//        resetAll        .addActionListener(app.getActionListener());
//        startRenaming   .addActionListener(app.getActionListener());
//        copyright       .addActionListener(app.getActionListener());
//        exit            .addActionListener(app.getActionListener());
//         add             .setActionCommand(ADD_FILES);
//         rmAll           .setActionCommand(REMOVE_ALL_FILES);
//         resetAll        .setActionCommand(DISCARD_ALL_CHANGES);
//         startRenaming   .setActionCommand(START_RENAME_PROCESS);
//         copyright       .setActionCommand(SHOW_COPYRIGHT);
//         exit            .setActionCommand(EXIT_APP);
        add           = createMenuItem("MenuBar.createFileMenu.add",              "MenuBar.AddFiles"           , ADD_FILES);            
        rmAll         = createMenuItem("MenuBar.createFileMenu.rmAll",            "MenuBar.RemoveAllFiles"     , REMOVE_ALL_FILES);     
        resetAll      = createMenuItem("MenuBar.createFileMenu.resetAll",         "MenuBar.discardAllChanges"  , DISCARD_ALL_CHANGES);  
        startRenaming = createMenuItem("MenuBar.createFileMenu.startRenaming",    "MenuBar.renameStartbtn"     , START_RENAME_PROCESS); 
        copyright     = createMenuItem("MenuBar.createFileMenu.copyright",        "MenuBar.showCopyRight"      , SHOW_COPYRIGHT);       
        exit          = createMenuItem("MenuBar.createFileMenu.exit",             "MenuBar.ExitApplication"    , EXIT_APP);             
        
        JMenu menu = new JMenu(msg("MenuBar.File"));
        menu.add(add);
        menu.add(createEditSelectionSubMenu());
        menu.addSeparator();
        menu.add(rmAll);
        menu.add(resetAll);

        menu.addSeparator();
        menu.add(startRenaming);
        
        menu.addSeparator();
        menu.add(copyright);
        menu.add(exit);
        
        return menu;
    }
    
    /**
     * creates and inserts the settings menu
     */
    private JMenu createEditMenu() {
        JMenu menu = new JMenu(msg("MenuBar.editMenu"));

        menu.add(createEditSettingsSubMenu());
        menu.add(createLookAndFeelSubMenu());
        
        return menu;
    }


    
    /**
     * adapts the state of the menubar to the model state.
     */
    void refreshDisplay() {
        int[] sel = app.getListView().getSelection();
        boolean somethingSelected = sel.length > 0;
        
        for (JMenuItem i : selectionMenuItems) {
            i.setEnabled(somethingSelected);
        }
        
        startRenaming.setEnabled( ! app.getRenamer().isEmpty());
    }
    
    /**
     * creates the submenu for the selection menu.
     * 
     * @return the submenu for the selection menu.
     */
    private JMenu createEditSelectionSubMenu() {
        JMenuItem rm    = new JMenuItem(msg("MenuBar.RemoveFiles"));
        JMenuItem mv    = new JMenuItem(msg("MenuBar.MoveFiles"));
        JMenuItem num   = new JMenuItem(msg("MenuBar.editNummerierung"));
        JMenuItem reset = new JMenuItem(msg("MenuBar.discardSelChanges"));
        
         rm   .addActionListener(app.getActionListener());
         mv   .addActionListener(app.getActionListener());
         num  .addActionListener(app.getActionListener());
         reset.addActionListener(app.getActionListener());
        
        rm   .setActionCommand(REMOVE_SELECTED_FILES);
        mv   .setActionCommand(MOVE_SELECTED_FILES);
        num  .setActionCommand(SET_NUMMERIERUNG_TO_SELECTION);
        reset.setActionCommand(DISCARD_SELECTIONS_CHANGES);
        
        JMenu menu = new JMenu(msg("MenuBar.createEditSelectionSubMenu.Root"));

        menu.add(rm);
        menu.add(mv);
        menu.add(num);
        menu.add(reset);

        selectionMenuItems.add(rm);
        selectionMenuItems.add(mv);
        selectionMenuItems.add(num);
        selectionMenuItems.add(reset);
        
        return menu;
    }
    
    private JMenuItem createMenuItem(String name, String msgId, String actionCommand) {
        JMenuItem i = new JMenuItem(msg(msgId));
        i.setName(name);
        i.addActionListener(app.getActionListener());
        i.setActionCommand(actionCommand);
        return i;
    }
    
    /**
     * creates the submenu for the settings menu.
     * 
     * @return the submenu for the settings menu.
     */
    private JMenu createEditSettingsSubMenu() {
        JMenuItem copySettings, filterSettings, dateSettings, extendedSettings;
        
//        copySettings     = new JMenuItem(msg("MenuBar.editMenu.copySettings"));
//        filterSettings   = new JMenuItem(msg("MenuBar.editMenu.filters"));
//        dateSettings     = new JMenuItem(msg("MenuBar.editMenu.dateFormat"));
//        extendedSettings = new JMenuItem(msg("MenuBar.editMenu.extended"));
//         copySettings    .setActionCommand(EDIT_COPY_SETTINGS);
//         filterSettings  .setActionCommand(EDIT_FILTER);
//         dateSettings    .setActionCommand(EDIT_DATE_FORMAT);
//         extendedSettings.setActionCommand(EDIT_ALL_SETTINGS);
//        ActionListener actionListener = app.getActionListener();
//        copySettings    .addActionListener(actionListener);
//        filterSettings  .addActionListener(actionListener);
//        dateSettings    .addActionListener(actionListener);
//        extendedSettings.addActionListener(actionListener);
        copySettings     = createMenuItem("MenuBar.createEditSettingsMenu.copySettings", "MenuBar.editMenu.copySettings", EDIT_COPY_SETTINGS);
        filterSettings   = createMenuItem("MenuBar.createEditSettingsMenu.filters"     , "MenuBar.editMenu.filters"     , EDIT_FILTER);     
        dateSettings     = createMenuItem("MenuBar.createEditSettingsMenu.dateFormat"  , "MenuBar.editMenu.dateFormat"  , EDIT_DATE_FORMAT);
        extendedSettings = createMenuItem("MenuBar.createEditSettingsMenu.extended"    , "MenuBar.editMenu.extended"    , EDIT_ALL_SETTINGS);
        
        JMenu menu = new JMenu(msg("MenuBar.editMenu.Root"));
        menu.add(copySettings);
        menu.add(filterSettings);
        menu.add(dateSettings);
        menu.addSeparator();
        menu.add(extendedSettings);
        
        return menu;
    }

    /**
     * creates the submenu for the look and feel menu.
     * 
     * @return the submenu for the look and feel menu.
     */
    private JMenu createLookAndFeelSubMenu() {
        JRadioButtonMenuItem system, java;
        
        system = new JRadioButtonMenuItem(msg("MenuBar.editMenu.systemLnF"));
        java = new JRadioButtonMenuItem(msg("MenuBar.editMenu.crossPfLnF"));
        
        system.addActionListener(app.getActionListener());
        java.addActionListener(app.getActionListener());
        
        system.setActionCommand(SET_SYSTEM_LOOK_AND_FEEL);
        java.setActionCommand(SET_CROSS_PLATFORM_LOOK_AND_FEEL);

        JMenu menu = new JMenu(msg("MenuBar.editMenu.lookAndFeel"));
        menu.add(system);
        menu.add(java);

        ButtonGroup group = new ButtonGroup();
        group.add(system);
        group.add(java);

        if (app.getRenamer()
               .getConfig()
               .getString(LOOK_AND_FEEL)
               .equals(LOOK_N_FEEL_SYSTEM)) {
            system.setEnabled(true);
            
        } else {
            java.setEnabled(true);
        }
        
        return menu;
    }
}
