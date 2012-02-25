package com.myapp.util.file;


import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.myapp.util.file.FileGlobber.IErrorHandler.ErrorType;


public class FileGlobber {


    public static interface IErrorHandler {
        
        public static enum ErrorType {
            NOT_A_DIRECTORY("Not a directory"),
            NO_SUCH_FILE("No such file"),
            PERMISSION("Cannot read directory");
            
            private final String message;
            private ErrorType(String message) {this.message = message;}
            public String getMessage() {return message;}
        }
        
        void handleError(ErrorType eventCode, String filePath);
    }
    
    static final class StandardErrorErrorHandler implements IErrorHandler {
        @Override
        public void handleError(ErrorType eventCode, String filePath) {
            System.err.println("WARNING: '"+filePath+"' ("+eventCode.getMessage()+")");
        }
    }
    
    static final class SwallowErrorHandler implements IErrorHandler {
        @Override
        public void handleError(ErrorType eventCode, String filePath) {}
    }
    
    
    public static final IErrorHandler DEFAULT_ERROR_HANDLER = new StandardErrorErrorHandler();
    public static final IErrorHandler NULL_ERROR_HANDLER = new SwallowErrorHandler();
    
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String FILE_SEP_LITERAL = Pattern.quote(FILE_SEP);
    private static final String DEFAULT_WILDCARD_SYMBOL = "*";

    
    private final String wildcardSymbol;
    private IErrorHandler errorHandler = DEFAULT_ERROR_HANDLER;
    private Set<String> resultSet = new TreeSet<String>();
    
    
    public FileGlobber(String wildcardSymbol) {
        this.wildcardSymbol = wildcardSymbol;
    }

    public FileGlobber() {
        this(DEFAULT_WILDCARD_SYMBOL);
    }
    
    public static void main(String[] args) {
        FileGlobber g = new FileGlobber();
        Set<String> result = g.expand(args[0]);
        
        for (String s : result) {
            System.out.println(s);
        }
    }
    
    
    public Set<String> expand(final String globExpression) {
        synchronized (resultSet) {
            resultSet.clear();
            recurse(globExpression, "");
            return resultSet;
        }
    }

    public void setErrorHandler(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    private void recurse(String globExpression, String expandedPath) {
        String[] pathElements = globExpression.split(FILE_SEP_LITERAL);
        StringBuilder sb = new StringBuilder(expandedPath);
        
        for (int i = 0; i < pathElements.length; i++) {
            boolean isLast = (i+1 == pathElements.length);
            String element = pathElements[i];
            
            if (! element.contains(wildcardSymbol)) {
                sb.append(element);
                
                if (isLast) {
                    String path = sb.toString();
                    File f = new File(path);
                    if (f.exists()) {
                        resultSet.add(path); /* gotcha */
                    }
                    return;
                }
                
                sb.append(FILE_SEP);
                continue;
            }

            /* so far, we are inside a dir where we will look for matches */
            String remainingExpr = tailPath(i+1, pathElements);
            String currentDirPath = sb.toString();
            traverseDirectory(isLast, element, remainingExpr, currentDirPath);
            
            return;
        }
    }

    private void traverseDirectory(boolean isLastExprElement,
                                   String currentExprElement,
                                   final String remainingExpr, // TODO: maybe we can determine isLast by the remainingExpr value here..
                                   final String currentDirPath) {
        final File currentDir = new File(currentDirPath);
        
        if (! currentDir.exists() || ! currentDir.isDirectory()) {
            return; /* this can never be a hit */
        }
        
        final String[] dirContents = currentDir.list();
        
        if (dirContents == null) {
            errorHandler.handleError(ErrorType.PERMISSION, currentDirPath);
            return;
        }
        
        Boolean remExprHasWildcard = null; 
        Matcher matcher = matcher(currentExprElement);
        
        for (String item : dirContents) {
            if (! matcher.reset(item).matches()) {
                continue; /* this can never be a hit */
            }
            
            String itemPath = currentDirPath + item;
            if (isLastExprElement) {
                resultSet.add(itemPath); /* gotcha */
                continue;
            }
            
            /* so far, we have found a dir matching the expr inside the resolved path*/
            
            remExprHasWildcard = remainingExpr.contains(wildcardSymbol);
            
            if (! remExprHasWildcard) {
                /* we can resolve the path immediately, hence 
                 * the expression not an "expression" any more. 
                 * (reduces number of created expensive filehandles) */
                String fullyResolved = currentDirPath+item+FILE_SEP+remainingExpr;
                File test = new File(fullyResolved);
                if (test.exists()) {
                    resultSet.add(fullyResolved); /* gotcha */
                }
                continue;
            }
            recurse(remainingExpr, itemPath);
        }
    }

    
    private String tailPath(int fromIndex, String[] pathElements) {
        StringBuilder bui = new StringBuilder();
        for (int i = fromIndex; i < pathElements.length; i++) {
            if (i != fromIndex) {
                bui.append(FILE_SEP);
            }
            bui.append(pathElements[i]);
        }
        return bui.toString();
    }

    private Matcher matcher(String element) {
        assert element.contains(wildcardSymbol) : element;
        String regex = computeGlobRegex(element, wildcardSymbol);
        Matcher m = Pattern.compile(regex).matcher("foo");
        return m;
    }

    private static String computeGlobRegex(final String element, final String wildcardSymbol) {
        StringBuilder regex = new StringBuilder();
        
        int from = 0;
        int wildcardIndex = element.indexOf(wildcardSymbol);
        
        while (wildcardIndex >= 0) {
            String literal = element.substring(from, wildcardIndex);
            if (! literal.isEmpty()) {
                regex.append(Pattern.quote(literal));
            }
            regex.append(".*");
            from = wildcardIndex + wildcardSymbol.length();
            wildcardIndex = element.indexOf(wildcardSymbol, from);
        }
        
        String remaining = element.substring(from);
        if (! remaining.isEmpty()) {
            regex.append(Pattern.quote(remaining));
        }
        
        return regex.toString();
    }

}
