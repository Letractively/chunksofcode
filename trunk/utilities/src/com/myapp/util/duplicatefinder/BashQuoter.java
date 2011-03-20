package com.myapp.util.duplicatefinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BashQuoter {
    
    private static final Pattern BASH_QUOTE_PATTERN = Pattern.compile(
                                             "(?<!\\\\)( |[&|'$`()\\#!\"])"); 

    private Matcher m = BASH_QUOTE_PATTERN.matcher("foo");
    
    public String quoteForBash(String path) {
        StringBuilder bui = new StringBuilder(path);
        m.reset(path);
        
        while (m.reset(bui).find()) {
            int length = bui.length();
            
            String prefix = bui.substring(0, m.start());
            String toBeQuoted = m.group(1);
            String suffix = bui.substring(m.end(), length);
            
            bui.append(prefix).append('\\').append(toBeQuoted).append(suffix);
            bui.delete(0, length);
        }
        
        return bui.toString();
    }
}