package com.myapp.tools.media.renamer.config;


/**
 * Definition of constant strings used in the properties file.
 * @author andre
 */
public interface IConstants {

    /**
     * constants used to define the name-elements order
     * @author andre
     */
    interface IIndexConstants {
        String INDEX_DATUM = "INDEX_DATUM";
        String INDEX_NUMMERIERUNG = "INDEX_NUMMERIERUNG";
        String INDEX_THEMA = "INDEX_THEMA";
        String INDEX_TITEL = "INDEX_TITEL";
        String INDEX_BESCHREIBUNG = "INDEX_BESCHREIBUNG";
        String INDEX_ORIGINALNAME = "INDEX_ORIGINALNAME";
    }

    /**
     * constants used to define the name-elements and their properties
     * @author andre
     */
    interface INameConstants {
        String PREFIX = "PREFIX";
        String SUFFIX = "SUFFIX";
        //
        String DATUM_PREFIX = "DATUM_PREFIX";
        String DATUM_FORMAT = "DATUM_FORMAT";
        String DATUM_SUFFIX = "DATUM_SUFFIX";
        //
        String NUMMERIERUNG_PREFIX = "NUMMERIERUNG_PREFIX";
        String NUMMERIERUNG_START = "NUMMERIERUNG_START";
        String NUMMERIERUNG_ANSTIEG = "NUMMERIERUNG_ANSTIEG";
        String NUMMERIERUNG_SUFFIX = "NUMMERIERUNG_SUFFIX";
        //
        String THEMA_PREFIX = "THEMA_PREFIX";
        String THEMA_SUFFIX = "THEMA_SUFFIX";
        //
        String TITEL_PREFIX = "TITEL_PREFIX";
        String TITEL_SUFFIX = "TITEL_SUFFIX";
        //
        String BESCHREIBUNG_PREFIX = "BESCHREIBUNG_PREFIX";
        String BESCHREIBUNG_SUFFIX = "BESCHREIBUNG_SUFFIX";
        //
        String ORIGINALNAME_PREFIX = "ORIGINALNAME_PREFIX";
        String ORIGINALNAME_SUFFIX = "ORIGINALNAME_SUFFIX";
        String ORIGINALNAME_MIT_ENDUNG = "ORIGINALNAME_MIT_ENDUNG";
        //
        String FILE_NAME_EXTENSION = "FILE_NAME_EXTENSION";
    }

    /**
     * internal properties
     * @author andre
     */
    interface ISysConstants {
        String DEFAULT_NAME_TITEL = "DEFAULT_NAME_TITEL";
        String DEFAULT_NAME_THEMA = "DEFAULT_NAME_THEMA";
        String DEFAULT_NAME_BESCHREIBUNG = "DEFAULT_NAME_BESCHREIBUNG";
        String REPLACE_ORIGINAL_FILES = "REPLACE_ORIGINAL_FILES";
        String DESTINATION_RENAMED_FILES = "DESTINATION_RENAMED_FILES";
        String APPLICATION_LOG_LEVEL = "APPLICATION_LOG_LEVEL";
        String FILE_FILTERS = "FILE_FILTERS";


        // gui:
        String WINDOW_DEFAULT_HEIGHT ="WINDOW_DEFAULT_HEIGHT";
        String WINDOW_DEFAULT_WIDTH = "WINDOW_DEFAULT_WIDTH";
        String WINDOW_POSITION_X = "WINDOW_POSITION_X";
        String WINDOW_POSITION_Y = "WINDOW_POSITION_Y";
        
        String EXIT_WITHOUT_ASKING = "EXIT_WITHOUT_ASKING";
        String SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES";
        String LAST_ACCESSED_FILE_PATH = "LAST_ACCESSED_FILE_PATH";
        String RECURSE_INTO_DIRECTORIES = "RECURSE_INTO_DIRECTORIES";

        String FILECHOOSER_DEFAULT_HEIGHT = "FILECHOOSER_DEFAULT_HEIGHT";
        String FILECHOOSER_DEFAULT_WIDTH = "FILECHOOSER_DEFAULT_WIDTH";
        String FILECHOOSER_POSITION_X = "FILECHOOSER_POSITION_X";
        String FILECHOOSER_POSITION_Y = "FILECHOOSER_POSITION_Y";

        String EXCLUDE_DUPLICATE_FILES = "EXCLUDE_DUPLICATE_FILES";
        String OVERWRITE_EXISTING_FILES = "OVERWRITE_EXISTING_FILES";
        
        
        
        String COLUMN_WIDTH_BESCHREIBUNG = "COLUMN_WIDTH_BESCHREIBUNG";
        String COLUMN_WIDTH_THEMA = "COLUMN_WIDTH_THEMA";
        String COLUMN_WIDTH_TITEL = "COLUMN_WIDTH_TITEL";
        String COLUMN_WIDTH_DATUM = "COLUMN_WIDTH_DATUM";
        String COLUMN_WIDTH_NUMMER = "COLUMN_WIDTH_NUMMER";
        String COLUMN_WIDTH_TYP = "COLUMN_WIDTH_TYP";
        String COLUMN_WIDTH_DATEIGROESSE = "COLUMN_WIDTH_DATEIGROESSE";
        String COLUMN_WIDTH_QUELLDATEI = "COLUMN_WIDTH_QUELLDATEI";
        String COLUMN_WIDTH_ZIELDATEI = "COLUMN_WIDTH_ZIELDATEI";
        
        String COLUMN_NAME_BESCHREIBUNG = "COLUMN_NAME_BESCHREIBUNG";
        String COLUMN_NAME_NUMMER = "COLUMN_NAME_NUMMER";
        String COLUMN_NAME_QUELLDATEI = "COLUMN_NAME_QUELLDATEI";
        String COLUMN_NAME_THEMA = "COLUMN_NAME_THEMA";
        String COLUMN_NAME_DATUM = "COLUMN_NAME_DATUM";
        String COLUMN_NAME_TYP = "COLUMN_NAME_TYP";
        String COLUMN_NAME_TITEL = "COLUMN_NAME_TITEL";
        String COLUMN_NAME_DATEIGROESSE = "COLUMN_NAME_DATEIGROESSE";
        String COLUMN_NAME_ZIELDATEI = "COLUMN_NAME_ZIELDATEI";
        
        
        String LOOK_AND_FEEL = "LOOK_AND_FEEL";
        String LOOK_N_FEEL_CROSS_PLATFORM = "CROSS_PLATFORM";
        String LOOK_N_FEEL_SYSTEM = "SYSTEM_DEFAULT";
    }
}
