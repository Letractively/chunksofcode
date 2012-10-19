package com.myapp.tool.gnomestart.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumnModel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.CyclicBufferAppender;

import com.myapp.tool.gnomestart.Config.GuiSettings;
import com.myapp.tool.gnomestart.DesktopStarter;
import com.myapp.tool.gnomestart.DesktopStarter.IStateChangeListener;
import com.myapp.tool.gnomestart.programstate.IWindowManager;
import com.myapp.tool.gnomestart.programstate.Window;
import com.myapp.util.swing.Util;

@SuppressWarnings("serial")
public class StatusWindow extends JPanel implements IStateChangeListener
{


    // TodoItem
    static final String PRECONDITION = "wait for condition";
    static final String START = "start application";
    static final String WAITFORSTARTUP = "wait for startup";
    static final String LAYOUT = "layout window";

    
    
    private MyTableModel myTableModel;

    private JTable myTable;
    private JTextArea logMessages;
    private JProgressBar progressBar;

    final DesktopStarter model;
    private final PatternLayout logLayout;


    public StatusWindow(DesktopStarter starter) {
        super(new BorderLayout());
        model = starter;
        logLayout = createPatternLayout();
        initComponents();
        notifyStateChanged();
    }

    private void initComponents() {
        myTableModel = new MyTableModel(this);
        myTable = new JTable(myTableModel);

        TableColumnModel columnModel = myTable.getTableHeader().getColumnModel();
        columnModel.getColumn(MyTableModel.INDEX).setPreferredWidth(10);
        columnModel.getColumn(MyTableModel.NAME).setPreferredWidth(70);
        columnModel.getColumn(MyTableModel.TYPE).setPreferredWidth(50);
        columnModel.getColumn(MyTableModel.DESCRIPTION).setPreferredWidth(400);
        columnModel.getColumn(MyTableModel.STATUS).setPreferredWidth(10);

        progressBar = new JProgressBar(0, myTableModel.getRowCount());
        Util.title(progressBar, progressString());
        add(progressBar, BorderLayout.NORTH);

        JScrollPane scrollpane = new JScrollPane(myTable);
        JPanel scrollContainer = new JPanel(new BorderLayout());
        Util.title(scrollContainer, "Work list");
        scrollContainer.add(scrollpane, BorderLayout.CENTER);
        add(scrollContainer, BorderLayout.CENTER);

        logMessages = new JTextArea(10, 200);
        Font bar = new Font(Font.MONOSPACED, Font.PLAIN, logMessages.getFont().getSize());
        logMessages.setFont(bar);
        logMessages.setEditable(false);
        JScrollPane logScroller = new JScrollPane(logMessages);
        JPanel logScrollerContainer = new JPanel(new BorderLayout());
        Util.title(logScrollerContainer, "Log");
        logScrollerContainer.add(logScroller, BorderLayout.CENTER);
        add(logScrollerContainer, BorderLayout.SOUTH);

        GuiSettings gs = model.getConfig().getGuiSettings();
        setPreferredSize(new Dimension(gs.getWindowPreferredWidth(),
                                       gs.getWindowPreferredHeight()));
    }

    private String myWinId = null;

    private void setGuiWindowToForeground() {
        IWindowManager winMgr = model.getConfig().getWinMgr();

        if (myWinId == null) {
            List<Window> windows = winMgr.determineWindowStates();
            String title = model.getConfig().getGuiSettings().getWindowTitle();
            Window w = searchWindowId(title, windows);
            if (w == null) {
                StringBuilder bui = new StringBuilder();
                bui.append("could not find a window titled: ");
                bui.append(title);
                model.getLog().error(bui.toString());
                return;
            }
            myWinId = w.getWinId();
            model.getLog().debug("found my winid: "+myWinId);
        }

        winMgr.setFocusOnWindow(myWinId);
    }

    private static Window searchWindowId(String title, List<Window> windows) {
        for (Window e : windows) {
            String winTitle = e.getWinTitle();
            if (winTitle.contains(title)) {
                return e;
            }
        }
        return null;
    }

    public void notifyStateChanged() {
        progressBar.setValue(myTableModel.getPendingItemCount());
        myTableModel.fireTableDataChanged();
        Util.title(progressBar, "Status: " + progressString());
        final Level configLevel = model.getConfig().getGuiSettings().getLogLevel();

        StringBuilder bui = new StringBuilder();
        CyclicBufferAppender<ILoggingEvent> appender = model.getLogBuffer();

        for (int i = 0, n = appender.getLength(); i < n; i++) {
            ILoggingEvent e = (ILoggingEvent) appender.get(i);
            Level eventLevel = e.getLevel();

            if (eventLevel.isGreaterOrEqual(configLevel)) {
                String msg = logLayout.doLayout(e);
                bui.append(msg);
            }
        }

        logMessages.setText(bui.toString());

        // scroll down:
        logMessages.setCaretPosition(logMessages.getDocument().getLength());
        setGuiWindowToForeground();
    }

    private PatternLayout createPatternLayout() {
        PatternLayout layout = new PatternLayout();
        layout.setContext(model.getLog().getLoggerContext());
        GuiSettings gs = model.getConfig().getGuiSettings();
        if (gs.getLogPattern() != null) {
            layout.setPattern(gs.getLogPattern());
        }
        layout.start();
        return layout;
    }

    private String progressString() {
        return myTableModel.getPendingItemCount() + "/" + myTableModel.getRowCount();
    }

    public static IStateChangeListener createAndShowGui(DesktopStarter starter) {
        JFrame frame = new JFrame(starter.getConfig().getGuiSettings().getWindowTitle());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatusWindow panel = new StatusWindow(starter);
        frame.setContentPane(panel);
        frame.pack();
        Util.centerFrame(frame);
        frame.setVisible(true);
        panel.setGuiWindowToForeground();
        return panel;
    }
}
