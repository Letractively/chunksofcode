package com.myapp.util.soundsorter.wizard.model;

import java.io.Serializable;

/**
 *  extracts a metadata value from a given ISong object
 */
public interface IPropertyExtractor extends Serializable
{
    String extractValue(ISong song);
}