package com.myapp.util.soundsorter.wizard.model;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.myapp.util.format.Util;

/**
 * backs the metadata for a song list. a set of named propertySpreads are fed
 * with the added songs and can be requested.
 * 
 * @author andre
 * 
 */
public class SongListMeta implements Serializable 
{
    private static final long serialVersionUID = -1511291893491085954L;
    
    public static final String ARTIST = "artist";
    public static final String GENRE = "genre";
    
    private static final String NL = System.getProperty("line.separator");
    private static final int TO_STRING_MAX_ENTRIES = 10;
    
    private File physicalLocation;
    private String lookupPath;
    private int songCount;
    private Map<String, PropertySpread> properties = new TreeMap<String, PropertySpread>();
    
    private transient String cachedToString = null;
    private transient String cachedHTMLString = null;
    
    
    /**
     * creates a new SongListMetadata using the given lookup object to get
     * metadata from
     */
    public SongListMeta() {
        PropertySpread artistSpread = new PropertySpread(
            ARTIST,
            new ArtistNameExtractor()
        );
        
        PropertySpread genreSpread = new PropertySpread(
            GENRE,
            new GenreExtractor()
        );

        properties.put(ARTIST, artistSpread);
        properties.put(GENRE, genreSpread);
    }
    
    /**
     * creates a new SongListMetadata using the given lookup object to get
     * metadata from, and applies to the given directory using the given lookup
     * path.
     * @param metaDataLookup
     * @param lookupPath
     * @param physicalLocation
     */
    public SongListMeta(IMetaDataSource metaDataLookup, 
                            String lookupPath, 
                            File physicalLocation) {
        this();
        applyToDirectory(lookupPath, physicalLocation, metaDataLookup);
    }
    

    /**
     * get all the songs in the directory using the lookup, and calculate the
     * metadata for them.
     * @param pLookupPath
     * @param pPhysicalLocation
     */
    public void applyToDirectory(String pLookupPath,
                                 File pPhysicalLocation,
                                 IMetaDataSource metaDataLookup) {
        this.physicalLocation = pPhysicalLocation;
        this.lookupPath = pLookupPath;
        Collection<ISong> songsInDir = metaDataLookup.getSongsInDirectory(lookupPath);
        songCount = songsInDir.size();
        cachedToString = null;
        cachedHTMLString = null;
        
        for (Iterator<PropertySpread> i = properties.values().iterator(); i.hasNext();) {
            PropertySpread next = i.next();
            next.applyToSongList(songsInDir);
        }
    }
    
    /**
     * @return
     * @see java.util.Map#keySet()
     */
    public Set<String> getSpreadFlavors() {
        return new HashSet<String>(properties.keySet());
    }

    public PropertySpread getSpread(String flavour) {
        return properties.get(flavour);
    }
    
    public int getSongCount() {
        return songCount;
    }

    public String getLookupPath() {
        return lookupPath;
    }
    
    public File getPhysicalLocation() {
        return physicalLocation;
    }
    
    /**
     * compares a specific metadata facet of another songlist with the metadata
     * of this songlist. the metadata will be compared with fuzzy logic.
     * 
     * @param other
     * @param propertyName the property to calculate the difference for
     * @return a double between 0 and 1, 0 means no common entries, 1 means perfect match
     */
    public MatchSet calcFuzzyEquality(SongListMeta other, String propertyName) {
        PropertySpread here  = this.properties.get(propertyName);
        PropertySpread there = other.properties.get(propertyName);
        return here.calcFuzzyEquality(there);
    }
    
    public MatchResult calcFuzzyEquality(SongListMeta other) {
        return new MatchResult(this, other);
    }
    
    public Set<String> getCommonFlavours(SongListMeta other) {
        return getCommonFlavours(this, other);
    }
    
    public static Set<String> getCommonFlavours(SongListMeta m1, SongListMeta m2) {
        return Util.getCommonElements(m1.getSpreadFlavors(), m2.getSpreadFlavors());
    }

    ///////////////// toString impl ///////////////////
    
    
    @Override
    public String toString() {
        if (cachedToString == null) {
            synchronized (this) {
                if (cachedToString == null)
                    cachedToString = toString(TO_STRING_MAX_ENTRIES);
            }
        }
            
        return cachedToString;
    }
    
    public String toString(int linesMax) {
        StringBuilder sb = new StringBuilder();
        sb.append("location: ");
        sb.append(getPhysicalLocation().getAbsolutePath());
        sb.append(" (files: ");
        sb.append(songCount);
        sb.append(")");
        sb.append(NL);
        
        for (Iterator<String> itr = properties.keySet().iterator(); itr.hasNext();) {
            String next = itr.next();
            appendProperty(linesMax, next, sb);
        }
        
        return sb.toString();
    }

    private void appendProperty(int linesMax, String propertyName, StringBuilder sb) {
        PropertySpread spread  = properties.get(propertyName);
        sb.append(NL);
        sb.append("PROPERTY: ");
        sb.append(propertyName);
        sb.append(" (Values total: ");
        sb.append(spread.getValuesOrdered().size());
        sb.append(")");
        sb.append(NL);
        int step = 0;

        for (Iterator<String> itr = spread.getValuesOrdered().iterator(); itr.hasNext();) {
            if (step++ >= linesMax) {
                break;
            }
            
            String genre = itr.next();
            int cnt = spread.getOccurenceCount(genre);
            double percent = spread.getRatio(genre) * 100;

            sb.append(Util.hackToLength(genre, 25));
            
            if (cnt < 1000) {sb.append(' ');}
            if (cnt < 100)  {sb.append(' ');}
            if (cnt < 10)   {sb.append(' ');}
            
            sb.append(cnt);
            sb.append("   ");
            String relStr = Util.getTwoDigitDoubleString(percent);

            if (percent < 100) {sb.append(' ');}
            if (percent < 10)  {sb.append(' ');}
            
            sb.append(relStr);
            sb.append(" %");
            
            if (itr.hasNext()) {}sb.append(NL);//XXX
        }
    }
    
    public String toHtmlString() {
        if (cachedHTMLString == null) {
            synchronized (this) {
                if (cachedHTMLString == null)
                    cachedHTMLString = toHtmlString(TO_STRING_MAX_ENTRIES);
            }
        }
            
        return cachedHTMLString;
    }
    
    public String toHtmlString(int linesMax) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<pre>location: ");
        sb.append(getPhysicalLocation());
        sb.append("</pre>");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>wert</th>");
        sb.append("<th>anzahl</th>");
        sb.append("<th>anteil</th>");
        sb.append("</tr>");
        
        for (Iterator<String> itr = properties.keySet().iterator(); itr.hasNext();) {
            String next = itr.next();
            appendHtmlProperty(linesMax, next, sb);
        }

        sb.append("</table>");
        sb.append("</html>");
        return sb.toString();
    }

    private void appendHtmlProperty(int linesMax, String propertyName, StringBuilder sb) {
        PropertySpread spread  = properties.get(propertyName);
        sb.append("property: ");
        sb.append(propertyName);
        sb.append(" (values total: ");
        sb.append(spread.getValuesOrdered().size());
        sb.append(")");
        sb.append(NL);
        int step = 0;
        
        sb.append("<tr>");
        sb.append("<td colspan='3'> property:&nbsp;");
        sb.append(propertyName);
        sb.append("</td>");
        sb.append("</tr>");

        for (Iterator<String> itr = spread.getValuesOrdered().iterator(); itr.hasNext();) {
            if (step++ >= linesMax) {
                break;
            }
            
            String genre = itr.next();
            int cnt = spread.getOccurenceCount(genre);
            double percent = spread.getRatio(genre) * 100;
            
            sb.append("<tr>");
            sb.append("<td>").append(genre).append("</td>");
            sb.append("<td>").append(cnt).append("</td>");
            sb.append("<td>").append(Util.getTwoDigitDoubleString(percent)).append(" %</td>");
            sb.append("</tr>");
            
            if (itr.hasNext()) {sb.append(NL);}
        }
    }

    
    public String calcDiffString(SongListMeta other) {
        return new MatchFormatter(this, other).toString();
    }
    
    
    
    
    
    
    
    
    
    /**a dummy implementation used for testing, does not have any actions.*/
    public static final SongListMeta DUMMY = new SongListMeta() {
        private static final long serialVersionUID = -8623624422791346694L;
        public void applyToDirectory(String pLookupPath, File pPhysicalLocationFile, IMetaDataSource metaDataLookup) {}
        public int getSongCount() {return 0;}
        public String getLookupPath() {return "dummy_getLookupPath_"+hashCode();}
        public File getPhysicalLocation() {return new File("/tmp/dummy_getPhysicalLocation");}
        public String toString() {return "dummy_toString_"+hashCode();}
        public String toHtmlString() {return "dummy_getHtmlString_"+hashCode();}
        public String toString(int linesMax) {return "dummy_toString_"+hashCode();}
        public String toHtmlString(int linesMax) {return "dummy_getHtmlString_"+hashCode();}
        public MatchSet calcFuzzyEquality(SongListMeta other, String propertyName) {return MatchSet.FAIL;}
    };
}
