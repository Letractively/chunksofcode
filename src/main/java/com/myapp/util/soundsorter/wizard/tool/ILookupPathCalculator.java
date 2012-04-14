package com.myapp.util.soundsorter.wizard.tool;

import java.io.File;

public interface ILookupPathCalculator
{
    public abstract File overrideLookupPath(File physicalLocation);
    
    
    
    public static final class Default implements ILookupPathCalculator {
        @Override
        public File overrideLookupPath(File physicalLocation) {
            return physicalLocation;
        }
    }
}