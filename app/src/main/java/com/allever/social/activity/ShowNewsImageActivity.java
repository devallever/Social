package com.allever.social.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.MyShowImageViewPagerAdapter;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/5/6.
 * 查看动态图片
 */
public class ShowNewsImageActivity extends BaseActivity{
    private ViewPager viewPager;
    private MyShowImageViewPagerAdapter ad;
    private TextView tv_position;
    private  String[] list_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_news_image_activity_layout);

        String[] list_path = getIntent().getStringArrayExtra("listpath");
        int position = getIntent().getIntExtra("position",0);

        viewPager = (ViewPager)this.findViewById(R.id.id_show_news_image_activity_viewpager);
        ad = new MyShowImageViewPagerAdapter(getSupportFragmentManager(),list_path);
        viewPager.setAdapter(ad);
        viewPager.setCurrentItem(position);
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

}
