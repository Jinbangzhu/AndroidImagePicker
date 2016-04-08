package com.cndroid.pickimagelib.bean;

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

    public Uri getAlbumImageUri() {
        return albumImageUrl;
    }

    public void setAlbumImageUri(Uri albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    private Uri albumImageUrl;

    public int getImageCount() {
        return imageCount;
    }

    public void increaseImageCount() {
        imageCount++;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    private int imageCount;

    public boolean isHasChosen() {
        return hasChosen;
    }

    public void setHasChosen(boolean hasChosen) {
        this.hasChosen = hasChosen;
    }

    private boolean hasChosen;
}
