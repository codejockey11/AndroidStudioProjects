package com.example.audioplayer;

public class MediaListItem {
    public String mArtist;
    public String mPerformer;
    public String mAlbum;
    public String mTitle;
    public String mUri;
    public Integer mIndex;

    public MediaListItem(String artist, String performer, String album, String title, String uri) {
        mArtist = artist;
        mPerformer = performer;
        mAlbum = album;
        mTitle = title;
        mUri = uri;
    }
}
