package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.MyFragmentPagerAdapter;
import com.allever.social.fragment.VisitedNewsUserFragment;
import com.allever.social.fragment.VisitedUserUserFragment;
import com.allever.social.network.util.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by XM on 2016/6/3.\
 * 谁看过我界面
 */
public class VisitedUserActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visited_user_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_VISITED_USER_LIST:
                        handleVisitedUserList(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("谁看过我");

        initData();

        getVisitedUserList();


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
        tabLayout = (TabLayout)this.findViewById(R.id.id_visited_user_activity_tablayout);
        viewPager = (ViewPager)this.findViewById(R.id.id_visited_user_activity_viewpager);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
    }

    private void getVisitedUserList(){
        OkhttpUtil.getVisitedUserList(handler);
    }

    private void handleVisitedUserList(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if(!root.success){
            return;
        }

        adapter.addFragment(new VisitedUserUserFragment(),"资料(" + root.visiteduser_count + ")");
        adapter.addFragment(new VisitedNewsUserFragment(), "动态(" + root.visitednews_list.size() + ")");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



    }


    class Root{
        boolean success;
        String message;
        int visiteduser_count;
        int visitednews_count;
        List<User> visiteduser_list;
        List<User> visitednews_list;

    }

    class User{
        String id;
        String username;
        String nickname;
        String sex;
        double distance;
        String user_head_path;
        String signature;
        int age;
        String constellation;
        String occupation;
        int is_vip;
    }
}
