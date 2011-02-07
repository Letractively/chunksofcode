package com.myapp;


import java.util.*;
import org.directwebremoting.annotations.*;


/**
 * Data access class for product catalogue.
 * In this implementation, backing store is a simple static map
 *
 * the annotations of this class and its methods are equivalent to:

<create creator="new" javascript="Store">
    <param name="class" value="com.myapp.Store"/>
    <include method="getArticle"/>
    <include method="findArticles"/>
</create>

 * @author andre
 */
@RemoteProxy
public class Store {

    private static Map<String, Article> allItems;

    /**
     * Returns a list of items in the catalogue that have names or
     * descriptions matching the search expression
     *
     * @param expression Text to search item names and descriptions for
     * @return list of all matching items
     */
    @RemoteMethod
    public static List<Article> findArticles( String expression ) {
        List<Article> foundItems = new ArrayList<Article>();
        String[] words = expression.trim().toLowerCase().split( "\\s" );

        for ( Article i : allItems.values() ) {
            String itemStr = i.getId() + " " + i.getDescription() + " " + i.getName();
            itemStr = itemStr.toLowerCase();

            boolean matches = true;

            for ( String s : words )
                if (  ! itemStr.contains( s ) )
                    matches = false;

            if ( matches )
                foundItems.add( i );
        }

        return foundItems;
    }

    @RemoteMethod
    public static Article getArticle( String id ) {
        return allItems.get( id );
    }


    static {
        allItems = new HashMap<String, Article>();
        allItems.put( "id_01", new Article( "id_01", "Barebone Server & Net-PC", "Barebone Shuttle ZUB PF401 Tragetasche f.XPCs silber/schwarz", 1999 ) );
        allItems.put( "id_02", new Article( "id_02", "Gehäuse & Netzteil", "Codegen Midi-Tower Gehäuse 4046-1 350W Front USB weiss", 1999 ) );
        allItems.put( "id_03", new Article( "id_03", "Casemodding", "LED Flash Light  Sharkoon 30cm,  12LEDs blau", 3499 ) );
        allItems.put( "id_04", new Article( "id_04", "Grafikkarten", "AOpen MX4000 V128,GF MX4000,128MB,TVout,Retail", 4999 ) );
        allItems.put( "id_05", new Article( "id_05", "MP3 Player", "Acer USB Stick  128 MB MP3+Radio Flash Stick", 8999 ) );
        allItems.put( "id_06", new Article( "id_06", "Netzwerk Wireless", "Netgear WG311TIS, 802.11g+ 108Mbit Wireless PCI Adapter", 14999 ) );
        allItems.put( "id_07", new Article( "id_07", "TV/Video Karten", "Pinnacle Software Steinberg CUBASIS VST 5 PC, Retail", 19999 ) );
        allItems.put( "id_08", new Article( "id_08", "Remote Connectivity", "Netgear WGT624IS, 802.11g+ DSL-Router + 108Mbit Access Point", 24999 ) );
        allItems.put( "id_09", new Article( "id_09", "Netzwerk Zubehör", "Patchkabel Twisted Pair  20,0m  S/FTP  RJ-45  grau, einzeln", 29999 ) );
        allItems.put( "id_10", new Article( "id_10", "Controller und I/O-Devices", "Silicom EtherSerial PC Card LAN 10Base-T/BNC +RS-232 seriell", 4499 ) );
        allItems.put( "id_11", new Article( "id_11", "Kühler", "Thermaltake Kühler Für K8 Socket 754/940 Athlon64 und Opt.", 5999 ) );
        allItems.put( "id_12", new Article( "id_12", "Drucker HP Multifunktion", "  DRU MF Tinte HP Office Jet 5510/Kop./Scan/Fax", 7999 ) );
        allItems.put( "id_13", new Article( "id_13", "DIMM PC Kompatibel", "DIMM SD 128MB komp. / HP DJ 500/PS", 9999 ) );
        allItems.put( "id_14", new Article( "id_14", "Drucker HP Großformat", "DRU Plotter HP Designjet 800PS A1  61cm (Halbpalette)", 11999 ) );
    }
}
