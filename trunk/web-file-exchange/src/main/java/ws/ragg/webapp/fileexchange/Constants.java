package ws.ragg.webapp.fileexchange;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Constants {
    
    public static final String CONFIG_FILE_NAME = "file-exchange-app.properties";
    
    public static final String INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE = "upload.factory.maxmemsize";
    public static final String INIT_PARAM_UPLOAD_FILE_MAXSIZE       = "upload.file.maxsize";
    public static final String INIT_PARAM_UPLOAD_TARGET_PATH        = "upload.target.path";
    public static final String INIT_PARAM_CONFIG_FILE_NAME          = "config.file.name";
    
    @SuppressWarnings("serial")
    public static final Set<String> INIT_PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(){{
        add(INIT_PARAM_UPLOAD_FACTORY_MAXMEMSIZE);
        add(INIT_PARAM_UPLOAD_FILE_MAXSIZE      );
        add(INIT_PARAM_UPLOAD_TARGET_PATH       );
        add(INIT_PARAM_CONFIG_FILE_NAME         );
    }});

    
    public static final long DEFAULT_MAX_UPLOAD_FILE_SIZE = 1024L * 1024 * 10;
    public static final int DEFAULT_MAX_MEMORY_SIZE = 1024 * 1024;


    private Constants() {}
}
