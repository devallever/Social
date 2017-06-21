package com.allever.social.fragment;

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
import com.allever.social.activity.AddRecruitActivity;
import com.allever.social.activity.GetVipActivity;
import com.allever.social.activity.SearchUserActivity;
import com.allever.social.adapter.MyFragmentPagerAdapter;
import com.allever.social.foundModule.ui.NewsListFragment;
import com.allever.social.foundModule.ui.UserListFragment;
import com.allever.social.utils.SharedPreferenceUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;

/**
 * Created by XM on 2016/4/15.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener,RippleView.OnRippleCompleteListener{
    private int position = 0;
    //private ImageButton btn_add;
    private RippleView rv_add;
    private ImageView iv_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_layout,container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.id_main_fragment_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        viewPager.removeAllViews();

        rv_add = (RippleView)view.findViewById(R.id.id_main_fragment_rv_add);
        //rv_add.setOnClickListener(this);
        rv_add.setOnRippleCompleteListener(this);

        iv_add = (ImageView)view.findViewById(R.id.id_main_fragment_iv_add);

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
        adapter.addFragment(new UserListFragment(), "人");
        adapter.addFragment(new NewsListFragment(), "动态");
        //adapter.addFragment(new NearbyGroupFragment(), "群组");
        //adapter.addFragment(new HotFragment(), "推荐");
        //adapter.addFragment(new NearbyPostFragment(),"招聘");
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        switch (position){
            case 0:
                iv_add.setImageResource(R.mipmap.ic_search_white_24dp);
                break;
            case 1:
            case 2:
            case 3:
                iv_add.setImageResource(R.mipmap.ic_add_24_dp);
                break;
        }
        //Toast.makeText(getActivity(),position+"", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //this.position = position;
        //Toast.makeText(getActivity(),position+"", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onComplete(RippleView rippleView) {
        Intent intent;
        switch (position){
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
            case 3:
                if (SharedPreferenceUtil.getVip().equals("1")){
                    intent = new Intent(getActivity(), AddRecruitActivity.class);
                    startActivity(intent);
                }else{
                    Dialog dialog  = new Dialog(getActivity(),"提示","您不是会员,无法发布招聘。\n是否开通会员?");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), GetVipActivity.class);
                            startActivity(intent);

                        }
                    });
                    dialog.show();
                }


                break;
        }
    }


}
