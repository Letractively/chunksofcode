package com.myapp.consumptionanalysis.config;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.myapp.consumptionanalysis.util.StringUtils;

public class ConfigException extends Exception
{

    private static final long serialVersionUID = - 2589920976090740635L;


    private List<String> configErrors = null;
    private String debugString = null;
    private String configFilePath = null;


    public ConfigException(String arg0) {
        super(arg0);
    }

    public ConfigException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    void setConfigErrors(List<String> configErrors) {
        this.configErrors = configErrors;
    }

    public List<String> getConfigErrors() {
        return configErrors;
    }

    public void setDebugString(String debugString) {
        this.debugString = debugString;
    }

    public String getDebugString() {
        return debugString;
    }

    public String getErrorString() {
        if (configErrors == null  || configErrors.isEmpty()) {
            return null;
        }
        return dumpErrorMsgs(Collections.singletonList(this));
    }

    void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public static String dumpErrorMsgs(List<ConfigException> ex) {
        StringBuilder bui = new StringBuilder();
        for (ConfigException e : ex) {
            if (null != e.getConfigFilePath()) {
                bui.append(StringUtils.NL);
                bui.append("ConfigFile: ");
                File f = new File(e.getConfigFilePath());
                bui.append(f.getName());
                bui.append(StringUtils.NL);
                bui.append("In Verzeichnis: ");
                bui.append(f.getParentFile().getAbsolutePath());
                bui.append(StringUtils.NL);
            }
            bui.append(e.getMessage());
            bui.append(StringUtils.NL);
            int errorCount = e.getConfigErrors().size();

            for (int i = 0; i < errorCount; i++) {
                bui.append(i + 1);
                bui.append(".) ");
                String err = e.getConfigErrors().get(i);
                bui.append(err);

                if (i < errorCount - 1) {
                    bui.append(StringUtils.NL);
                }
            }
        }
        return bui.toString();
    }
}
