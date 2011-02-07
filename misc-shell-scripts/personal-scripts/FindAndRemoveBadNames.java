import java.util.*;
import java.util.regex.*;
import java.io.*;

public class FindAndRemoveBadNames {

    private interface IVisitor<T> {
        void visit(T t);
    }

    private static final FileFilter ACCEPT_ALL = new FileFilter() {
        public boolean accept(File pathname) { return true; }
    };
    
    private static class MyFilter implements FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            String absLc = f.getAbsolutePath().toLowerCase();
            if (absLc.endsWith(".jpeg") || absLc.endsWith(".jpg")) 
                return false;
            return true;
        }
    }
    
    private static final String nl = System.getProperty("line.separator");
    
    private static final Pattern BAD_NAMED_FILES = Pattern.compile(
      "  ^                                         " + 
      "  ( # group 1: parent path incl SEP   #     " + nl +
      "      .+                                    " +    
             File.separator                          +    
      "  )                                         " +    
      "  ( # group 2: name before exclude    #     " + nl +
      "      [^"+File.separator+"]+?               " +    
      "  )                                         " +    
      "  ( # group 3: exclusion pattern      #     " + nl +
      "      [^a-z0-9"+File.separator+"]*          " +
      "      (?: xxx | www | dvd | xvid )          " +
      "      [^a-z0-9"+File.separator+"]*          " +    
      "  )                                         " +    
      "  ( # group 4: name after exclude     #     " + nl +
      "      [^"+File.separator+"]+                " + 
      "  )                                         " + 
      "  ( # group 5: file suffix            #     " + nl +
      "      \\.                                   " +    
      "      [_a-z0-9]{1,5}                        " + 
      "  )                                         " + 
      "  $                                         " 
      ,Pattern.CASE_INSENSITIVE | Pattern.COMMENTS
    );
    
    
    
    
    public static void main(String[] rootFiles) {
        if (rootFiles == null || rootFiles.length <= 0) { // TODO: remove this debug thing
            rootFiles = new String[] {"/media/warez/porn/videos/"};
        }
        
        SortedMap<String, String> targets = calcNewNames(rootFiles);
        Map<String, List<String>> duplicates = groupKeysByValues(targets);
        Map<String, String> renameReally = uniquifyDuplicates(duplicates);

        for (String s : renameReally.keySet()) {
            System.out.println("rm -v \""+s+".jpeg\"");
        }
        
        int i = 0;
        for (Map.Entry<String, String> e : renameReally.entrySet()) {
            System.out.println("mv -v \""+e.getKey()+"\" \""+e.getValue()+"\"");
            i++;
        }
        
        System.out.println("elements: " + i);
    }
    
    /**
     * collect all files that are badly named. recursive. 
     * @param rootFiles
     * @return a mapping from each file and its corresponding mv target
     */
    private static SortedMap<String, String> calcNewNames(String[] rootFiles) {
        final SortedMap<String, String> newNamesByPath = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        final Matcher matcher = BAD_NAMED_FILES.matcher("nasen");
        
        for (String path : rootFiles) {
            callFileRecursively(new File(path), new MyFilter(), new IVisitor<File>() {
                @Override
                public void visit(File f) {
                    String absPath = f.getAbsolutePath();
                    
                    if (matcher.reset(absPath).matches()) {
                        String trans = matcher.group(1) +  // parent path
                                       matcher.group(2) +  // name before exclusion
                                       matcher.group(5);   // suffix

                        newNamesByPath.put(absPath, trans);
                    }
                }
            });
        }
        
        return newNamesByPath;
    }

    /**
     * returns a reverse mapping view from values to keys.
     * 
     * @param map
     * @return a mapping with a list of keys to each value in the given map.
     *         multiple keys pointing to the same value will be grouped in a
     *         list.
     */
    private static <T> Map<String, List<T>> groupKeysByValues(Map<T, String> map) {
        Set<String> uniqueValues = new HashSet<String>(map.values());
        Map<String, List<T>> keysByValues = new HashMap<String, List<T>>();
        
        for (Map.Entry<T, String> e : map.entrySet()) {
            T originalPath = e.getKey();
            String newPath = e.getValue();
            boolean removed = uniqueValues.remove(newPath);
            List<T> l = null;
            
            if (removed) {
                l = new ArrayList<T>(1);
                keysByValues.put(newPath, l);
                
            } else { // some source file already grabbed this target name!
                l = keysByValues.get(newPath);
            }
            
            l.add(originalPath);
        }
        
        return keysByValues;
    }
    
    /**
     * create a map where each element of the duplicates points to a corrected 
     * version of the key element. 
     * 
     * @param duplicates
     * @return
     */
    private static <T> Map<T, String> uniquifyDuplicates(Map<String, List<T>> duplicates) {
        Map<T, String> correctedTargets = new TreeMap<T, String>();
        
        for (Map.Entry<String, List<T>> e : duplicates.entrySet()) {
            List<T> l = e.getValue();
            
            if (l.size() == 1) {
                correctedTargets.put(l.get(0), e.getKey());
                continue;
            }
            
            for (int j = 0; j < l.size(); j++) {
                Matcher m = Pattern.compile("^(.*)(\\.[_a-z0-9]{1,5})$").matcher(e.getKey());
                
                if ( ! m.find())
                    throw new RuntimeException();
                
                String name =  m.group(1);
                String extension =  m.group(2);
                
                correctedTargets.put(l.get(j), (name +".part"+ j + extension) );
            }
        }
        
        return correctedTargets;
    }

    private static void callFileRecursively(File file, FileFilter filter, IVisitor<File> visitor) {
        if (file.isFile()) {
            visitor.visit(file);
            return;
        }

        if (filter == null) {
            filter = ACCEPT_ALL;
        }
        
        if (file.isDirectory()) {
            for (File child : file.listFiles(filter)) {
                callFileRecursively(child, filter, visitor);
            }
            
            return;
        }

        throw new RuntimeException("file: " + file + ",  ");
    }
}
