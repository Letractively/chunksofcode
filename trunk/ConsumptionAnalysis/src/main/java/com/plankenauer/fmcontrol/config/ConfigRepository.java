package com.plankenauer.fmcontrol.config;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public final class ConfigRepository
{

    public final class ParseResultHolder
    {

        private final String project;
        private final String fileName;
        private final String repoRoot;
        private final Config config;
        private final ConfigException error;

        public ParseResultHolder(String repoRoot,
                                 String project,
                                 String fileName,
                                 Config config,
                                 ConfigException error) {
            this.project = project;
            this.fileName = fileName;
            this.config = config;
            this.error = error;
            this.repoRoot = repoRoot;
        }

        public Config getConfig() {
            return config;
        }

        public ConfigException getError() {
            return error;
        }

        public String getProject() {
            return project;
        }

        public String getRepoRoot() {
            return repoRoot;
        }

        public String getFileName() {
            return fileName;
        }
    }

    private static final class IsFileFilter implements FileFilter
    {
        public boolean accept(File pathname) {
            return pathname.isFile()
//                    && pathname.canRead()
            ;
        }
    }

    private static final class IsDirFilter implements FileFilter
    {
        public boolean accept(File pathname) {
            return pathname.isDirectory() && pathname.canRead();
        }
    }



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



    public List<String> getAllProjectNames() {
        File[] dirs = repositoryRoot.listFiles(new IsDirFilter());
        List<String> l = new ArrayList<>();

        if (dirs != null) {
            for (File d : dirs) {
                l.add(d.getName());
            }
        }
        
        return l;
    }

    public List<String> getAllQueryNames(String project) {
        File projDir = new File(repositoryRoot, project);
        File[] files = projDir.listFiles(new IsFileFilter());
        List<String> l = new ArrayList<>();

        for (File d : files) {
            l.add(d.getName());
        }

        return l;
    }

    public List<ParseResultHolder> parseEverything() {
        List<ParseResultHolder> result = new ArrayList<>();

        for (String project : getAllProjectNames()) {
            for (String query : getAllQueryNames(project)) {
                ParseResultHolder h = parseConfigHolder(project, query);
                result.add(h);
            }
        }

        return result;
    }

    public ParseResultHolder parseConfigHolder(String project, String configFileName) {
        File projectDir = new File(repositoryRoot, project);
        File configFile = new File(projectDir, configFileName);

        Config cfg = null;
        ConfigException error = null;

        try {
            cfg = ConfigParser.parseConfig(configFileName, configFile);
        } catch (ConfigException e) {
            error = e;
        }

        return new ParseResultHolder(repositoryRoot.getAbsolutePath(),
                                     project,
                                     configFileName,
                                     cfg,
                                     error);
    }

    public Config parseConfig(String project, String filename) throws ConfigException {
        ParseResultHolder h = parseConfigHolder(project, filename);

        if (h.error != null) {
            throw h.error;
        }

        return h.config;
    }
}
