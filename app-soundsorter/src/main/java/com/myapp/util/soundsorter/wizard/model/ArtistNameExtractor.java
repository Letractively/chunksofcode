package com.myapp.util.soundsorter.wizard.model;

final class ArtistNameExtractor implements IPropertyExtractor 
{
    private static final long serialVersionUID = -1872538671685540955L;

    @Override
    public String extractValue(ISong song) {
        return (song.getArtist() == null) 
                    ? null 
                    : song.getArtist().trim().toLowerCase();
    }
}