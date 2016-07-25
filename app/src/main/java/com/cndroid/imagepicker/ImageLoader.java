package com.cndroid.imagepicker;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cndroid.pickimagelib.PickupImageDisplay;

/**
 * Created by jinbangzhu on 7/25/16.
 */

public class ImageLoader extends PickupImageDisplay {
    @Override
    public void displayImage(ImageView imageView, String fullImagePath) {
        Glide.with(imageView.getContext())
                .load(Uri.parse("file://" + fullImagePath))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(imageView);
    }


    @Override
    public void showTipsForLimitSelect(int limit) {
        Toast.makeText(App.getInstance(), "max allow" + limit, Toast.LENGTH_LONG).show();
    }
}
