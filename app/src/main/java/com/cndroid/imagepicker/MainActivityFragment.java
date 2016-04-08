package com.cndroid.imagepicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cndroid.pickimagelib.PickupImageBuilder;
import com.cndroid.pickimagelib.PickupImageCallBack;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener, PickupImageCallBack {

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.findViewById(R.id.btn_pickup_single_image).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pickup_single_image:
                PickupImageBuilder.with(getActivity())
                        .setTitle("选择要导入的脚印")
                        .defaultChosen()
                        .registerCallBackListener(this)
                        .startPickupImage();
                break;
        }
    }

    @Override
    public void onGetUserChosenImages(List<String> imageUrls) {
        Log.d("getUserChosenImages", imageUrls.toString());
    }
}
