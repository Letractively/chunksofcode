package com.myapp.tools.media.renamer.config;

import static com.myapp.tools.media.renamer.config.DefaultConfiguration.defaultPropertiesStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Msg;

/**
 * responsible for reading and writing properties from and to the file
 * system
 */
final class PropertiesIO {

    private static final Logger L = Log.defaultLogger();

    
    /**
     * no instance needed.
     */
    private PropertiesIO() {}


    /**
     * writes a map of string-pairs to the given file. if the file exists, it
     * will be read line for line, replacing only the old values with the new
     * values. so comments are being kept, though only the values are being
     * replaced. <br>
     * if the destination file does not exist, the properties file from the jar
     * will be the template file.
     * 
     * @param customProps
     *            the map containing the custom properties to persist
     * @param destination
     *            the file where the properties will be written to. if there is
     *            such a file existing, the existing file will be read and its
     *            key/value lines will be adapted with the new properties. if
     *            there is no existing file, the default properties file from
     *            the jarfile will be taken as template.
     * @return true if no error occured.
     * @throws IOException
     *             at any io-errors :-P
     */
    static boolean writeProperties(Map<String, String> customProps,
                                   File destination) throws IOException {
        synchronized (PropertiesIO.class) {
            L.info(Msg.msg("PropertiesIO.writeProperties.start"));

            File tempFile = null;
            BufferedInputStream existingCfgInStream = null;
            FileInputStream in = null;
            FileOutputStream out = null;
            PrintStream ps = null;
            FileChannel fromChannel = null, toChannel = null;
            String line = null;

            try {       
                existingCfgInStream = new BufferedInputStream(
                            destination.exists() 
                                    ? new FileInputStream(destination)
                                    : defaultPropertiesStream());

                //write out properties with custom values to tempfile
                tempFile = File.createTempFile("properties-", ".tmp", null);        
                ps = new PrintStream(tempFile);

                while ((line = Utils.readLine(existingCfgInStream)) != null) {
                    String lineReady2write = setupLine(line, customProps);
                    ps.println(lineReady2write);
                }
                    
                //props are now written to tempfile
                //copy tempfile to custom properties file
                
                destination.getParentFile().mkdirs();
                in = new FileInputStream(tempFile);
                out = new FileOutputStream(destination, false);
                
                fromChannel = in.getChannel();
                toChannel = out.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);

                L.info(Msg.msg("PropertiesIO.writeProperties.done")
                             .replace("#file#", destination.getAbsolutePath()));
                return true;

            } finally {
                if (existingCfgInStream != null)
                    existingCfgInStream.close();
                if (ps != null)
                    ps.close();
                if (fromChannel != null)
                    fromChannel.close();
                if (toChannel != null)
                    toChannel.close();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (tempFile != null && tempFile.exists())
                    tempFile.delete();
            }
        }
    }


    /**
     * writes a map of string-pairs to the given file. if the file exists, it
     * will be read line for line, replacing only the old values with the new
     * values. so comments are being kept, though only the values are being
     * replaced. <br>
     * if the destination file does not exist, the properties file from the jar
     * will be the template file.
     * 
     * @param custom
     *            the map containing the custom properties to persist
     * @param path
     *            the file where the properties will be written to
     * @return true if no error occured.
     * @throws IOException
     */
    static boolean write(Map<String, String> custom, String path)
                                                            throws IOException {
        return writeProperties(custom, new File(path));
    }


    /**
     * reads the properties from the given properties file, and parses its
     * values into a simple String map which will be returned. <br>
     * an empty map will be returned if no custom properties file could be
     * located
     * 
     * @param path
     *            the path of the file to read the properties from
     * @return the string string map containing the properties
     * @throws IOException
     */
    static Map<String, String> read(String path) throws IOException {
        return read(new File(path));
    }

    /**
     * reads the properties from the given input stream, and parses its
     * values into a simple String map which will be returned. <br>
     * an empty map will be returned if no custom properties file could be
     * located
     * 
     * @param in
     *          the inputstream to read the properties from
     * @return the string string map containing the properties
     * @throws IOException
     */
    static Map<String, String> read(InputStream in) throws IOException {
        synchronized (PropertiesIO.class) {
            Map<String, String> m = new HashMap<String, String>();
            Properties p = new Properties();
            Reader reader = new InputStreamReader(in, "UTF-8");
            p.load(reader);

            int i = 0;
            String key, value;

            for (Enumeration<Object> e = p.keys(); e.hasMoreElements();) {
                key = (String)e.nextElement();
                value = removeQuotesIfAny(p.getProperty(key));
                m.put(key, value);
                i ++;
            }

            L.info(Msg.msg("PropertiesIO.read.done")
                                .replace("#number#", String.valueOf(i)));
            return m;   
        }
    }


    /**
     * reads the properties from the given properties file, and parses its
     * values into a simple String map which will be returned. <br>
     * an empty map will be returned if no custom properties file could be
     * located
     * 
     * @param file
     *            the file to read the properties from
     * @return the string string map containing the properties
     * @throws IOException
     */
    private static Map<String, String> read(File file) throws IOException {
        if ( ! file.exists())
            return new HashMap<String, String>(0);

        return read(new FileInputStream(file));
    }


    /**
     * Overwrites the configutation file with the given user text.
     * 
     * @param text
     *            the text an user has set as new config
     * @return the name of the backupfile which is made every time
     * @throws IOException
     *             if an ioerror occured
     */
    static String overWrite(String text) throws IOException {
        File cfgFile = new File(DefaultConfiguration.getCustomConfigPath());

        String backupPath = cfgFile.getPath() + ".backup";
        File backupFile = null;

        for (int i = 1; ; i++) {
            String tmp = backupPath;

            if (i < 100)
                if (i < 10)
                    tmp += "00";
                else
                    tmp += "0";

            backupFile = new File(tmp + i);

            if ( ! backupFile.exists()) break;
        }

        
        L.info(Msg.msg("PropertiesIO.overWrite.backingUp")
                    .replace("#orig#", cfgFile.getAbsolutePath())
                    .replace("#backup#", backupFile.getAbsolutePath()));

        PrintWriter pw = new PrintWriter(backupFile);
        BufferedReader br = new BufferedReader(new FileReader(cfgFile));
        String line = null;

        while (null != (line = br.readLine())) pw.println(line);

        pw.close();
        br.close();

        L.info(Msg.msg("PropertiesIO.overWrite.backedUp"));



        L.info(Msg.msg("PropertiesIO.overWrite.writeNewConfig")
                            .replace("#file#", cfgFile.getAbsolutePath()));
        
        pw = new PrintWriter(cfgFile);
        pw.print(text);
        pw.close();

        L.info(Msg.msg("PropertiesIO.overWrite.newConfigWritten"));

        return backupFile.getAbsolutePath();
    }

    /**
     * converts a line of an properties file to match the given props obj. if
     * the line represents a key / value pari in the proper syntax, AND its
     * value is being altered by the given map, the value will be replaced.
     * 
     * value strings are always being enclosed by double quotes before
     * persisting, to keep leading and trailing whitespace explicit.
     * 
     * @param line
     *            the line which will be altered by the new settings.
     * @param custom
     *            the custom properties map. the values in this map will
     *            override the existing values, if present.
     * @return the adapted string, if necerssary.
     * @throws IllegalStateException
     */
    private static String setupLine(String line, Map<String, String> custom)
                                                  throws IllegalStateException {
        if (line == null)
            return null;

        line = line.trim();

        if (line.length() <= 0 || line.startsWith("#"))
            return line;

        String[] parts = line.trim().split("=");

        if (parts.length != 2) 
            throw new IllegalStateException("invalid property syntax: " + line);

        String key = parts[0].trim();
        String value = null;

        if (custom != null)
            value = custom.get(key);
        
        if (value == null)
            // FIXME: do not refer to defaultconfiguration explicitly here!
            value = DefaultConfiguration.getDefaultValue0(key);
        
        if (value == null) 
            throw new IllegalStateException("unknown key in line: " + line);
        
        value = addQuotesIfNone(value);

        return key + "=" + value;
    }


    /**
     * removes enclosing quotes from a string, if and only if the string
     * starts and ends with a doublequote &quot; character
     * 
     * @param val
     *            the string to be "trimmed"
     * @return the "trimmed" string without leading and trailing
     *         doublequotes
     */
    private static String removeQuotesIfAny(String val) {
        if (val.startsWith("\"") && val.endsWith("\""))
            return val.substring(1, val.length() - 1);

        return val;
    }

    /**
     * adds leading and trailing doublequotes to a string
     * 
     * @param val
     *            the string to be enclosed by doublequotes
     * @return the string enclosed by doublequotes
     */
    private static String addQuotesIfNone(String val) {
        if (val.startsWith("\"") && val.endsWith("\"")) 
            return val;

        return "\"" + val + "\"";
    }
}