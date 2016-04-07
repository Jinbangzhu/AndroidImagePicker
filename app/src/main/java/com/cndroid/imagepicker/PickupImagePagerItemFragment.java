package com.cndroid.imagepicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by jinbangzhu on 4/6/16.
 */
public class PickupImagePagerItemFragment extends Fragment {
    private PickupImageItem imageItem;
    private PhotoView photoView;

    public static PickupImagePagerItemFragment getInstance(PickupImageItem imageItem) {
        PickupImagePagerItemFragment fragment = new PickupImagePagerItemFragment();
        fragment.imageItem = imageItem;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pickup_image_pager_item, container, false);
        photoView = (PhotoView) view.findViewById(R.id.iv_preview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(getContext())
                .load(imageItem.getImageUri())
                .into(photoView);
    }
}
