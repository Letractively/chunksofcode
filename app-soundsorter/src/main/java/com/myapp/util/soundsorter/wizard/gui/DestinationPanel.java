package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.myapp.util.soundsorter.wizard.model.DestinationTargets;

@SuppressWarnings("serial")
final class DestinationPanel extends JPanel {


    static enum Groups {
        
        INTERPRETS("Interpret Verzeichnisse", 2, -1), 
        MIXES("Mixes und Maxi Alben", 2, -1), 
        SINGLE("einzelne Songs", 2 , -1), 
        UNSORTED("unsortiert", 1, -1);

        private String label;
        private int cols, width;

        private Groups(String label, int cols, int width) {
            this.label = label;
            this.cols = cols;
            this.width = width;
        }
        
        public String getLabel() {
            return label;
        }
        
        public int getCols() {
            return cols;
        }
        
        public int getWidth() {
            return width;
        }
    }
    
    private DestinationGroupPanel interpretDirsCtrl, 
                                    maxiMixDirsCtrl,
                                    singleSongDirsCtrl, 
                                    unsortedDirsCtrl;
    
    
    public DestinationPanel(MainFrame mf) {
        super(new BorderLayout());
        
        DestinationTargets targets = mf.getApplication().getDestinationTargets();
        Collection<File> interpretDirs,maxiMixDirs,singleSongDirs,unsortedDirs;
        
        interpretDirs = targets.getInterpretTargetDirs().values();
        maxiMixDirs = targets.getMaxiMixTargetDirs().values();
        singleSongDirs = targets.getSingleSongTargetDirs().values();
        unsortedDirs = targets.getUnsortedTargetDirs().values();

        InitProgressBar initBar = new InitProgressBar(
                                                      interpretDirs.size()
                                                      + maxiMixDirs.size()
                                                      + singleSongDirs.size()
                                                      + unsortedDirs.size()
                                             );

        interpretDirsCtrl  = new DestinationGroupPanel(mf, Groups.INTERPRETS);
        maxiMixDirsCtrl    = new DestinationGroupPanel(mf, Groups.MIXES);
        singleSongDirsCtrl = new DestinationGroupPanel(mf, Groups.SINGLE);
        unsortedDirsCtrl   = new DestinationGroupPanel(mf, Groups.UNSORTED);

        interpretDirsCtrl .addInitListener(initBar);
        maxiMixDirsCtrl   .addInitListener(initBar);
        singleSongDirsCtrl.addInitListener(initBar);
        unsortedDirsCtrl  .addInitListener(initBar);
        
        interpretDirsCtrl .setDestinationDirs(interpretDirs) ;
        maxiMixDirsCtrl   .setDestinationDirs(maxiMixDirs) ;
        singleSongDirsCtrl.setDestinationDirs(singleSongDirs) ;
        unsortedDirsCtrl  .setDestinationDirs(unsortedDirs) ;

        interpretDirsCtrl .setAlignmentY(TOP_ALIGNMENT);
        maxiMixDirsCtrl   .setAlignmentY(TOP_ALIGNMENT) ;
        singleSongDirsCtrl.setAlignmentY(TOP_ALIGNMENT) ;
        unsortedDirsCtrl  .setAlignmentY(TOP_ALIGNMENT) ;
        
        interpretDirsCtrl .setAlignmentX(LEFT_ALIGNMENT);
        maxiMixDirsCtrl   .setAlignmentX(LEFT_ALIGNMENT) ;
        singleSongDirsCtrl.setAlignmentX(LEFT_ALIGNMENT) ;
        unsortedDirsCtrl  .setAlignmentX(LEFT_ALIGNMENT) ;
        

        JPanel mainPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPane.add(interpretDirsCtrl);
        mainPane.add(maxiMixDirsCtrl);
        mainPane.add(singleSongDirsCtrl);
        mainPane.add(unsortedDirsCtrl);
        JScrollPane scroller = new JScrollPane(mainPane);

        super.add(scroller, BorderLayout.CENTER);
        initBar.exit();
    }
    
    public static void main(String[] args) {
        InitProgressBar b = new InitProgressBar(29);
        
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            
            b.notifyDirInitialized("nasen");
        }
    }
    
}
