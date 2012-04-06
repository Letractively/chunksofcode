package ws.ragg.webapp.fileexchange.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
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


public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 3318435173245734039L;
    private Logger log = org.apache.log4j.Logger.getLogger(UploadServlet.class);
    
    
    private static final String INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE = "upload.factory.maxmemsize";
    private static final String INIT_PARAM_UPLOAD_FILE_MAXSIZE = "upload.file.maxsize";
    private static final String INIT_PARAM_UPLOAD_TARGET_PATH = "upload.target.path";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final Random RANDOM = new Random();

    
    private static final long DEFAULT_MAX_UPLOAD_FILE_SIZE = 1024L * 1024 * 10;
    private static final int DEFAULT_MAX_MEMORY_SIZE = 1024 * 1024;
    
    private String uploadTargetPath;
    private int maxMemSize = DEFAULT_MAX_MEMORY_SIZE;
    private long maxFileSize = DEFAULT_MAX_UPLOAD_FILE_SIZE;
    private DiskFileItemFactory factory;
    
    
    
    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig cfg = getServletConfig();
        String uploadPath = cfg.getInitParameter(INIT_PARAM_UPLOAD_TARGET_PATH);
        
        if (uploadPath != null && ! uploadPath.trim().isEmpty()) {
            File trg = new File(uploadPath.trim());
            
            if (trg.exists()) {
                if (trg.isFile() || (trg.isDirectory() && ! trg.canWrite())) {
                    throw new ServletException(
                        "init-parameter '"+INIT_PARAM_UPLOAD_TARGET_PATH+"' must " +
                        "specify a writeable directory. invalid value: '"+trg+"'");
                }
                log.debug("using existing upload target dir: '"+trg+"'");
            } else {
                trg.mkdirs();
                if (! trg.exists()) {
                    throw new ServletException(
                        "could not create upload target directory " +
                        "at '"+trg+"' specified by " +
                        "init-parameter '"+INIT_PARAM_UPLOAD_TARGET_PATH+"'");
                }
                log.debug("using created upload target dir: '"+trg+"'");
            }
        } else {
            throw new ServletException(
                "missing init-parameter: '"+INIT_PARAM_UPLOAD_TARGET_PATH+"' " +
                "(must specify a writeable directory)");
        }
        uploadTargetPath = uploadPath;
        
        String fileMaxSize = cfg.getInitParameter(INIT_PARAM_UPLOAD_FILE_MAXSIZE);
        if (fileMaxSize != null) {
            maxFileSize = Long.parseLong(fileMaxSize.trim());
        }
        
        String maxMemSizeStr = cfg.getInitParameter(INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE);
        if (maxMemSizeStr != null) {
            maxMemSize = Integer.parseInt(fileMaxSize.trim());
        }
        
        factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File(uploadTargetPath));
        
        log.info("Initialized successfully.");
        log.debug(INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE+" = "+maxMemSize);
        log.debug(INIT_PARAM_UPLOAD_FILE_MAXSIZE+" = "+maxFileSize);
        log.debug(INIT_PARAM_UPLOAD_TARGET_PATH+" = "+uploadTargetPath);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                                        throws ServletException, IOException {
        log.debug("doPost() START");
        debugRequestState(req);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);

        // create a unique directory where the file(s) are stored inside:
        File targetDir = createUniqueTargetDir();
        
        try {
            @SuppressWarnings("unchecked")
            List<FileItem> fileItems = upload.parseRequest(req);
            log.info("file-items: "+fileItems.size());

            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    log.debug("item: "+item);
                    continue;
                }
                storeFile(targetDir, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("error while fetching file(s)!", e);
        }
        log.debug("doPost() END");
    }

    private void storeFile(File targetFileDir, FileItem fileItem) throws Exception {
        String fieldName = fileItem.getFieldName();
        String fileName = fileItem.getName();
        String contentType = fileItem.getContentType();
        long sizeInBytes = fileItem.getSize();
        File saveLocation = new File(targetFileDir, fileName);

        log.debug("uploading file, fieldName    : '"+fieldName+"'");
        log.debug("uploading file, contentType  : '"+contentType+"'");
        log.debug("uploading file, fileName     : '"+fileName+"'");
        log.debug("uploading file, sizeInBytes  : '"+sizeInBytes+"'");
        log.debug("uploading file, saveLocation : '"+saveLocation+"'");

        fileItem.write(saveLocation);
        
        ////////////////// XXX hack: artificial delay instead of fileItem.write(saveLocation)
        // InputStream inputStream = fileItem.getInputStream();
        // OutputStream outputStream = new FileOutputStream(saveLocation);
        // byte[] buffer = new byte[1024];
        // 
        // for (int read = -1; (read = inputStream.read(buffer)) > 0;) {
        //     outputStream.write(buffer, 0, read);
        //     Thread.sleep(1000);
        //     log.debug("written: "+read+" file: "+fileName);
        // }
        ////////////////// XXX hack end ////////////////////////
        
        log.info("FILE uploaded: "+saveLocation);
    }
    
    protected File createUniqueTargetDir() {
        StringBuilder bui = new StringBuilder();
        bui.append(uploadTargetPath);
        
        if ( ! uploadTargetPath.endsWith(FILE_SEPARATOR)) {
            bui.append(FILE_SEPARATOR);
        }
        
        // the builder is now set to the configured base directory 
        
        bui.append("upload__");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        bui.append(fmt.format(new Date()));
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
        log.debug("doGet() START");
//        super.doGet(req, resp);
        log.debug("doGet() END");
    }
    

    @SuppressWarnings("rawtypes") 
    private void debugRequestState(HttpServletRequest r) {
        if (! log.isDebugEnabled()) {
            return;
        }
        
        for (Enumeration e = r.getHeaderNames(); e != null && e.hasMoreElements();) {
            String name = e.nextElement().toString();
            log.debug("debugRequestState() header name:  "+name);
            String value = String.valueOf(r.getHeader(name));
            log.debug("debugRequestState() header value: "+value);
        }

        for (Enumeration e = r.getAttributeNames(); e != null && e.hasMoreElements();) {
            String name = String.valueOf(e.nextElement());
            log.debug("debugRequestState() attr name:  "+name);
            String val = String.valueOf(r.getAttribute(name));
            log.debug("debugRequestState() attr value: "+val);
        }
        
        for (Enumeration e = r.getParameterNames(); e != null && e.hasMoreElements();) {
            String name = String.valueOf(e.nextElement());
            log.debug("debugRequestState() param name:  "+name);
            String val = String.valueOf(r.getParameter(name));
            log.debug("debugRequestState() param value: "+val);
        }
        
        Cookie[] cookies = r.getCookies();
        if (cookies == null || cookies.length <= 0) {
            log.debug("debugRequestState() No cookies!");
        } else {
            for (Cookie c : cookies) {
                log.debug("debugRequestState() Cookie Name:   " + c.getName()   );
                log.debug("debugRequestState() Cookie Value:  " + c.getValue()  );
                log.debug("debugRequestState() Cookie Domain: " + c.getDomain() );
            }
        }
        
        log.debug("debugRequestState() done");
    }
}
