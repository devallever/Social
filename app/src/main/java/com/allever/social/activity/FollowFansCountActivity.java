package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.MyFragmentPagerAdapter;
import com.allever.social.fragment.FansUserFragment;
import com.allever.social.fragment.FollowUserFragment;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/7/27.
 */
public class FollowFansCountActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private int page_position;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private Handler handler;

    private int position;
    private String username;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_fans_count_activity_layout);

        page_position = getIntent().getIntExtra("page_position",0);
        username = getIntent().getStringExtra("username");

        actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("关注");

        initData();

    }

    private void initData(){
        tabLayout = (TabLayout)this.findViewById(R.id.id_follow_fans_count_activity_tablayout);
        viewPager = (ViewPager)this.findViewById(R.id.id_follow_fans_count_activity_viewpager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FollowUserFragment(username),"关注");
        adapter.addFragment(new FansUserFragment(username), "粉丝");
        viewPager.setAdapter(adapter);
        //viewPager.removeAllViews();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(page_position);

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        switch (position){
            case 0:
                actionBar.setTitle("关注");
                break;
            case 1:
                actionBar.setTitle("粉丝");
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
