package com.myapp.util.soundsorter.wizard.model;

final class GenreExtractor implements IPropertyExtractor 
{
    private static final long serialVersionUID = 7761647947034124023L;

    @Override
    public String extractValue(ISong song) {
        return (song.getGenre() == null) 
                    ? null 
                    : song.getGenre().trim().toLowerCase();
    }
}