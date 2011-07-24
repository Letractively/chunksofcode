package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.controller.Msg.msg;
import static javax.swing.BorderFactory.createTitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Log.IMessageListener;
import com.myapp.tools.media.renamer.controller.Util;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.view.ISettingsView;
import com.myapp.util.swing.datechooser.JCalendar;
import com.myapp.util.swing.datechooser.JDateChooser;

/**
 * helds the components in the southern control area which allows the user to
 * set the name, date, and stuff for the application
 * 
 * @author andre
 * 
 */
@SuppressWarnings("serial")
class SettingsView extends JPanel implements IMessageListener,
                                              IActionCommands,
                                              ISettingsView,
                                              ISysConstants {

    static final String LINE_SEPARATOR =  System.getProperty("line.separator");
    static final Dimension PIC_SIZE = new Dimension(144, 144);
    
    private ThumbnailStore thumbnails = 
                       new ThumbnailStore(PIC_SIZE.width, PIC_SIZE.height, 500);
    private final SwingApplication app;
    
    private JLabel logLabel;  
    private JLabel destinationLbl;
    private JButton destinationBtn;
    private JDateChooser dateChooser;
    
    private JTextField titelTxf, themaTxf, beschreibungTxf, startNrTxf;

    private JLabel pictureArea;
    private JButton startBtn;

    /**
     * creates a controlpanel for the given application
     * 
     * @param app
     *            the application
     */
    public SettingsView(SwingApplication app) {
        super(new BorderLayout());
        this.app = app;
        initComponents();
        initListeners();
        layoutComponents();
    }


    /**
     * creates the components used by the control panel
     */
    private void initComponents() {
        IRenamer renamer = app.getRenamer();

        logLabel = new JLabel();
        logLabel.setBorder(BorderFactory.createTitledBorder(msg(
                                                "ControlPanel.logLbl.border")));

        destinationLbl = new JLabel(calcDestinationLblText());
        destinationBtn = new JButton(msg("ControlPanel.destinationBtn.text"));

        dateChooser = new JDateChooser(new JCalendar(new Date()));
        dateChooser.setPreferredSize(
                    new Dimension(140, dateChooser.getPreferredSize().height));
        
        startNrTxf = new JTextField(""+renamer.getConfig().getNummerierungStart());
        titelTxf = new JTextField(renamer.getConfig().getDefaultTitel());
        themaTxf = new JTextField(renamer.getConfig().getDefaultThema());
        beschreibungTxf = new JTextField(
                            renamer.getConfig().getDefaultBeschreibung());

        startBtn = new JButton(msg("ControlPanel.startRenamingBtn.text"));
        
        Font font = startBtn.getFont();
        startBtn.setFont(new Font(font.getName(),
                                  Font.BOLD, 
                                  font.getSize() + 1));
        startBtn.setEnabled(false);
        
        pictureArea = new JLabel();
    }


    /**
     * registers all components at their listeners
     */
    private void initListeners() {
        ActionListener l = app.getActionListener();

        destinationBtn.addActionListener(l);
        destinationBtn.setActionCommand(EDIT_COPY_SETTINGS);

        startBtn.addActionListener(l);
        startBtn.setActionCommand(IActionCommands.START_RENAME_PROCESS);

        dateChooser.addPropertyChangeListener("date", 
                                              app.getPropertyChangeListener());
        
        startNrTxf.addActionListener(l);
        startNrTxf.setActionCommand(IActionCommands.SET_NUMMERIERUNG_START);
        
        Log.addMessageListener(this, true);
        
        logLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                app.getActionListener().actionPerformed(
                      new ActionEvent(SettingsView.this, 1, SHOW_LOG_HISTORY));
            }
        });
    }
    
    /**
     * sets up the layout of the components
     */
    private void layoutComponents() {
        final JPanel targetDateAndStartControl = new JPanel(new BorderLayout());
        final JPanel filePropPanel = new JPanel(new GridLayout(1, 0));
        
        //destination
        targetDateAndStartControl.add(new JPanel(new BorderLayout()) {{
            add(destinationLbl, BorderLayout.CENTER);
            add(destinationBtn, BorderLayout.EAST);
            setBorder(createTitledBorder(msg("ControlPanel.destination.border")));
        }}, BorderLayout.EAST);
        
        //date
        JPanel date = new JPanel(new BorderLayout()) {{
            add(new JButton(msg("SettingsView.layoutComponents.formatBtn")) {{
                addActionListener(app.getActionListener());
                setActionCommand(IActionCommands.EDIT_DATE_FORMAT);
            }}, BorderLayout.EAST);
            add(dateChooser, BorderLayout.CENTER);
            setBorder(createTitledBorder(msg("ControlPanel.date.border")));
        }};
        
        //date
        JPanel startNr = new JPanel(new BorderLayout()) {{
            add(new JButton(msg("SettingsView.layoutComponents.startNrTxf")) {{
                addActionListener(app.getActionListener());
                setActionCommand(IActionCommands.SET_NUMMERIERUNG_START);
            }}, BorderLayout.EAST);
            add(startNrTxf, BorderLayout.CENTER);
            setBorder(createTitledBorder(msg("ControlPanel.startNr.border")));
        }};

        targetDateAndStartControl.add(startNr, BorderLayout.CENTER);
        targetDateAndStartControl.add(date, BorderLayout.WEST);
        
        
        //themaControl
        filePropPanel.add(createPropertyControl(
                            msg("ControlPanel.themaBtn.text"),
                            msg("ControlPanel.themaSelBtn.text"), 
                            msg("ControlPanel.themaUnchangedBtn.text"), 
                            IActionCommands.SET_THEMA_TO_ALL, 
                            IActionCommands.SET_THEMA_TO_SELECTION,
                            IActionCommands.SET_THEMA_TO_UNCHANGED,
                            msg("ControlPanel.all.TooltipText"), 
                            msg("ControlPanel.selected.TooltipText"), 
                            msg("ControlPanel.unChanged.TooltipText"), 
                            msg("ControlPanel.thema.border"), 
                            themaTxf));
        
        //titelControl
        filePropPanel.add(createPropertyControl(
                            msg("ControlPanel.titelBtn.text"),
                            msg("ControlPanel.titelSelBtn.text"), 
                            msg("ControlPanel.titelUnchangedBtn.text"), 
                            IActionCommands.SET_TITEL_TO_ALL, 
                            IActionCommands.SET_TITEL_TO_SELECTION,
                            IActionCommands.SET_TITEL_TO_UNCHANGED,
                            msg("ControlPanel.all.TooltipText"), 
                            msg("ControlPanel.selected.TooltipText"), 
                            msg("ControlPanel.unChanged.TooltipText"), 
                            msg("ControlPanel.titel.border"), 
                            titelTxf));
        
        //beschreibungControl
        filePropPanel.add(createPropertyControl(
                            msg("ControlPanel.beschreibungBtn.text"),
                            msg("ControlPanel.beschreibungSelBtn.text"), 
                            msg("ControlPanel.beschreibungUnchangedBtn.text"), 
                            IActionCommands.SET_BESCHREIBUNG_TO_ALL, 
                            IActionCommands.SET_BESCHREIBUNG_TO_SELECTION,
                            IActionCommands.SET_BESCHREIBUNG_TO_UNCHANGED,
                            msg("ControlPanel.all.TooltipText"), 
                            msg("ControlPanel.selected.TooltipText"), 
                            msg("ControlPanel.unChanged.TooltipText"), 
                            msg("ControlPanel.beschreibung.border"), 
                            beschreibungTxf));
            
        // insert into frame
        add(new JPanel(new BorderLayout()) {{
            add(new JPanel(new BorderLayout()) {{
                add(targetDateAndStartControl, BorderLayout.NORTH);
                add(filePropPanel, BorderLayout.SOUTH);
            }}, BorderLayout.CENTER);
            add(logLabel, BorderLayout.SOUTH);
        }}, BorderLayout.CENTER);
        
        //image preview
        pictureArea = new JLabel() {{
            setPreferredSize(PIC_SIZE);
            setSize(PIC_SIZE);
            setMinimumSize(PIC_SIZE);
            setMaximumSize(PIC_SIZE);
        }};

        //start btn
        add(new JPanel(new BorderLayout()) {{
            add(pictureArea, BorderLayout.NORTH);
            add(startBtn, BorderLayout.SOUTH);
        }}, BorderLayout.EAST);
    }

    
    @Override
    public Object getUIComponent() {
        return this;
    }

    @Override
    public void setImageToPreview(String pathToImageFile) { 
        if (pathToImageFile == null) {
            pictureArea.setIcon(null);
            return;
        }
        
        Image img = thumbnails.getCachedImage(pathToImageFile);
        pictureArea.setIcon(img != null ? new ImageIcon(img) : null);
    }

    /**
     * returns the thumbnailstore used by theis settingsview
     * 
     * @return the thumbnailstore
     */
    ThumbnailStore getThumbnailStore() {
        return thumbnails;
    }
    
    
    @Override
    public void messageOccured(LogRecord record) {
        Log.getLogRecords().add(0, record);
        logLabel.setText(Util.logRecordToString(record));
        calculateToolTipText();
    }

    @Override
    public Date getSelectedDate() {
        return dateChooser.getDate();
    }

    @Override
    public String getTitelText() {
        return titelTxf.getText();
    }

    @Override
    public int getStartNr() {
        String startNrText = null;
        try {
            startNrText = startNrTxf.getText();
            startNrTxf.setForeground(null);
            return Integer.parseInt(startNrText);
            
        } catch (NumberFormatException e) {
            startNrTxf.setForeground(Color.red);
            Log.defaultLogger().info("not a number:'"+startNrText+"'");
            return -1;
        }
    }
    
    void setNummerierungStart(int startNr) {
        startNrTxf.setForeground(null);
        startNrTxf.setText(startNr+"");
    }

    @Override
    public String getBeschreibungText() {
        return beschreibungTxf.getText();
    }

    @Override
    public String getThemaText() {
        return themaTxf.getText();
    }

    @Override
    public void resetDestinationText() {
        destinationLbl.setText(calcDestinationLblText());
    }

    @Override
    public void setRenamingEnabled(boolean enabled) {
        startBtn.setEnabled(enabled);
    }
    

    /**
     * calculates and sets the text for the tooltip of the log label component
     */
    private void calculateToolTipText() {
        StringBuilder bui = new StringBuilder("<html>");
        List<LogRecord> records = Log.getLogRecords();
        
        for (int i = 0, s = records.size() - 1; i < s && i < 10; i++) 
            bui.append("<tr><td>")
               .append(Util.date(records.get(i)))
               .append("</td><td><b>")
               .append(records.get(i).getMessage())
               .append("</b></td></tr>");
        logLabel.setToolTipText(bui.append("</html>").toString());
    }

    /**
     * @return the destination path or an "rename" label text if the user
     *         selected copy files
     */
    private String calcDestinationLblText() {
        IRenamerConfiguration cfg = app.getRenamer().getConfig();

        return cfg.getBoolean(REPLACE_ORIGINAL_FILES)
                        ? msg("ControlPanel.destinationLbl.text.replaceFiles")
                        : cfg.getDestinationPath();
    }
    
    /**
     * creates a container with a textfield and three buttons to control a
     * specific property with tree buttons.
     * 
     * @param btnTextForAll
     *            the btnTextForAll
     * @param btnTextForSelection
     *            the btnTextForSelection
     * @param btnTextForUnchanged
     *            the btnTextForUnchanged
     * @param actionCmdForAll
     *            the actionCmdForAll
     * @param actionCmdForSelection
     *            the actionCmdForSelection
     * @param actionCmdForUnchanged
     *            the actionCmdForUnchanged
     * @param toolTipTextForAll
     *            the toolTipTextForAll
     * @param toolTipTextForSelection
     *            the toolTipTextForSelection
     * @param toolTipTextForUnchanged
     *            the toolTipTextForUnchanged
     * @param borderText
     *            the borderText
     * @param textField
     *            the textField
     * @return the component containing the controls
     */
    private JComponent createPropertyControl(String btnTextForAll, 
                                            String btnTextForSelection,
                                            String btnTextForUnchanged,
                                            String actionCmdForAll,
                                            String actionCmdForSelection,
                                            String actionCmdForUnchanged,
                                            String toolTipTextForAll,
                                            String toolTipTextForSelection,
                                            String toolTipTextForUnchanged,
                                            String borderText,
                                            JTextField textField) {
        textField.addActionListener(app.getActionListener());
        textField.setActionCommand(actionCmdForSelection);
        
        
        final JButton forAll = new JButton(btnTextForAll);
        forAll.setActionCommand(actionCmdForAll);
        forAll.setToolTipText(toolTipTextForAll);
        forAll.addActionListener(app.getActionListener());

        final JButton forSelection = new JButton(btnTextForSelection);
        forSelection.setActionCommand(actionCmdForSelection);
        forSelection.setToolTipText(toolTipTextForSelection);
        forSelection.addActionListener(app.getActionListener());
        
        final JButton forUnchanged = new JButton(btnTextForUnchanged);
        forUnchanged.setActionCommand(actionCmdForUnchanged);
        forUnchanged.setToolTipText(toolTipTextForUnchanged);
        forUnchanged.addActionListener(app.getActionListener());
        
        
        JPanel rv = new JPanel(new BorderLayout());
        rv.add(textField, BorderLayout.CENTER);
        rv.setBorder(BorderFactory.createTitledBorder(borderText));
        rv.add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {{
                    add(forAll);
                    add(forSelection);
                    add(forUnchanged);
               }}, 
               BorderLayout.SOUTH);
        
        return rv;
    }


    @Override
    public void persistSettings(IRenamerConfiguration cfg) {}
}