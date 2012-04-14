package com.myapp.util.songsorter;

import java.io.File;
import java.util.Arrays;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;

/**
 * @author andre
 * 
 */
public final class Algorithms {

    /**
     * returns the levenshtein distance of two strings<br>
     * Der Levenshtein-Algorithmus (auch Edit-Distanz genannt) errechnet die
     * Mindestanzahl von Editierungsoperationen, die notwendig sind, um eine
     * bestimmte Zeichenkette soweit abzuädern, um eine andere bestimmte
     * Zeichenkette zu erhalten.<br>
     * Die wohl bekannteste Weise die Edit-Distanz zu berechnen erfolgt durch
     * den sogenannten Dynamic-Programming-Ansatz. Dabei wird eine Matrix
     * initialisiert, die für jede (m, N)-Zelle die Levenshtein-Distanz
     * (levenshtein distance) zwischen dem m-Buchstabenpräfix des einen Wortes
     * und des n-Präfix des anderen Wortes enthält.<br>
     * Die Tabelle kann z.B. von der oberen linken Ecke zur untereren rechten
     * Ecke gefüllt werden. Jeder Sprung horizontal oder vertikal entspricht
     * einer Editieroperation (Einfügen bzw. Löschen eines Zeichens) und
     * "kostet" einen bestimmte virtuellen Betrag.<br>
     * Die Kosten werden normalerweise auf 1 für jede der Editieroperationen
     * eingestellt. Der diagonale Sprung kostet 1, wenn die zwei Buchstaben in
     * die Reihe und Spalte nicht bereinstimmen, oder im Falle einer
     * Übereinstimmung 0.<br>
     * Jede Zelle minimiert jeweils die lokalen Kosten. Daher entspricht die
     * Zahl in der untereren rechten Ecke dem Levenshtein-Abstand zwischen den
     * beiden Wörtern.
     * 
     * @param s
     * @param t
     * @return the levenshtein dinstance
     */
    public static int levenshteinDistance(String s, String t) {
        final int sLen = s.length(), tLen = t.length();

        if (sLen == 0)
            return tLen;
        if (tLen == 0)
            return sLen;

        int[] costsPrev = new int[sLen + 1]; // previous cost array, horiz.
        int[] costs = new int[sLen + 1];     // cost array, horizontally
        int[] tmpArr;                        // helper to swap arrays
        int sIndex, tIndex;                  // current s and t index
        int cost;                            // current cost value
        char tIndexChar;                     // char of t at tIndexth pos.

        for (sIndex = 0; sIndex <= sLen; sIndex++)
            costsPrev[sIndex] = sIndex;

        for (tIndex = 1; tIndex <= tLen; tIndex++) {
            tIndexChar = t.charAt(tIndex - 1);
            costs[0] = tIndex;

            for (sIndex = 1; sIndex <= sLen; sIndex++) {
                cost = (s.charAt(sIndex - 1) == tIndexChar) ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, to the
                // diagonally left and to the up +cost
                costs[sIndex] = Math.min(Math.min(costs[sIndex - 1] + 1,
                                                  costsPrev[sIndex] + 1),
                                         costsPrev[sIndex - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            tmpArr = costsPrev;
            costsPrev = costs;
            costs = tmpArr;
        }

        // we just switched costArr and prevCostArr, so prevCostArr now actually
        // has the most recent cost counts
        return costsPrev[sLen];
    }





    /**
     * returns the name of the interpret of a audio file, assuming the interpret is
     * in the file's name separated from the rest by " - ".
     * 
     * @param songFile the file of the song to get the interpret from
     * @return the name of the interpret, or null, if not found
     */
    public static String getInterpretNameFromFile(File songFile) {
        String artist = songFile.getName();
        artist = artist.replace('_', ' ');

        int firstBlankSurroundedDashIndex = artist.indexOf(" - ");

        if (firstBlankSurroundedDashIndex <= 0) {
            return null;
        }

        artist = artist.substring(0, firstBlankSurroundedDashIndex).trim();

        if (artist.length() == 0) {
            return null;
        }

        return artist;
    }
    /**
     * returns the name of the interpret of a id3 tagged audio file
     * 
     * @param songFile
     * @return the name of the interpret, or null, if not found
     */
    public static String readInterpretFromID3Tag(File f) {
        String artist = null;
        String foo = null;

        try {
            for (ID3Tag tag : new MP3File(f).getTags()) {
                if (tag instanceof ID3V1Tag) {
                    ID3V1Tag t = (ID3V1Tag) tag;
                    foo = t.getArtist();
                    if (foo != null) {
                        artist = foo;
                        break;
                    }
                } else if (tag instanceof ID3V2Tag) {
                    ID3V2Tag t = (ID3V2Tag) tag;
                    foo = t.getArtist();
                    if (foo != null) {
                        artist = foo;
                        break;
                    }
                }
            }
        } catch (ID3Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return artist;
    }



    /**
     * creates a pattern from a interpret name, used for comparison and matching
     * 
     * @param fileName
     * @return
     */
    public static String convertInterpretToPattern(String fileName) {
        String[] splitter = fileName.trim().toLowerCase().split(
                                              "[ 0-9\\(\\)&'`\"\\.\\+\\,]{1}");
        Arrays.sort(splitter);
        StringBuilder bui = new StringBuilder();

        for (int i = 0, limit = splitter.length; i < limit; i++) {
            String split = splitter[i].trim();
            if (split.length() <= 1
                || split.equals("the")
                || split.equals("der")
                || split.equals("and")
                || split.equals("das")
             // || split.equals("die") // commented because of the english word
                || split.equals("feat")
                || split.equals("ft")
                || split.equals("vs")) {
                continue;
            }
            bui.append(split);
            bui.append(',');
        }

        if (bui.length() == 0) {
            return null;
        }

        bui.setLength(bui.length() - 1);
        return bui.toString();
    }
}
