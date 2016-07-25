package com.cndroid.pickimagelib;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.cndroid.pickimagelib.Intents.ImagePicker.REQUEST_CODE_PREVIEW;
import static com.cndroid.pickimagelib.Intents.ImagePicker.REQUEST_CODE_TAKEPHOTO;
import static com.cndroid.pickimagelib.Intents.ImagePicker.RESULT_CODE_CANCEL;
import static com.cndroid.pickimagelib.Intents.ImagePicker.RESULT_CODE_DONE;
import static com.cndroid.pickimagelib.Intents.ImagePicker.RESULT_CODE_REFRESH;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class PickupImageActivity extends AppCompatActivity {


    private List<PickupImageItem> filteredPickupImageItems = new ArrayList<>();

    private PickupImageHolder pickupImageHolder;

    private TextView tvCurrentAlbumName;
    private TextView tvSelectedCount, tvDone;
    private PickupImageAdapter pickupImageAdapter;
    private String mCurrentPhotoPath;


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

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            pickupImageHolder = new PickupImageHolder();

            Bundle bundle = getIntent().getExtras();
            int limit = bundle.getInt(Intents.ImagePicker.LIMIT, Integer.MAX_VALUE);
            pickupImageHolder.setLimit(limit);
            pickupImageHolder.setImageDisplay((PickupImageDisplay) bundle.getSerializable(Intents.ImagePicker.IMAGEDISPLAY));
            boolean showCamera = bundle.getBoolean(Intents.ImagePicker.SHOWCAMERA, false);
            pickupImageHolder.setShowCamera(showCamera);


            initialToolBar();
            initialSelectedCountText();

            RecyclerView mRecyclerView = initialRecyclerView();

            getImageFromMedia();

            filteredPickupImageItems.addAll(pickupImageHolder.getAllImageItems());
            pickupImageHolder.setFilteredPickupImageItems(filteredPickupImageItems);

            ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());
            setAdapter(mRecyclerView);
            initialDoneListener();
        } else {
            pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            assert pickupImageHolder != null;

            filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();

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
        outState.putSerializable(Intents.ImagePicker.PHOTO_PATH, mCurrentPhotoPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            assert pickupImageHolder != null;

            filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
            mCurrentPhotoPath = savedInstanceState.getString(Intents.ImagePicker.PHOTO_PATH);
        }
    }

    private void initialDoneListener() {
        LinearLayout llDone = (LinearLayout) findViewById(R.id.ll_done);
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
        pickupImageAdapter.setImageDisplay(pickupImageHolder.getImageDisplay());
        pickupImageAdapter.setImageItemList(filteredPickupImageItems);
        pickupImageAdapter.setOnItemClickListener(new PickupImageAdapter.OnItemClickListener() {
            @Override
            public void onImageViewTaped(int position) {
                if (isTapedCamera(position)) {
                    dispatchTakePictureIntent();
                } else {
                    startActivityForResult(new Intent(PickupImageActivity.this, PickupImagePreviewActivity.class)
                            .putExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder)
                            .putExtra(Intents.ImagePicker.POSITION, position), REQUEST_CODE_PREVIEW);
                }
            }

            @Override
            public void onCheckBoxImageChecked(PickupImageItem item, int position) {
                if (pickupImageHolder.isFull() && !item.isSelected()) {
                    pickupImageHolder.getImageDisplay().showTipsForLimitSelect(pickupImageHolder.getLimit());
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

    private boolean isTapedCamera(int position) {
        return position == 0 && pickupImageHolder.isShowCamera();
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
                filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
                pickupImageAdapter.setImageItemList(pickupImageHolder.getFilteredPickupImageItems());
                refreshAdapter();
            } else if (resultCode == RESULT_CODE_DONE) {
                pickupImageHolder = (PickupImageHolder) data.getSerializableExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER);
                filteredPickupImageItems = pickupImageHolder.getFilteredPickupImageItems();
                done();
            }
        } else if (requestCode == REQUEST_CODE_TAKEPHOTO) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra(Intents.ImagePicker.RESULT_ITEMS, new String[]{mCurrentPhotoPath});
                setResult(RESULT_OK, intent);

                galleryAddPic();
                onBackPressed();
            }
        }
    }

    /**
     * Done
     */
    private void done() {
        Intent intent = new Intent();
        intent.putExtra(Intents.ImagePicker.RESULT_ITEMS, pickupImageHolder.getSelectedImages());
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
        adapter.setImageDisplay(pickupImageHolder.getImageDisplay());
        adapter.setAlbumItems(pickupImageHolder.getAlbumItems());

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

                /**
                 * only show camera when the filter is all
                 */
//                addCameraIfNeed(filteredPickupImageItems);


                if (position == 0) {// All

                    filteredPickupImageItems.addAll(pickupImageHolder.getAllImageItems());
                } else {// filter
                    for (PickupImageItem imageItem : pickupImageHolder.getAllImageItems()) {
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
        List<PickupImageItem> allImageItems = new ArrayList<>();
        List<AlbumItem> albumItems = new ArrayList<>();

        pickupImageHolder.setAllImageItems(allImageItems);
        pickupImageHolder.setAlbumItems(albumItems);

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

        addCameraIfNeed(allImageItems);


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


                String imgPath = fullPath.substring(0, fullPath.lastIndexOf(File.separator));
                String albumName = imgPath.substring(imgPath.lastIndexOf(File.separator) + 1, imgPath.length());
                long dateAdded = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                PickupImageItem pickupImageItem = new PickupImageItem();
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


                createAlbum(albumItems, fullPath, albumName);

                setChosenState(fullPath, pickupImageItem);
            }

            setChosenCount(allImageItems, defaultChosenAll);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
    }

    private void addCameraIfNeed(List<PickupImageItem> allImageItems) {
        if (pickupImageHolder.isShowCamera()) {
            PickupImageItem pickupImageItem = new PickupImageItem();
            pickupImageItem.setAlbumName(Intents.ImagePicker.SHOWCAMERA);
            pickupImageItem.setImagePath(Intents.ImagePicker.SHOWCAMERA);
            pickupImageItem.setSelected(false);
            pickupImageItem.setDateAdded(0);
            allImageItems.add(pickupImageItem);
        }
    }

    private void setChosenState(String fullPath, PickupImageItem pickupImageItem) {
        String[] selectedImages = getSelectedImageArray();
        if (null != selectedImages && selectedImages.length > 0) {
            for (String selectedImage : selectedImages) {
                if (fullPath.equals(selectedImage)) {
                    pickupImageItem.setSelected(true);
                }
            }
        }
    }

    private void setChosenCount(List<PickupImageItem> allImageItems, boolean defaultChosenAll) {
        if (defaultChosenAll)
            pickupImageHolder.setSelectedCount(allImageItems.size());
        else {
            String[] selectedImages = getSelectedImageArray();
            if (null != selectedImages && selectedImages.length > 0) {
                pickupImageHolder.setSelectedCount(selectedImages.length);
            }
        }
    }

    private String[] getSelectedImageArray() {
        return getIntent().getExtras().getStringArray(Intents.ImagePicker.SELECTEDIMAGES);
    }

    private void createAlbum(List<AlbumItem> albumItems, String fullPath, String albumName) {
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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKEPHOTO);
            }
        }
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
