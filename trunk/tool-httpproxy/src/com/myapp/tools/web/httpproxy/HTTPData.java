package com.myapp.tools.web.httpproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.myapp.tools.web.httpproxy.format.JavaScriptCodeFormatter;
import com.myapp.util.log.Log;

/**
 * backs the data being transferred during a http request or response. offers
 * various methods to extract metadata.
 * 
 * @author andre
 */
@SuppressWarnings("deprecation")
public class HTTPData {
    
    public enum ContentTypes {

        TEXT,
        IMAGE,
        UNKNOWN;

        public static ContentTypes getType(String contentType) {
            if (contentType == null || contentType.length() > 150)
                return UNKNOWN;

            String lowercase = contentType.toLowerCase();
            if (lowercase.startsWith("text"))
                return TEXT;
            if (lowercase.startsWith("image"))
                return IMAGE;

            return UNKNOWN;
        }
    }

    /** matches against something like "HTTP/1.1 200 OK" */
    private static final Pattern RESPONSE_CODE_PATTERN;
    
    /** matches against something like "Host: localhost:12345" */
    private static final Pattern HOST_PATTERN;
    
    /** matches against something like "Content-Length: 3833" */
    private static final Pattern CONTENT_LENGTH_PATTERN;
    
    /**
     * matches against something like "Content-Type: image/x-icon" known types:
     * <pre>
     * application/x-javascript
     *     image/gif       image/jpeg      image/png       image/x-icon
     *     text/css        text/html       text/html; charset=iso-8859-1
     *     text/html; charset=utf-8        text/html; charset=UTF-8
     *     text/javascript; charset=UTF-8
     * </pre>
     */
    private static final Pattern CONTENT_TYPE_PATTERN;
    
    /** matches against something like "Content-Encoding: gzip" */
    private static final Pattern CONTENT_ENCODING_PATTERN;
    
    /** groups something like hostname:8080 */
    static final Pattern HOSTNAME_PORT_PATTERN;

    
    static {
        RESPONSE_CODE_PATTERN = Pattern.compile(
                                            "http(.+?)\\s([0-9].+?)\\s(.+?)",
                                            Pattern.CASE_INSENSITIVE);
        
        HOST_PATTERN = Pattern.compile("host:\\s([^\\p{Space}].+?)",
                                       Pattern.CASE_INSENSITIVE);
        
        CONTENT_LENGTH_PATTERN = Pattern.compile("content-length:\\s([0-9].+?)",
                                                 Pattern.CASE_INSENSITIVE);
        
        CONTENT_TYPE_PATTERN = Pattern.compile("content-type:\\s(.+?)",
                                               Pattern.CASE_INSENSITIVE);
        
        CONTENT_ENCODING_PATTERN = Pattern.compile("content-encoding:\\s(.+?)",
                                                   Pattern.CASE_INSENSITIVE);
        
        HOSTNAME_PORT_PATTERN = Pattern.compile("(.+?):([0-9].+?)");
    }
    
    /*
    ------------------- basic data ----------------------
     */

    private ByteArrayOutputStream byteArrOS = new ByteArrayOutputStream();
    private String hostName = null;
    private int responseCode = 200;

    /*
    ------------------- meta-data------------------------
     */
    private int contentLength = -1;
    private String contentType = null;
    private String headerString = null;
    private String contentEncoding = null;
    private boolean headerFinished = false;

    /*
    ------------------ BASIC METHODS --------------------------
     */
    public void saveBytes(byte[] b) throws IOException {
        byteArrOS.write(b);
    }

    public void writeTo(OutputStream out) throws IOException {
        byteArrOS.writeTo(out);
    }

    public synchronized void saveHeader(StringBuilder headerBui) {
        headerString = headerBui.toString();
        try {
            byteArrOS.write(headerString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        headerFinished = true;
    }

    public String getHeader() {
        return headerString;
    }

    public void saveBytes(byte[] b, int off, int len) {
        byteArrOS.write(b, off, len);
    }

    public void write(int b) {
        byteArrOS.write(b);
    }

    @Override
    public String toString() {
        return formattedToString();
    }

    public byte[] toByteArray() {
        return byteArrOS.toByteArray();
    }

    public int size() {
        return byteArrOS.size();
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getHostName() {
        return hostName;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    /*
    ------------------ METADATA EXTRACT METHODS --------------------------
     */
    
    /**
     * tries to parse content-length, contenttype, hostname, responsecode and
     * contentencoding from a line of a http header and saves the result. this
     * method returns after the first metadata piece was found. if the header
     * was already written, this method will do nothing.
     * 
     * @param headerLine
     *            a line of a http header
     * @return if something could be extracted.
     */
    public boolean parseHeaderMetaData(String headerLine) {
        return     ( ! headerFinished ) 
                && ( headerLine.length() < 150 )
                && (       tryParseContentLength(headerLine)
                        || tryParseContentType(headerLine)
                        || tryParseHostName(headerLine) 
                        || tryParseResponseCode(headerLine)
                        || tryParseContentEncoding(headerLine)
                    );
    }
    
    /**
     * get the contentlength from a line like 'Content-Length: 1150'
     * 
     * @param line
     *            to extract from
     * @return if something could be parsed from this line
     */
    private boolean tryParseContentLength(String headerLine) {
        Matcher m = CONTENT_LENGTH_PATTERN.matcher(headerLine);
        if (m.matches())
            try {
                contentLength = Integer.parseInt(m.group(1));
                Log.logln("got content-length: '" + contentLength + "' " +
                          "from line '" + headerLine + "'");
                return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        return false;
    }

    /**
     * parses the host name from a line like 'Host: localhost:12345'
     * 
     * @param line
     *            to extract from
     * @return if something could be parsed from this line
     */
    private boolean tryParseHostName(String headerLine) {
        Matcher m = HOST_PATTERN.matcher(headerLine);
        if (m.matches()) {
            hostName = m.group(1);
            Log.logln("got host name: '" + hostName + "' " +
                      "from line '" + headerLine + "'");
            return true;
        }
        return false;
    }

    /**
     * parses the response code of a line like : 'HTTP/1.1 200 OK'
     * 
     * @param headerLine
     *            the line to extract code from
     * @return if something could be parsed from this line
     */
    private boolean tryParseResponseCode(String headerLine) {
        Matcher m = RESPONSE_CODE_PATTERN.matcher(headerLine);
        if (m.matches())
            try {
                this.responseCode = Integer.parseInt(m.group(2));
                Log.logln("got response code: '" + responseCode + "' " +
                          "from line '" + headerLine + "'");
                return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        return false;
    }

    /**
     * parses the content type of a line like : 'Content-Type: image/x-icon'
     * 
     * @param headerLine
     *            the line to extract type from
     * @return if something could be parsed from this line
     */
    private boolean tryParseContentType(String headerLine) {
        Matcher m = CONTENT_TYPE_PATTERN.matcher(headerLine);
        if (m.matches()) {
            contentType = m.group(1);
            Log.logln("got content type: '" + contentType + "' " +
                      "from line '" + headerLine + "'");
            return true;
        }
        return false;
    }

    private boolean tryParseContentEncoding(String headerLine) {
        Matcher m = CONTENT_ENCODING_PATTERN.matcher(headerLine);
        if (m.matches()) {
            contentEncoding = m.group(1);
            Log.logln("got content encoding: '" + contentType + "' " +
                      "from line '" + headerLine + "'");
            return true;
        }
        return false;
    }
    
    
    
    

    /*
    ------------------ CONTENT FORMATTING METHODS --------------------------
     */
    
    public String formattedToString() {
        List<String> strings = new ArrayList<String>();
        String[] headerLines = getHeader().split(ProxyThread.LINE_BREAK);

        for (String line : headerLines)
            strings.add(line);

        StringBuilder bui = new StringBuilder();
        bui.append("[----------------HEADER START--------------]\n");

        int i = 0;
        for (String s : strings) {
            bui.append("[");
            if (++i < 10)
                bui.append(' ');
            bui.append(i);
            bui.append(".]       ");
            bui.append(s);
            bui.append(ProxyThread.LINE_BREAK);
        }

        bui.append("[----------------HEADER END----------------]\n");
        byte[] body = getBodyBytes();
        if (body.length > 0) {
            bui.append("[------BODY START type='");
            bui.append(contentType);
            bui.append("'------------------]\n");

            /*printing body (write all bytes from header end to end)*/
            ContentTypes type = ContentTypes.getType(contentType);
            if (type == ContentTypes.TEXT) {
                String text = null;
                if (contentEncoding != null &&
                    contentEncoding.toLowerCase().startsWith("gzip"))
                    text = Utils.unGZipData(body);
                else
                    text = new String(body);

                text = formatCodeContent(text);
                bui.append(text);

            } else
                bui.append(Utils.byteArrToHexString(body));

            bui.append(ProxyThread.LINE_BREAK);
            bui.append("[----------------BODY END------------------]\n");
        } else
            bui.append("[----------------NO BODY DATA--------------]\n");
        return bui.toString();
    }

    private String formatCodeContent(String code) {
        if (contentType != null 
                && contentType.toLowerCase().indexOf("javascript") >= 0) {
            
            toFile("/home/andre/Desktop/unformatted.js", code);

            code = JavaScriptCodeFormatter.formatJavaScript(code);

            toFile("/home/andre/Desktop/formatted.js", code);
        }
        return code;
    }
    
    private void toFile(String path, String content) {
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(new File(path), false);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * extract port from hostname, if defined
     * 
     * @param hostName
     *            the string like localhost:12345
     * @return null if the hostname did not contain the port number, or
     * 
     *         <pre>
     *         an object[2] where
     *              0 is the port name as string and
     *              1 is the port number as integer.
     * </pre>
     */
    public Object[] splitHostNameAndPort() {
        Matcher m = HOSTNAME_PORT_PATTERN.matcher(hostName);
        
        if (m.matches()) {
            try {
                String name = m.group(1);
                Integer num = new Integer(m.group(2));
                return new Object[] { name, num };
                
            } catch (NumberFormatException e) {}
        }
        
        return null;
    }

    /**
     * returns the bytes of the streamed data without the header contents
     * 
     * @return the body bytes of the streamed data
     */
    private byte[] getBodyBytes() {
        byte[] allBytes = byteArrOS.toByteArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int headerByteCount = headerString.getBytes().length;
        int remainingLength = byteArrOS.size() - headerByteCount;
        baos.write(allBytes, headerByteCount, remainingLength);

        return baos.toByteArray();
    }

    /** test the function of the patterns @param foo bar */
    public static void main(String... foo) {
        boolean ok = true;
        ok = ok && testPattern(
                CONTENT_LENGTH_PATTERN, "Content-Length: 3833", 1);
        ok = ok && testPattern(
                HOST_PATTERN, "Host: localhost:12345", 1);
        ok = ok && testPattern(
                RESPONSE_CODE_PATTERN, "HTTP/1.1 200 OK", 2);
        ok = ok && testPattern(
                CONTENT_TYPE_PATTERN, "Content-Type: image/x-icon", 1);
        ok = ok && testPattern(
                CONTENT_ENCODING_PATTERN, "Content-Encoding: gzip", 1);
        ok = ok && testPattern(
                HOSTNAME_PORT_PATTERN, "localhost:12345", 1, 2);

        System.out.println("all tests ok: " + ok);
    }

    /**
     * tests a pattern against a string and prints the given groups
     * 
     * @param p
     *            the pattern
     * @param test
     *            the string to test the pattern against
     * @param groupsToPrint
     *            the groups to print out.
     * @return if a match was found
     */
    private static boolean testPattern(Pattern p,
                                       String test,
                                       int... groupsToPrint) {
        Matcher m = p.matcher(test);
        StringBuilder bui = new StringBuilder();
        bui.append("matching teststring '");
        bui.append(test);
        bui.append("'\nagainst pattern '");
        bui.append(p.pattern());
        bui.append("' ...\n");

        boolean matches = m.matches();
        if (matches) {
            bui.append("[MATCH]");
            for (int g : groupsToPrint) {
                bui.append(" group(");
                bui.append(g);
                bui.append(") = '");
                bui.append(m.group(g));
                bui.append("'");
            }
        } else
            bui.append("[DON'T MATCH]");

        System.out.println(bui);
        System.out.println();
        return matches;
    }

}





/*
------------------ HELPER METHODS --------------------------
 */

/** @author andre */
class Utils {

    static final int CARRIAGE_RETURN = 0xD;
    static final int LINE_FEED = 0xA;
    static final int NULL_BYTE = 0x0;

    static String unGZipData(byte[] data) {
        try {
            ByteArrayInputStream bais;
            GZIPInputStream zippedIn;
            ByteArrayOutputStream unzippedOut;

            bais = new ByteArrayInputStream(data);
            zippedIn = new GZIPInputStream(bais);
            unzippedOut = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesInBuffer;

            while ((bytesInBuffer = zippedIn.read(buffer)) > 0)
                unzippedOut.write(buffer, 0, bytesInBuffer);

            return new String(unzippedOut.toByteArray());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void byteToHexString(byte b, StringBuilder bui) {
        String hex = Integer.toHexString(b & 0xff);
        if (hex.length() == 1)
            bui.append('0');
        bui.append(hex);
    }

    static String byteArrToHexString(byte[] bytes) {
        StringBuilder bui = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byteToHexString(bytes[i], bui);
            if (i != bytes.length - 1)
                bui.append('-');
        }
        return bui.toString();
    }

    /**
     * reads the next line from an given inputstream. lines are be separated as
     * specified in the http-protocol. (0x0, '\n' or '\r' or "\r\n" (god damn
     * it)
     * 
     * @param in
     *            the inputstream to read from
     * @return the next line
     */
    static final String readHttpTerminatedLine(InputStream in) {
        StringBuilder bui = new StringBuilder();
        int b;
        try {
            /*return null if byte represents no data*/
            in.mark(1);
            if (in.read() == -1)
                return null;
            else
                in.reset();
            /*read bytes until line terminator*/
            while ((b = in.read()) >= 0)
                if (b == CARRIAGE_RETURN || b == LINE_FEED || b == NULL_BYTE)
                    break;
                else
                    bui.append((char) b);
            /*check if a '\r' follows after  a '\n' char*/
            if (b == CARRIAGE_RETURN) {
                in.mark(1);
                if (in.read() != LINE_FEED)
                    /*CR without following NL, jump back one byte*/
                    in.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bui.toString();
    }

    static String getSummary(StringBuilder log,
                             Socket client,
                             String hostName,
                             int hostPort,
                             String req,
                             String resp,
                             long timeNeeded) {
        log.insert(0, "\n###############################################\n" +
                      "## FINISHED:        SUMMARY                  ##" +
                      "\n###############################################\n");
        log.append("\n###############################################\n");
        log.append("## FINISHED:        DETAIL                   ##");
        log.append("\n###############################################\n");
        log.append("## Request from ");
        log.append(client);
        log.append(" to host ");
        log.append(hostName);
        log.append(":");
        log.append(hostPort);
        log.append("\n");
        log.append("##   (");
        log.append(req.length());
        log.append(" bytes sent (incl. header), ");
        log.append(resp.length());
        log.append(" bytes returned (incl. header), ");
        log.append(Long.toString(timeNeeded));
        log.append(" ms elapsed)");
        log.append("\n");
        log.append("####### REQUEST:  ###################################\n");
        log.append(req);
        log.append("####### END OF REQUEST ##############################\n");
        log.append("####### RESPONSE: ###################################\n");
        log.append(resp);
        log.append("####### END OF RESPONSE #############################\n");
        return log.toString();
    }

}
