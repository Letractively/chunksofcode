package com.myapp.consumptionanalysis.config;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class ConfigRepository implements Serializable
{

    private static final long serialVersionUID = 699540086117853733L;


    public static final class ParseResultHolder implements
                                               Comparable<ParseResultHolder>,
                                               Serializable
    {
        private static final long serialVersionUID = 4270594958412013116L;

        private final NumericStringComparator cmp = new NumericStringComparator(false);

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

        @Override
        public int compareTo(ParseResultHolder o2) {
            String s1 = project + "/" + fileName;
            String s2 = o2.project + "/" + o2.fileName;
            return cmp.compare(s1, s2);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
            result = prime * result + ((project == null) ? 0 : project.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (! (obj instanceof ParseResultHolder)) {
                return false;
            }
            ParseResultHolder other = (ParseResultHolder) obj;
            if (fileName == null) {
                if (other.fileName != null) {
                    return false;
                }
            } else if (! fileName.equals(other.fileName)) {
                return false;
            }
            if (project == null) {
                if (other.project != null) {
                    return false;
                }
            } else if (! project.equals(other.project)) {
                return false;
            }
            return true;
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

        Collections.sort(result);
        return result;
    }

    public ParseResultHolder parseConfigHolder(String project, String configFileName) {
        if (project == null) {
            throw new RuntimeException("Der Parameter 'project' ist zwingend erforderlich!");
        }
        if (configFileName == null) {
            throw new RuntimeException("Der Parameter 'configFileName' ist zwingend erforderlich!");
        }
        File projectDir = new File(repositoryRoot, project);
        File configFile = new File(projectDir, configFileName);

        Config cfg = null;
        ConfigException error = null;

        try {
            cfg = ConfigParser.parseConfig(configFileName, configFile);
        } catch (ConfigException e) {
            error = e;
        } catch (Exception e) {
            error = new ConfigException("Ein genereller Fehler ist aufgetreten: "
                    + e.getMessage(), e);
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
