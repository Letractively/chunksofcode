package com.myapp.util.duplicatefinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ScriptGenerator {

    private List<String[]> files;
    private File hashFile;
    private BashQuoter2 quoter = new BashQuoter2();
    private boolean verbose = false;

    public ScriptGenerator(File hashFile) throws IOException {
        this.hashFile = hashFile;
        files = parseHashFile(hashFile);
    }
    
    public void generateCrunchScriptOLD(File scriptOutput, PrintStream logOut) throws IOException {
        logOut.println("# will now GENERATE crunch script to file "+scriptOutput);
        logOut.println("# start: "+Cruncher.now());
        PrintWriter out = new PrintWriter(scriptOutput);
        
        StringBuilder b = new StringBuilder("#!/bin/bash\n\n");
        b.append("function krach() {\n");
        b.append("    echo \"$@\"\n");
        b.append("    exit 1\n");
        b.append("}\n");
        b.append("\n");

        int count = 0;
        final int totalSize = files.size();
        
        for (String[] group : files) {
            b.append("######### handling "+(group.length-1)+" files sharing same hashcode (group "+(count+1)+" / "+totalSize+") #########\n\n");
            handleFileGroup2(b, group);
            
            if (count ++ % 100 == 0) {
                b.append("\n\necho \"files done: "+count+" / "+totalSize+" " + "("+ new Double((double) count / (double) totalSize  * 100d).toString()+" % )\" >&2 ;\n\n");
                out.print(b.toString());
                out.flush();
                b.setLength(0);
            }
        }

        b.append("\necho \"files done TOTAL: "+count+"\"  >&2 ;\n");
        out.print(b.toString());
        out.flush();
        out.close();
        
        logOut.println("# number of items to check for hash collisions: "+count);
        logOut.println("# end:   "+Cruncher.now());
        logOut.println("# done with GENERATING crunch script.");
    }
    
    public void generateCrunchScript(File scriptOutput, PrintStream logOut) throws IOException, InterruptedException {
        logOut.println("# will now GENERATE crunch script to file "+scriptOutput);
        logOut.println("# start: "+Cruncher.now());
        PrintWriter out = new PrintWriter(scriptOutput);
        
        StringBuilder b = new StringBuilder("#!/bin/bash\n\n");
        b.append("function krach() {\n");
        b.append("    echo \"$@\"\n");
        b.append("    exit 1\n");
        b.append("}\n");
        b.append("\n");

        final int lines = Hasher.linesOfFile(hashFile); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(hashFile)));
        final Matcher m = Pattern.compile(
            "^  \\s*  ([a-fA-F0-9]{40})  \\s*  (.*)  \\s*  $", Pattern.COMMENTS
        ).matcher("foo");
        int linesDone = 0, groups = 0;
        String hash, path, currentHash = "";
        List<String> currentPaths = new ArrayList<String>();
        
        for (String line; (line = reader.readLine()) != null; linesDone++) {
            if ( ! m.reset(line).matches())
                throw new RuntimeException("'"+line+"'");
            
            hash = m.group(1).toLowerCase();
            path = m.group(2);

            if (currentHash.equals(hash)) {
                currentPaths.add(path);
                continue;
            }
            
            // hash different than group before here!
            
            // handle files in collected group:
            currentHash = hash;
            int count = currentPaths.size();
            
            if (count > 2) {
                String[] group = currentPaths.toArray(new String[count]);
                b.append("######### handling "+(group.length-1)+" files sharing same hashcode (group "+(count+1)+" / "+lines+") #########\n\n");
                handleFileGroup2(b, group);
                groups++;
                
                if (count ++ % 100 == 0) {
                    b.append("\n\necho \"files done: "+count+" / "+lines+" " + "("+ new Double((double) count / (double) lines  * 100d).toString()+" % )\" >&2 ;\n\n");
                    out.print(b.toString());
                    out.flush();
                    b.setLength(0);
                }
            }

            // start a new group:
            
            currentPaths.clear();
            currentPaths.add(currentHash);
            currentPaths.add(path);
        }
        

        b.append("\necho \"files done TOTAL: "+linesDone+"\"  >&2 ;\n");
        out.print(b.toString());
        out.flush();
        out.close();
        
        double filesPerGroup = linesDone;
        filesPerGroup /= groups;
        
        logOut.println("# number of groups to check for hash collisions: "+groups+" with "+Double.toString(filesPerGroup).replaceFirst("(?<=[.]\\d{2})\\d+", "") +" files per group.");
        logOut.println("# end:   "+Cruncher.now());
        logOut.println("# done with GENERATING crunch script.");
    }
    
    
    private void handleFileGroup2(StringBuilder b, String[] group) {
        int len = group.length;
        String currentHash = group[0]; 
        List<String> quoted = new ArrayList<String>(len);
        
        for (int i = 1; i < len; i++)
            quoted.add(quoter.quoteForBash(group[i]));

        // declare variables for group:
        b.append("SHA1="+currentHash+"\n");
        
        for (int i = 0, n = quoted.size(); i < n; i++)
            b.append("FILE_" + i + "=" + quoted.get(i) + "\n");


        b.append("\n");
        b.append("if [ ");
        int i = 0;
        
        for (Iterator<String> itr = quoted.iterator(); itr.hasNext(); i++) {
            b.append("! -f \"$FILE_").append(i).append("\"");
            itr.next();
            if (itr.hasNext()) b.append(" -o ");
        }
        
        b.append(" ] ; then\n     krach one or more elements is not a regular file. hashcode: $SHA1\nfi\n\n");
        
        final String firstFile = "$FILE_0", vrbs = verbose ? "-v" : "";
        String iThFile;
        
        i = 1;
        for (int n = quoted.size(); i < n; i++) {
            iThFile = "$FILE_"+i;
            b.append("if ( ! diff -q \""+firstFile+"\" \""+iThFile+"\" > /dev/null) ; then\n");
            b.append("     echo HASHCOLLISION $SHA1 \""+firstFile+"\" \""+iThFile+"\"\n");
            b.append("else rm "+vrbs+" \""+iThFile+"\" || krach ERROR WITH rm FILE_"+i+" at $SHA1\n");
            b.append("     ln "+vrbs+" \""+firstFile+"\" \""+iThFile+"\" " +
            		            "|| krach ERROR WITH ln FILE_"+i+" at $SHA1\n");
            b.append("fi\n");
        }
        
        b.append("\n\n");
    }
    
    
    public static List<String[]> parseHashFile(File hashFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(hashFile)));
        List<String[]> files = new ArrayList<String[]>(32*1024);
        
        final Matcher m = Pattern.compile(
            "^  \\s*  ([a-fA-F0-9]{40})  \\s*  (.*)  \\s*  $", Pattern.COMMENTS
        ).matcher("foo");

        
        // put paths of files with the same hashcode in a list 
        // (when 2 or more existing)
        
        String hash, path, currentHash = "";
        List<String> currentPaths = new ArrayList<String>();

        int i = 0;

        for (String line; (line = reader.readLine()) != null; i++) {
            if ( ! m.reset(line).matches())
                throw new RuntimeException("'"+line+"'");
            
            hash = m.group(1).toLowerCase();
            path = m.group(2);

            if (currentHash.equals(hash)) {
                currentPaths.add(path);
                continue;
            }
            
            currentHash = hash;
            int count = currentPaths.size();
            
            if (count > 2) {
                files.add(currentPaths.toArray(new String[count]));
            }

            currentPaths.clear();
            currentPaths.add(currentHash);
            currentPaths.add(path);
            
            if (i % 1000 == 0 && i > 0) {
                System.err.println("\nscript generator : lines done: "+i+"\n");
            }
        }  
        
        return files;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}