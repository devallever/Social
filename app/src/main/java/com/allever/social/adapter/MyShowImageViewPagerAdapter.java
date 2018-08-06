package com.allever.social.adapter;

/**
 * Created by XM on 2016/5/6.
 */
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.allever.social.fragment.ShowNewsImageFragment;

import java.util.ArrayList;

/**
 * Created by XM on 2015/11/22.
 */
public class MyShowImageViewPagerAdapter extends FragmentPagerAdapter {

    private String[] list_image_path;

    public MyShowImageViewPagerAdapter(FragmentManager fm, String[] list_path){
        super(fm);
        list_image_path = list_path;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        String path = list_image_path[position];
        ShowNewsImageFragment fragment = new ShowNewsImageFragment(list_image_path[position], list_image_path.length, position+1);
        return fragment;

    }

    @Override
    public int getCount() {
        return list_image_path.length;
    }
}
