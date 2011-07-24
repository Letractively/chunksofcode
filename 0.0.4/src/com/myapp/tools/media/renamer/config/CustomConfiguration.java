package com.myapp.tools.media.renamer.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.myapp.tools.media.renamer.controller.Msg;

/**
 * extends the default properties of the ability to save and load custom
 * settins.
 * 
 * @author andre
 */
public abstract class CustomConfiguration extends DefaultConfiguration {
    
    private Map<String, String> props = new HashMap<String, String>();

    /**
     * this constructor tries to read the custom properties on call.
     */
    protected CustomConfiguration() {
        try {
            loadCustomSettings();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getCustomProperty(String key) {
        return props.get(key);
    }

    @Override
    public void setCustomProperty(String key, String value) {
        if (key == null) throw new IllegalStateException("key is null");
        
            // FIXME workaround, because of broken file paths in windows env on
            // paths like C:\nStartsWithN\rStartsWithR\tStartsWithT :
        if (   key.equals(LAST_ACCESSED_FILE_PATH)
            || key.equals(DESTINATION_RENAMED_FILES)) {
                //System.out.println("workaround before: " + value);
            value = replaceEnvironmentVariables(value);
                //System.out.println("workaround after:  " + value);
        }
        
        props.put(key, value);
        L.info(Msg.msg("CustomConfigration.customPropertySet")
                            .replace("#key#", key)
                            .replace("#value#", value));
    }

    @Override
    public void clearCustomProperties() {
        if (props != null && props.size() > 0)
            props.clear();
        L.info(Msg.msg("CustomConfigration.customPropertiesDiscarded"));
    }

    @Override
    public boolean saveCustomSettings() throws IOException {
        return PropertiesIO.write(props, getCustomConfigPath());
    }

    @Override
    public boolean loadCustomSettings() throws IOException {
        L.info(Msg.msg("CustomConfigration.loadCustomSettingsFrom")
                                 .replace("#path#", getCustomConfigPath()));

        clearCustomProperties();
        props = PropertiesIO.read(getCustomConfigPath());
        L.info(Msg.msg("CustomConfigration.loadCustomSettings.loaded")
                        .replace("#number#", String.valueOf(props.size())));
        return true;
    }

    public InputStream getAsStream() {
        File customFile = new File(getCustomConfigPath());

        try {
            if (customFile.exists()) {
                return new BufferedInputStream(new FileInputStream(customFile));
                
            } else {
                return defaultPropertiesStream();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
