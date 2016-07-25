package com.cndroid.pickimagelib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cndroid.pickimagelib.bean.PickupImageItem;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by jinbangzhu on 4/6/16.
 */
public class PickupImagePagerItemFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pi_layout_pickup_image_pager_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.iv_preview);
        PickupImageDisplay imageDisplay = (PickupImageDisplay) getArguments().getSerializable(Intents.ImagePicker.IMAGEDISPLAY);

        PickupImageItem imageItem = (PickupImageItem) getArguments().getSerializable(Intents.ImagePicker.IMAGEITEM);
        if (null == imageItem || imageDisplay == null) return;

        imageDisplay.displayImage(photoView, imageItem.getImagePath());
    }
}
