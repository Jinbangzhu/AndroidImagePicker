package com.cndroid.imagepicker;

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

import com.cndroid.imagepicker.bean.PickupImageItem;
import com.cndroid.imagepicker.views.CustomTouchScrollViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_image_pager);

        initialActionBar();
        initialSelectedCountText();

        initialDownAction();
        // load data
        imageItemList = PickupImageHolder.getInstance().getFilterPickupImageItems();


        initialPageView();
    }

    private void initialDownAction() {
        LinearLayout linearLayoutDone = (LinearLayout) findViewById(R.id.ll_done);
        assert linearLayoutDone != null;
        linearLayoutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(PickupImageActivity.RESULT_CODE_DONE);
                onBackPressed();
            }
        });
    }

    private void initialSelectedCountText() {
        tvSelectedCount = (TextView) findViewById(R.id.tv_selected_count);
        tvDone = (TextView) findViewById(R.id.tv_done);
        PickupImageHolder.getInstance().setupTextViewState(tvSelectedCount, tvDone);
    }

    private void initialPageView() {
        int selectedPosition = getIntent().getExtras().getInt("position", 0);
        pager = (CustomTouchScrollViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
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
            getSupportActionBar().setTitle(R.string.ip_str_preview);
        }

        setAppBarBackgroundTransparent();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PickupImageItem imageItem = imageItemList.get(pager.getCurrentItem());

        MenuItem menuItem = menu.findItem(R.id.action_check_state);
        if (imageItem.isSelected()) {
            menuItem.setIcon(getResources().getDrawable(R.mipmap.ip_icon_check_on));
        } else {
            menuItem.setIcon(getResources().getDrawable(R.mipmap.ip_icon_check_off));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pickup_image_check_state, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check_state) {
            PickupImageItem imageItem = imageItemList.get(pager.getCurrentItem());
            imageItem.setSelected(!imageItem.isSelected());
            PickupImageHolder.getInstance().processSelectedCount(imageItem, tvSelectedCount, tvDone);

            supportInvalidateOptionsMenu();

            setResult(PickupImageActivity.RESULT_CODE_REFRESH);
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PickupImagePagerItemFragment.getInstance(imageItemList.get(position));
        }

        @Override
        public int getCount() {
            return imageItemList.size();
        }
    }


    public void setAppBarBackgroundTransparent() {


        if (Build.VERSION.SDK_INT > 11) {
//            mToolbar.setBackgroundColor(Color.parseColor("#00000000"));
            mAppBar.setBackgroundResource(R.color.pickup_image_preview_bar_bg);

            mToolbar.setBackgroundResource(R.color.pickup_image_preview_bar_bg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppBar.setElevation(0);
            }
        }
    }

}
