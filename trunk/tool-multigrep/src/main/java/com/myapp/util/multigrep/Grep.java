package com.myapp.util.multigrep;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.util.log.unixcolors.UnixStringColorizer;


public class Grep
{

    static final FileCmp FILE_CMP = new FileCmp();

    private static final class FileCmp implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2) {
            return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
        }
    }


    public static final Logger log = LoggerFactory.getLogger(Grep.class);

    private SearchParameters params;
    private SearchResult result;

    private File currFile;

    /** the regexes that were not found yet in the current file. if the
     * regex will find a match, it will be removed from this set. */
    private Set<String> currFileToBeFoundRegexes = new HashSet<String>();

    /** a lifo that stores the last ten lines read of the current file */
    @SuppressWarnings("serial")
    private List<String> currFileLastTenLinesReadLifo = new LinkedList<String>() {
        public boolean add(String e) {
            while (size() >= 10) {
                remove(0);
            }
            return super.add(e);
        }
    };

    private LineNumberReader currFileReader;
    private Map<String, List<Highlight>> currFileHighlights = null;
    private List<String> currFilePreReadLines = new LinkedList<String>();
    private int currFileLineNumber;

    public Grep() {
    }



    public static void main(String[] args) {
        SearchParameters params;
        try {
            params = SearchParameters.parseCommandlineArgs(args);
        } catch (Exception e) {
            System.err.println(SearchParameters.getUsageHelp());
            System.err.println();
            System.err.println(e);
            System.exit(1);
            return;
        }

        Grep grep = new Grep();
        log.debug("highlight: {}", params.isHighlight());
        SearchResult result = grep.search(params);
        System.out.println(result.toString());
    }

    public SearchResult search(SearchParameters params2) {
        synchronized (this) {
            log.debug("starting search...");
            this.params = params2;
            this.result = new SearchResult();
            searchRecursively(params.getSearchRootDir());
            return result;
        }
    }

    private void searchRecursively(File fileOrDir) {
        if (fileOrDir.isFile()) {
            try {
                initStateForFileSearch(fileOrDir);
                searchFile();
            } catch (IOException e) {
                log.error("an error occured while searching in: " + fileOrDir, e);
            }

            saveStateOfLastFileSearch();
            return;
        }

        log.debug("entering directory '{}'", fileOrDir);
        File[] listFiles = fileOrDir.listFiles();
        Arrays.sort(listFiles, FILE_CMP);

        for (File f : listFiles) {
            searchRecursively(f);
        }
    }

    private void saveStateOfLastFileSearch() {
        if (! currFileToBeFoundRegexes.isEmpty()) {
            return;
        }

        // this was a hit, every regex had foud a match
        Map<String, List<Highlight>> highlights = null;

        if (params.isHighlight()) {
            assert currFileHighlights != null;
            highlights = Collections.unmodifiableMap(new LinkedHashMap<String, List<Highlight>>(currFileHighlights));
        }
        result.addHit(currFile, highlights);

        if (! params.isHighlight()) { // just print the file path and return
            System.out.println(currFile.toString());
            log.info(currFile.toString());
            return;
        }

        // -- print the highlights for this file if matched:

        log.info("Matching file(s) in directory: {}", currFile.getParentFile());

        String print = currFile.getName();
        if (params.isColored()) {
            print = UnixStringColorizer.paintGreen(print);
        }
        log.info("File satisfied all search criterias: {}", print);

        Iterator<String> regexes = params.getRegexes().keySet().iterator();

        while (regexes.hasNext()) {
            String regex = regexes.next();

            print = regex;
            if (params.isColored()) {
                print = UnixStringColorizer.paintYellow(print);
            }
            log.info((regexes.hasNext() ? "|" : "`") + "--- regex: '{}'", print);
            List<Highlight> hl = currFileHighlights.get(regex);

            for (Iterator<Highlight> hli = hl.iterator(); hli.hasNext();) {
                Highlight highlight = hli.next();

                String treeString = (regexes.hasNext() ? "|" : " ") + //
                        "  " + (hli.hasNext() ? "|" : "`");
                log.info(treeString + "--- match: '{}'",
                         highlight.getMatchString(params.isColored()));
            }
        }

        System.out.println();
    }


    private void initStateForFileSearch(File file) {
        currFileLineNumber = 0;
        currFile = file;
        currFileLastTenLinesReadLifo.clear();
        currFileToBeFoundRegexes.clear();
        currFileToBeFoundRegexes.addAll(params.getRegexes().keySet());

        if (params.isHighlight()) {
            if (currFileHighlights == null) {
                currFileHighlights = new LinkedHashMap<String, List<Highlight>>();
            } else {
                currFileHighlights.clear();
            }
        } else {
            currFileHighlights = null;
        }
    }

    private void searchFile() throws IOException {
        try {
            currFileReader = new LineNumberReader(new FileReader(currFile));
            String currentLine = currFileReader.readLine();

            while (currentLine != null) {
                searchLine(currentLine);
                currFileLastTenLinesReadLifo.add(currentLine);

                int preReadLinesCount = currFilePreReadLines.size();
                if (preReadLinesCount == 0) {
                    if (currFileReader == null) {
                        // end of file reached and no lines in preRead buffer
                        return;
                    }
                    currentLine = currFileReader.readLine();
                } else {
                    currentLine = currFilePreReadLines.remove(0);
                }

                if (! params.isHighlight() && currFileToBeFoundRegexes.isEmpty()) {
                    log.info(currFile.getAbsolutePath());
                    return;
                }

                currFileLineNumber++;
            }

        } finally {
            savelyCloseCurrFileStream();
        }
    }

    private void searchLine(final String line) {
        final boolean printHighlights = params.isHighlight();
        Iterator<Map.Entry<String, Matcher>> iter = params.getRegexes()
                                                          .entrySet()
                                                          .iterator();

        while (iter.hasNext()) {
            final Entry<String, Matcher> e = iter.next();
            final String regex = e.getKey();
            final boolean alreadyHaveMatch = ! currFileToBeFoundRegexes.contains(regex);

            if (alreadyHaveMatch && ! printHighlights) {
                // we already found a match for this regex, and
                // if we don't wanna highlight, we're done for this
                // regex (in this file).
                continue;
            }

            final Matcher matcher = e.getValue();
            if (matcher.reset(line).find()) {
                if (! alreadyHaveMatch) {
                    // mark regex as matched for this file
                    currFileToBeFoundRegexes.remove(regex);
                }
            } else {
                continue; // nothing found
            }

            if (! printHighlights) {
                continue; // done for this regex in current file
            }


            // --- nice, we have a match and want to store highlights. ---

            List<Highlight> list = currFileHighlights.get(regex);
            if (list != null) {
                if (list.size() >= params.getHighLimit()) {
                    // we have collected enough highlights
                    // for this regex in this file
                    continue;
                }
            } else {
                list = new ArrayList<Highlight>();
                currFileHighlights.put(regex, list);
            }

            // --- build and keep the highlighted string ---
            Highlight highlight = generateHighlight(matcher, line);
            list.add(highlight);
        }
    }

    private Highlight generateHighlight(Matcher matcher, final String line) {
        final String regex = matcher.pattern().pattern();
        final int lineNumber = currFileLineNumber;
        final String match = matcher.group();
        final String prefix = generatePrefix(matcher, line);
        final String suffix = generateSuffix(matcher, line);

        Highlight hl = new Highlight(regex, line, lineNumber, match, prefix, suffix);
        return hl;
    }

    private String generateSuffix(Matcher matcher, final String line) {
        final int suffixLength = params.getSuffixChars();
        StringBuilder tmp = new StringBuilder();
        tmp.append(line.substring(matcher.end()));

        // start with already pre-read lines:
        for (String s : currFilePreReadLines) {
            s = s.trim().replaceAll("\\s+", " ");
            tmp.append(" ");
            tmp.append(s);
            if (tmp.length() >= suffixLength) {
                break;
            }
        }

        if (tmp.length() >= suffixLength) {
            if (tmp.length() > suffixLength) {
                tmp.setLength(suffixLength);
            }
            return tmp.toString();
        }

        String preRead = null;
        boolean readSuccess = false;

        try {
            preRead = currFileReader.readLine();
            readSuccess = true;

            while (preRead != null) {
                currFilePreReadLines.add(preRead);

                tmp.append(" ").append(preRead.trim().replaceAll("\\s+", " "));
                if (tmp.length() >= suffixLength) {
                    break; // read enough for highlight!
                }

                preRead = currFileReader.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (readSuccess && preRead == null) {
                // we read something, and the end of stream was reached
                savelyCloseCurrFileStream();
            }
        }

        if (tmp.length() > suffixLength) {
            tmp.setLength(suffixLength);
        }

        return tmp.toString();
    }

    private String generatePrefix(Matcher matcher, String line) {
        final int prefixLength = params.getPrefixChars();
        StringBuilder tmp = new StringBuilder();

        // insert the prefix of the match of the current line:
        tmp.append(line.substring(0, matcher.start()));

        // read backwards as long as we have enough chars for the prefix.
        // the newest line will be added first, etc.
        int lastIndex = currFileLastTenLinesReadLifo.size() - 1;

        for (int i = lastIndex; i >= 0 && tmp.length() < prefixLength; i--) {
            String ln = currFileLastTenLinesReadLifo.get(i)
                                                    .trim()
                                                    .replaceAll("\\s+", " ")
                    + " ";
            tmp.insert(0, ln);
        }

        if (tmp.length() > prefixLength) {
            int charsToDel = tmp.length() - prefixLength;
            tmp.delete(0, charsToDel);
        }

        return tmp.toString();
    }


    private void savelyCloseCurrFileStream() {
        try {
            if (currFileReader != null) {
                currFileReader.close();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        currFileReader = null;
    }
}
