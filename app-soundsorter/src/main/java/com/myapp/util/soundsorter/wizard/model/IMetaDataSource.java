package com.myapp.util.soundsorter.wizard.model;

import java.util.Collection;

public interface IMetaDataSource 
{

    Collection<ISong> getSongsInDirectory(String pathPart);

}
