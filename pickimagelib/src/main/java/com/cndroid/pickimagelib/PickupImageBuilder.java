package com.cndroid.pickimagelib;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by jinbangzhu on 4/7/16.
 */
public class PickupImageBuilder {

    private Activity activity;
    private Intent intent;

    public static PickupImageBuilder with(Activity appCompatActivity) {
        PickupImageBuilder builder = new PickupImageBuilder();
        builder.activity = appCompatActivity;
        builder.intent = new Intent(appCompatActivity, PickupImageActivity.class);
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

    public PickupImageBuilder registerCallBackListener(PickupImageCallBack pickupImageCallBack) {
        PickupImageHolder.getInstance().registerPickupImageCallBack(pickupImageCallBack);
        return this;
    }


    public void startPickupImage() {
        activity.startActivityForResult(intent, PickupImageActivity.REQUEST_CODE_PICKUP);
    }
}
