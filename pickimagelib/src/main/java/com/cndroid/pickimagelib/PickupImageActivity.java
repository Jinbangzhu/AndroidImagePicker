package com.cndroid.pickimagelib;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cndroid.pickimagelib.adapter.AlbumChooserAdapter;
import com.cndroid.pickimagelib.adapter.PickupImageAdapter;
import com.cndroid.pickimagelib.bean.AlbumItem;
import com.cndroid.pickimagelib.bean.PickupImageItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class PickupImageActivity extends AppCompatActivity {
    public static final int RESULT_CODE_DONE = 0x1;
    public static final int RESULT_CODE_REFRESH = 0x2;
    public static final int RESULT_CODE_CANCEL = 0x4;
    public static final int REQUEST_CODE_PREVIEW = 0x3;
    public static final int REQUEST_CODE_PICKUP = 0x5;

    public static final String RESULT_ITEMS = "result_items";


    private List<PickupImageItem> allImageItems;
    private List<PickupImageItem> filteredPickupImageItems = new ArrayList<>();
    private String[] selectedImages;

    private PickupImageHolder pickupImageHolder;

    // Album name
    private ArrayList<AlbumItem> albumItems;

    private TextView tvCurrentAlbumName;
    private TextView tvSelectedCount, tvDone;
    private LinearLayout llDone;
    private PickupImageAdapter pickupImageAdapter;

    private PickupImageDisplay imageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pi_layout_activity_pickup_image);

        /**
         * default result code is cancel
         *
         * when user chosen image, result is RESULT_CODE_DONE
         */
        setResult(RESULT_CODE_CANCEL);
        if (null == savedInstanceState) {
            pickupImageHolder = new PickupImageHolder();

            Bundle bundle = getIntent().getExtras();
            int limit = bundle.getInt(Intents.ImagePicker.LIMIT, Integer.MAX_VALUE);
            pickupImageHolder.setLimit(limit);
            selectedImages = bundle.getStringArray(Intents.ImagePicker.SELECTEDIMAGES);
            imageDisplay = (PickupImageDisplay) getIntent().getExtras().getSerializable(Intents.ImagePicker.IMAGEDISPLAY);


            initialToolBar();
            initialSelectedCountText();

            RecyclerView mRecyclerView = initialRecyclerView();

            getImageFromMedia();

            filteredPickupImageItems.addAll(allImageItems);

            pickupImageHolder.setAllImageItems(allImageItems);
            pickupImageHolder.setFilteredPickupImageItems(filteredPickupImageItems);


            ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());


            setAdapter(mRecyclerView);

            initialDoneListener();
        } else {
            pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            assert pickupImageHolder != null;

            filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
            allImageItems = pickupImageHolder.getAllImageItems();
            imageDisplay = (PickupImageDisplay) savedInstanceState.getSerializable(Intents.ImagePicker.IMAGEDISPLAY);
            albumItems = (ArrayList<AlbumItem>) savedInstanceState.getSerializable(Intents.ImagePicker.ALBUMITEMS);

            initialToolBar();
            initialSelectedCountText();

            RecyclerView mRecyclerView = initialRecyclerView();
            ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());
            setAdapter(mRecyclerView);

            initialDoneListener();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder);
        outState.putSerializable(Intents.ImagePicker.SELECTEDIMAGES, selectedImages);
        outState.putSerializable(Intents.ImagePicker.IMAGEDISPLAY, imageDisplay);
        outState.putParcelableArrayList(Intents.ImagePicker.ALBUMITEMS, albumItems);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            assert pickupImageHolder != null;

            imageDisplay = (PickupImageDisplay) savedInstanceState.getSerializable(Intents.ImagePicker.IMAGEDISPLAY);
            filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
            allImageItems = pickupImageHolder.getAllImageItems();
            albumItems = (ArrayList<AlbumItem>) savedInstanceState.getSerializable(Intents.ImagePicker.ALBUMITEMS);

        }
    }

    private void initialDoneListener() {
        llDone = (LinearLayout) findViewById(R.id.ll_done);
        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    private void initialSelectedCountText() {
        tvSelectedCount = (TextView) findViewById(R.id.tv_selected_count);
        tvDone = (TextView) findViewById(R.id.tv_done);
    }

    private void setAdapter(RecyclerView mRecyclerView) {
        pickupImageAdapter = new PickupImageAdapter();
        pickupImageAdapter.setImageDisplay(imageDisplay);
        pickupImageAdapter.setImageItemList(filteredPickupImageItems);
        pickupImageAdapter.setOnItemClickListener(new PickupImageAdapter.OnItemClickListener() {
            @Override
            public void onImageViewTaped(int position) {
                startActivityForResult(new Intent(PickupImageActivity.this, PickupImagePreviewActivity.class)
                        .putExtra(Intents.ImagePicker.IMAGEDISPLAY, imageDisplay)
                        .putExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder)
                        .putExtra(Intents.ImagePicker.POSITION, position), REQUEST_CODE_PREVIEW);
            }

            @Override
            public void onCheckBoxImageChecked(PickupImageItem item, int position) {
                if (pickupImageHolder.isFull() && !item.isSelected()) {
                    imageDisplay.showTipsForLimitSelect(pickupImageHolder.getLimit());
                } else {
                    item.setSelected(!item.isSelected());
                    pickupImageHolder.processSelectedCount(item);
                    ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());
                    pickupImageAdapter.notifyItemChanged(position);
                }
            }
        });
        mRecyclerView.setAdapter(pickupImageAdapter);
    }

    @NonNull
    private RecyclerView initialRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvCurrentAlbumName = (TextView) findViewById(R.id.tv_current_album_name);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return mRecyclerView;
    }

    private void initialToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String title = bundle.getString(Intents.ImagePicker.TITLE);
            if (!TextUtils.isEmpty(title)) ab.setTitle(title);

            int titleRes = bundle.getInt(Intents.ImagePicker.TITLERES, 0);
            if (titleRes != 0) ab.setTitle(titleRes);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREVIEW) {
            if (resultCode == RESULT_CODE_REFRESH) {
                pickupImageHolder = (PickupImageHolder) data.getSerializableExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER);
                allImageItems = pickupImageHolder.getAllImageItems();
                filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
                pickupImageAdapter.setImageItemList(pickupImageHolder.getFilteredPickupImageItems());
                refreshAdapter();
            } else if (resultCode == RESULT_CODE_DONE) {
                pickupImageHolder = (PickupImageHolder) data.getSerializableExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER);
                allImageItems = pickupImageHolder.getAllImageItems();
                filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
                done();
            }
        }
    }

    /**
     * Done
     */
    private void done() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_ITEMS, pickupImageHolder.getSelectedImages());
        setResult(RESULT_OK, intent);
        // flush cached data
        pickupImageHolder.flush();
        onBackPressed();
    }


    private void refreshAdapter() {
        if (null != pickupImageAdapter) {
            pickupImageAdapter.notifyDataSetChanged();
        }
        ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClickCurrentAlbumName(View v) {
        View albumChooserView = View.inflate(this, R.layout.pi_layout_album_list, null);

        RecyclerView recyclerView = (RecyclerView) albumChooserView.findViewById(R.id.album_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        final AlbumChooserAdapter adapter = new AlbumChooserAdapter();
        adapter.setImageDisplay(imageDisplay);
        adapter.setAlbumItems(albumItems);

        recyclerView.setAdapter(adapter);

        float itemHeight = getResources().getDimension(R.dimen.pi_album_selector_item_height);

        float maxHeight = getResources().getDisplayMetrics().heightPixels - 2 * itemHeight;
        int totalHeight = (int) (itemHeight * adapter.getItemCount());

        int fixableHeight = totalHeight > maxHeight ? (int) maxHeight : totalHeight;
        albumChooserView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fixableHeight));

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(albumChooserView);
        dialog.show();

        adapter.setOnItemClickListener(new AlbumChooserAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(AlbumItem item, int position) {
                dialog.cancel();
                adapter.unChooseAll();
                item.setHasChosen(true);
                tvCurrentAlbumName.setText(item.getAlbumName());

                filteredPickupImageItems.clear();

                if (position == 0) {// All
                    filteredPickupImageItems.addAll(allImageItems);
                } else {// filter
                    for (PickupImageItem imageItem : allImageItems) {
                        if (imageItem.getAlbumName().equals(item.getAlbumName())) {
                            filteredPickupImageItems.add(imageItem);
                        }
                    }
                }
                pickupImageAdapter.notifyDataSetChanged();
            }
        });

    }


    private void getImageFromMedia() {
        allImageItems = new ArrayList<>();
        albumItems = new ArrayList<>();

        long startTime = 0l;
        boolean defaultChosenAll = false;
        boolean dcimOnly = false;


        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            startTime = bundle.getLong(Intents.ImagePicker.STARTTIME);
            defaultChosenAll = bundle.getBoolean(Intents.ImagePicker.DEFAULTCHOSEN, false);
            dcimOnly = bundle.getBoolean(Intents.ImagePicker.DCIMONLY, false);

            // millisecond to second
            if (String.valueOf(startTime).length() > 10) startTime /= 1000;
        }

        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION};
            final String selection = MediaStore.Images.Media.DATE_ADDED + " > " + startTime;
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, orderBy);
            assert imageCursor != null;

            while (imageCursor.moveToNext()) {
                String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                // DCIM/Camera
                if (dcimOnly && (!fullPath.toLowerCase().contains("dcim") || !fullPath.toLowerCase().contains("camera")))
                    continue;

                PickupImageItem pickupImageItem = new PickupImageItem();


                String imgPath = fullPath.substring(0, fullPath.lastIndexOf(File.separator));
                String albumName = imgPath.substring(imgPath.lastIndexOf(File.separator) + 1, imgPath.length());
                long dateAdded = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                pickupImageItem.setAlbumName(albumName);
                pickupImageItem.setImagePath(fullPath);
                pickupImageItem.setSelected(defaultChosenAll);
                pickupImageItem.setDateAdded(dateAdded);

                allImageItems.add(pickupImageItem);

                // add `all images` folder
                if (albumItems.size() == 0) {
                    AlbumItem item = new AlbumItem();
                    item.setAlbumName(getString(R.string.pickup_image_all_images));
                    item.setAlbumImagePath(pickupImageItem.getImagePath());
                    item.setHasChosen(true);
                    item.increaseImageCount();
                    albumItems.add(item);

                    // set albumName
                    tvCurrentAlbumName.setText(item.getAlbumName());
                } else {
                    // always increase for all images
                    albumItems.get(0).increaseImageCount();
                }


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
                    item.setImageCount(1);
                    albumItems.add(item);
                }

                if (null != selectedImages && selectedImages.length > 0) {
                    for (String selectedImage : selectedImages) {
                        if (fullPath.equals(selectedImage)) {
                            pickupImageItem.setSelected(true);
                        }
                    }
                }
            }

            if (defaultChosenAll)
                pickupImageHolder.setSelectedCount(allImageItems.size());
            else {
                if (null != selectedImages && selectedImages.length > 0) {
                    pickupImageHolder.setSelectedCount(selectedImages.length);
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
