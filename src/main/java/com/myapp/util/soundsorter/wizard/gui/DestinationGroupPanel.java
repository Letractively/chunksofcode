package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.myapp.util.soundsorter.wizard.gui.DestinationPanel.Groups;


@SuppressWarnings("serial")
class DestinationGroupPanel extends JPanel 
{
    
    private MainFrame mainFrame;
    private JPanel centerPanel;
    private String groupName;
    
    
    private List<IInitProgressListener> iDestinationInitListeners = new ArrayList<IInitProgressListener>();


    
    public DestinationGroupPanel(MainFrame mainFrame, String groupName) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        this.groupName = groupName;
        centerPanel = new JPanel(new GridLayout(0, 1));
    }
    
    public DestinationGroupPanel(MainFrame mainFrame, Groups group) {
        super(new BorderLayout());
        this.mainFrame = mainFrame;
        this.groupName = group.getLabel();
        centerPanel = new JPanel(new GridLayout(0, group.getCols()));
        
        if (group.getWidth() > 0) {
            setPreferredSize(new Dimension(group.getWidth(), 500));
        }
    }
    
    private void setUpComponents() {
        add(centerPanel, BorderLayout.CENTER);
        Border border = new EmptyBorder(1,1,1,1);
        border = new TitledBorder(border, groupName);
        setBorder(border);
    }

    /**
     * constructs buttons for each file in the group. the
     * DestinationDirControls, which represents a destination dir, will be
     * initialized, (may take a while). registered destinationListeners will
     * be notified after each parsed directory
     * 
     * @param destinationDirs
     *            the dirs to create a list of buttons for
     */
    public void setDestinationDirs(Collection<File> destinationDirs) {
        List<File> dirs = new ArrayList<File>(destinationDirs);
        Collections.sort(dirs);
        Iterator<IInitProgressListener> dilItr;
        
        for (Iterator<File> itr = dirs.iterator(); itr.hasNext();) {
            File nextDir = itr.next();
            DestinationButton ddc = new DestinationButton(mainFrame, nextDir);
            mainFrame.getApplication().addNextDirChosenListener(ddc);
            centerPanel.add(ddc);
            
            dilItr = iDestinationInitListeners.iterator();
            
            while (dilItr.hasNext()) {
                dilItr.next().notifyDirInitialized(nextDir.getAbsolutePath());
            }
        }
        
        setUpComponents();
    }
    
    public void addInitListener(IInitProgressListener i) {
        iDestinationInitListeners.add(i);
    }
}