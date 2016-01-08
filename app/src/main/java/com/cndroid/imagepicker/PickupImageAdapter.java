package com.cndroid.imagepicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class PickupImageAdapter extends RecyclerView.Adapter<PickupImageAdapter.ViewHolder> {
    public void setImageItemList(List<PickupImageItem> imageItemList) {
        this.imageItemList = imageItemList;
    }

    private List<PickupImageItem> imageItemList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickup_image_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(imageItemList.get(position).getImageUri())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageItemList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.iv_image);
        }
    }

}
