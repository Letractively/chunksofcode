package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.myapp.util.format.Util;
import com.myapp.util.soundsorter.wizard.model.MatchResult;
import com.myapp.util.soundsorter.wizard.model.SongListMeta;
import com.myapp.util.soundsorter.wizard.tool.Application;
import com.myapp.util.soundsorter.wizard.tool.INextDirChosenListener;

@SuppressWarnings("serial")
final class DestinationButton extends JPanel implements INextDirChosenListener 
{
    
    private static final Font MONOSPACED = new Font(Font.MONOSPACED, Font.PLAIN, 8);
    
    private final SongListMeta meta;

    private JLabel locationLabel;
    private JLabel percentLabel;
    private MainFrame frame;
    private Application app;
    private JPanel background;
    private Color regularColor;
    
    
    
    public DestinationButton(MainFrame mainFrame, File physicalLocation) {
        super(new BorderLayout());
        frame = mainFrame;
        app = mainFrame.getApplication();
        meta = app.getSortedDirMeta(physicalLocation);
        setupComponents();
    }
    
    public void setupComponents() {
        locationLabel = new JLabel();
        locationLabel.setFont(MONOSPACED);
        background = new JPanel(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        String path = meta.getLookupPath().replace("SORTED/", "");
        locationLabel.setText(Util.hackToLength(path, 20));
        locationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        percentLabel = new JLabel();
        percentLabel.setMinimumSize(new Dimension(40, 8));
        percentLabel.setPreferredSize(new Dimension(40, 8));
        percentLabel.setFont(MONOSPACED);
        
        background.add(locationLabel, BorderLayout.CENTER);
        background.add(percentLabel, BorderLayout.EAST);
        background.setBorder(new EmptyBorder(1, 5, 1, 10));

        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                frame.getUnsortedDirsPanel().showSideBySide(meta);
                background.setBackground(regularColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                background.setBackground(regularColor);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
                File src = app.getCurrentUnsortedDir();
                File dst = meta.getPhysicalLocation();
                Integer status = null;
                Exception caught = null;
                
                try {
                    status = app.getActionExecutor().handleFiles(src, dst);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    caught = e1;
                }
                
                if (status == null || status != 0 || caught != null) {
                    String message = "Something went wrong: " + 
                        "Status="+status+" " +
                    	"src="+src+", " +
                    	"dst="+dst+", " +
                    	"caught="+caught;
                    
                    JOptionPane.showMessageDialog(null, 
                         message
                       , "An error occured!"
                       , JOptionPane.ERROR_MESSAGE  
                    );
                }
                
                app.loadNextDir();
            }
        };
        
        for (JComponent c : new JComponent[] {this, background, percentLabel, locationLabel, }) {
            c.addMouseListener(mouseAdapter);
        }
        
        super.add(background);
    }
    
    public SongListMeta getGod() {
        return meta;
    }
    
    
    @Override
    public void nextDirChosen(Application context) {
        SongListMeta currentUnsortedMeta = context.getCurrentUnsortedDirMeta();
        double diffRating;
        MatchResult matchResult = null;
        
        if (meta == SongListMeta.DUMMY) {
            diffRating = 0;
            matchResult = MatchResult.FAIL;
            
        } else {
            matchResult =  meta.calcFuzzyEquality(currentUnsortedMeta);
            diffRating = matchResult.getHighestMatchValue();
        }
        
        Color bg = locationLabel.getBackground(), fg = locationLabel.getForeground();
        
        if (meta.getSongCount() <= 0) {
            if ( ! meta.getPhysicalLocation().exists())
                fg = Color.red;
            else
                fg = Color.orange;
        } else {
            bg = calculateMatchingColor(diffRating);
        }

        regularColor = bg;
        background.setBackground(bg); 
        locationLabel.setForeground(fg); 
        locationLabel.setToolTipText("<html>Ordner "+meta.getPhysicalLocation()+"<br>Passt zu: " + Util.getTwoDigitDoubleString(diffRating*100) +" %</html>");

        if (diffRating >= 0.01) {
            percentLabel.setText(Util.getTwoDigitDoubleString(diffRating*100) +" %");
       
        } else {
            percentLabel.setText(" - ");
        }
    }

    public static Color calculateMatchingColor(double rating) {
        double FACTOR = 2.0d;

        if (rating * FACTOR > 1) {
            rating = 1 / FACTOR;
        }

        //        int red = 255;
        //        int green = 255;
        //        int blue = new Double(255d * (1 - (rating * FACTOR))).intValue();
        int red = new Double(255d * (1 - (rating * FACTOR))).intValue();
        int green = /*new Double(255d * (1 - (rating * FACTOR))).intValue()*/255;
        int blue =  new Double(255d * (1 - (rating * FACTOR))).intValue()/*255*/;
        Color c = new Color(red, green, blue);
        return c;
    }
    
    //    public static void main(String[] args) {
    //        double d = 0.01d;
    //        Color c = calculateMatchingColor(d);
    //        JFrame jf = new JFrame();
    //        jf.getContentPane().setBackground(c);
    //        jf.setVisible(true);
    //        jf.setSize(100,100);
    //        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //        jf.pack();
    //    }
}