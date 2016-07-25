package com.cndroid.pickimagelib;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cndroid.pickimagelib.bean.PickupImageItem;
import com.cndroid.pickimagelib.views.CustomTouchScrollViewPager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by jinbangzhu on 4/6/16.
 */
public class PickupImagePreviewActivity extends AppCompatActivity {

    private List<PickupImageItem> imageItemList;
    protected AppBarLayout mAppBar;
    protected Toolbar mToolbar;
    CustomTouchScrollViewPager pager;
    private TextView tvSelectedCount, tvDone;
    PageAdapter adapter;

    private PickupImageDisplay imageDisplay;


    private PickupImageHolder pickupImageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pi_layout_activity_pickup_image_pager);
        setResult(RESULT_CANCELED);

        if (savedInstanceState == null) {
            pickupImageHolder = (PickupImageHolder) getIntent().getSerializableExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            imageDisplay = (PickupImageDisplay) getIntent().getExtras().getSerializable(Intents.ImagePicker.IMAGEDISPLAY);
        } else {
            pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
            assert pickupImageHolder != null;

            imageDisplay = (PickupImageDisplay) savedInstanceState.getSerializable(Intents.ImagePicker.IMAGEDISPLAY);

        }
        initialActionBar();
        initialSelectedCountText();

        initialDoneAction();
        // load data
        imageItemList = pickupImageHolder.getFilteredPickupImageItems();


        initialPageView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder);
        outState.putSerializable(Intents.ImagePicker.IMAGEDISPLAY, imageDisplay);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pickupImageHolder = (PickupImageHolder) savedInstanceState.getSerializable(Intents.ImagePicker.PICKUPIMAGEHOLDER);
        assert pickupImageHolder != null;

        imageDisplay = (PickupImageDisplay) savedInstanceState.getSerializable(Intents.ImagePicker.IMAGEDISPLAY);

    }

    private void initialDoneAction() {
        LinearLayout linearLayoutDone = (LinearLayout) findViewById(R.id.ll_done);
        assert linearLayoutDone != null;
        linearLayoutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder);
                setResult(PickupImageActivity.RESULT_CODE_DONE, intent);
                onBackPressed();
            }
        });
    }

    private void initialSelectedCountText() {
        tvSelectedCount = (TextView) findViewById(R.id.tv_selected_count);
        tvDone = (TextView) findViewById(R.id.tv_done);
        ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());

    }

    private void initialPageView() {
        int selectedPosition = getIntent().getExtras().getInt(Intents.ImagePicker.POSITION, 0);
        pager = (CustomTouchScrollViewPager) findViewById(R.id.pager);
        adapter = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(selectedPosition);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initialActionBar() {
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.pi_str_preview);
        }

        setAppBarBackgroundTransparent();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PickupImageItem imageItem = imageItemList.get(pager.getCurrentItem());

        MenuItem menuItem = menu.findItem(R.id.pi_action_check_state);
        if (imageItem.isSelected()) {
            menuItem.setIcon(getResources().getDrawable(R.mipmap.pi_icon_check_on));
        } else {
            menuItem.setIcon(getResources().getDrawable(R.mipmap.pi_icon_check_off));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pi_menu_check_state, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pi_action_check_state) {
            PickupImageItem imageItem = imageItemList.get(pager.getCurrentItem());

            if (pickupImageHolder.isFull() && !imageItem.isSelected()) {
                imageDisplay.showTipsForLimitSelect(pickupImageHolder.getLimit());
            } else {
                imageItem.setSelected(!imageItem.isSelected());
                pickupImageHolder.processSelectedCount(imageItem);
                ViewHelper.setupTextViewState(tvSelectedCount, tvDone, pickupImageHolder.getSelectedCount());

                supportInvalidateOptionsMenu();

                Intent intent = new Intent();
                intent.putExtra(Intents.ImagePicker.PICKUPIMAGEHOLDER, pickupImageHolder);
                setResult(PickupImageActivity.RESULT_CODE_REFRESH, intent);
            }

        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Intents.ImagePicker.IMAGEITEM, imageItemList.get(position));
            bundle.putSerializable(Intents.ImagePicker.IMAGEDISPLAY, imageDisplay);
            PickupImagePagerItemFragment fragment = new PickupImagePagerItemFragment();

            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return imageItemList.size();
        }
    }


    public void setAppBarBackgroundTransparent() {


        if (Build.VERSION.SDK_INT > 11) {
            mAppBar.setBackgroundResource(R.color.pickup_image_preview_bar_bg);

            mToolbar.setBackgroundResource(R.color.pickup_image_preview_bar_bg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBar.setElevation(0);
            }
        }
    }

}
