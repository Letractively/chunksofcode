package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.myapp.util.soundsorter.wizard.model.MatchFormatter;
import com.myapp.util.soundsorter.wizard.model.SongListMeta;
import com.myapp.util.soundsorter.wizard.tool.Application;
import com.myapp.util.soundsorter.wizard.tool.INextDirChosenListener;

@SuppressWarnings("serial")
final class MetaDataDisplay extends JPanel implements INextDirChosenListener 
{
    private JTextArea leftArea = new JTextArea(14,170);
    private JTextArea rightArea = new JTextArea(14,170);
    private JTextArea diffArea = new JTextArea(8,250);
    private SongListMeta currentUnsortedMeta;

    
    public MetaDataDisplay() {
        super(new BorderLayout());
        
        title(leftArea, "Genres Aktuell");
        title(rightArea, "Genres Zielordner");
        title(diffArea, "Gemeinsamkeiten");
        
        Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        leftArea.setFont(monospaced);
        leftArea.setEditable(false);
        rightArea.setFont(monospaced);
        rightArea.setEditable(false);
        diffArea.setFont(monospaced);
        diffArea.setEditable(false);
        
        JPanel leftRightBox = new JPanel(new GridLayout(1, 2));
        leftRightBox.setMinimumSize(new Dimension(200, 240));
        leftRightBox.add(leftArea);
        leftRightBox.add(rightArea);
        
        add(leftRightBox, BorderLayout.CENTER);
        add(diffArea, BorderLayout.SOUTH);
    }
    
    private static void title(JComponent c, String title) {
        c.setBorder(BorderFactory.createTitledBorder(title));
    }

    @Override
    public void nextDirChosen(Application context) {
        File currentUnsortedDir = context.getCurrentUnsortedDir();
        currentUnsortedMeta = context.getGenresOfUnsortedDir(currentUnsortedDir);
        leftArea.setText(currentUnsortedMeta.toString(5));
        System.out.println("UnsortedDirsControl.nextDirChosen()");
    }
    
    // invoked on mouse over from the destinationButton instances
    void showSideBySide(SongListMeta candidate) {
        MatchFormatter dsc;
        dsc = new MatchFormatter(currentUnsortedMeta, candidate);
        diffArea.setText(dsc.toString());
        rightArea.setText(candidate.toString(4));
    }
}
