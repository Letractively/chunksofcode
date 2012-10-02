package com.plankenauer.fmcontrol.config;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConfigRepository
{

    private final File repositoryRoot;


    public ConfigRepository(File repositoryRoot) {
        if (repositoryRoot == null) {
            throw new NullPointerException();
        }
        this.repositoryRoot = repositoryRoot;
    }

    public ConfigRepository(String repositoryRootPath) {
        this(new File(repositoryRootPath));
    }

    public Config parseConfig(String project, String configFileName) throws ConfigException {
        File projectDir = new File(repositoryRoot, project);
        File configFile = new File(projectDir, configFileName);

        return ConfigParser.parseConfig(configFileName, configFile);
    }
    
    public Map<String,Config> parseAllValidConfigs(String project) {
        try {
            return parseAllConfigs(project, true);
        } catch (ConfigException e) {
            Map<String,Config> none = Collections.emptyMap();
            return none;
        }
    }
    public Map<String,Config> parseAllConfigs(String project) throws ConfigException {
        return parseAllConfigs(project, false);
    }

    private Map<String,Config> parseAllConfigs(String project, boolean ignoreInvalid) throws ConfigException {
        Map<String,Config> configs = new TreeMap<>();
        List<ConfigException> failureList = new ArrayList<>();
        File projectDir = new File(repositoryRoot, project);

        for (File configFile : projectDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isFile();
            }
        })) {
            File f = new File(projectDir, configFile.getName());
            Config parsed;

            try {
                parsed = ConfigParser.parseConfig(f.getName(), f);
                configs.put(f.getName(), parsed);

            } catch (ConfigException e) {
                failureList.add(e);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        if (ignoreInvalid) {
            return configs;
        }
        
        if (! failureList.isEmpty()) {
            String trace = Str.dumpErrorMsgs(failureList);
            ConfigException summaryError = new ConfigException(trace);
            throw summaryError;
        }

        return configs;
    }
}
