package com.myapp.videotools.cli;
//
//import java.io.File;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.myapp.videotools.Configuration;
//
//
//public class AppStartup {
//    
//    private static final String PATH_SEP = System.getProperty("path.separator");
//    private static final String CLASS_PATH_PROPKEY    = "CLASS_PATH";
//    private static final String JAR_FILE_NAME_PROPKEY = "JAR_FILE_NAME";
//    private static final String APP_BASE_PROPKEY      = "APP_BASE";
//    private static final String TARGET_DIR_PROPKEY    = "TARGET_DIR";
//
//    
//    private static String classpath = null;
//    private static String jarFileName = null;
//    private static String targetDir = null;
//    private static String appBaseDir = null;
//
//    private static boolean dbg = false;
//    
//    
//    
//    
//    public static void main(final String[] args) throws Exception {    
//        List<String> argsList = Arrays.asList(args);
//        if (    argsList.contains(Parameters.FLAG_DEBUG_OUTPUT)
//             || argsList.contains(Parameters.FLAG_TRACE_OUTPUT)) {
//            dbg = true;
//        }
//        
//        dbg("current working directory: " + System.getProperty("user.dir"));
//        dbg(argsList.toString());
//        
//        URLClassLoader sysCL = (URLClassLoader) ClassLoader.getSystemClassLoader();
//        Properties props = Configuration.getInstance().getProperties();
//
//        
//        
//
//        ////////////////////////////        
//        // JAR_FILE_NAME
//        ////////////////////////////
//        
//        jarFileName = props.getProperty(JAR_FILE_NAME_PROPKEY);
//        dbg("jarFileName:   " + jarFileName);
//        
//        
//        
//
//        ////////////////////////////
//        // APP_BASE
//        ////////////////////////////
//        
//        appBaseDir = props.getProperty(APP_BASE_PROPKEY);
//        dbg("appBaseDir:    " + appBaseDir);
//
//        
//        
//
//        ////////////////////////////
//        // TARGET_DIR
//        ////////////////////////////
//        
//        targetDir = props.getProperty(TARGET_DIR_PROPKEY);
//        
//        while (targetDir.contains(APP_BASE_PROPKEY)) {
//            targetDir = targetDir.replace(APP_BASE_PROPKEY, appBaseDir);
//        }
//        
//        Matcher m = Pattern.compile("\\{([.a-zA-Z0-9]+)\\}").matcher(targetDir);
//        
//        while (m.find()) {
//            targetDir = targetDir.substring(0, m.start()) +
//                        System.getProperty(m.group(1)) + 
//                        targetDir.substring(m.end(), targetDir.length());
//            m.reset(targetDir);
//        }
//        
//        dbg("targetDir:     " + targetDir);
//        
//        
//        
//        
//        ////////////////////////////
//        // CLASS_PATH
//        ////////////////////////////
//
//        classpath = props.getProperty(CLASS_PATH_PROPKEY);
//        m.reset(classpath);
//        
//        while (m.find()) {
//            classpath = classpath.substring(0, m.start()) +
//                        System.getProperty(m.group(1)) + 
//                        classpath.substring(m.end(), classpath.length());
//            m.reset(classpath);
//        }
//        
//        classpath = classpath.replaceAll(APP_BASE_PROPKEY, Matcher.quoteReplacement(appBaseDir))
//                             .replaceAll(TARGET_DIR_PROPKEY, Matcher.quoteReplacement(targetDir))
//                             .replaceAll(JAR_FILE_NAME_PROPKEY, Matcher.quoteReplacement(jarFileName));
//        dbg("classpath:     " + classpath);
//        
//        
//        
//
//        ////////////////////////////
//        // EXTEND CLASSPATH OF VM:
//        ////////////////////////////
//
//        
//        String[] cpStrParts = classpath.split(PATH_SEP);
//        URL[] urls = new URL[cpStrParts.length];
//
//        for (int i = 0; i < cpStrParts.length; i++) {
//            String cpElem = cpStrParts[i];
//            dbg(cpElem);
//            File cpElemFile = new File(cpElem);
//            URL url = cpElemFile.toURI().toURL();
//            urls[i] = url;
//        }
//
//        
//        // invoke protected method: // FIXME: HACK !!!
//        final Class<?>  urlClClass = URLClassLoader.class;
//        final Class<?>[] parameters = new Class[] {URL.class};
//        final Method method = urlClClass.getDeclaredMethod("addURL", parameters);
//        final boolean accessible = method.isAccessible();
//        
//        if ( ! accessible) {
//            method.setAccessible(true);
//        }
//
//        for (int i = 0; i < cpStrParts.length; i++) {
//            String cpElem = cpStrParts[i];
//            File file = new File(cpElem);
//
//            if (cpElem.endsWith("*")) { // wildcard cp element
//                cpElem = cpElem.substring(0, cpElem.length() - 1);
//                file =  new File(cpElem);
//                
//                if (file.isDirectory()) {
//                    File[] filesUnderWildcard = file.listFiles();
//                    for (int j = 0; j < filesUnderWildcard.length; j++) {
//                        File f = filesUnderWildcard[j];
//                        URL url = f.toURI().toURL();
//                        dbg("adding url to classpath: " + url);
//                        method.invoke(sysCL, new Object[] { url });
//                    }
//                    
//                } else {
//                    throw new RuntimeException("wildcard cp elem not dir: " + cpElem);
//                }
//                
//            } else if (file.exists()) { // single source:
//                if (file.isDirectory()) {
//                    File[] subFiles = file.listFiles();
//                    
//                    for (int j = 0; j < subFiles.length; j++) {
//                        File f = subFiles[j];
//                        URL url = f.toURI().toURL();
//                        dbg("adding url to classpath: " + url);
//                        method.invoke(sysCL, new Object[] { url });
//                    }
//                    
//                } else if (file.isFile()) {
//                    URL url = file.toURI().toURL();
//                    dbg("adding url to classpath: " + url);
//                    method.invoke(sysCL, new Object[] { url });
//                } 
//                
//            } else {
//                throw new RuntimeException("cp elem not existing: " + cpElem);
//            }
//        }
//        
//        method.setAccessible(accessible);
//
//        
//        
//
//        ////////////////////////////
//        // START APPLICATION:
//        ////////////////////////////
//        
//        CommandLineInterface.main(args);
//    }
//    
//
//    
//    private static void dbg(String msg) {
//        if (dbg) {
//            System.out.println(msg);
//        }
//    }
//}
