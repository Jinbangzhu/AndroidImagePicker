package com.cndroid.imagepicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PickupImageItem item = imageItemList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUri())
                .into(holder.imageView);
        holder.imageView.isSelected(item.isSelected());
        holder.imageView.setTapListener(new SelectableImageView.ITapListener() {
            @Override
            public void onTaped() {
                item.setSelected(!item.isSelected());
                notifyItemChanged(position);
            }
        });
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

        private SelectableImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (SelectableImageView) v.findViewById(R.id.iv_image);
        }
    }

}
