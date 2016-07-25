package com.cndroid.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cndroid.pickimagelib.PickupImageActivity;
import com.cndroid.pickimagelib.PickupImageBuilder;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    public MainActivityFragment() {
    }

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        view.findViewById(R.id.btn_pickup_single_image).setOnClickListener(this);
        return view;
    }

    String[] images;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickupImageActivity.REQUEST_CODE_PICKUP && resultCode == RESULT_OK && data != null) {
            images = data.getStringArrayExtra(PickupImageActivity.RESULT_ITEMS);
            if (null != images) {
                StringBuilder sb = new StringBuilder();
                for (String image : images) {
                    sb.append(image);
                    sb.append("\n");
                }
                textView.setText(sb.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pickup_single_image:
                PickupImageBuilder.with(MainActivityFragment.this)
                        .setTitle("选择要导入的脚印")
                        .selectedImages(images)
                        .setMaxChosenLimit(9)
//                        .registerCallBackListener(this)
                        .startPickupImage(new ImageLoader());
                break;
        }
    }


}
