package com.myapp.tools.media.renamer.config;

import java.util.Comparator;

import com.myapp.tools.media.renamer.model.INamePart;


public interface IRenamerConfiguration extends IRenamerConfigurable,
                                               Comparator<INamePart>  {
    String      getPrefix();
    String      getSuffix();

    String      getDatumFormat();
    String      getDatumPrefix();
    String      getDatumSuffix();

    String      getNummerierungPrefix();
    String      getNummerierungSuffix();
    int         getNummerierungStart();
    int         getNummerierungIncrement();

    String      getTitelPrefix();
    String      getTitelSuffix();
    String      getThemaPrefix();
    String      getThemaSuffix();
    String      getBeschreibungPrefix();
    String      getBeschreibungSuffix();

    String      getOrigNamePrefix();
    String      getOrigNameSuffix();
    boolean     isOrigNameMitSuffix();

    String      getDefaultBeschreibung();
    String      getDefaultThema();
    String      getDefaultTitel();    

    int         getIndexBeschreibung();
    int         getIndexDatum();
    int         getIndexNummerierung();
    int         getIndexOriginalname();
    int         getIndexThema();
    int         getIndexTitel();

    String      getDestinationPath();
    String      getLastAccessedPath();
}
