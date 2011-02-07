/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.tools.web.httpproxy.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author andre
 */
@SuppressWarnings("unused")
public class JavaScriptCodeFormatter {

    private static final String LITERAL_PREFIX = "LITERAL";
    private static final String LINE_SEPARATOR = 
                                           System.getProperty("line.separator");

    public static String formatJavaScript(String jsCode) {
        List<String> lines = new ArrayList<String>();

        lines.add(jsCode);
        lines = escapeLiterals(lines);
        lines = insertWhitespace(lines);
        lines = breakLines(lines);
        lines = intendLines(lines);
        lines = makeIfForAndWhileNice(lines);
        lines = mergeBackLiterals(lines);
        lines = putLeadingCommasAbove(lines);
        lines = mergeClosingBracesAndClosingParanthesis(lines);
        lines = makeElsesNice(lines);

        return printLineList(lines);
    }
    private static String printLineList(List<String> lines) {
        StringBuilder bui = new StringBuilder();
        for (String string : lines) {
            bui.append(string + LINE_SEPARATOR);
        }

        return bui.toString();
    }

    public static void main(String... a) throws FileNotFoundException,
                                                IOException {

        StringBuilder unformatted = new StringBuilder();
        InputStream in = new FileInputStream(new File(
                "/home/andre/Desktop/jquery-1.3.2.min.js"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = reader.readLine()) != null) {
            unformatted.append(line + LINE_SEPARATOR);
        }

        System.out.println(formatJavaScript(unformatted.toString()));
    }

    private static List<String> makeIfForAndWhileNice(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        Pattern ifForWhile = Pattern.compile("(if|for|while)\\s*\\(");
        
        String line = null;
        for (int i = 0, limit = lines.size(); i < limit; i++) {
            line = lines.get(i);
            Matcher m = ifForWhile.matcher(line);
            
            if (m.find()) {
                line = line.replaceFirst("if|for|while", m.group(1) + " ");
            }

            returnList.add(line);
        }

        return returnList;
    }

    
    private static List<String> putLeadingCommasAbove(List<String> lines) {
        Pattern leadingCommaPattern = Pattern.compile("^\\s*,");
        
        String line = null;
        for (int i = 0, limit = lines.size(); i < limit; i++) {
            line = lines.get(i);
            Matcher m = leadingCommaPattern.matcher(line);
            
            if (m.find()) {
                String prev = lines.get(i - 1);
                lines.set(i - 1, prev + " ,");
                lines.set(i, line.replaceFirst(",\\s*", ""));
            }
        }

        return lines;
    }

    private static List<String> mergeClosingBracesAndClosingParanthesis(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        Pattern leadingClosingParanthesis = Pattern.compile("^\\s*\\).*");
        
        String line = null;
        for (int i = 0, limit = lines.size(); i < limit; i++) {
            line = lines.get(i);
            Matcher m = leadingClosingParanthesis.matcher(line);
            
            if (m.find()) {
                String prev = returnList.get(returnList.size() - 1);
                
                if (prev.trim().endsWith("}")) {
                    returnList.set(returnList.size() - 1, prev + line.trim());
                    continue;
                }
            }

            returnList.add(line);
        }

        return returnList;
    }

    private static List<String> makeElsesNice(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        Pattern ifElsePattern = Pattern.compile("^\\s*else.*");
        
        String line = null;
        for (int i = 0, limit = lines.size(); i < limit; i++) {
            line = lines.get(i);
            Matcher m = ifElsePattern.matcher(line);
            
            if (m.find()) {
                String prev = returnList.get(returnList.size() - 1);
                
                if (prev.trim().endsWith("}")) {
                    returnList.set(returnList.size() - 1, prev + " " + line.trim());
                    continue;
                }
            }

            returnList.add(line);
        }

        return returnList;
    }

    private static List<String> template(List<String> lines) {
        List<String> returnList = new ArrayList<String>();

        for (String line : lines) {
            if (line.startsWith(LITERAL_PREFIX)) {
                returnList.add(line);
                continue;
            }
        }

        return returnList;
    }

    private static List<String> insertWhitespace(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        StringBuilder bui = new StringBuilder();

        Pattern blankBeforeAndAfter =  Pattern.compile(
                        "&&|\\|\\||(\\!|<|>|\\||&|\\+|-)?\\={1,3}|\\?|:|\\+|-|\\*|\\{|\\,|!");
        
        for (String line : lines) {
            if (line.startsWith(LITERAL_PREFIX)) {
                returnList.add(line);
                continue;
            }
            
            Matcher m = blankBeforeAndAfter.matcher(line);
            
            if ( ! m.find()) {
                returnList.add(line);
                continue;
            }

            bui.setLength(0);
            int from = 0;

            do {
                bui.append(line.substring(from, m.start()));
                bui.append(' ');
                bui.append(m.group());
                bui.append(' ');
                from = m.end();
                
            } while (m.find());
            
            bui.append(line.substring(from, line.length()));
            returnList.add(bui.toString());
        }

        return returnList;
    }

    private static List<String> mergeBackLiterals(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        final int linesInOriginal = lines.size();
        int linesInReturnList = 0;
        int literalLength = LITERAL_PREFIX.length();

        for (int i = 0; i < linesInOriginal; i++) {
            final String s = lines.get(i);
            if (s.startsWith(LITERAL_PREFIX)) {
                String lastLine = returnList.get(linesInReturnList - 1);
                returnList.remove(linesInReturnList - 1);
                String literal = s.substring(literalLength);
                String nextLine = lines.get(++i).trim();
                returnList.add(lastLine + " " + literal + " " + nextLine);
            } else {
                returnList.add(s);
                linesInReturnList++;
            }
        }

        return returnList;
    }

    private static List<String> intendLines(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        int intend = 0;
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith(LITERAL_PREFIX)) {
                returnList.add(line);
                continue;
            }
            line = line.trim();
            sb.setLength(0);

            if (line.startsWith("}")) {
                intend--;
                intend = intend < 0 ? 0 : intend;
            }
            for (int i = 0; i < intend; i++) {
                sb.append("    ");
            }
            sb.append(line);

            if (line.endsWith("{"))
                intend++;
            if (line.length() > 0)
                returnList.add(sb.toString());
        }

        return returnList;
    }

    private static List<String> breakLines(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        Matcher m;
        for (final String line : lines) {
            if (line.startsWith(LITERAL_PREFIX)) {
                returnList.add(line);
                continue;
            }

            m = Pattern.compile("\\}((\\s*;)?)", Pattern.COMMENTS).matcher(line);
            if (!m.find()) {
                returnList.add(line);
                continue;
            }

            int start = 0;
            do {
                returnList.add(line.substring(start, m.start()));
                returnList.add(m.group());
                start = m.end();
            } while (m.find());

            if (start < line.length())
                returnList.add(line.substring(start));
        }

        lines.clear();
        for (final String line : returnList) {
            if (line.startsWith(LITERAL_PREFIX)) {
                lines.add(line);
                continue;
            }

            m = Pattern.compile("\\{|;").matcher(line);
            if (!m.find()) {
                lines.add(line);
                continue;
            }

            int start = 0;
            do {
                lines.add(line.substring(start, m.end()));
                start = m.end();
            } while (m.find());

            if (start < line.length())
                lines.add(line.substring(start));
        }

        return lines;
    }

    /** parses the given lines for string literals within quotes or apostrophs
    and regular expression literals. literals will be inserted as new line
    with a prefix to separate them from the rest of the js-code.
    @param lines a list of lines within a js-document
    @return a list of lines, literal lines will be marked as literal lines.
     */
    private static List<String> escapeLiterals(List<String> lines) {
        List<String> returnList = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();

        boolean quote = false;
        boolean apostroph = false;
        boolean regex = false;

        boolean lastWasEscapeSymbol = false;
        boolean regexMayStart = true;

        for (String wholeLine : lines) {
            for (String line : wholeLine.split("\n|\r")) {
                builder.setLength(0);
                for (char c : line.toCharArray()) {

                    if (c == '\"')
                        quote = handleQuoteChar(quote,
                                                apostroph || regex,
                                                lastWasEscapeSymbol,
                                                builder,
                                                returnList);
                    else if (c == '\'')
                        apostroph = handleApostrophChar(apostroph,
                                                        quote || regex,
                                                        lastWasEscapeSymbol,
                                                        builder,
                                                        returnList);
                    else if (c == '/')
                        regex = handleSlashChar(regex,
                                                quote || apostroph,
                                                lastWasEscapeSymbol,
                                                regexMayStart,
                                                builder,
                                                returnList);
                    else {
                        builder.append(c);

                        boolean nonEscBackSlash, insideLiteral;
                        nonEscBackSlash = c == '\\' && !lastWasEscapeSymbol;
                        insideLiteral = quote || apostroph || regex;
                        lastWasEscapeSymbol = nonEscBackSlash && insideLiteral;

                        if (!Character.isWhitespace(c))
                            regexMayStart = isValidRegexLiteralPosition(c);
                    }
                }
                /*the rest of the line:*/
                if (builder.length() > 0)
                    returnList.add(builder.toString().trim());
            }
        }
        return returnList;
    }

    private static boolean handleQuoteChar(boolean quote,
                                           boolean apostrophOrRegex,
                                           boolean lastWasEscapeSymbol,
                                           StringBuilder builder,
                                           List<String> returnList) {
        if (quote)
            if (lastWasEscapeSymbol)
                builder.append('\"');
            else {
                endLiteralSequence(builder, '\"', returnList);
                quote = false;
            }
        else if (apostrophOrRegex)
            builder.append('\"');
        else {
            startLiteralSequence(builder, '\"', returnList);
            quote = true;
        }
        return quote;
    }

    private static boolean handleApostrophChar(boolean apostroph,
                                               boolean quoteOrRegex,
                                               boolean lastWasEscapeSymbol,
                                               StringBuilder builder,
                                               List<String> returnList) {
        if (apostroph)
            if (lastWasEscapeSymbol)
                builder.append('\'');
            else {
                endLiteralSequence(builder, '\'', returnList);
                apostroph = false;
            }
        else if (quoteOrRegex)
            builder.append('\'');
        else {
            startLiteralSequence(builder, '\'', returnList);
            apostroph = true;
        }
        return apostroph;
    }

    private static boolean handleSlashChar(boolean regex,
                                           boolean quoteOrApostroph,
                                           boolean lastWasEscapeSymbol,
                                           boolean regexMayStart,
                                           StringBuilder builder,
                                           List<String> returnList) {
        if (regex)
            if (lastWasEscapeSymbol)
                builder.append('/');
            else {
                endLiteralSequence(builder, '/', returnList);
                regex = false;
            }
        else if (quoteOrApostroph)
            builder.append('/');
        else if (regexMayStart) {
            startLiteralSequence(builder, '/', returnList);
            regex = true;
        } else
            builder.append('/');

        return regex;
    }

    private static void startLiteralSequence(StringBuilder builder,
                                             char c,
                                             List<String> returnList) {
        returnList.add(builder.toString().trim());
        builder.setLength(0);
        builder.append(LITERAL_PREFIX);
        builder.append(c);
    }

    private static void endLiteralSequence(StringBuilder builder,
                                           char c,
                                           List<String> returnList) {
        builder.append(c);
        returnList.add(builder.toString()); /*do not need to trim here*/
        builder.setLength(0);
    }

    /** look if a regex may start after this char
    @param c the NONWHITESPACE char after a regex escape may occur
    @return if a regex may start after this char */
    private static boolean isValidRegexLiteralPosition(char c) {
        switch (c) {
            case '=':
            case '(':
            case '{':
            case '[':
            case ';':
            case ',':
            case ':':
            case '?':
            case '&':
            case '|':
            case '+':
            case '!':
                return true;
        }
        return false;
    }

}
