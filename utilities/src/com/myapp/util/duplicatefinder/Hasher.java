package com.myapp.util.duplicatefinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Hasher {
    
    private File searchroot;
    private File target;
    private BashQuoter2 quoter = new BashQuoter2();
    private File unsortedHashes;
    private File unsortedSizes;
    private File sortedBySize;
    private File filesWithSizeSibling;
    private boolean verbose = false;
    
    public Hasher(File root) {
        this.searchroot = root;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /**
     * creates a file containing a mapping sha1sum -&gt; absolutepath<br>
     * the file will be sorted by sha1sum alphabetically.
     * <pre>
     * v-------------SHA1SUM------------------v  v------ABSOLUTE PATH----------v
     * d3f664c7f5ea4e70c95792e0dc5ad9e7b275f0a8  /media/sound/folder1/a.mp3
     * d3f690f3184f9e86eaebbd3d045cf270ff8ea11e  /media/sound/bar/foo/adsf/c.mp3
     * d3f690f3184f9e86eaebbd3d045cf270ff8ea11e  /media/sound/d/asdf/mmmmm/i.mp3
     * d3f69de9f6ed20ac41b1ecbda6c008d6cb5c2394  /media/sound/e.mp3
     * </pre>
     * @throws IOException 
     * @throws InterruptedException 
     */
    public synchronized void createHashFile(File hashFileTarget, PrintStream logOut) throws IOException, InterruptedException {
        target = hashFileTarget;
        Matcher m = Pattern.compile("(|[.]\\w{1,5})$").matcher(target.getName());
        
        String name = m.reset().replaceFirst(".withsize-out-of-order$1");  // out of order
        unsortedSizes = new File(target.getParent(), name);
        
        name = m.reset().replaceFirst(".withsize-sorted-by-size$1");  // in order
        sortedBySize = new File(target.getParent(), name);
        
        determineSizes(logOut);
        sort(logOut, unsortedSizes, sortedBySize, true);

        name = m.reset().replaceFirst(".files-with-size-sibling$1");
        filesWithSizeSibling = new File(target.getParent(), name);
        
        determineHashesForEqualSizedFiles(logOut);
        
        name = m.reset().replaceFirst(".byhash-out-of-order$1");  // out of order
        unsortedHashes = new File(hashFileTarget.getParent(), name);

        hash(logOut);
        sort(logOut, unsortedHashes, target, false);
    }
    
    private void determineSizes(PrintStream logOut) throws IOException, InterruptedException {
        String b = "find "+ quoter.quoteForBash(searchroot.getAbsolutePath())+
                   " -type f -exec ls -l {} \";\" "+
                   "| cut -d\" \" -f 5,8-  "+
                   "| tee "+quoter.quoteForBash(unsortedSizes.getAbsolutePath());

        List<String> commands = new ArrayList<String>();
        commands.add("bash");
        commands.add("-c");
        commands.add(b);
        
        logOut.println("# DETERMINE SIZES FOR ALL FILEs in "+searchroot);
        logOut.println("# will now execute: \n# bash -c "+b);
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commands);
        logOut.println("# start: "+Cruncher.now());
        Process lookupSizes = pb.start();
        
        if (verbose) {
            final long start = System.currentTimeMillis();
            long last1000done = start;
            BufferedReader rdr = new BufferedReader(new InputStreamReader(
                                                 lookupSizes.getInputStream()));

            int i = 0;
            
            for (; null != (rdr.readLine()); i++) {
                if (i % 1000 == 0 && i > 0) {
                    long soFar = System.currentTimeMillis();
                    logOut.println("file sizes determided: "+i);
                    logOut.println("    total:    | "+msPerFile(start, i, soFar)+" "+filesPerSec(start, i, soFar)+")");
                    logOut.println("    last 1000:| "+msPerFile(last1000done, 1000, soFar)+" "+filesPerSec(last1000done, 1000, soFar)+")");
                    last1000done = soFar;
                }
            }
            
            long soFar = System.currentTimeMillis();
            logOut.println("DONE! TOTAL FILE SIZES DETERMINED: "+i+". seconds: "+((soFar - start)/1000L));
            logOut.println("average: "+msPerFile(start, i, soFar)+" "+filesPerSec(start, i, soFar));
        }
        
        lookupSizes.waitFor();
        logOut.println("# end:   "+Cruncher.now());
    }
    
    @SuppressWarnings("unused")
    private void determineHashesForEqualSizedFiles(PrintStream logOut) throws IOException, InterruptedException {
        /* how the sorted input file looks like:
        82 /media/datadisk/sound/classic_collection/rock/goth-emo/Hellogoodbye/._02 Here (In Your Arms).m4a
        23416 /media/datadisk/sound/classic_collection/rock/goth-emo/Hellogoodbye/._03 All Time Lows.m4a
        44344 /media/datadisk/sound/classic_collection/rock/goth-emo/Hellogoodbye/._04 Stuck To You.m4a
         */
        FileInputStream fis = new FileInputStream(sortedBySize);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        Collection<String> currentGroup = new HashSet<String>();
        long currentSize = -1;
        Matcher m = Pattern.compile("^\\s*(\\d+)\\s+(.*)\\s*$").matcher("foo");
        
        PrintStream buddyOut = new PrintStream(filesWithSizeSibling);
        
        for (String line = null; (line = br.readLine()) != null; ) {
            m.reset(line);
            
            if (! m.matches())
                throw new RuntimeException("line: " + line);
            
            long size = Long.parseLong(m.group(1));
            
            if (size != currentSize) { // next group starting
                if (currentGroup.size() > 1) { // found a group of files that mathc in size!
                    for (String s : currentGroup) {
                        buddyOut.println(s);
                    }
                }
                currentGroup.clear();
                currentSize = size;
            }

            if (size <= 0)
                continue;
            
            String path = m.group(2);
            currentGroup.add(path);
        }
        
        if (currentGroup.size() > 1) {
            for (String s : currentGroup) {
                buddyOut.println(s);
            }
        }

        buddyOut.flush();
        buddyOut.close();
        br.close();
    }
    
    private void hash(PrintStream logOut) throws IOException, InterruptedException {
        StringBuilder b = new StringBuilder();
        b.append("cat "+quoter.quoteForBash(filesWithSizeSibling.getAbsolutePath()));
        b.append(" | while read f; do sha1sum \"$f\"; done ");
        b.append(" | tee " + quoter.quoteForBash(unsortedHashes.getAbsolutePath()));

        List<String> commands = new ArrayList<String>();
        commands.add("bash");
        commands.add("-c");
        commands.add(b.toString());
        
        logOut.println("# creating hash sums for files in "+filesWithSizeSibling);
        logOut.println("# will now execute: \n# bash -c "+b.toString());
        logOut.println("# this may take a long time.");
        logOut.println("# start: "+Cruncher.now());
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commands);
        Process hash = pb.start();
        
        if (verbose) {
            final int count = linesOfFile(filesWithSizeSibling);
            
            final long start = System.currentTimeMillis();
            long last250done = start;
            BufferedReader rdr = new BufferedReader(new InputStreamReader(hash.getInputStream()));

            int i = 0;
            for (/*String line = null*/; null != (/*line = */rdr.readLine()); i++) {
                // logOut.println(line);
                if (i % 250 == 0 && i > 0) {
                    long soFar = System.currentTimeMillis();
                    logOut.println("files hashed: "+i+" / "+count);
                    logOut.println("    average:  | "+msPerFile(start, i, soFar)+" "+filesPerSec(start, i, soFar)+")");
                    logOut.println("    last 250: | "+msPerFile(last250done, 250, soFar)+" "+filesPerSec(last250done, 250, soFar)+")");
                    last250done = soFar;
                }
            }
            
            long soFar = System.currentTimeMillis();
            logOut.println("DONE! TOTAL hashed: "+i+". seconds: "+((soFar - start)/1000L));
            logOut.println("    average:"+msPerFile(start, i, soFar)+" "+filesPerSec(start, i, soFar)+")");
        }
        
        hash.waitFor();
        logOut.println("# end:   "+Cruncher.now());
    }
    
    private String msPerFile(long start, int i, long end) {
        double ms = end - start;
        return "ms/file: "+Double.toString(ms / i).replaceAll("(?<=[.]\\d\\d)\\d*", "");
    }
    
    private String filesPerSec(long start, int i, long end) {
        double secs = (end - start) / 1000d;
        return "files/sec: "+Double.toString(i / secs).replaceAll("(?<=[.]\\d\\d)\\d*", "");
    }
    
    static int linesOfFile(File f) throws InterruptedException, IOException {
        List<String> commands = new ArrayList<String>();
        commands.add("bash");
        commands.add("-c");
        commands.add("wc -l "+new BashQuoter2().quoteForBash(f.getAbsolutePath()));

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commands);
        Process wc = pb.start();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(wc.getInputStream()));
        String output = null;
        int lineCount = -1;
        
        try {
            output = rdr.readLine();
            Matcher m = Pattern.compile("^\\s*(\\d+)\\s+.*$").matcher(output);
            if (! m.find()) 
                throw new RuntimeException("did not match: "+ m);
            String number = m.group(1);
            lineCount = Integer.parseInt(number);
            
        } catch (Exception e) {
            throw new RuntimeException("error while counting lines of "+f+ " output="+output, e);
        }
        // 25320 /home/andre/Desktop/test-hashfile.byhash-out-of-order.sha1

        wc.waitFor();
        return lineCount;
    }
    
    private static void sort(PrintStream logOut, File unsorted, File sorted, boolean numeric) throws InterruptedException, IOException {
        BashQuoter2 quoter = new BashQuoter2();
        StringBuilder b = new StringBuilder();
        b.append("cat ");
        b.append(quoter.quoteForBash(unsorted.getAbsolutePath()));

        b.append(" | sort");
        
        if (numeric) 
            b.append(" -n");
        
        // write to file:
        b.append(" > "+quoter.quoteForBash(sorted.getAbsolutePath()));

        List<String> commands = new ArrayList<String>();
        commands.add("bash");
        commands.add("-c");
        commands.add(b.toString());

        logOut.println("# sorting file: "+unsorted);
        logOut.println("# will now execute: \n# bash -c "+b);
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commands);
        logOut.println("# start: "+Cruncher.now());
        Process sort = pb.start();
        sort.waitFor();
        logOut.println("# end:   "+Cruncher.now());
    }
}