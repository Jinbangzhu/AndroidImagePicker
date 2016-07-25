package com.cndroid.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cndroid.pickimagelib.Intents;
import com.cndroid.pickimagelib.PickupImageBuilder;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.cndroid.pickimagelib.Intents.ImagePicker.REQUEST_CODE_PICKUP;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    public MainActivityFragment() {
    }

    private TextView textView;
    private String[] selectedImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        view.findViewById(R.id.btn_pickup_single_image).setOnClickListener(this);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKUP && resultCode == RESULT_OK && data != null) {
            selectedImages = data.getStringArrayExtra(Intents.ImagePicker.RESULT_ITEMS);
            if (null != selectedImages) {
                showSelectedImages();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pickup_single_image:
                PickupImageBuilder.with(MainActivityFragment.this)
                        .setTitle("Pickup Image")
                        .selectedImages(selectedImages)
                        .showCamera(true)
                        .setMaxChosenLimit(9)
                        .startPickupImage(new ImageLoader());
                break;
        }
    }

    private void showSelectedImages() {
        StringBuilder sb = new StringBuilder();
        for (String image : selectedImages) {
            File file = new File(image);
            sb.append(file.getName());
            sb.append("\n");
        }
        textView.setText(sb.toString());
    }


}
