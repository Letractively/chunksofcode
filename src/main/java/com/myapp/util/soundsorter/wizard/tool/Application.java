package com.myapp.util.soundsorter.wizard.tool;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bff.javampd.MPD;

import com.myapp.util.cache.disk.ObjectStore;
import com.myapp.util.format.Util;
import com.myapp.util.soundsorter.wizard.model.DestinationTargets;
import com.myapp.util.soundsorter.wizard.model.IDestinationDefinition;
import com.myapp.util.soundsorter.wizard.model.IMetaDataSource;
import com.myapp.util.soundsorter.wizard.model.IUnsortedInterpretDirSrc;
import com.myapp.util.soundsorter.wizard.model.MPDAdapter;
import com.myapp.util.soundsorter.wizard.model.SongListMeta;



public class Application {

    
    /**
     * sets the lookupPath to the parent folder of the given folder if this one of 
     * "lonesome files", "maximix files" or "unsorted"
     * 
     * @author andre
     *
     */
    private class LookupPathHack implements ILookupPathCalculator 
    {               // FIXME: ugly
        
        @Override
        public File overrideLookupPath(File physicalLocation) {
            String name = physicalLocation.getName();
            Config cfg = Config.getInstance(); 
            String lonesome = cfg.getProperty(Config.PROPKEY_LONESOME_FILES_DIR_NAME);
            String maximix = cfg.getProperty(Config.PROPKEY_MAXIMIX_DIR_NAME);
            String unsorted = cfg.getProperty(Config.PROPKEY_UNSORTED_DIR_NAME);

            boolean oneLevelUpHack = name.equals(lonesome)
                                  || name.equals(maximix)
                                  || name.equals(unsorted);
            
            if (oneLevelUpHack) {
                return physicalLocation.getParentFile();
            }
            
            return physicalLocation;
        }
    }

    
    private static final String CACHE_FLAG_PROPKEY = "USE_OBJECTSTORE_CACHE";
    public static String OBJECT_STORE_NAME = Application.class.getName() + ".cachedMetadata";

    
    private Set<INextDirChosenListener> dirChosenListeners = new HashSet<INextDirChosenListener>();
    private ObjectStore<SongListMeta> cachedMetadata;
    
    
    private Config config;
    private IMetaDataSource metaDataLookup;
    private IUnsortedInterpretDirSrc unsortedDirList;
    private DestinationTargets destinationTargets;
    private IActionExecutor actionExecutor;
    private ILookupPathCalculator lookupPathCalculator;

    private Iterator<File> unsortedFiles;
    private File currentUnsortedDir;
    private SongListMeta currentUnsortedDirGod;


    private boolean cache = false;
    

    
    public Application() {
        long totalStart = Util.now();
        config = Config.getInstance();
        String prefix = "Application.Application() ";
        System.out.println(prefix + "initializing...");

        long start = Util.now();
        metaDataLookup = new MPDAdapter(config.getMpd());
        Util.log(prefix + "MPDMetadataLookup created!", start);
        
        start = Util.now();
        unsortedDirList = new UnsortedDirList(config);
        Util.log(prefix + "UnsortedDirList created!", start);

        start = Util.now();
        IDestinationDefinition destinationDefinition = new DestinationDirDefinition(config);
        destinationTargets = destinationDefinition.getDestinationTargets();
        Util.log(prefix + "destinationDefinition created!", start);
        
        start = Util.now();
        actionExecutor = new ActionExecutor();
        Util.log(prefix + "ActionExecutor created!", start);
        
        start = Util.now();
        cache = Boolean.parseBoolean(config.getProperty(CACHE_FLAG_PROPKEY));
        
        if (cache) {
            cachedMetadata = new ObjectStore<SongListMeta>(OBJECT_STORE_NAME);
        
        } else {
            ObjectStore<SongListMeta> dummy = ObjectStore.empty();
            cachedMetadata = dummy;
        }
        
        Util.log(prefix + "ObjectStore created!", start);
        lookupPathCalculator = new LookupPathHack();
        Util.log(prefix + "Application initialized! TOTAL TIME:", totalStart);
    }
    
    

    public void start() {
        unsortedFiles = unsortedDirList.getInterpretDirs().iterator();
    }
    
    public boolean hasNextDir() {
        return unsortedFiles.hasNext();
    }

    public void loadNextDir() {
        currentUnsortedDir = unsortedFiles.next();
        currentUnsortedDirGod = getGenresOfUnsortedDir(currentUnsortedDir);
        
        for (Iterator<INextDirChosenListener> itr = dirChosenListeners.iterator(); itr.hasNext();) {
            INextDirChosenListener l = itr.next();
            l.nextDirChosen(this);
        }
    }
    
    public boolean addNextDirChosenListener(INextDirChosenListener e) {
        return dirChosenListeners.add(e);
    }
    
    public boolean removeNextDirChosenListener(INextDirChosenListener o) {
        return dirChosenListeners.remove(o);
    }
    
    public File getCurrentUnsortedDir() {
        return currentUnsortedDir;
    }
    
    public SongListMeta getCurrentUnsortedDirMeta() {
        return currentUnsortedDirGod;
    }

    public String getUnsortedLookupPath(String fileSystemPath) {
        String key = UnsortedDirList.PROPKEY_TO_SORT_ROOT_PATH;
        String path = config.getProperty(key);
        String lookupPath = fileSystemPath.replace(path, "TO_SORT");
        return lookupPath;
    }
    
    public String getSortedLookupPath(String fileSystemPath) {
        String key = DestinationDirDefinition.PROPKEY_TARGET_ROOT_PATH;
        String path = config.getProperty(key);
        String lookupPath = fileSystemPath.replace(path, "SORTED");
        return lookupPath;
    }

    /**
     * creates a {@link SongListMeta} instance for a specific directory
     * 
     * @param physicalLocation
     *            the file object for the dir where the files are located in
     * @return a {@link SongListMeta} representing the songs in the given
     *         dir
     */
    public SongListMeta getGenresOfUnsortedDir(File physicalLocation) {
        String lookupPath = getUnsortedLookupPath(physicalLocation.getAbsolutePath());
        SongListMeta god = new SongListMeta(metaDataLookup, lookupPath, physicalLocation);
        return god;
    }

    /**
     * creates a {@link SongListMeta} instance for a specific directory,
     * or loads it from the cache.
     * 
     * @param physicalLocation
     *            the file object for the dir where the files are located in
     * @param overrideLookupLocation
     *            a file where to search for metadata instead of the given file
     * @return a {@link SongListMeta} representing the songs in the given
     *         dir
     */
    public SongListMeta getSortedDirMeta(File physicalLocation) {
        File lookup = lookupPathCalculator.overrideLookupPath(physicalLocation);
        
        if (lookup == null) {
            lookup = physicalLocation;
        }
        
        String lookupPath = getSortedLookupPath(lookup.getAbsolutePath());
        String mappingKey = lookupPath + "@" + physicalLocation.getAbsolutePath();
        SongListMeta meta = null;
        
        if (cache ) {
            meta = cachedMetadata.get(mappingKey);
        }
        
        if (meta == null) {
            meta = new SongListMeta(metaDataLookup, lookupPath, physicalLocation);
            cachedMetadata.put(mappingKey, meta);
        }
        
        return meta;
    }
    
    public Config getConfig() {
        return config;
    }

    public IMetaDataSource getMatcher() {
        return metaDataLookup;
    }

    public DestinationTargets getDestinationTargets() {
        return destinationTargets;
    }
    
    public IActionExecutor getActionExecutor() {
        return actionExecutor;
    }

    public MPD getMPD() {
        return config.getMpd();
    }
    
}
