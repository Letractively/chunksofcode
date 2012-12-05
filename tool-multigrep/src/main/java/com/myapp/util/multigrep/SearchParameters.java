package com.myapp.util.multigrep;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchParameters {

    private static final String PARAM_IGNORE_CASE_SHORT = "-i";
    private static final String PARAM_IGNORE_CASE = "--ignore-case";
    private static final String PARAM_COLORED = "--colored";
    private static final String PARAM_HIGHLIGHT = "--highlight";
    private static final String PARAM_SUFFIX = "--suffix=";
    private static final String PARAM_PREFIX = "--prefix=";
    private static final String PARAM_HITLIMIT = "--hitlimit=";



    public static final Logger log = LoggerFactory.getLogger(SearchParameters.class);
    // where are we looking for?
    private File searchRootDir;

    // what are we looking for?
    private Map<String, Matcher> regexes;

    // do we want to display highlights?
    private boolean highlight = false;

    // how many chars will be displayed before the match; ignored if !highlight.
    private int prefixChars = 65;

    // how many chars will be displayed after the match; ignored if !highlight.
    private int suffixChars = 65;

    // how many highlights per file and regex to keep max; ignored if !highlight.
    private int highLimit = 5;

    private boolean colored = false;

    private boolean globalCaseInsensitive = false;



    public SearchParameters(final String searchRoot,
                            final List<String> regexList,
                            final List<String> optionList) throws Exception {
        log.debug("searchRoot: {}", searchRoot);
        log.debug("optionList: {}", optionList);
        log.debug("regexList: {}", regexList);

        setSearchRootDir(new File(searchRoot));

        for (String o : optionList) {
            if (o.startsWith(PARAM_PREFIX)) {
                String numberString = o.substring(1 + o.indexOf("=")).trim();
                try {
                    int i = Integer.parseInt(numberString);
                    setPrefixChars(i);

                } catch (NumberFormatException e) {
                    throw new RuntimeException("prefix must be a number. you defined: '"
                                               + o + "'");
                }

            } else if (o.startsWith(PARAM_HITLIMIT)) {
                String numberString = o.substring(1 + o.indexOf("=")).trim();
                try {
                    int i = Integer.parseInt(numberString);
                    setHighLimit(i);

                } catch (NumberFormatException e) {
                    throw new RuntimeException("hitlimit must be a number. you defined: '"
                                               + o + "'");
                }

            } else if (o.startsWith(PARAM_SUFFIX)) {
                String numberString = o.substring(1 + o.indexOf("=")).trim();
                try {
                    int i = Integer.parseInt(numberString);
                    setSuffixChars(i);

                } catch (NumberFormatException e) {
                    throw new RuntimeException("suffix must be a number. you defined: '"
                                               + o + "'");
                }

            } else if (o.equals(PARAM_HIGHLIGHT)) {
                setHighlight(true);

            } else if (o.equals(PARAM_COLORED)) {
                setColored(true);

            } else if (o.equals(PARAM_IGNORE_CASE_SHORT)
                       || o.equals(PARAM_IGNORE_CASE)) {
                setGlobalCaseInsensitive(true);

            } else {
                throw new RuntimeException("unknown option:" + o);
            }
        }

        setRegexes(regexList);
    }

    public boolean isHighlight() {
        return highlight;
    }

    public File getSearchRootDir() {
        return searchRootDir;
    }

    public void setSearchRootDir(File searchRootDir) {
        this.searchRootDir = searchRootDir;
    }

    public Map<String, Matcher> getRegexes() {
        return regexes;
    }

    public void setRegexes(List<String> regexList) {
        Map<String, Matcher> tempMap = new LinkedHashMap<String, Matcher>();
        for (String regex : regexList) {
            Pattern pattern;
            if (globalCaseInsensitive) {
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(regex);
            }
            tempMap.put(regex, pattern.matcher("foo"));
        }
        this.regexes = Collections.unmodifiableMap(tempMap);
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public int getPrefixChars() {
        return prefixChars;
    }

    public void setPrefixChars(int prefixChars) {
        this.prefixChars = prefixChars;
    }

    public int getSuffixChars() {
        return suffixChars;
    }

    public void setSuffixChars(int suffixChars) {
        this.suffixChars = suffixChars;
    }

    public int getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(int highLimit) {
        this.highLimit = highLimit;
    }

    public boolean isColored() {
        return colored;
    }

    public void setColored(boolean colored) {
        this.colored = colored;
    }

    public boolean isGlobalCaseInsensitive() {
        return globalCaseInsensitive;
    }

    public void setGlobalCaseInsensitive(boolean globalCaseInsensitive) {
        this.globalCaseInsensitive = globalCaseInsensitive;
    }



    public static SearchParameters parseCommandlineArgs(String[] args)
            throws Exception {
        if (args == null || args.length < 2) {
            throw new RuntimeException("less than 2 args is always invalid: "
                                       + Arrays.toString(args));
        }

        final String searchRoot = args[0];
        final List<String> regexList = new ArrayList<String>();
        final List<String> optionList = new ArrayList<String>();

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                optionList.add(arg);
            } else {
                regexList.add(arg);
            }
        }

        SearchParameters params = new SearchParameters(searchRoot,
                                                       regexList,
                                                       optionList);
        return params;
    }

    public static String getUsageHelp() {
        final String nl = System.getProperty("line.separator");
        return ""
               + nl
               +

               "Usage: multigrep [SEARCH_ROOT] [OPTION]... [PATTERN]..."
               + nl
               +

               "Search for multiple PATTERNS in SEARCH_ROOT, which may be a file or a directory."
               + nl +

               "PATTERN is a Java Regular Expression." + nl +

               "" + nl +

               "Example: multigrep workspace 'void main' 'import\\s+java\\.awt\\.'"
               + nl +

               "" + nl + "Options:" + nl +

               PARAM_IGNORE_CASE_SHORT + " or " + PARAM_IGNORE_CASE + nl
               + "^^ enables the ignore case flag for all regexes" + nl + ""
               + nl +

               PARAM_COLORED + "" + nl + "^^ print the output with colors" + nl
               +

               PARAM_HIGHLIGHT + "" + nl + "^^ print the matches found" + nl +

               PARAM_SUFFIX + "<INTEGER>" + nl
               + "^^ how many chars to print before the match" + nl +

               PARAM_PREFIX + "<INTEGER>" + nl
               + "^^ how many chars to print after the match" + nl +

               PARAM_HITLIMIT + "<INTEGER>" + nl
               + "^^ how many hits to print par regex when highlighting" + nl +

               "";
    }
}
