import java.util.Iterator;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDDatabase;

@SuppressWarnings("unused")
public class MPD_Main {

    private static MPD MPD;
    private static MPDDatabase DB;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        MPD = new MPD("localhost");
        DB = MPD.getMPDDatabase();
        
//        main1(args);
        main2(args);
        
        long end = System.currentTimeMillis();
        System.out.print("time needed: " + (end - start) + " ms");
    }
    
    public static void main2( String[] args) throws Exception {
        Iterator<String> itr = DB.listAllFiles().iterator();
        StringBuilder buffer = new StringBuilder();
        
        for (int counter = 0; itr.hasNext(); counter++) {
            String counterStr = Integer.toString(counter);
            
            for (int i = 8 - counterStr.length(); i-- > 0; buffer.append(' '));
            
            buffer.append(counterStr);
            buffer.append(".  ");
            String file = itr.next();
            buffer.append(file);
            
            for (int i = 30 - file.length(); i-- > 0; buffer.append(' '));
            
            if (counter % 1000 == 0) {
                printAndReset(buffer);
            } else {
                buffer.append("\n");
            }
        }
        
        printAndReset(buffer);
    }
    
    public static void main1(String[] args) throws Exception {
        Iterator<String> itr = DB.listAllArtists().iterator();
        StringBuilder buffer = new StringBuilder();
        
        for (int counter = 0; itr.hasNext(); counter++) {
            String counterStr = Integer.toString(counter);
            
            for (int i = 8 - counterStr.length(); i-- > 0; buffer.append(' '));
            
            buffer.append(counterStr);
            buffer.append(".  ");
            String artist = itr.next();
            buffer.append(artist);
            
            for (int i = 30 - artist.length(); i-- > 0; buffer.append(' '));

//          buffer.append(" - ");
//          Collection<MPDSong> songs = db.searchArtist(artist);
//          buffer.append(songs.size());
//          buffer.append(" songs\n");
            
            if (counter % 1000 == 0) {
                printAndReset(buffer);
            } else {
                buffer.append("\n");
            }
        }

        String version = MPD.getVersion();
        buffer.append("DefaultSongSource.main() version =     " + version).append("\n");
        
        int albumcount = DB.getAlbumCount();
        buffer.append("DefaultSongSource.main() albumcount =  " + albumcount).append("\n");
        
        int songcount = DB.getSongCount();
        buffer.append("DefaultSongSource.main() songcount =   " + songcount).append("\n");
        
        int artistcount = DB.getArtistCount();
        buffer.append("DefaultSongSource.main() artistcount = " + artistcount).append("\n");

        printAndReset(buffer);
    }
    
    public static void printAndReset(StringBuilder bui) {
        System.out.println(bui.toString());
        bui.setLength(0);
    }
}
