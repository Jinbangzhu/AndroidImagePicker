package com.cndroid.pickimagelib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

/**
 * Created by jinbangzhu on 4/7/16.
 */
public class PickupImageBuilder {

    private Intent intent;


    private Activity activity;
    private android.app.Fragment fragment;
    private android.support.v4.app.Fragment supportFragment;

    public static PickupImageBuilder with(Activity appCompatActivity) {
        PickupImageBuilder builder = new PickupImageBuilder();
        builder.activity = appCompatActivity;
        builder.intent = new Intent(builder.activity, PickupImageActivity.class);
        return builder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static PickupImageBuilder with(android.app.Fragment fragment) {
        PickupImageBuilder builder = new PickupImageBuilder();
        builder.activity = fragment.getActivity();
        builder.fragment = fragment;
        builder.intent = new Intent(fragment.getContext(), PickupImageActivity.class);
        return builder;
    }

    public static PickupImageBuilder with(android.support.v4.app.Fragment fragment) {
        PickupImageBuilder builder = new PickupImageBuilder();
        builder.activity = fragment.getActivity();
        builder.supportFragment = fragment;
        builder.intent = new Intent(fragment.getContext(), PickupImageActivity.class);
        return builder;
    }

    public PickupImageBuilder setTitle(String title) {
        intent.putExtra("title", title);
        return this;
    }

    public PickupImageBuilder setTitle(int title) {
        intent.putExtra("titleRes", title);
        return this;
    }

    public PickupImageBuilder setMaxChosenLimit(int limit) {
        intent.putExtra("limit", limit);
        return this;
    }

    public PickupImageBuilder defaultChosen() {
        intent.putExtra("defaultChosen", true);
        return this;
    }

    public PickupImageBuilder DCIMOnly() {
        intent.putExtra("dcimOnly", true);
        return this;
    }

    public PickupImageBuilder selectedImages(String[] selectedImages) {
        intent.putExtra("selectedImages", selectedImages);
        return this;
    }

    /**
     * from startTime , between startTime to now
     *
     * @param startTime start time of image
     * @return
     */
    public PickupImageBuilder startTime(long startTime) {
        intent.putExtra("startTime", startTime);
        return this;
    }

//    public PickupImageBuilder registerCallBackListener(PickupImageCallBack pickupImageCallBack) {
//        PickupImageHolder.getInstance().registerPickupImageCallBack(pickupImageCallBack);
//        return this;
//    }


    public void startPickupImage() {
        if (null != fragment)
            fragment.startActivityForResult(intent, PickupImageActivity.REQUEST_CODE_PICKUP);
        else if (null != supportFragment)
            supportFragment.startActivityForResult(intent, PickupImageActivity.REQUEST_CODE_PICKUP);
        else
            activity.startActivityForResult(intent, PickupImageActivity.REQUEST_CODE_PICKUP);
    }
}
