package com.cndroid.imagepicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class AlbumChooserAdapter extends RecyclerView.Adapter<AlbumChooserAdapter.ViewHolder> {
    public void setAlbumItems(List<AlbumItem> albumItems) {
        this.albumItems = albumItems;
    }

    private List<AlbumItem> albumItems;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AlbumItem albumItem = albumItems.get(position);
        Glide.with(holder.imageView.getContext())
                .load(albumItem.getAlbumImageUrl())
                .into(holder.imageView);
        holder.tvAlbumName.setText(albumItem.getAlbumName());
        holder.tvAlbumImageCount.setText(holder.imageView.getContext().getString(R.string.pickup_image_count_unit, albumItem.getImageCount()));
        holder.ivIsChoosed.setVisibility(albumItem.isHasChosen() ? View.VISIBLE : View.GONE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener)
                    onItemClickListener.onItemClicked(albumItem, position);
            }
        });
//        if (albumItem.is)
    }

    @Override
    public int getItemCount() {
        return albumItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(AlbumItem item, int position);
    }


    public void unChooseAll() {
        for (AlbumItem item : albumItems) {
            item.setHasChosen(false);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private ImageView imageView, ivIsChoosed;
        private TextView tvAlbumName, tvAlbumImageCount;
        private View view;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.iv_album_cover);
            ivIsChoosed = (ImageView) v.findViewById(R.id.iv_choosed);
            tvAlbumName = (TextView) v.findViewById(R.id.tv_album_name);
            tvAlbumImageCount = (TextView) v.findViewById(R.id.tv_album_image_count);
            view = v;
        }
    }

}
