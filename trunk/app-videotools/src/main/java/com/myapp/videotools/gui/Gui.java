package com.myapp.videotools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.myapp.util.swing.Util;
import com.myapp.util.swing.datechooser.JSpinField;
import com.myapp.videotools.AppStatistics;
import com.myapp.videotools.IVideoThumbnailer;
import com.myapp.videotools.cli.Parameters;

public class Gui {

    private JFrame frame;
    private JPanel cp; // content pane
    
    private JTextField targetPathTextField;
    private JCheckBox recursiveCheckbox;
    private JSpinField rowsSpinner;
    private JSpinField colsSpinner;
    private JSpinField heightSpinner;
    private JSpinField widthSpinner;
    
    
    Gui() {
        frame = new JFrame("videotool");
        cp = new JPanel(new BorderLayout());
        frame.setContentPane(cp);

        targetPathTextField = new JTextField();
        recursiveCheckbox = new JCheckBox();
        rowsSpinner = new JSpinField(0, 100);
        colsSpinner = new JSpinField(0, 100);
        
        setupContents();
    }
    
    
    private void showGui() {
        frame.setPreferredSize(new Dimension(400,400));
        frame.pack();
        Util.centerFrame(frame);
        Util.quitOnClose(frame);
        frame.setVisible(true);
    }
    

    private void setupContents() {
        // options Area
        JPanel optionsArea = new JPanel(new GridLayout(0,1));
        
        
        { // path Area
            JPanel pathArea = new JPanel(new BorderLayout());
            Util.title(targetPathTextField, "video file/directory");
            pathArea.add(targetPathTextField, BorderLayout.CENTER);
            
            JButton chooseButton = new JButton("select source");
            chooseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { choose(); }
            });
            pathArea.add(chooseButton, BorderLayout.EAST);
            optionsArea.add(pathArea);
        }
        
            
        { // recursive checkbox
            recursiveCheckbox.setSelected(true);
            JPanel checkboxArea = new JPanel(new BorderLayout());
            checkboxArea.add(new JLabel("apply on directories (recursive)"),
                                BorderLayout.CENTER);
            checkboxArea.add(recursiveCheckbox, BorderLayout.EAST);
            Util.title(checkboxArea, "target type: directory or file");
            optionsArea.add(checkboxArea);
        }
        { // rows/cols
            JPanel rowColArea = new JPanel(new BorderLayout());
            Util.title(rowColArea, "rows / columns of snapshots to create");
            rowColArea.add(new JLabel("result image will contain r * c thumbs"));
            rowsSpinner.setValue(IVideoThumbnailer.DEFAULT_BIG_PIC_ROWS);
            colsSpinner.setValue(IVideoThumbnailer.DEFAULT_BIG_PIC_COLS);
            rowsSpinner.setMinimumSize(new Dimension(50,2));
            colsSpinner.setMinimumSize(new Dimension(50,2));
            rowsSpinner.setPreferredSize(new Dimension(50,2));
            colsSpinner.setPreferredSize(new Dimension(50,2));
            JPanel spinnersArea = new JPanel(new GridLayout(1, 2));
            spinnersArea.add(rowsSpinner);
            spinnersArea.add(colsSpinner);
            rowColArea.add(spinnersArea, BorderLayout.EAST);
            optionsArea.add(rowColArea);
        }

        { // thumb height/width
            JPanel gridSpecArea = new JPanel(new BorderLayout());
            Util.title(gridSpecArea, "height / width of snapshots to create");
            gridSpecArea.add(new JLabel("snapshots resolution: H x W"));
            heightSpinner = new JSpinField(0, 10000);
            widthSpinner = new JSpinField(0, 10000);
            heightSpinner.setValue(IVideoThumbnailer.DEFAULT_THUMB_HEIGHT);
            widthSpinner.setValue(IVideoThumbnailer.DEFAULT_THUMB_WIDTH);
            heightSpinner.setMinimumSize(new Dimension(50,2));
            widthSpinner.setMinimumSize(new Dimension(50,2));
            heightSpinner.setPreferredSize(new Dimension(50,2));
            widthSpinner.setPreferredSize(new Dimension(50,2));
            JPanel spinnersArea = new JPanel(new GridLayout(1, 2));
            spinnersArea.add(heightSpinner);
            spinnersArea.add(widthSpinner);
            gridSpecArea.add(spinnersArea, BorderLayout.EAST);
            optionsArea.add(gridSpecArea);
        }
        
        
        // start button
        JButton startButton = new JButton("start thumbnail process");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { start(); }
        });
        

        // master layout
        String text = 
        "<html>" +
            "<h2>Videothumbnailer</h2>" +
            "<p>" +
                "use this tool to create thumbnail images for your video files!" +
            "</p><br/><p>" +
                "<small>(c) andre ragg 2011</small>" +
            "</p>" +
        "</html>";
        
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        cp.add(label, BorderLayout.NORTH );
        
        
        JPanel center = new JPanel(new BorderLayout());
        center.add(optionsArea, BorderLayout.NORTH);
        cp.add(center, BorderLayout.CENTER);
        
        
        cp.add(startButton, BorderLayout.SOUTH);
    }
    
    public void choose() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setPreferredSize(new Dimension(500,600));
        Util.clickOnDetailViewButton(chooser);
        String path = targetPathTextField.getText();
        if (recursiveCheckbox.isSelected()) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        
        if (path != null) {
            File file = new File(path);
            if (file.isFile()) {
                chooser.setSelectedFile(file);
            }
        }
        
        int result = chooser.showOpenDialog(frame);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected != null) {
                targetPathTextField.setText(selected.getAbsolutePath());
            }
        }
    }
    
    public void start() {
        List<String> commands = new ArrayList<String>();
        commands.add(Parameters.CMD_CREATE_BIG_PICTURE);
        String path = targetPathTextField.getText();
        
        if (path == null || (path = path.trim()).isEmpty()) {
            errMsg("no target selected!");
            return;
        }
        
        File target = new File(path);
        
        if (target.isFile()) {
            if (recursiveCheckbox.isSelected()) {
                errMsg("when checkbox is set, you must choose a direcory! " +
                       "you selected: '"+path+"'");
                return;
            }
            commands.add(Parameters.PARAM_INPUT_FILE);
            commands.add(path);
            
        } else if (target.isDirectory()) {
            if ( ! recursiveCheckbox.isSelected()) {
                errMsg("when checkbox is unset, you must choose a file! " +
                       "you selected: '"+path+"'");
                return;
            }
            commands.add(Parameters.PARAM_BIG_PIC_ROOT_DIR);
            commands.add(path);
            commands.add(Parameters.FLAG_RECURSIVE);
        
        } else {
            errMsg("invalid path! (neither dir nor file) you selected: '"+path+"'");
        }
        
        System.out.println("commands: "+commands);
        
//        String[] args = commands.toArray(new String[commands.size()]);
//        CommandLineInterface cli = new CommandLineInterface();
//        cli.process(args);
    }
    
    private void errMsg(String msg) {
        JOptionPane.showMessageDialog(
          frame,
          msg,
          "error",
          JOptionPane.ERROR_MESSAGE
        );
    }

    public static void launch() {
        Runnable swingHook = new Runnable() {
            public void run() {
                AppStatistics.getInstance().setApplicationStart();
                
                Gui gui = new Gui();
                gui.showGui();
            }
        };
        
        SwingUtilities.invokeLater(swingHook);
    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}
