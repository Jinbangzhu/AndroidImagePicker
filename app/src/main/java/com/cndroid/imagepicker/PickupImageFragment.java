package com.cndroid.imagepicker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cndroid.imagepicker.adapter.AlbumChooserAdapter;
import com.cndroid.imagepicker.adapter.PickupImageAdapter;
import com.cndroid.imagepicker.bean.AlbumItem;
import com.cndroid.imagepicker.bean.PickupImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbangzhu on 1/11/16.
 */
public class PickupImageFragment extends Fragment implements View.OnClickListener {

    private List<PickupImageItem> pickupImageItems;
    private List<AlbumItem> albumItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pickup_image, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        TextView tvCurrentAlbumName = (TextView) view.findViewById(R.id.tv_current_album_name);
        ImageView viewDummy = (ImageView) view.findViewById(R.id.view_dummy);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);


        tvCurrentAlbumName.setOnClickListener(this);

        getImageFromMedia();

        PickupImageAdapter adapter = new PickupImageAdapter();
        adapter.setImageItemList(pickupImageItems);
        mRecyclerView.setAdapter(adapter);
    }

    public void onClickCurrentAlbumName() {
        // show popup window
        View albumChooserView = View.inflate(getContext(), R.layout.album_list, null);

        RecyclerView recyclerView = (RecyclerView) albumChooserView.findViewById(R.id.album_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        AlbumChooserAdapter adapter = new AlbumChooserAdapter();
        adapter.setAlbumItems(albumItems);

        recyclerView.setAdapter(adapter);

        float itemHeight = getResources().getDimension(R.dimen.pi_album_selector_item_height);
        int autoHeight = (int) (itemHeight * albumItems.size());

        int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels - itemHeight * 2);
        autoHeight = autoHeight > maxHeight ? maxHeight : autoHeight;


        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        albumChooserView.getLayoutParams().height = autoHeight;
        dialog.setContentView(albumChooserView);

//        dialog.show();


//        final PopupWindow popupWindow = new PopupWindow(albumChooserView, LinearLayout.LayoutParams.MATCH_PARENT, autoHeight, true);
//        popupWindow.setAnimationStyle(R.style.PickupImageAlbumChooserAnimation);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.showAtLocation(rlBottomBar, Gravity.LEFT | Gravity.BOTTOM, 0, rlBottomBar.getHeight());
//        mDrawable.startTransition(200);
//
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mDrawable.reverseTransition(200);
//            }
//        });
//        albumChooserView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//
//            }
//        });

    }


    private void getImageFromMedia() {
        String lastAlbumName = null;
        pickupImageItems = new ArrayList<>();
        albumItems = new ArrayList<>();

        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            assert imageCursor != null;


            while (imageCursor.moveToNext()) {
                PickupImageItem pickupImageItem = new PickupImageItem();


                String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                String imgPath = fullPath.substring(0, fullPath.lastIndexOf("/"));
                String albumName = imgPath.substring(imgPath.lastIndexOf("/") + 1, imgPath.length());

                pickupImageItem.setAlbumName(albumName);
                pickupImageItem.setImagePath(fullPath);
                pickupImageItem.setSelected(false);
                pickupImageItem.setImageUri(Uri.parse("file://" + fullPath));

                pickupImageItems.add(pickupImageItem);


                // add album
                if (TextUtils.isEmpty(lastAlbumName)) {
                    AlbumItem item = new AlbumItem();
                    item.setAlbumName(getString(R.string.pickup_image_all_images));
                    item.setAlbumImagePath(fullPath);
                    item.setAlbumImageUrl(Uri.parse("file://" + fullPath));
                    albumItems.add(item);
                }

                // All Images to increase
                albumItems.get(0).increaseImageCount();

                boolean isAddedAlbum = false;
                for (AlbumItem albumItem : albumItems) {
                    if (albumName.equals(albumItem.getAlbumName())) {
                        isAddedAlbum = true;
                        albumItem.increaseImageCount();
                    }
                }

                if (!isAddedAlbum) {
                    AlbumItem item = new AlbumItem();
                    item.setAlbumName(albumName);
                    item.setAlbumImagePath(fullPath);
                    item.setAlbumImageUrl(Uri.parse("file://" + fullPath));
                    albumItems.add(item);
                }
                lastAlbumName = albumName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_current_album_name:
                onClickCurrentAlbumName();
                break;
        }
    }
}
