package ws.ragg.webapp.fileexchange.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import ws.ragg.webapp.fileexchange.Constants;

/**
 * @author andre
 *
 */
public class UploadServlet extends HttpServlet {

    
    private static final String SESSION_CLIENT_TO_TARGETDIR = "clientfinger-to-targetdir-mapping";
    private static final String CLIENT_FINGERPRINT_KEY = "client-fingerprint";
    private static final String WORKER_ID_KEY = "workerId";
    
    private static final long serialVersionUID = 3318435173245734039L;
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");


    private Logger log = org.apache.log4j.Logger.getLogger(UploadServlet.class);
    private String uploadTargetPath;
    private int maxMemSize = Constants.DEFAULT_MAX_MEMORY_SIZE;
    private long maxFileSize = Constants.DEFAULT_MAX_UPLOAD_FILE_SIZE;
    private DiskFileItemFactory factory;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    
    public UploadServlet() {
        log.debug("Constructor called!");
    }
    
    @Override
    public void init() throws ServletException {
        log.info("Initializing...");
        super.init();
        
        // create properties that will be used to init this servlet instance:
        Properties properties = readConfig();

        // first the upload target path:
        final String uploadPathProperty = properties.getProperty(
                                     Constants.INIT_PARAM_UPLOAD_TARGET_PATH);
        if (uploadPathProperty != null && ! uploadPathProperty.trim().isEmpty()) {
            File trg = new File(uploadPathProperty.trim());
            
            if (trg.exists()) {
                if (trg.isFile() || (trg.isDirectory() && ! trg.canWrite())) {
                    throw new ServletException(
                        "init-parameter '"+uploadPathProperty+"' must " +
                        "specify a writeable directory. invalid value: '"+trg+"'");
                }
                log.debug("using existing upload target dir: '"+trg+"'");
            } else {
                trg.mkdirs();
                if (! trg.exists()) {
                    throw new ServletException(
                        "could not create upload target directory " +
                        "at '"+trg+"' specified by " +
                        "init-parameter '"+uploadPathProperty+"'");
                }
                log.debug("using created upload target dir: '"+trg+"'");
            }
        } else {
            throw new ServletException(
                "missing init-parameter: '"+uploadPathProperty+"' " +
                "(must specify a writeable directory)");
        }
        uploadTargetPath = uploadPathProperty;
        

        // then the maximum upload file size and max memory size:
        final String uploadFileMaxSizeProperty = properties.getProperty(
                                    Constants.INIT_PARAM_UPLOAD_FILE_MAXSIZE);
        if (uploadFileMaxSizeProperty != null) {
            maxFileSize = Long.parseLong(uploadFileMaxSizeProperty.trim());
        }
        
        final String uploadFactoryMaxMemProperty = properties.getProperty(
                                  Constants.INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE);
        if (uploadFactoryMaxMemProperty != null) {
            maxMemSize = Integer.parseInt(uploadFactoryMaxMemProperty.trim());
        }
        
        
        factory = new DiskFileItemFactory();
        factory.setSizeThreshold(maxMemSize);
        
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File(uploadTargetPath));
        
        log.debug("configured: "+Constants.INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE+" = "+maxMemSize);
        log.debug("configured: "+Constants.INIT_PARAM_UPLOAD_FILE_MAXSIZE+" = "+maxFileSize);
        log.debug("configured: "+Constants.INIT_PARAM_UPLOAD_TARGET_PATH+" = "+uploadTargetPath);
        log.info("Initialized successfully.");
    }

    protected Properties readConfig() {
        Properties mergedProps = new Properties();
        final ServletConfig cfg = getServletConfig();
        
        // 1.) load DEFAULTS from the file-exchange-app.properties file.
        String defaultConfig = Constants.CONFIG_FILE_NAME;
        mergeFileProperties(mergedProps, defaultConfig, "default-config");
        
        // 2.) check the servlet config for an CONFIG-FILE-PARAMETER.
        // these properties are more specific to this servlet than the default
        // config, so it will override the properties loaded so far.
        String configFileParam = cfg.getInitParameter(
                                         Constants.INIT_PARAM_CONFIG_FILE_NAME);
        if (configFileParam != null) {
            mergeFileProperties(
                  mergedProps, configFileParam.trim(), "specified-config-file");
        } else {
            log.debug("No alternative property file given.");
        }
        
        //3.) check the SERVLET-CONFIG for init-parameter properties:
        // this configuration is most specific to this servlet instance, so
        // it will override the properties loaded so far.
        for (String key : Constants.INIT_PARAMETER_NAMES) {
            String val = cfg.getInitParameter(key);
            if (val != null) {
                log.debug("put from init-parameter : "+key+" = '"+val+"'");
                mergedProps.setProperty(key, val);
            }
        }
        return mergedProps;
    }

    /** read a properties file and set the loaded attributes to the
     * provided map.
     * 
     * @param properties
     *            the properties where the new attributes will be
     *            stored. the attrs from the loaded file will override
     *            existing attrs.
     * @param propertiesFileName */
    private void mergeFileProperties(Properties properties,
                                     String propertiesFileName,
                                     String tag) {
        ClassLoader cl = getClass().getClassLoader();
        InputStreamReader reader = null;
        InputStream confStream = null;
        
        try {
            if (log.isTraceEnabled()) {
                Enumeration<URL> res = cl.getResources(propertiesFileName);
                log.trace("resources matching "+tag+": " + (res != null ? Collections.list(res) : "[nothing]"));
            }
            Properties tmp = new Properties();
            confStream = cl.getResourceAsStream(propertiesFileName);
            reader = new InputStreamReader(confStream, "UTF-8");
            
            log.debug("Loading "+tag+" properties from "+propertiesFileName+" ...");
            tmp.load(reader);
            
            Iterator<Entry<Object, Object>> it = tmp.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Object, Object> entry = it.next();
                String key = String.valueOf(entry.getKey());
                String val = String.valueOf(entry.getValue());
                properties.setProperty(key, val);
                log.debug("put : "+key+" = '"+val+"'");
            }
            
        } catch (Exception e) {
            String msg = "Could not load "+tag+" properties! " +
                         "(File: "+propertiesFileName+")";
            log.warn(msg);
            log.debug(msg, e);
            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            } else if (confStream != null) {
                try {
                    confStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    
    @SuppressWarnings({"null"})
    private void authenticateClient(HttpServletRequest req) throws Exception { 
        // check if there is a client fingerprint in this request:
        String fingerPrint = req.getParameter(CLIENT_FINGERPRINT_KEY);
        
        if (fingerPrint == null) {
            String msg = "request did not contain a fingerprint!";
            log.error(msg);
            throw new Exception(msg);
        }
        
        log.debug("client fingerprint: "+fingerPrint);
        HttpSession s = req.getSession();
        Map<String, String> clientToTargetdir = getClientToTargetDirMap(req);
        if (clientToTargetdir == null) {
            synchronized (this) {
            if (clientToTargetdir == null) {
                clientToTargetdir = new HashMap<String, String>();
                s.setAttribute(SESSION_CLIENT_TO_TARGETDIR, clientToTargetdir);
                log.debug("session-to-targetdir-map created.");
            }
            }
        } else {
            log.debug("using existing session-to-targetdir-map with "+
                      clientToTargetdir.size() + " entries.");
        }
        
        String uploadDirKey = dateFormat.format(new Date());
        clientToTargetdir.put(fingerPrint, uploadDirKey);
        log.info("client registered. fingerprint: "+fingerPrint);
        log.debug("upload directory key: "+uploadDirKey);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getClientToTargetDirMap(HttpServletRequest r) {
        HttpSession session = r.getSession(false);
        if (session == null) {
            return null;
        }
        return (Map<String,String>)session.getAttribute(SESSION_CLIENT_TO_TARGETDIR);
    }

    private String getUploadDirectoryKey(HttpServletRequest req, 
                                         String clientFingerprint) throws Exception {
        Map<String, String> clientToTargetDirMap = getClientToTargetDirMap(req);
        if (clientToTargetDirMap == null) {
            throw new Exception("could not lookup: "+SESSION_CLIENT_TO_TARGETDIR);
        }
        
        String dirKey = clientToTargetDirMap.get(clientFingerprint);
        if (dirKey == null) {
            throw new Exception("not a registered client: "+clientFingerprint);
        }
        
        return dirKey;
    }

    // performed by the upload client to upload a file.
    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(final HttpServletRequest req, HttpServletResponse resp)
                                        throws ServletException, IOException {
        log.debug("START");
        debugRequestState(req);
        boolean multipartContent = ServletFileUpload.isMultipartContent(req);
        
        if (! multipartContent) {
            // this must be a "ping server" request from the uplManager client.
            try {
                authenticateClient(req);
            } catch (Exception e) {
                resp.getWriter().println("HANDSHAKE FAILED. MESSAGE="+e.getMessage());
            }
            return;
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);

        List<FileItem> fileItems = null;
        try {
            fileItems = new ArrayList<FileItem>(upload.parseRequest(req));
            
        } catch (SizeLimitExceededException e2) {
            log.info("rejecting upload, file too big!"+e2);
            resp.getWriter().println("UPLOAD FAILED. MESSAGE="+e2.getMessage());
            return;
            
        } catch (Exception e1) {
            String msg = "upload rejected. Error: "+e1.getMessage();
            log.error(msg, e1);
            resp.getWriter().println("UPLOAD FAILED. MESSAGE="+e1.getMessage());
            return;
        }
        
        log.info(fileItems.size()+" file-items parsed.");
        
        // look for the client fingerprint:
        String clientFingerPrint = null;
        String workerId = null;
        
        for (Iterator<FileItem> it = fileItems.iterator(); it.hasNext(); ) {
            FileItem item = it.next();
            if (! item.isFormField()) {
                continue;
            }
            it.remove();
            String fieldName = item.getFieldName();
            String value = item.getString();
            log.debug("removed item "+fieldName+" = "+value);
            
            if (fieldName.equals(CLIENT_FINGERPRINT_KEY)) {
                clientFingerPrint = value;
                log.debug("found fingerprint: "+clientFingerPrint);
            } else if (fieldName.equals(WORKER_ID_KEY)) {
                workerId = value;
                log.debug("found workerId: "+workerId);
            }
        }

        if (clientFingerPrint == null)
            throw new ServletException("no client fingerprint found!");
        if (workerId == null)
            throw new ServletException("no workerId found!");
        if (fileItems.size() != 1)
            throw new ServletException("expecting exactly one file input per request.");

        try {
            String directoryKey = getUploadDirectoryKey(req, clientFingerPrint);
            // create a unique directory where the file(s) are stored inside:
            File targetDir = createUniqueTargetDir(directoryKey, workerId);
            performUpload(targetDir, fileItems.get(0));
            resp.getWriter().print("UPLOAD OK");
            
        } catch (Exception e) {
            String msg = "upload rejected. error while saving file: "+e.getMessage();
            log.warn(msg);
            log.debug(msg, e);
            resp.getWriter().print("UPLOAD FAILED. MESSAGE="+e.getMessage());
        }
        
        log.debug("END");
    }
    
    private void performUpload(File targetDir, FileItem fileItem) throws Exception, IOException {
        String fileName = fileItem.getName();
        File saveLocation = new File(targetDir, fileName);
        long sizeInBytes = fileItem.getSize();
        log.debug("Uploading file, fileName: '" + fileName + "', size: '" + sizeInBytes + "'");
        
        InputStream inputStream = null;
        OutputStream outputStream = null;
        
        try {
            inputStream = fileItem.getInputStream();
            outputStream = new FileOutputStream(saveLocation);
    
             final int bytesPerSecond = 100 * 1024;
             byte[] buffer = new byte[bytesPerSecond];
             
             for (int read = -1, written = 0; (read = inputStream.read(buffer)) > 0;) {
                 outputStream.write(buffer, 0, read);
                 written += read;
                 Thread.sleep(1000);
             }
            
            log.info("Uploaded file '"+fileName+"' to '"+saveLocation+"'.");
            
        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (Exception e) { }
            }
            if (outputStream != null) {
                try { outputStream.close(); } catch (Exception e) { }
            }
        }
    }
    
    protected File createUniqueTargetDir(String directoryKey, String workerId) {
        StringBuilder bui = new StringBuilder();
        bui.append(uploadTargetPath);
        
        if ( ! uploadTargetPath.endsWith(FILE_SEPARATOR)) {
            bui.append(FILE_SEPARATOR);
        }
        
        directoryKey = directoryKey.trim().replaceAll(FILE_SEPARATOR, "_");
        workerId = workerId.trim().replaceAll(FILE_SEPARATOR, "_");
        
        // the builder is now set to the configured base directory.
        // append a unique named folder for this upload 

        bui.append("session__");
        bui.append(directoryKey);
        bui.append(FILE_SEPARATOR);

        // the builder is now set to the base directory for this session
        
        bui.append("item__");
        if (workerId.matches("[0-9]+")) {
            for (int i = 3 - workerId.length(); i-- > 0; bui.append('0'));
        }
        bui.append(workerId);
//        bui.append("__");
//        bui.append(dateFormat.format(new Date()));

        File targetDir = null;
        final int lengthBeforeSalt = bui.length();
        
        for (int salt = 2; ; salt++) {
            targetDir = new File(bui.toString());
            
            if (! targetDir.exists()) {
                targetDir.mkdirs();
                break; // unique dir created, done!
            }
            
            log.warn("!! target dir already existing !! dir='"+targetDir+"'");
            // workaround:
            bui.setLength(lengthBeforeSalt);
            bui.append("__");
            bui.append(salt);
        }
        return targetDir;
    }

    @SuppressWarnings({"rawtypes", "unchecked", "serial"}) 
    private void debugRequestState(final HttpServletRequest r) {
        if ( ! log.isDebugEnabled()) {
            return;
        }
        debugEnum(r.getHeaderNames(), "Header", new HashMap() {
            public Object get(Object key) {
                return r.getHeader(String.valueOf(key));
            }
        });
        
        debugEnum(r.getAttributeNames(), "Attribute", new HashMap() {
            public Object get(Object key) {
                return r.getAttribute(String.valueOf(key));
            }
        });
        
        debugEnum(r.getParameterNames(), "Parameter", new HashMap() {
            public Object get(Object key) {
                return r.getParameter(String.valueOf(key));
            }
        });
        
        HttpSession session = r.getSession(false);
        if (session == null) {
            log.debug("No http session!");
        } else {
            log.debug("Session id: "+session.getId());
            debugEnum(r.getSession().getAttributeNames(), "Session Attribute", new HashMap() {
                public Object get(Object key) {
                    return r.getSession().getAttribute(String.valueOf(key));
                }
            });
        }
        
        Cookie[] cookies = r.getCookies();
        if (cookies == null || cookies.length <= 0) {
            log.debug("No cookies!");
        } else {
            for (Cookie c : cookies) {
                log.debug("Cookie Name:   " + c.getName()   );
                log.debug("Cookie Value:  " + c.getValue()  );
                log.debug("Cookie Domain: " + c.getDomain() );
            }
        }
    }
    
    private void debugEnum(Enumeration<String> names, 
                                       String tag, 
                                       Map<String, Object> lookup) {
        List<String> list = Collections.list(names);
        log.debug(tag+"-Names: "+list.size());
        for (String name : list) {
            Object value = lookup.get(name);
            log.debug(tag+": "+name+" = "+value);
        }
    }
}
