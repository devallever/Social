package com.allever.social.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.allever.social.R;
import com.allever.social.activity.AddGroupActivity;
import com.allever.social.activity.AddNewsActivity;
import com.allever.social.activity.SearchUserActivity;
import com.allever.social.adapter.MyFragmentPagerAdapter;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/4/15.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener,RippleView.OnRippleCompleteListener{
    private int mPosition = 0;
    private RippleView mRvAdd;
    private ImageView mIvAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_layout,container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.id_main_fragment_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        viewPager.removeAllViews();

        mRvAdd = (RippleView)view.findViewById(R.id.id_main_fragment_rv_add);
        mRvAdd.setOnRippleCompleteListener(this);

        mIvAdd = (ImageView)view.findViewById(R.id.id_main_fragment_iv_add);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.id_main_fragment_tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }


    private void setupViewPager(ViewPager viewPager) {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UserListFragment(), "用户");
        adapter.addFragment(new NewsListFragment(), "动态");
        //adapter.addFragment(new NearbyGroupFragment(), "群组");
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageSelected(int position) {
        this.mPosition = position;
        switch (position){
            case 0:
                mIvAdd.setImageResource(R.mipmap.ic_search_white_24dp);
                break;
            case 1:
            case 2:
            case 3:
                mIvAdd.setImageResource(R.mipmap.ic_add_24_dp);
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
    public void onComplete(RippleView rippleView) {
        Intent intent;
        switch (mPosition){
            case 0:
                intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getActivity(), AddNewsActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getActivity(), AddGroupActivity.class);
                startActivity(intent);
                break;
        }
    }


}
