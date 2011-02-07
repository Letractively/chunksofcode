package com.myapp.util.soundsorter.wizard.model;

import static com.myapp.util.format.Util.hackToLength;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.myapp.util.format.Util;


public final class MatchFormatter {

    private static String NL = System.getProperty("line.separator");
    
    private SongListMeta right;
    private SongListMeta left;

    private StringBuilder builder = null;
    private String currentFlavour = null;
    private boolean headerWritten = false;
    
    
    public MatchFormatter(SongListMeta left, SongListMeta right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        builder = new StringBuilder();
        buildString();
        return builder.toString();
    }

    private void buildString() {
        List<String> commonFlavours = new ArrayList<String>(left.getCommonFlavours(right));
        
        if (commonFlavours.isEmpty()) {
            builder.append("-");
            return;
        }
        
        Collections.sort(commonFlavours, String.CASE_INSENSITIVE_ORDER);
        
        for (String flavour : commonFlavours) {
            currentFlavour = flavour;
            appendFlavourMatches();
        }       
    }

    private void appendFlavourMatches() {
        MatchSet matchSet = left.calcFuzzyEquality(right, currentFlavour);
        String valueLeft, valueRight, rateStr;
        boolean firstEqualityWritten = false;
        
        for (Match m : matchSet) {
            double percent = m.getMatchRate() * 100;
            
            if (percent < 1d) {
                continue;
            }
            
            if ( ! firstEqualityWritten) {
                if ( ! headerWritten) {
                    writeHeader();
                    headerWritten = true;
                }
                
                builder.append("----- Matching type: ");
                builder.append(currentFlavour);
                builder.append(" -----");
                builder.append(NL);
                firstEqualityWritten = true;
            }
            
            valueLeft = m.getValue1();
            valueRight = m.getValue2();
            rateStr = Util.getNDigitsDoubleString(percent, 2) + " %";
            builder.append(hackToLength(valueLeft, 40));
            builder.append(" <--  "+hackToLength(rateStr, 9)+"  -->   ");
            builder.append(hackToLength(valueRight, 40));
            builder.append(NL);
        }
    }
    
    private void writeHeader() {
        builder.append(hackToLength(left.getPhysicalLocation().getName(), 40));
        builder.append(" <- items matched ->   ");
        builder.append(hackToLength(right.getPhysicalLocation().getName(), 40));
        builder.append(NL);
    }
}
