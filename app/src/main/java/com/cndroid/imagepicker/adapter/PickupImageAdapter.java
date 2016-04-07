package com.cndroid.imagepicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cndroid.imagepicker.bean.PickupImageItem;
import com.cndroid.imagepicker.R;
import com.cndroid.imagepicker.views.SelectableImageView;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PickupImageItem item = imageItemList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUri())
                .into(holder.imageView);
        holder.imageView.isSelected(item.isSelected());

        if (item.isSelected()) {
            holder.imageViewCheckState.setImageResource(R.mipmap.ip_icon_check_on);
        } else {
            holder.imageViewCheckState.setImageResource(R.mipmap.ip_icon_check_off);
        }

        holder.imageViewCheckState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener)
                    onItemClickListener.onCheckBoxImageChecked(item, position);
            }
        });
        holder.imageView.setTapListener(new SelectableImageView.ITapListener() {
            @Override
            public void onTaped() {
                if (null != onItemClickListener)
                    onItemClickListener.onImageViewTaped(position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onImageViewTaped(int position);

        void onCheckBoxImageChecked(PickupImageItem item, int position);
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
        private ImageView imageViewCheckState;

        public ViewHolder(View v) {
            super(v);
            imageView = (SelectableImageView) v.findViewById(R.id.iv_image);
            imageViewCheckState = (ImageView) v.findViewById(R.id.iv_check_state);
        }
    }

}
