package com.myapp.consumptionanalysis.config;

import java.util.Collections;
import java.util.List;

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
        return Str.dumpErrorMsgs(Collections.singletonList(this));
    }

    void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }
}
