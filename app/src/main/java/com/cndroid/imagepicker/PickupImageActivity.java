package com.cndroid.imagepicker;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbangzhu on 1/8/16.
 */
public class PickupImageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PickupImageItem> pickupImageItems;
    private List<AlbumItem> albumItems;

    private TextView tvCurrentAlbumName;
    private ImageView viewDummy, ivAlbumNameBarBackground;
    private RelativeLayout rlBottomBar;
    private TransitionDrawable mDrawableDummy, mDrawableAlbumNameBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvCurrentAlbumName = (TextView) findViewById(R.id.tv_current_album_name);
        viewDummy = (ImageView) findViewById(R.id.view_dummy);
        ivAlbumNameBarBackground = (ImageView) findViewById(R.id.iv_album_name_bar_background);
        mDrawableDummy = (TransitionDrawable) viewDummy.getDrawable();
        mDrawableAlbumNameBar = (TransitionDrawable) ivAlbumNameBarBackground.getDrawable();

        rlBottomBar = (RelativeLayout) findViewById(R.id.rl_bottom);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);


        getImageFromMedia();

        PickupImageAdapter adapter = new PickupImageAdapter();
        adapter.setImageItemList(pickupImageItems);
        mRecyclerView.setAdapter(adapter);
    }


    public void onClickCurrentAlbumName(View v) {
        // show popup window
        View albumChooserView = View.inflate(this, R.layout.album_list, null);

        RecyclerView recyclerView = (RecyclerView) albumChooserView.findViewById(R.id.album_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        AlbumChooserAdapter adapter = new AlbumChooserAdapter();
        adapter.setAlbumItems(albumItems);

        recyclerView.setAdapter(adapter);

        float itemHeight = getResources().getDimension(R.dimen.pi_album_selector_item_height);

        float maxHeight = getResources().getDisplayMetrics().heightPixels - 2 * itemHeight;
        int totalHeight = (int) (itemHeight * adapter.getItemCount());

        int fixableHeight = totalHeight > maxHeight ? (int) maxHeight : totalHeight;


        final PopupWindow popupWindow = new PopupWindow(albumChooserView, LinearLayout.LayoutParams.MATCH_PARENT, fixableHeight, true);
        popupWindow.setAnimationStyle(R.style.PickupImageAlbumChooserAnimation);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(rlBottomBar, Gravity.BOTTOM, 0, rlBottomBar.getHeight() + getSoftButtonsBarSizePort());
        mDrawableDummy.startTransition(200);
        mDrawableAlbumNameBar.startTransition(200);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mDrawableDummy.reverseTransition(200);
                mDrawableAlbumNameBar.reverseTransition(200);
            }
        });
        albumChooserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

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
