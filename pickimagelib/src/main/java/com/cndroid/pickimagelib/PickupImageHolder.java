package com.cndroid.pickimagelib;

import com.cndroid.pickimagelib.bean.PickupImageItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jinbangzhu on 4/6/16.
 */
public class PickupImageHolder implements Serializable {

    private int selectedCount = 0;
    private int limit;

    private List<PickupImageItem> allImageItems;
    private List<PickupImageItem> filteredPickupImageItems;

    public List<PickupImageItem> getAllImageItems() {
        return allImageItems;
    }

    public void setAllImageItems(List<PickupImageItem> allImageItems) {
        this.allImageItems = allImageItems;
    }


    public List<PickupImageItem> getFilteredPickupImageItems() {
        return filteredPickupImageItems;
    }

    public void setFilteredPickupImageItems(List<PickupImageItem> filteredPickupImageItems) {
        this.filteredPickupImageItems = filteredPickupImageItems;
    }


    public void processSelectedCount(PickupImageItem imageItem) {
        if (imageItem.isSelected())
            increaseSelectedCount();
        else
            decreaseSelectedCount();
    }

    public boolean isFull() {
        return selectedCount >= this.limit;
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
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

    public String[] getSelectedImages() {
        if (0 == selectedCount) return null;
        String[] result = new String[selectedCount];
        int position = 0;
        for (PickupImageItem filterPickupImageItem : filteredPickupImageItems) {
            if (filterPickupImageItem.isSelected()) {
                result[position] = filterPickupImageItem.getImagePath();
                position++;
            }
        }
        return result;
    }

//    public void registerPickupImageCallBack(PickupImageCallBack imagePickupCallBack) {
//        this.imagePickupCallBack = imagePickupCallBack;
//    }


    public void flush() {
        if (null != allImageItems) allImageItems.clear();
        if (null != filteredPickupImageItems) filteredPickupImageItems.clear();

        allImageItems = null;
        filteredPickupImageItems = null;
        selectedCount = 0;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
