package com.cndroid.pickimagelib.bean;

import java.io.Serializable;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class PickupImageItem implements Serializable {

    private boolean selected;
    private String imagePath;
    private long dateAdded;
    private String albumName;


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }


}
