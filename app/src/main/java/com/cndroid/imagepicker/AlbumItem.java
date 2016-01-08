package com.cndroid.imagepicker;

import android.net.Uri;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class AlbumItem {
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImagePath() {
        return albumImagePath;
    }

    public void setAlbumImagePath(String albumImagePath) {
        this.albumImagePath = albumImagePath;
    }

    private String albumName;
    private String albumImagePath;

    public Uri getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(Uri albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    private Uri albumImageUrl;
}
