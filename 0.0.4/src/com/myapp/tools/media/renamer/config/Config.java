package com.myapp.tools.media.renamer.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myapp.tools.media.renamer.model.INamePart;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.naming.impl.BeschreibungNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.DatumNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.ExtensionNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.NummerierungNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.OriginalNameNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.PrefixNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.SuffixNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.ThemaNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.TitelNamePart;

/**
 * provides a IRenamerConfiguration singleton and implements the renamer
 * specific methods. the name elements configuration of the application is also
 * being parsed by this class.
 * 
 * @author andre
 * 
 */
public final class Config extends CustomConfiguration 
                          implements IConstants.IIndexConstants,
                                     IConstants.ISysConstants,
                                     IConstants.INameConstants,
                                     IRenamerConfiguration {

    protected static final Map<Class<? extends INamePart>, Integer>
                           DEFAULT_NAME_ELEMENTS;
    
    static {
        /*init DEFAULT_NAME_ELEMENTS */
        Map<Class<? extends INamePart>, Integer> npm;
        npm = new HashMap<Class<? extends INamePart>, Integer>();

        npm.put(PrefixNamePart.class,       Integer.MIN_VALUE);
        npm.put(BeschreibungNamePart.class, defaultInt(INDEX_BESCHREIBUNG));
        npm.put(DatumNamePart.class,        defaultInt(INDEX_DATUM));
        npm.put(NummerierungNamePart.class, defaultInt(INDEX_NUMMERIERUNG));
        npm.put(OriginalNameNamePart.class, defaultInt(INDEX_ORIGINALNAME));
        npm.put(ThemaNamePart.class,        defaultInt(INDEX_THEMA));
        npm.put(TitelNamePart.class,        defaultInt(INDEX_TITEL));
        npm.put(SuffixNamePart.class,       Integer.MAX_VALUE -1);
        npm.put(ExtensionNamePart.class,    Integer.MAX_VALUE);

        DEFAULT_NAME_ELEMENTS = Collections.unmodifiableMap(npm);
        L.finer("default name elements loaded");
    }



    private static volatile IRenamerConfiguration singleton = null;

    /**
     * global reference to the irenamerconfiguration singleton
     * 
     * @return the only one instance needed by the application
     */
    public static final IRenamerConfiguration getInstance() {
        if (singleton == null)
            synchronized (Config.class) {
                if (singleton == null) 
                    singleton = new Config();
            }

        return singleton;
    }


    private Map<Class<? extends INamePart>, Integer> customNameElements;



    /**
     * sole default constructor, private access.
     */
    private Config() {
        customNameElements = new HashMap<Class<? extends INamePart>, Integer>();
    }



    @Override
    public List<INamePart> getNameElementsList(IRenamer renamer) {
        Set<Class<? extends INamePart>> classes;
        classes = new HashSet<Class<? extends INamePart>>();
        classes.addAll(DEFAULT_NAME_ELEMENTS.keySet());
        classes.addAll(customNameElements.keySet());

        List<INamePart> l = new ArrayList<INamePart>();
        for (Class<? extends INamePart> c : classes) {
            try {
                int index = getIndex(c);
                if (    index >= 0 ||
                        c == PrefixNamePart.class || 
                        c == SuffixNamePart.class || 
                        c == ExtensionNamePart.class    ) {
                    l.add(instance(c, renamer));
                }
            } catch (Exception e) {
                L.throwing(DefaultConfiguration.class.getName(),
                                                      "getNameElementsList", e);
                throw new RuntimeException(e);
            }
        }

        Collections.sort(l, this);
        return l;
    }

    @Override
    public void clearCustomProperties() {
        super.clearCustomProperties();

        if (customNameElements != null && customNameElements.size() > 0)
            customNameElements.clear();
    }

    @Override
    public Integer getIndex(Class<? extends INamePart> c) {
        Integer i = customNameElements.get(c);
        if (i == null)
            i = defaultIndex(c);
        if (i == null)
            throw new RuntimeException("not in map : " + c);
        return i;
    }

    @Override
    public void setCustomNameElement(Class<? extends INamePart> c, int i) {
        customNameElements.put(c, i);
        L.fine("custom elem set :k=" + c.getSimpleName() + ".class, v=" + i);
    }

    @Override
    public int compare(INamePart o1, INamePart o2) {
        return getIndex(o1.getClass()).compareTo(getIndex(o2.getClass()));
    }

    /**
     * returns the defaultvalue for the index of a name elements type's order.
     * 
     * @param c
     *            the type of the nameelement
     * @return the index of the nameelement
     */
    private static final Integer defaultIndex(Class<? extends INamePart> c) {
        return DEFAULT_NAME_ELEMENTS.get(c);
    }

    /**
     * creates an instance of a given class and returns it as an inameelement
     * 
     * @param c
     *            the class we want an instance of
     * @return an instance of the given class
     */
    private static final INamePart instance(Class<?> c, IRenamer renamer) {
        try {
            Object o = c.newInstance();
            INamePart np = (INamePart) o;
            np.init(renamer);
            return np;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setCustomProperty(String key, String value) {

        // workaround, because of broken file paths in windows env on
        // paths like C:\nStartsWithN\rStartsWithR\tStartsWithT :
        if (   key.equals(LAST_ACCESSED_FILE_PATH)
            || key.equals(DESTINATION_RENAMED_FILES)) {
            
            // System.out.println("workaround before: " + value);
            value = replaceEnvironmentVariables(value);
            // System.out.println("workaround after:  " + value);
        }

        super.setCustomProperty(key, value);
    }



    /**
     * overwrites the content of the configfile with the given string.
     * a backup of the config file will be created.
     * @param newProps
     * @throws IOException
     */
    public void overWriteConfig(String newProps) throws IOException {
        PropertiesIO.overWrite(newProps);
    }   




    // dumb shortcut methods to implement irenamerconfiguration 






    @ Override
    public int getIndexBeschreibung() {
        return getInt(INDEX_BESCHREIBUNG);
    }

    @ Override
    public int getIndexDatum() {
        return getIndex(DatumNamePart.class);
    }

    @ Override
    public int getIndexNummerierung() {
        return getIndex(NummerierungNamePart.class);
    }

    @ Override
    public int getIndexOriginalname() {
        return getIndex(OriginalNameNamePart.class);
    }

    @ Override
    public int getIndexThema() {
        return getIndex(ThemaNamePart.class);
    }

    @ Override
    public int getIndexTitel() {
        return getIndex(TitelNamePart.class);
    }

    @ Override
    public String getPrefix() {
        return getString(PREFIX);
    }

    @ Override
    public String getSuffix() {
        return getString(SUFFIX);
    }

    @ Override
    public String getDatumFormat() {
        return getString(DATUM_FORMAT);
    }

    @ Override
    public String getDatumPrefix() {
        return getString(DATUM_PREFIX);
    }

    @ Override
    public String getDatumSuffix() {
        return getString(DATUM_SUFFIX);
    }

    @ Override
    public String getNummerierungPrefix() {
        return getString(NUMMERIERUNG_PREFIX);
    }

    @ Override
    public String getNummerierungSuffix() {
        return getString(NUMMERIERUNG_SUFFIX);
    }

    @ Override
    public int getNummerierungStart() {
        return getInt(NUMMERIERUNG_START);
    }

    @ Override
    public int getNummerierungIncrement() {
        return getInt(NUMMERIERUNG_ANSTIEG);
    }

    @ Override
    public String getTitelPrefix() {
        return getString(TITEL_PREFIX);
    }

    @ Override
    public String getTitelSuffix() {
        return getString(TITEL_SUFFIX);
    }

    @ Override
    public String getThemaPrefix() {
        return getString(THEMA_PREFIX);
    }

    @ Override
    public String getThemaSuffix() {
        return getString(THEMA_SUFFIX);
    }

    @ Override
    public String getBeschreibungPrefix() {
        return getString(BESCHREIBUNG_PREFIX);
    }

    @ Override
    public String getBeschreibungSuffix() {
        return getString(BESCHREIBUNG_SUFFIX);
    }

    @ Override
    public String getOrigNamePrefix() {
        return getString(ORIGINALNAME_PREFIX);
    }

    @ Override
    public String getOrigNameSuffix() {
        return getString(ORIGINALNAME_SUFFIX);
    }

    @ Override
    public boolean isOrigNameMitSuffix() {
        return getBoolean(ORIGINALNAME_MIT_ENDUNG);
    }

    @ Override
    public String getDefaultBeschreibung() {
        return getString(DEFAULT_NAME_BESCHREIBUNG);
    }

    @ Override
    public String getDefaultThema() {
        return getString(DEFAULT_NAME_THEMA);
    }

    @ Override
    public String getDefaultTitel() {
        return getString(DEFAULT_NAME_TITEL);
    }

    @ Override
    public String getDestinationPath() {
        return getTranslatedSystemProperty(DESTINATION_RENAMED_FILES);
    }
    
    @Override
    public String getLastAccessedPath() {
        return getTranslatedSystemProperty(LAST_ACCESSED_FILE_PATH);
    }
}
