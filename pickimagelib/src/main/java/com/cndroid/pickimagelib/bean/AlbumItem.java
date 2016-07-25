package com.cndroid.pickimagelib.bean;

import java.io.Serializable;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class AlbumItem implements Serializable {
    private String albumName;
    private String albumImagePath;
    private int imageCount;
    private boolean hasChosen;

    public AlbumItem() {
    }

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


    public int getImageCount() {
        return imageCount;
    }

    public void increaseImageCount() {
        imageCount++;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }


    public boolean isHasChosen() {
        return hasChosen;
    }

    public void setHasChosen(boolean hasChosen) {
        this.hasChosen = hasChosen;
    }
}
