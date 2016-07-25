package com.cndroid.pickimagelib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class AlbumItem implements Parcelable {
    private String albumName;
    private String albumImagePath;
    private int imageCount;
    private boolean hasChosen;

    public AlbumItem() {
    }

    private AlbumItem(Parcel in) {
        albumName = in.readString();
        albumImagePath = in.readString();
        imageCount = in.readInt();
        hasChosen = in.readByte() != 0;
    }

    public static final Creator<AlbumItem> CREATOR = new Creator<AlbumItem>() {
        @Override
        public AlbumItem createFromParcel(Parcel in) {
            return new AlbumItem(in);
        }

        @Override
        public AlbumItem[] newArray(int size) {
            return new AlbumItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(albumImagePath);
        dest.writeInt(imageCount);
        dest.writeByte((byte) (hasChosen ? 1 : 0));
    }
}
