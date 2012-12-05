package com.myapp.util.soundsorter.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bff.javampd.MPD;

import com.myapp.util.soundsorter.wizard.model.IMetaDataSource;
import com.myapp.util.soundsorter.wizard.model.ISong;
import com.myapp.util.soundsorter.wizard.tool.Application;
import com.myapp.util.soundsorter.wizard.tool.IAudioPlayer;
import com.myapp.util.soundsorter.wizard.tool.INextDirChosenListener;
import com.myapp.util.soundsorter.wizard.tool.MPDAudioPlayer;

@SuppressWarnings("serial")
class PlayerPanel extends JPanel implements INextDirChosenListener ,ActionListener 
{
    static final class PlayerListSelectionListener implements ListSelectionListener 
    {
        private final PlayerPanel _playControl;

        PlayerListSelectionListener(PlayerPanel playControl) {
            this._playControl = playControl;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Object value = this._playControl.getSongList().getSelectedValue();
            
            if (value != null && (value instanceof ISong)) {
                this._playControl.setCurrentSong((ISong) value);
            
            } else {
                this._playControl.setCurrentSong((ISong) null);
            }
        }
    }

    static final class PlayerListCellRenderer implements ListCellRenderer 
    {
        @Override
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel textLbl = new JLabel(String.valueOf(value)); // avoids nullpointer
            textLbl.setFont(PlayerPanel.MONOSPACED);
            
            if (value instanceof ISong) {
                final ISong song = (ISong) value;
                textLbl.setText(
                          index                   + ".) " + 
                          song.getArtist()        + " - " + 
                          song.getTitle()         + " " + 
                          (song.getLength() / 60) + ":" + 
                          (song.getLength() % 60)
                );
                textLbl.setToolTipText("<html><pre>"+song.toString()+"</pre></html>");

            } else {
                textLbl.setToolTipText("<html><font color='red'>Das ist kein MPDSong: "+value+"</font></html>");
            }
            
            if (isSelected) {
                textLbl.setForeground(Color.blue);
            }
            
            return textLbl;
        }
    }
    

    private static final Comparator<ISong> SONG_COMPARATOR = new SongComparator();
    static final Font MONOSPACED = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    private static final Font CURRENT_SONG_TFL_FONT = new Font(Font.DIALOG, Font.PLAIN, 9);
    private static final Random RANDOM = new Random();
    
    private final ListCellRenderer cellRenderer = new PlayerListCellRenderer();
    private final ListSelectionListener listSelectionListener = new PlayerListSelectionListener(this);
    
    private ISong currentSong = null;
    private IAudioPlayer player;
    private DefaultListModel listModel = new DefaultListModel();
    private JList songList = new JList(listModel);
    private JTextField currentSongTfl = new JTextField("currentSongTfl");
    private JCheckBox alwaysRandomCheckbox = new JCheckBox();
    private JButton playBtn;
    private JButton stopBtn;
    private JButton skipBtn;
    private JButton backBtn;
    private JButton nextBtn;
    private JButton rndmBtn;



    
    
    public PlayerPanel(MPD mpd) {
        player = new MPDAudioPlayer(mpd);
        setupComponents();
    }
    
    

    @Override
    public void nextDirChosen(Application app) {
        File currentDir = app.getCurrentUnsortedDir();
        String search = app.getUnsortedLookupPath(currentDir.getAbsolutePath());
        IMetaDataSource metaDataLookup = app.getMatcher();
        
        Collection<ISong> songsColl = metaDataLookup.getSongsInDirectory(search);
        ArrayList<ISong> sortedSongs = new ArrayList<ISong>(songsColl);
        Collections.sort(sortedSongs, SONG_COMPARATOR);
        
        listModel.removeAllElements();
        currentSong = null;
        
        for (Iterator<ISong> itr = sortedSongs.iterator(); itr.hasNext();) {
            listModel.addElement(itr.next());
        }
        
        if (songList.getSelectedIndex() < 0 && listModel.getSize() > 0) {
            songList.setSelectedIndex(0);
        }
        
        if (alwaysRandomCheckbox.isSelected()) {
            playRandomSongRandomPosition();
        }
    }
    
    private void playRandomSongRandomPosition() {
        player.stop();
        if (listModel.isEmpty()) {
            return;
        }
        int randomSongIndex = listModel.getSize() - 1;
        double salt = RANDOM.nextDouble();
        randomSongIndex = Double.valueOf(randomSongIndex * salt).intValue();
        songList.setSelectedIndex(randomSongIndex);

        int randomPosition = currentSong.getLength() - 15; // don't play very end of song
        randomPosition = Math.max(0, randomPosition); // avoid negative positions :-)
        salt = RANDOM.nextDouble();
        randomPosition = Double.valueOf(randomPosition * salt).intValue();
        
        player.play(currentSong.getFile(), randomPosition);
    }
    
    private void setupComponents() {
        songList.setCellRenderer(cellRenderer);
        songList.getSelectionModel().addListSelectionListener(listSelectionListener);
        
        playBtn = new JButton(">");    
        stopBtn = new JButton("||");    
        skipBtn = new JButton(">30");   
        backBtn = new JButton("<10");   
        nextBtn = new JButton(">>");    
        rndmBtn = new JButton("rnd");
        
        Font f = playBtn.getFont();
        f = new Font(f.getName(), f.getStyle(), f.getSize() -1);
        playBtn.setFont(f);
        stopBtn.setFont(f);
        skipBtn.setFont(f);
        backBtn.setFont(f);
        nextBtn.setFont(f);
        rndmBtn.setFont(f);
        
        alwaysRandomCheckbox = new JCheckBox();
        alwaysRandomCheckbox.setSelected(false);
        alwaysRandomCheckbox.setToolTipText("automatisch random ausführen");
        
        playBtn.addActionListener(this);  
        stopBtn.addActionListener(this);  
        skipBtn.addActionListener(this);  
        backBtn.addActionListener(this);  
        nextBtn.addActionListener(this);  
        rndmBtn.addActionListener(this);

        currentSongTfl.setBorder(BorderFactory.createTitledBorder("current Song"));
        currentSongTfl.setEditable(false);
        currentSongTfl.setFont(CURRENT_SONG_TFL_FONT);
        
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonContainer.add(playBtn);
        buttonContainer.add(stopBtn);
        buttonContainer.add(skipBtn);
        buttonContainer.add(backBtn);
        buttonContainer.add(nextBtn);
        buttonContainer.add(rndmBtn);
        buttonContainer.add(alwaysRandomCheckbox);
        
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(currentSongTfl, BorderLayout.CENTER);
        headerContainer.add(buttonContainer, BorderLayout.SOUTH);

        JScrollPane listScroller = new JScrollPane(songList);
        super.setLayout(new BorderLayout());
        super.add(headerContainer, BorderLayout.NORTH);
        super.add(listScroller, BorderLayout.CENTER);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        
        if (src == playBtn) {
            String file = currentSong.getFile();
            player.play(file, 0);
            
        } else if (src == stopBtn) {
            player.stop();
            
        } else if (src == skipBtn) {
            player.jump(30);
            
        } else if (src == backBtn) {
            player.jump(-10);
            
        } else if (src == nextBtn) {
            int index = 1 + listModel.indexOf(currentSong);
            
            if (index >= listModel.getSize()) {
                player.stop();
            }

            songList.setSelectedIndex(index);
            player.play(currentSong.getFile(), 0);
            
        } else if (src == rndmBtn) {
            playRandomSongRandomPosition();
        }
    }
    
    void setCurrentSong(ISong song) {
        currentSong = song;
        
        if (currentSong != null) {
            currentSongTfl.setText(currentSong.getTitle());
            
        } else {
            currentSongTfl.setText("not a song: " + song);
        }
    }
    
    public ISong getCurrentSong() {
        return currentSong;
    }
    
    public JList getSongList() {
        return songList;
    }
}
