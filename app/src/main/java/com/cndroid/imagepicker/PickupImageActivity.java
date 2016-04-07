package com.cndroid.imagepicker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cndroid.imagepicker.adapter.AlbumChooserAdapter;
import com.cndroid.imagepicker.adapter.PickupImageAdapter;
import com.cndroid.imagepicker.bean.AlbumItem;
import com.cndroid.imagepicker.bean.PickupImageItem;

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
    public static final String RESULT_IMAGES = "result_images_url";

    private List<PickupImageItem> pickupImageItems;
    private List<PickupImageItem> filterPickupImageItems = new ArrayList<>();

    // Album name
    private List<AlbumItem> albumItems;

    private TextView tvCurrentAlbumName;
    private TextView tvSelectedCount, tvDone;
    private LinearLayout llDone;
    private PickupImageAdapter pickupImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_image);

        /**
         * default result code is cancel
         *
         * when user chosen image, result is RESULT_CODE_DONE
         */
        setResult(RESULT_CODE_CANCEL);

        initialToolBar();
        initialSelectedCountText();

        RecyclerView mRecyclerView = initialRecyclerView();

        getImageFromMedia();

        filterPickupImageItems.addAll(pickupImageItems);

        PickupImageHolder.getInstance().setPickupImageItems(pickupImageItems);
        PickupImageHolder.getInstance().setFilterPickupImageItems(filterPickupImageItems);

        setAdapter(mRecyclerView);

        initialDoneListener();
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
        pickupImageAdapter.setImageItemList(filterPickupImageItems);
        pickupImageAdapter.setOnItemClickListener(new PickupImageAdapter.OnItemClickListener() {
            @Override
            public void onImageViewTaped(int position) {
                startActivityForResult(new Intent(PickupImageActivity.this, PickupImagePreviewActivity.class).putExtra("position", position), REQUEST_CODE_PREVIEW);
            }

            @Override
            public void onCheckBoxImageChecked(PickupImageItem item, int position) {
                item.setSelected(!item.isSelected());
                PickupImageHolder.getInstance().processSelectedCount(item, tvSelectedCount, tvDone);
                pickupImageAdapter.notifyItemChanged(position);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREVIEW) {
            if (resultCode == RESULT_CODE_REFRESH) {
                refreshAdapter();
            } else if (resultCode == RESULT_CODE_DONE) {
                done();
            }
        }
    }

    /**
     * Done
     */
    private void done() {
        setResult(RESULT_CODE_DONE);
        // put selected images to intentData
        getIntent().putExtra(RESULT_IMAGES, PickupImageHolder.getInstance().getSelectedImagesUrl());


        // flush cached data
        PickupImageHolder.getInstance().flush();

        onBackPressed();
    }


    private void refreshAdapter() {
        if (null != pickupImageAdapter) {
            pickupImageAdapter.notifyDataSetChanged();
        }
        PickupImageHolder.getInstance().setupTextViewState(tvSelectedCount, tvDone);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClickCurrentAlbumName(View v) {
        // show popup window
        View albumChooserView = View.inflate(this, R.layout.album_list, null);

        RecyclerView recyclerView = (RecyclerView) albumChooserView.findViewById(R.id.album_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        final AlbumChooserAdapter adapter = new AlbumChooserAdapter();
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

                filterPickupImageItems.clear();

                if (position == 0) {// All
                    filterPickupImageItems.addAll(pickupImageItems);
                } else {// filter
                    for (PickupImageItem imageItem : pickupImageItems) {
                        if (imageItem.getAlbumName().equals(item.getAlbumName())) {
                            filterPickupImageItems.add(imageItem);
                        }
                    }
                }
                pickupImageAdapter.notifyDataSetChanged();
            }
        });

    }


    private void getImageFromMedia() {
        String lastAlbumName = null;
        pickupImageItems = new ArrayList<>();
        albumItems = new ArrayList<>();

        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
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

                // First
                if (albumItems.size() == 0) {
                    AlbumItem item = new AlbumItem();
                    item.setAlbumName(getString(R.string.pickup_image_all_images));
                    item.setAlbumImageUrl(pickupImageItem.getImageUri());
                    item.setHasChosen(true);
                    albumItems.add(item);
                } else {
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
                    item.setAlbumImageUrl(Uri.parse("file://" + fullPath));
                    item.setImageCount(1);
                    albumItems.add(item);
                }
                lastAlbumName = albumName;
//
//                Uri uri = Uri.parse("file://" + imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
//                int orientation = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

//                adapter.mContent.add(uri);
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


    private int getSoftButtonsBarSizePort() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

}
