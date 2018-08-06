package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.fragment.ChatFragment;
import com.allever.social.fragment.ContactFragment;
import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/4/15.
 */
public class FriendActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_friend_toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.id_friend_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.id_friend_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.id_friend_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.id_friend_tabs);
        tabLayout.setupWithViewPager(viewPager);

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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.id_drawer_menu_index:
                intent = new Intent(this, SocialMainActivity.class);
                startActivity(intent);
                break;
            case R.id.id_drawer_menu_friend:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.id_drawer_menu_notification:
                drawer.closeDrawer(GravityCompat.START);
                intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.id_drawer_menu_setting:
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this,"You Click Setting Menu",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_drawer_menu_about:
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this,"You Click About Menu",Toast.LENGTH_LONG).show();
                break;

        }

        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "聊天");
        adapter.addFragment(new ContactFragment(), "好友");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
