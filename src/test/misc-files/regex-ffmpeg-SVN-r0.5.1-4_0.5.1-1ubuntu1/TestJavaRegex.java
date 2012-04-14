import java.util.*;
import java.util.regex.*;
import java.io.*;


public class TestJavaRegex {
    
    
    private static final String NL = System.getProperty("line.separator");
    
    
    
    private static final String pattern = 
        "    Video:  \\s*                                               "+NL
        
        +"   (                         # GROUP 1: video codec           "+NL
        +"      [a-zA-Z0-9]+                                            "+NL
        +"      (?:  \\s+  \\/  \\s+  0x[0-9]+  )  ?                    "+NL
        +"   )                                                          "+NL
                                                                        
        +"   \\s*  ,?  \\s*                                             "+NL
                                                                        
        +"   (                         # GROUP 2: videoformat           "+NL
        +"      [a-zA-Z0-9]+                                            "+NL
        +"   )  ?                                                       "+NL
                                                                        
        +"   \\s*  ,?  \\s*                                             "+NL
                                                                        
        +"   (                         # GROUP 3: height and width      "+NL
        +"      [0-9]+  x  [0-9]+                                       "+NL
        +"   )                                                          "+NL
                                                                        
        +"   \\s*  ,?  \\s*                                             "+NL
        +"   .*?                                                        "+NL 
                                                                        
        +"                                                              "+NL
        +"   (                      # GROUP 4: video  bitrate           "+NL
        +"          [0-9]+ (?:  \\.  [0-9]+ ) ?                         "+NL
        +"          (?= \\s+   kb\\/s )                                 "+NL
        +"   )  ?                                                       "+NL
                                                                        
        +"   .*?                                                        "+NL 
                                                                        
        +"   (                         # GROUP 5:  framerate            "+NL
        +"      [0-9]+ (?:  \\.  [0-9]+  | k ) ? \\s+  (?= tbr )        "+NL
        +"   )                                                         "+NL;      

    private static final int flags = Pattern.COMMENTS;


    

    public static void main(String[] args) throws Exception {
//        processMultilined();
        processSingleLined();
    }
    
    private static void processSingleLined() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Matcher matcher = Pattern.compile(pattern, flags).matcher("not yet set");
        StringBuilder b = new StringBuilder();

        for (String line = null; (line = br.readLine()) != null;) {
            if (matcher.reset(line).find()) {
                b.setLength(0);
                
                { /* ******** print matching lines *********/
//                    b.append(line);
//                    b.append(NL);
                }
                
                { /* ******** print all groups: *********/
                    
//                    for (int i = 1, gc = matcher.groupCount(); i <= gc; i++) {
//                        String group = matcher.group(i);
//                        if (group == null)
//                            group = "------";
//                        b.append(group);
//                        
//                        for (int p = 25 - group.length();
//                             p-- >= 0; 
//                             b.append(' '));
//                    }
                }
                
                { /* ******** print one group: *********/
                    
                    String group = matcher.group(4);
                    if (group == null) 
                    {
//                        group = "------";
//                        b.append(line).append(NL);
                        b.append(group);
                        
//                        for (int p = 25 - group.length();
//                             p-- >= 0; 
//                             b.append(' '));
                    }
                }

//                b.append(NL);
                System.out.println(b.toString());
                
            } else { /* ******** print non-matching lines: *********/ 
                // System.out.println("nothing found: "+line);
            }
        }
    }
    
    private static void processMultilined() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder wholeInput = new StringBuilder();

        for (String line = null; (line = br.readLine()) != null;) {
            wholeInput.append(line).append(NL);
        }
        
        Matcher matcher = Pattern.compile(pattern, flags).matcher(wholeInput);
        
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
