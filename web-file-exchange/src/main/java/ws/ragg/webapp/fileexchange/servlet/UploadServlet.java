package ws.ragg.webapp.fileexchange.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import ws.ragg.webapp.fileexchange.Constants;

/**
 * @author andre
 *
 */
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 3318435173245734039L;
    private Logger log = org.apache.log4j.Logger.getLogger(UploadServlet.class);
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final Random RANDOM = new Random();

    private String uploadTargetPath;
    private int maxMemSize = Constants.DEFAULT_MAX_MEMORY_SIZE;
    private long maxFileSize = Constants.DEFAULT_MAX_UPLOAD_FILE_SIZE;
    private DiskFileItemFactory factory;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
    
    public UploadServlet() {
        log.debug("Default Constructor called!");
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

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                                        throws ServletException, IOException {
        log.debug("START");
        debugRequestState(req);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);

        // create a unique directory where the file(s) are stored inside:
        File targetDir = createUniqueTargetDir();

        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(req);
            
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
        
        try {
            log.info("file-items: "+fileItems.size());

            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    log.debug("item.isFormField : "+item);
                    continue;
                }
                storeFile(targetDir, item);
                resp.getWriter().print("UPLOAD OK");
            }
        } catch (Exception e) {
            String msg = "upload rejected. error while saving file: "+e.getMessage();
            log.warn(msg);
            log.debug(msg, e);
            resp.getWriter().print("UPLOAD FAILED. MESSAGE="+e.getMessage());
        }
        
        log.debug("END");
    }

    private void storeFile(File targetFileDir, FileItem fileItem) throws Exception {
        String fieldName = fileItem.getFieldName();
        String fileName = fileItem.getName();
        String contentType = fileItem.getContentType();
        long sizeInBytes = fileItem.getSize();
        File saveLocation = new File(targetFileDir, fileName);

        if (log.isDebugEnabled()) {
            log.debug("uploading file, fieldName    : '" + fieldName + "'");
            log.debug("uploading file, contentType  : '" + contentType + "'");
            log.debug("uploading file, fileName     : '" + fileName + "'");
            log.debug("uploading file, sizeInBytes  : '" + sizeInBytes + "'");
            log.debug("uploading file, saveLocation : '" + saveLocation + "'");
        }
        
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

                 if (log.isDebugEnabled()) {
                     String done = new DecimalFormat("0.0%").format((double) written / (double) sizeInBytes);
                     log.debug("wrote: "+written+" bytes ("+done+") to file: "+fileName);
                 }
                 
                 Thread.sleep(1000);
             }
            
            log.info("FILE uploaded: "+saveLocation);
            
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    protected File createUniqueTargetDir() {
        StringBuilder bui = new StringBuilder();
        bui.append(uploadTargetPath);
        
        if ( ! uploadTargetPath.endsWith(FILE_SEPARATOR)) {
            bui.append(FILE_SEPARATOR);
        }
        
        // the builder is now set to the configured base directory.
        // append a unique named folder for this upload 
        
        bui.append("upload__");
        bui.append(dateFormat.format(new Date()));
        bui.append("__");
        
        File targetDir = null;
        
        for (final int lengthBeforeSalt = bui.length();;) {
            int salt = RANDOM.nextInt(Integer.MAX_VALUE);
            bui.append(salt);
            bui.append(FILE_SEPARATOR);
            targetDir = new File(bui.toString());
            
            if (! targetDir.exists()) {
                targetDir.mkdir();
                break; // unique dir created, done!
            }
            bui.setLength(lengthBeforeSalt);
        }
        return targetDir;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("START");
//        super.doGet(req, resp);
        log.debug("END");
    }
    

    @SuppressWarnings("rawtypes") 
    private void debugRequestState(HttpServletRequest r) {
        if (! log.isDebugEnabled()) {
            return;
        }
        
        Enumeration e = null;
        for (e = r.getHeaderNames(); e != null && e.hasMoreElements();) {
            String name = e.nextElement().toString();
            log.debug("header name:  "+name);
            String value = String.valueOf(r.getHeader(name));
            log.debug("header value: "+value);
        }

        for (e = r.getAttributeNames(); e != null && e.hasMoreElements();) {
            String name = String.valueOf(e.nextElement());
            log.debug("attr name:  "+name);
            String val = String.valueOf(r.getAttribute(name));
            log.debug("attr value: "+val);
        }
        
        for (e = r.getParameterNames(); e != null && e.hasMoreElements();) {
            String name = String.valueOf(e.nextElement());
            log.debug("param name:  "+name);
            String val = String.valueOf(r.getParameter(name));
            log.debug("param value: "+val);
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
        
        log.debug("done");
    }
}
