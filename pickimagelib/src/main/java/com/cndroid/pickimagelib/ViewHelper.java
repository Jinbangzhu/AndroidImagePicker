package com.cndroid.pickimagelib;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by jinbangzhu on 7/24/16.
 */

public class ViewHelper {

    public static void setupTextViewState(TextView tvCount, TextView tvDone, int selectedCount) {
        if (tvCount != null) {
            tvCount.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
            tvCount.setText(String.valueOf(selectedCount));

            if (selectedCount != 0)
                tvCount.startAnimation(AnimationUtils.loadAnimation(tvCount.getContext(), R.anim.pi_anim_scaler));
        }

        if (tvDone != null) {
            tvDone.setEnabled(selectedCount > 0);
        }
    }
}
