package com.cndroid.pickimagelib;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by jinbangzhu on 7/25/16.
 */

public abstract class PickupImageDisplay implements Serializable {
    /**
     * display image
     */
    public abstract void displayImage(ImageView imageView, String fullImagePath);

    /**
     * camera
     *
     * @param imageView
     */
    public abstract void displayCameraImage(ImageView imageView);

    /**
     * show tips when selected images is full
     *
     * @param limit max allow selected
     */
    public abstract void showTipsForLimitSelect(int limit);
}
