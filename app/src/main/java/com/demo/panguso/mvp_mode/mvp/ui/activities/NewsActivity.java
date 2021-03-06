package com.demo.panguso.mvp_mode.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.demo.panguso.mvp_mode.R;
import com.demo.panguso.mvp_mode.common.Constants;
import com.demo.panguso.mvp_mode.mvp.event.ScrollToTopEvent;
import com.demo.panguso.mvp_mode.mvp.presenter.impl.NewsPresenterImpl;
import com.demo.panguso.mvp_mode.mvp.ui.activities.base.BaseActivity;
import com.demo.panguso.mvp_mode.mvp.ui.adapter.NewsFragmetPagerAdapter;
import com.demo.panguso.mvp_mode.mvp.ui.fragment.NewsListFragment;
import com.demo.panguso.mvp_mode.mvp.view.NewsView;
import com.demo.panguso.mvp_mode.mvp.view.drawview.FlodDrawerLayout;
import com.demo.panguso.mvp_mode.utils.MyUtils;
import com.demo.panguso.mvp_mode.utils.RxBus;
import com.demo.panguso.mvp_mode.utils.SharedPreferencesUtil;
import com.demo.panguso.mvp_mode.widget.ChannelChangeEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import greendao.NewsChannelTable;
import rx.functions.Action1;


public class NewsActivity extends BaseActivity implements NewsView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.navigation_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    FlodDrawerLayout mDrawerLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.add_channel_iv)
    ImageView mAddChannel;

    private String mCurrentViewPagerName;
    private List<String> mChannelNames;

    @Inject
    NewsPresenterImpl mNewsPresenter;
    private ArrayList<Fragment> mNewsFragmentList = new ArrayList<>();

    protected void initViews() {
        mPresenter = mNewsPresenter;
        mPresenter.attachView(this);
        mIsHasNavigationView = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance().toObservable(ChannelChangeEvent.class)
                .subscribe(new Action1<ChannelChangeEvent>() {
                    @Override
                    public void call(ChannelChangeEvent channelItemMoveEvent) {
                        mNewsPresenter.onChangedDb();

                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news;
    }

    @Override
    protected void initInjector() {
        mActivityComponent.inject(this);
    }


    @OnClick({R.id.fab, R.id.add_channel_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_channel_iv:
                Intent intent = new Intent(this, NewsChannelActivity.class);
                startActivity(intent);
                break;
            case R.id.fab:
                RxBus.getInstance().toObservable(ScrollToTopEvent.class);
                break;
        }
    }

    @Override
    public void initViewPager(List<NewsChannelTable> list) {
        mNewsFragmentList.clear();
        List<String> channelName = new ArrayList<>();
        if (list != null) {
            for (NewsChannelTable channelTable : list) {
                NewsListFragment newsFragment = createListFragment(channelTable);
                mNewsFragmentList.add(newsFragment);
                channelName.add(channelTable.getNewsChannelName());
            }
        }
        NewsFragmetPagerAdapter adapter = new NewsFragmetPagerAdapter(getSupportFragmentManager(), channelName, mNewsFragmentList);
        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mTabs.setupWithViewPager(mViewPager);
        MyUtils.dynamicSetTabLayoutMode(mTabs);
        setPageChangeListener();
        mChannelNames = channelName;
        int currentViewPagerPosition = getCurrentViewPagerPosition();
        mViewPager.setCurrentItem(currentViewPagerPosition, false);
    }

    private void setPageChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerName = mChannelNames.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private NewsListFragment createListFragment(NewsChannelTable channelTable) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, channelTable.getNewsChannelId());
        bundle.putString(Constants.NEWS_TYPE, channelTable.getNewsChannelType());
        bundle.putInt(Constants.CHANNEL_POSITION, channelTable.getNewsChannelIndex());
        Log.e("TAG", "{channelTable.getNewsChannelId()}" + channelTable.getNewsChannelIndex());
        Log.e("TAG", "{channelTable.getNewsChannelName()}" + channelTable.getNewsChannelName());
        Log.e("TAG", "{channelTable.getNewsChannelType()}" + channelTable.getNewsChannelType());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


    @Override
    public void showErrorMsg(String message) {

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void changeDayOrNight() {
        if (SharedPreferencesUtil.getIsNightMode()) {
            changeToDay();
            SharedPreferencesUtil.setIsNightMode(false);
        } else {
            changeToNight();
            SharedPreferencesUtil.setIsNightMode(true);
        }
        recreate();
    }

    //得到当前所NewsActivity所展示的页面
    public int getCurrentViewPagerPosition() {
        int position = 0;
        if (mCurrentViewPagerName != null) {
            for (int i = 0; i < mChannelNames.size(); i++) {
                if (mCurrentViewPagerName.equals(mChannelNames.get(i))) {
                    position = i;
                }
            }
        }
        return position;
    }
}
