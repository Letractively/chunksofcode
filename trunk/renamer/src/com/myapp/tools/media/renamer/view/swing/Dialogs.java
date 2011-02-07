package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.config.IConstants.INameConstants;
import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.TimeSpan;
import com.myapp.tools.media.renamer.controller.Util;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.view.IDialogs;

/**
 * common dialogs of the application are encapsulated by this class.
 * 
 * @author andre
 */
@SuppressWarnings("serial")
class Dialogs implements ISysConstants, INameConstants, IDialogs {

    private static final String LINE_SEPARATOR;
    private static final String DATEFORMAT_HELPRESOURCE;
    private static final String[] ILLEGAL_PATH_CHARS;
    
    static {
        LINE_SEPARATOR =  System.getProperty("line.separator");
        DATEFORMAT_HELPRESOURCE = "/dateformat.html";
        ILLEGAL_PATH_CHARS = 
                   new String[] {"\\", "/", ":", "*", "\"", "<", ">", "|", "?"};
    }

    private final IApplication app;

    /**
     * creates a Dialogs instance for the given app
     * 
     * @param app
     *            the application to serve for
     */
    Dialogs(SwingApplication app) {
        this.app = app;
    }



@Override
public boolean showQuitDialog() {
    IRenamerConfiguration cfg = app.getRenamer().getConfig();

    if ( ! cfg.getBoolean(EXIT_WITHOUT_ASKING)) {
        JPanel question = new JPanel(new GridLayout(0, 1));
        JCheckBox chkbox = new JCheckBox(msg(
                                 "Dialogs.showQuitDialog.dontAskInFuture"));

        question.add(new JLabel(msg("Dialogs.showQuitDialog.reallyWantToExit")),
                     CENTER);
        question.add(chkbox, SOUTH);

        int i = showConfirmDialog((Component) app.getUIComponent(),
                                  question,
                                  "JRenamer", 
                                  OK_CANCEL_OPTION);

        if (chkbox.isSelected())
            cfg.setCustomProperty(EXIT_WITHOUT_ASKING, "true");

        if (i != OK_OPTION) return false;
    }

    return true;
}



@Override
public void showErrorMessage(String msg, Throwable t) {
    showMessageDialog(
              (Component) app.getUIComponent(),
              new JLabel(msg("Dialogs.showErrorMessage.anErrorOccured")
                      .replace("#msg#", msg)
                      .replace("#stacktrace#", Util.stackTraceToString(t))), 
              msg("Dialogs.showErrorMessage.errorTitle"),
              ERROR_MESSAGE);
}



@Override
public void showErrorWhileSavingConfigDialog(Throwable t) {
    showErrorMessage(msg("Dialogs.errorWhileSavingCustomSettings"), t);
}



@Override
public boolean showHugeSelectionWarning() {
    return YES_NO_OPTION == showConfirmDialog(
                    (Component) app.getElementChooser().getUIComponent(),
                    msg("Dialogs.showManyFilesWereSelected.msgTxt"),
                    msg("Dialogs.showManyFilesWereSelected.title"),
                    YES_NO_OPTION);
}



/**
 * shows a simple info dialog displaying a string.
 * 
 * @param string
 *            the msg to display
 */
public void showInfoDialog(String string) {
    showMessageDialog((Component)app.getUIComponent(), string);
}



@Override
public void showNoFileWasAddedDialog() {
    showInfoDialog(msg("Dialogs.noFileWasAdded.msg"));
}



@Override
public Map<String, String> showChooseDestinationDialog() {
    final IRenamerConfiguration cfg = app.getRenamer().getConfig();
    final Map<String, String> answer = new HashMap<String, String>();


    // create components-------------------------------------------


    final JRadioButton renameRadio = new JRadioButton(
                                  msg("Dialogs.chooseDestination.renameRadio"));
    final JRadioButton copyRadio = new JRadioButton(
                                    msg("Dialogs.chooseDestination.copyRadio"));
    final JTextField targetTxFld = new JTextField(cfg.getDestinationPath()); 
    final JButton chooseDestBtn = new JButton(
                         msg("Dialogs.chooseDestination.chooseDestinationBtn"));
    final JFileChooser jfc = new JFileChooser();

    final InsertOptionsPanel insertOptions = new InsertOptionsPanel(app);


    // setup components--------------------------------------------


    new ButtonGroup() {{
        add(copyRadio);
        add(renameRadio);
    }};

    boolean replace = cfg.getBoolean(REPLACE_ORIGINAL_FILES);


    targetTxFld.setEnabled(replace);
    targetTxFld.setEditable(false);
    targetTxFld.setBorder(createTitledBorder(msg(
                                "Dialogs.chooseDestination.targetPrefix")));

    chooseDestBtn.setEnabled(replace);


    ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == renameRadio) {
                targetTxFld.setEnabled(false);
                chooseDestBtn.setEnabled(false);
                answer.put(REPLACE_ORIGINAL_FILES, "true");

            } else if (e.getSource() == copyRadio) {
                targetTxFld.setEnabled(true);
                chooseDestBtn.setEnabled(true);
                answer.put(REPLACE_ORIGINAL_FILES, "false");

            } else if (e.getSource() == chooseDestBtn) {
                if (selectDestinationDir(jfc)) {
                    String path = jfc.getSelectedFile().getAbsolutePath();
                    answer.put(DESTINATION_RENAMED_FILES, path);
                    targetTxFld.setText(path);
                }
            }
        }
    };

    renameRadio.addActionListener(listener);
    copyRadio.addActionListener(listener);
    chooseDestBtn.addActionListener(listener);


    if (replace)
        renameRadio.doClick();
    else
        copyRadio.doClick();



    // layout components---------------------------------------------



    final JPanel radioPnl = new JPanel(new GridLayout(0,1)) {{
        add(renameRadio);
        add(copyRadio);
    }};

    final JPanel chooseDirPnl = new JPanel(new BorderLayout()) {{
        add(targetTxFld, NORTH);
        
        add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{
                add(chooseDestBtn);
            }},
            CENTER);

        add(new JLabel() {{
                setText(msg("Dialogs.chooseDestination.pressOkToSave"));
                setBorder(new EmptyBorder(20,10,10,10));
            }},
            SOUTH);
    }};

    JPanel question = new JPanel(new BorderLayout()) {{
        add(new JLabel() {{
                setText(msg("Dialogs.chooseDestination.headline"));
                setBorder(new EmptyBorder(10,10,20,10));
            }},
            NORTH);
        add(radioPnl, CENTER);
        add(chooseDirPnl, SOUTH);
        add(insertOptions, EAST);
    }};

    insertOptions.setBorder(BorderFactory.createTitledBorder(msg(
                            "Dialogs.chooseDestination.insertOptions.border")));
    

    // show dialog-----------------------------------------------


    boolean ok = OK_OPTION == showOptionDialog(
            (Component) app.getUIComponent(),
            question,
            msg("Dialogs.chooseDestination.title"),
            OK_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null,
            new Object[] {
                msg("Dialogs.chooseDestination.saveButtonText").intern(),
                msg("Dialogs.chooseDestination.cancelButtonText")
            },
            msg("Dialogs.chooseDestination.saveButtonText").intern());
    
    if (ok) {
        answer.put(EXCLUDE_DUPLICATE_FILES, 
                               insertOptions.isExcludeDuplicateSelected() + "");
        answer.put(RECURSE_INTO_DIRECTORIES, 
                                      insertOptions.isRecursiveSelected() + "");
        answer.put(SHOW_HIDDEN_FILES, 
                                     insertOptions.isShowHiddenSelected() + "");
    }
    
    return ok ? answer : null;
}



/**
 * shows the dialog where the user can set the copy/rename settings
 * 
 * @param c
 *            the jfilechooser to be used to choose the destination dir
 * @return true if the user selected a file by pressing ok.
 */
private boolean selectDestinationDir(JFileChooser c) {
    Log.defaultLogger().info(msg("Log.dialogs.selectdestStart"));
    IRenamerConfiguration cfg = app.getRenamer().getConfig();
    c.setFileSelectionMode(DIRECTORIES_ONLY);
    c.setMultiSelectionEnabled(false);

    File destination = null; 
    try {
        destination = new File(cfg.getDestinationPath());
        destination = destination.getParentFile();
        
    } catch (Exception x) {
        x.printStackTrace();
        Log.defaultLogger().info("Error while loading " +
                                 "destinations parent dir " +
                                 "(" + x + ")");
        destination = null;
    }

    if (destination != null && ! destination.canWrite()) destination = null;

    c.setCurrentDirectory(destination);
    int i = c.showDialog((Component) app.getUIComponent(),
                         msg("Dialogs.chooseDestination.btnText"));
    File f = c.getSelectedFile();
    Log.defaultLogger().info(msg("Log.dialogs.selectdestEnd")
       .replace("#destination#", f == null ? "null" : f.getAbsolutePath()));
    return APPROVE_OPTION == i;
}



@Override
public String showEditConfigFileDialog() {
    // create components: --------------------------------------------------

    final JTextArea ta = new JTextArea(35, 100);
    final JButton resetButton = new JButton(
                                    msg("Dialogs.editProperties.resetBtn"));


    //setup components: ----------------------------------------------------

    ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, ta.getFont().getSize()));
    ta.setText(Utils.readProperties(app));
    ta.setCaretPosition(0);
    
    resetButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (OK_OPTION != showConfirmDialog(
                                (Component) app.getUIComponent(), 
                                msg("Dialogs.editProperties.resetMessage"), 
                                msg("Dialogs.editProperties.resetTitle"),
                                YES_NO_OPTION,
                                WARNING_MESSAGE)) {
                return;
            }

            try {
                IRenamerConfiguration cfg = app.getRenamer().getConfig();
                cfg.clearCustomProperties();
                cfg.saveCustomSettings();
                ta.setText(Utils.readProperties(app));
                ta.setCaretPosition(0);

            } catch (IOException x) {
                x.printStackTrace();
                showErrorMessage(
                      msg("Dialogs.editProperties.ErrorWhileResetting"), x);
            }
        }
    });


    // layout components: --------------------------------------------------

    JPanel question = new JPanel(new BorderLayout()) {{
        add(new JScrollPane(ta), CENTER);
        add(new JPanel(new BorderLayout()) {{add(resetButton, EAST);}}, SOUTH);
    }};


    // show dialog: --------------------------------------------------------

    if (OK_OPTION == showOptionDialog(
                        (Component) app.getUIComponent(),
                        question,
                        msg("Dialogs.editProperties.title"),
                        OK_CANCEL_OPTION,
                        PLAIN_MESSAGE,
                        null,
                        new Object[] {
                            msg("Dialogs.editProperties.saveBtn").intern(),
                            msg("Dialogs.editProperties.cancelBtn")
                        },
                        msg("Dialogs.editProperties.saveBtn").intern())) {
        return ta.getText();

    } else {
        return null;
    }
}



@Override
public void showLogHistoryDialog() {
    final JPanel message = new JPanel(new BorderLayout());
    List<LogRecord> records = Log.getLogRecords();
    int i = records.size();
    StringBuilder bui = new StringBuilder();
    
    for (LogRecord r : records) {
        bui.append(i < 10 ? ("  " + i) : (i < 100 ? (" " + i) : i))
           .append('.')
           .append(' ')
           .append(Util.logRecordToString(r))
           .append(LINE_SEPARATOR);
        i--;
    }

    int length = bui.length();
    length -= LINE_SEPARATOR.length();
    length = length < 0 ? 0 : length;

    bui.setLength(length);

    JTextArea txt = new JTextArea(bui.toString(), 15, 75);

    txt.setFont(new Font(
            Font.MONOSPACED,
            Font.PLAIN,
            ((Component) app.getUIComponent()).getFont().getSize()));
    
    txt.setEditable(false);
    message.add(new JScrollPane(txt));

    new JFrame() {{
        setTitle(msg("ControlPanel.popupLog.title"));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(message);
        pack();
        setSize(new Dimension(900, 450));
        setLocation(Utils.getCenteredPosition(this));
        setVisible(true);
    }};
}


@Override
public String showFilterDefinitionDialog() {
    IRenamerConfiguration cfg = app.getRenamer().getConfig();
    final JTextField filterTxtFld = new JTextField(
                                 cfg.getString(ISysConstants.FILE_FILTERS));
    final JCheckBox applyFilterToCurrentList = new JCheckBox(msg(
                        "Dialogs.showFilterSelectionDialog.applyChbxText"));
    applyFilterToCurrentList.setSelected(false);
    final boolean sizeGt0 = app.getRenamer().getSize() > 0;
    applyFilterToCurrentList.setEnabled(sizeGt0);
    
    String filterText;
    
    for (;;) {
        int val = showConfirmDialog(
            (Component) app.getUIComponent(), 
            new JPanel(new BorderLayout()) {{
                add(new JLabel(
                    msg("Dialogs.showFilterSelectionDialog.Prompt")),
                    NORTH);
                add(filterTxtFld, CENTER);
                add(applyFilterToCurrentList, SOUTH);
            }},
            msg("Dialogs.showFilterSelectionDialog.Title"),
            OK_CANCEL_OPTION
        );
        
        if (val != OK_OPTION) return null;
        
        filterText = filterTxtFld.getText();
        if (Util.FILTER_PATTERN.matcher(filterText).matches()) break;
        
        showMessageDialog(
            (Component) app.getUIComponent(), 
            new JLabel(
                    msg("Dialogs.showFilterSelectionDialog.invalidFilter")
                    .replace("#regex#", Util.FILTER_PATTERN.pattern())
                    .replace("#example#", "jpg,jpeg,gif,tif, bmP")),
            "Error",
            ERROR_MESSAGE
        );
    }
    
    if (sizeGt0 && applyFilterToCurrentList.isSelected()) {
        app.getRenamer().applyFilter(
              /*FIXME: this should be called by the controller !!!*/
                        Util.createFileFilterFromCommaSepList(filterText));
    }
    return filterText;
}



@Override
public void showRenameProcessFinished() {
    IRenamer r = app.getRenamer();

    final String line = msg("Dialogs.showRenameProcessFinished.reportLine")
                                    .replaceAll("#newLine#", LINE_SEPARATOR);

    final StringBuilder bui = new StringBuilder();
    for (Iterator<IRenamable> i = r.iterator(); i.hasNext();) {
        IRenamable f = i.next();
        bui.append(line.replace("#oldPath#", Util.oldAbsolutePath(f))
                       .replace("#newPath#", Util.newAbsolutePath(f)));
        bui.append(LINE_SEPARATOR);
    }
    
    String head = msg("Dialogs.showRenameProcessFinished.header");
    head = head.replace("#num#", Integer.toString(app.getRenamer().getSize()));
    
    JTextArea textArea = new JTextArea(bui.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("monospaced", Font.PLAIN, 10));

    JPanel contentPane = new JPanel(new BorderLayout());
    contentPane.add(new JLabel(head), NORTH);
    contentPane.add(new JScrollPane(textArea), CENTER);
    
    JFrame frame = new JFrame(msg("ControlPanel.popupLog.title"));
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setContentPane(contentPane);
    frame.pack();
    frame.setSize(new Dimension(900, 450));
    frame.setLocation(Utils.getCenteredPosition(frame));
    frame.setVisible(true);
}



/**
 * informs the user that another instance is already running.
 * 
 * @param date
 *            the timespan between now and the starting time of the existing
 *            application instance
 * @return true if the user wants to exit the current application
 */
public static boolean showAnotherInstanceRunning(final TimeSpan date) {
    return YES_OPTION == showOptionDialog(
        null,
        new JLabel(
            "<html>" +
                "<h2>" + 
                    msg("Dialogs.anotherInstanceRunning.headLine") +
                "</h2>" +
                msg("Dialogs.anotherInstanceRunning.beforeDate") +
                date.toString() +
                msg("Dialogs.anotherInstanceRunning.afterDate") + "<br>" +
                msg("Dialogs.anotherInstanceRunning.description") +
            "</html>"), 
        msg("Dialogs.anotherInstanceRunning.title"), 
        YES_NO_OPTION, 
        WARNING_MESSAGE, 
        null, 
        new Object[] {
            msg("Dialogs.anotherInstanceRunning.overrideOption"),
            msg("Dialogs.anotherInstanceRunning.exitOption").intern()
        }, 
        msg("Dialogs.anotherInstanceRunning.exitOption").intern());
}


@Override
public int showWantToOverwriteDialog(String location) {
    assert location != null;
    int i = showOptionDialog(
        (Component) app.getUIComponent(), 
        new JLabel(msg("Dialogs.showWantToOverwriteFiles.Question")
                                              .replace("#fileName#", location)), 
        msg("Dialogs.showWantToOverwriteFiles.Title"), 
        JOptionPane.YES_NO_CANCEL_OPTION, 
        WARNING_MESSAGE, 
        null,
        new Object[] {
            msg("Dialogs.showWantToOverwriteFiles.DoOverwrite"),          // 0
            msg("Dialogs.showWantToOverwriteFiles.DoOverwriteAll"),       // 1
            msg("Dialogs.showWantToOverwriteFiles.CancelOption").intern() // 2
        }, 
        msg("Dialogs.showWantToOverwriteFiles.CancelOption").intern());
    
    if (i == JOptionPane.YES_OPTION)     return IDialogs.OVERWRITE_THIS_FILE;
    if (i == JOptionPane.NO_OPTION)      return IDialogs.OVERWRITE_ALL_FILES;
    if (i == JOptionPane.CANCEL_OPTION)  return IDialogs.CANCEL;
    
    assert false : i;
    return IDialogs.CANCEL;
}



@Override
public int showMoveInsertionDialog() {
    final IndexControlPanel indexCtrl = new IndexControlPanel(app);
    
    if (OK_OPTION == showOptionDialog(
          (Component) app.getUIComponent(),
          new JPanel(new BorderLayout()) {{
             add(new JLabel(msg("Dialogs.showMoveInsertionDialog.header")),
                 NORTH);
             add(indexCtrl, CENTER);
          }},
          msg("Dialogs.showMoveInsertionDialog.title"), 
          OK_CANCEL_OPTION, 
          QUESTION_MESSAGE, 
          null, 
          null,
          null)) {
        return indexCtrl.getSelectedIndex();
    }
    return -1;
}



@Override
public void showCannotWriteDialog(IRenamable f) {
    showErrorMessage(msg("Dialogs.showCannotWriteDialog.msg")
                                 .replace("#file#", f.getNewAbsolutePath()), 
                     new IOException(f.getNewAbsolutePath()));
}



@Override
public NummerierungsSettings showEditNummerierungSettings() {
    IRenamerConfiguration cfg = app.getRenamer().getConfig();
    
    final JTextField prefix = new JTextField(cfg.getNummerierungPrefix());
    final JTextField suffix = new JTextField(cfg.getNummerierungSuffix());
    final JSpinner start = new JSpinner(new SpinnerNumberModel(
                                 cfg.getNummerierungStart(), 1, 1000000000, 1));
    final JSpinner increm = new JSpinner(new SpinnerNumberModel(
                                  cfg.getNummerierungIncrement(), 1, 10000, 1));
    final JCheckBox save = new JCheckBox(msg(
                         "Dialogs.showEditNummerierungSettings.saveAsDefault"));

    prefix.setBorder(createTitledBorder(msg(
                               "Dialogs.showEditNummerierungSettings.prefix")));
    suffix.setBorder(createTitledBorder(msg(
                               "Dialogs.showEditNummerierungSettings.suffix")));
    start.setBorder(createTitledBorder(msg(
                                "Dialogs.showEditNummerierungSettings.start")));
    increm.setBorder(createTitledBorder(msg(
                            "Dialogs.showEditNummerierungSettings.increment")));
    
   boolean ok = OK_OPTION == showOptionDialog(
        (Component) app.getUIComponent(),
        new JPanel(new BorderLayout()) {{
            add(new JLabel(msg("Dialogs.showEditNummerierungSettings.header")),
                NORTH);
            add(new JPanel(new GridLayout(0, 2)) {{ add(prefix);
                                                    add(suffix);
                                                    add(start);
                                                    add(increm); }}, CENTER);
            add(save, BorderLayout.SOUTH);
        }},
        msg("Dialogs.showEditNummerierungSettings.tile"), 
        OK_CANCEL_OPTION, 
        QUESTION_MESSAGE, 
        null, 
        null,
        null);
        
    if ( ! ok) {
        return null;
    }
    
    NummerierungsSettings settings = new NummerierungsSettings(
                            ((Integer) start.getModel().getValue()).intValue(), 
                            ((Integer) increm.getModel().getValue()).intValue(),
                            prefix.getText(),
                            suffix.getText());
    
    if (save.isSelected()) {
        cfg.setCustomProperty(NUMMERIERUNG_PREFIX, settings.prefix);
        cfg.setCustomProperty(NUMMERIERUNG_SUFFIX, settings.suffix);
        cfg.setCustomProperty(NUMMERIERUNG_START, settings.start+"");
        cfg.setCustomProperty(NUMMERIERUNG_ANSTIEG, settings.increment +"");
        
        try {
            cfg.saveCustomSettings();
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
    
    return settings;
}



void showCopyRight() {
    showMessageDialog((Component) app.getUIComponent(), 
                      new JLabel(msg("Dialogs.showCopyRight.msg")),
                      msg("Dialogs.showCopyRight.title"),
                      INFORMATION_MESSAGE,
                      null);
}



@Override
public String showEditDateFormat() {
    //get needed values:
        
    IRenamerConfiguration cfg = app.getRenamer().getConfig();
    final String actualFormat = cfg.getDatumFormat();
    final String defaultFormat = cfg.getDefaultValue(DATUM_FORMAT);
    
    
    //create components for message panel:
    
    final Vector<String> items = new Vector<String>();
    for (String item : new String[] {actualFormat,
                                     defaultFormat,
                                     "yyyy.MM.dd G 'at' HH-mm-ss z",
                                     "EEE, MMM d, ''yy",
                                     "h-mm a",
                                     "hh 'o''clock' a, zzzz",
                                     "K mm a, z",
                                     "yyyyy.MMMMM.dd GGG hh-mm aaa",
                                     "EEE, d MMM yyyy HH-mm-ss Z",
                                     "yyMMddHHmmssZ",
                                     "yyyy-MM-dd'T'HH-mm-ss.SSSZ"}) {
        if ( ! items.contains(item)) {
            items.add(item);
        }
    }

    final JButton help = new JButton(msg("Dialogs.showEditDateFormat.helpBtn"));
    final JComboBox choicesCmB = new JComboBox(new DefaultComboBoxModel(items));
    final JTextField customTxF = new JTextField(actualFormat);
    final JButton previewBtn = new JButton(msg(
                                       "Dialogs.showEditDateFormat.apply"));
    final JLabel previewLbl = new JLabel(
                         new SimpleDateFormat(actualFormat).format(new Date()));
   
    
    //setup components for message panel:
    
    choicesCmB.setBorder(createTitledBorder(msg(
                                "Dialogs.showEditDateFormat.combobox.border")));
    customTxF.setBorder(createTitledBorder(msg(
                           "Dialogs.showEditDateFormat.customPattern.border")));
    previewLbl.setBorder(createTitledBorder(msg(
                                 "Dialogs.showEditDateFormat.preview.border")));
    
    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object caller = e.getSource();
            final String newPattern;
            final DateFormat format;
            
            if (caller == choicesCmB) {
                newPattern = items.get(choicesCmB.getSelectedIndex());
                format = createDateFormat(newPattern, false);

                if (format == null) {
                    throw new RuntimeException("invalid : " + newPattern);
                }
            
            } else if (caller == customTxF || caller == previewBtn) {
                newPattern = customTxF.getText().trim();
                format = createDateFormat(newPattern, true);
                
                if (format == null) return; // invalid pattern !
            
            } else if (caller == help) {
                showDateFormatHelp();
                return;
                
            } else {
                throw new IllegalArgumentException("source: " + e.getSource());
            }
            
            customTxF.setText(newPattern);
            previewLbl.setText(format.format(new Date()));
        }
    }; // listener end

    choicesCmB.addActionListener(listener);
    customTxF.addActionListener(listener);
    previewBtn.addActionListener(listener);
    help.addActionListener(listener);
    
    
    //layout components for message panel:
    
    JPanel messagePnl = new JPanel(new BorderLayout());
    messagePnl.add(
        new JPanel(new BorderLayout()) {{
            add(new JLabel(msg("Dialogs.showEditDateFormat.description")),
                NORTH);
            add(customTxF, CENTER);
            add(previewBtn, EAST);
        }},
        NORTH);

    messagePnl.add(new JPanel(new BorderLayout()) {{add(choicesCmB, CENTER);}}, 
                   CENTER);

    
    //create dialog for question:
    
    JPanel previewAndHelp = new JPanel(new BorderLayout());
    previewAndHelp.add(previewLbl, CENTER);
    previewAndHelp.add(help, BorderLayout.EAST);
    
    messagePnl.add(previewAndHelp, SOUTH);
    final String yes = msg("Dialogs.showEditDateFormat.yesOption");
    assert yes != null;

    JOptionPane pane = new JOptionPane(
                         messagePnl,
                         QUESTION_MESSAGE,
                         YES_NO_CANCEL_OPTION,
                         null,
                         new String[] {
                             yes,
                             msg("Dialogs.showEditDateFormat.cancelOption")
                         });
    
    pane.setVisible(true);

    JFrame appFrame = (JFrame) app.getUIComponent();
    JDialog dialog = pane.createDialog(
                                   appFrame,
                                   msg("Dialogs.showEditDateFormat.title"));
    
    dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    dialog.setVisible(true);
    

    //dialog will appear and block app until user response just here
    
    Object selected = pane.getValue();
    if (selected == null || ! selected.equals(yes))
        return null;
        
    String pattern = customTxF.getText().trim();
    if (null == createDateFormat(pattern, true))
        return null;
    
    return pattern;
}



/**
 * takes a dateformat pattern and creates a simpledateformat object from it.
 * if the pattern is invalid, a error msg will be shown to the user.
 * 
 * @param pattern
 *            the pattern to parse
 * @return the dateformat object, or null if the pattern was invalid.
 */
private static DateFormat createDateFormat(String pattern, boolean alert) {
    boolean illegalCharacters = false;

    try {
        DateFormat df = new SimpleDateFormat(pattern);
        String example = df.format(new Date());
        
        for (String invalid : ILLEGAL_PATH_CHARS)
            if (example.contains(invalid)) {
                illegalCharacters = true;
                break;
            }
    
        if ( ! illegalCharacters) return df;
        
    } catch (IllegalArgumentException e) {
    }

    final String htmlInvalid = Arrays.toString(ILLEGAL_PATH_CHARS)
                                     .replaceAll("\"", "&quot;")
                                     .replaceAll("<", "&lt;")
                                     .replaceAll(">", "&gt;");

    if (alert) {
        showMessageDialog(null,
                          msg("Dialogs.showEditDateFormat.invalid.msg")
                              .replace("#pattern#", pattern)
                              .replace("#invalid#", htmlInvalid),
                          msg("Dialogs.showEditDateFormat.invalid.title"),
                          ERROR_MESSAGE);
    }
    
    return null;
}



/**
 * shows an explanation of the dateformat pattern used by the datum format
 * option.
 */
private static void showDateFormatHelp() {
    //get file "dateformat.html" from jar:
    
    Reader reader = null;
    InputStream stream = null;
    int read = -1;
    char[] buf = new char[1024];
    StringBuilder bui = new StringBuilder();
    
    try {
        URL resource = IApplication.class.getResource(DATEFORMAT_HELPRESOURCE);
        stream = resource.openStream();
        reader = new InputStreamReader(stream);
        
        while ((read = reader.read(buf)) > 0) {
            bui.append(buf, 0, read);
        }
        
    } catch (IOException e) {
        throw new RuntimeException(
                          "could not read from jar: " + DATEFORMAT_HELPRESOURCE,
                          e);
    } finally {
        try {
            if (reader != null)
                reader.close();
            if (stream != null)
                stream.close();

        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    
    //set up components to display help:
    
    JTextPane pane = new JTextPane();
    pane.setContentType("text/html");
    pane.setText(bui.toString());
    pane.setPreferredSize(new Dimension(500, 500));
    pane.setCaretPosition(0);
    
    JFrame frame = new JFrame();
    frame.setContentPane(new JScrollPane(pane));
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.pack();

    frame.setVisible(true);
}



@Override
public boolean showWantToCreateDir(String destination) {
    String title = msg("Dialogs.showWantToCreateDir.title");
    String message = msg("Dialogs.showWantToCreateDir.msg")
                                        .replace("#dir#", destination);

    return YES_OPTION == showConfirmDialog(null,
                                           message,
                                           title,
                                           YES_NO_OPTION,
                                           QUESTION_MESSAGE);
}



@Override
public void showDuplicatesExcluded(List<IRenamable> duplicates) {
    assert duplicates != null;
    
    String title = msg("Dialogs.showDuplicatesExcluded.title");
    StringBuilder bui = new StringBuilder();
    
    for (IRenamable i : duplicates) {
        bui.append("<li>")
           .append(i.getSourceObject().getAbsolutePath())
           .append("</li>");
    }
    
    String message = msg("Dialogs.showDuplicatesExcluded.msg")
                                        .replace("#files#", bui.toString());
    
    showMessageDialog((Component) app.getUIComponent(),
                      new JLabel(message),
                      title,
                      JOptionPane.WARNING_MESSAGE,
                      null);
}



@Override
public void showAllFilesFilteredWarning() {
    String title = msg("Dialogs.showAllFilesFilteredWarning.title");
    String message = msg("Dialogs.showAllFilesFilteredWarning.msg");
    
    showMessageDialog((Component) app.getUIComponent(),
                      new JLabel(message),
                      title,
                      JOptionPane.WARNING_MESSAGE,
                      null);
}
}