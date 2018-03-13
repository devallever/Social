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
import com.allever.social.fragment.AllChatRankFragment;
import com.allever.social.fragment.MyChatRankFragment;
import com.allever.social.fragment.MyShareRankFragment;
import com.allever.social.fragment.NearbyFragment;
import com.allever.social.fragment.NearbyUserFragment;
import com.allever.social.fragment.ShareRankFragment;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/7/11.
 */
public class ChatRankActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private Handler handler;
    private ActionBar actionBar;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_rank_activity_layout);

        actionBar= getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("排行榜");

        initData();

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


    private void initData(){
        tabLayout = (TabLayout)this.findViewById(R.id.id_chat_rank_activity_tablayout);
        viewPager = (ViewPager)this.findViewById(R.id.id_chat_rank_activity_viewpager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ShareRankFragment(),"分享排行");
        adapter.addFragment(new MyShareRankFragment(), "我");
        adapter.addFragment(new AllChatRankFragment(),"聊天排行");

        viewPager.setAdapter(adapter);
        //viewPager.removeAllViews();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(this);

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        switch (position){
            case 0:
                actionBar.setTitle("分享排行");
                break;
            case 1:
                actionBar.setTitle("我");
                break;
            case 2:
                actionBar.setTitle("聊天排行");
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
