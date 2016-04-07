package com.cndroid.imagepicker;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbangzhu on 4/6/16.
 */
public class PickupImageHolder {


    private static PickupImageHolder ourInstance = new PickupImageHolder();

    public static PickupImageHolder getInstance() {
        return ourInstance;
    }

    private PickupImageHolder() {
    }


    private int selectedCount = 0;
    private List<PickupImageItem> pickupImageItems;
    private List<PickupImageItem> filterPickupImageItems;

    public List<PickupImageItem> getPickupImageItems() {
        return pickupImageItems;
    }

    public void setPickupImageItems(List<PickupImageItem> pickupImageItems) {
        this.pickupImageItems = pickupImageItems;
    }


    public List<PickupImageItem> getFilterPickupImageItems() {
        return filterPickupImageItems;
    }

    public void setFilterPickupImageItems(List<PickupImageItem> filterPickupImageItems) {
        this.filterPickupImageItems = filterPickupImageItems;
    }

    public void processSelectedCount(PickupImageItem imageItem) {
        processSelectedCount(imageItem, null, null);
    }

    public void processSelectedCount(PickupImageItem imageItem, TextView tvCount, TextView tvDone) {
        if (imageItem.isSelected())
            increaseSelectedCount();
        else
            decreaseSelectedCount();

        setupTextViewState(tvCount, tvDone);
    }

    public void setupTextViewState(TextView tvCount, TextView tvDone) {
        if (tvCount != null) {
            tvCount.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
            tvCount.setText(String.valueOf(this.selectedCount));

            if (selectedCount != 0)
                tvCount.startAnimation(AnimationUtils.loadAnimation(tvCount.getContext(), R.anim.pi_anim_scaler));
        }

        if (tvDone != null) {
            tvDone.setEnabled(selectedCount > 0);
        }
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public int increaseSelectedCount() {
        selectedCount++;
        return selectedCount;
    }

    public int decreaseSelectedCount() {
        selectedCount--;
        selectedCount = selectedCount < 0 ? 0 : selectedCount;

        return selectedCount;
    }

    public ArrayList<String> getSelectedImagesUrl() {
        if (0 == selectedCount) return null;
        ArrayList<String> result = new ArrayList<>(selectedCount);
        for (PickupImageItem imageItem : filterPickupImageItems) {
            if (imageItem.isSelected()) result.add(imageItem.getImagePath());
        }

        return result;
    }


    public void flush() {
        if (null != pickupImageItems) pickupImageItems.clear();
        if (null != filterPickupImageItems) filterPickupImageItems.clear();

        pickupImageItems = null;
        filterPickupImageItems = null;
    }
}
