package com.allever.social.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.social.R;
import com.allever.social.adapter.MyFragmentPagerAdapter;
import com.baidu.mobstat.StatService;

/**
 * Created by XM on 2016/4/15.
 */
public class FriendFragment extends Fragment {
    private final int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_fragment_layout,container,false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.id_friend_fragment_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.id_friend_fragment_tabs);
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
        adapter.addFragment(new MyEaseConversationListFragment(), "聊天");
        //adapter.addFragment(new ChatFragment(), "聊天");
        adapter.addFragment(new ContactFragment2(), "好友");
        //adapter.addFragment(new MyGroupFragment(), "群组");
        viewPager.setAdapter(adapter);
    }



}
