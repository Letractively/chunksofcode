package com.myapp.util.multigrep;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.myapp.util.log.unixcolors.UnixStringColorizer;

class SearchResult {

    private List<File> hits = new ArrayList<File>();
    private List<File> hitsReadonly = Collections.unmodifiableList(hits);
    private Map<File, Map<String, List<Highlight>>> high = new TreeMap<File, Map<String, List<Highlight>>>(Grep.FILE_CMP);
    private Map<File, Map<String, List<Highlight>>> highReadonly = Collections.unmodifiableMap(high);


    SearchResult() {
    }

    public void addHit(File hit, Map<String, List<Highlight>> highlights) {
        assert !hits.contains(hit) : hit + " already in " + hits;
        hits.add(hit);

        if (highlights != null) {
            high.put(hit, highlights);
        }
    }

    public void fixateSearch() {
        hits = hitsReadonly;
        high = highReadonly;
    }

    public List<File> getHits() {
        return hitsReadonly;
    }

    public Map<File, Map<String, List<Highlight>>> getHigh() {
        return highReadonly;
    }

}

class Highlight {

    private int lineNumber;
    private String line;
    private String regex;
    private String match;
    private String prefix;
    private String suffix;

    public Highlight(String regex,
                     String line,
                     int lineNumber,
                     String match,
                     String prefix,
                     String suffix) {
        this.regex = regex;
        this.line = line;
        this.lineNumber = lineNumber;
        this.match = match;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getLine() {
        return line;
    }

    public String getRegex() {
        return regex;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMatch() {
        return match;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getMatchString(boolean colored) {
        StringBuilder bui = new StringBuilder();
        String lineNum = Integer.toString(lineNumber);
        while (lineNum.length() < 5) {
            lineNum = " "+lineNum;
        }
        
        if (colored) {
            bui.append(UnixStringColorizer.paintGreen(lineNum));
        } else {
            bui.append(lineNum);
        }
        bui.append(": ");
        bui.append(prefix);

        if (colored) {
            bui.append(UnixStringColorizer.paintRed(match));
        } else {
            bui.append("__");
            bui.append(match);
            bui.append("__");
        }
        
        bui.append(suffix);
        return bui.toString();
    }
}
