package com.allever.social.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.MyRecruitItemBaseAdapter;
import com.allever.social.pojo.MyRecruitItem;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/21.
 * 我的招聘界面
 */
public class MyRecruitActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener,View.OnClickListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;
    private ListView listView;
    private MyRecruitItemBaseAdapter myRecruitItemBaseAdapter;
    private List<MyRecruitItem> list_myRecruitItem;

    private AddFloatingActionButton btn_add_recruit;

    private IntentFilter intentFilter;
    private UpdateMyRecruitListReceiver updateMyRecruitListReceiver;


    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recruit_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MY_RECRUIT_LIST:
                        handleMyRecruitList(msg);
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.updateMyRecruitList");
        updateMyRecruitListReceiver = new UpdateMyRecruitListReceiver();
        registerReceiver(updateMyRecruitListReceiver, intentFilter);

        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("我的招聘");

        listView = (ListView)this.findViewById(R.id.id_my_recruit_activity_listview);
        swipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.id_my_recruit_activity_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        btn_add_recruit = (AddFloatingActionButton)this.findViewById(R.id.id_my_recruit_activity_fab_add_recruit);
        btn_add_recruit.setOnClickListener(this);

        list_myRecruitItem = new ArrayList<>();

        getMyRecruit();


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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateMyRecruitListReceiver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_my_recruit_activity_fab_add_recruit:
                Intent intent = new Intent(this, AddRecruitActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, RecruitDataActivity.class);
        intent.putExtra("recruit_id", list_myRecruitItem.get(i).getId());
        startActivity(intent);
        //Toast.makeText(this,"id = " + list_myRecruitItem.get(i).getId(),Toast.LENGTH_LONG).show();
    }
//
//    @Override
//    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//        //Toast.makeText(this,"index = " + index,Toast.LENGTH_LONG).show();
//        switch (index){
//            case 0:
//                Toast.makeText(this,"modify",Toast.LENGTH_LONG).show();
//                break;
//            case 1:
//                Toast.makeText(this,"delete",Toast.LENGTH_LONG).show();
//                break;
//        }
//
//        return false;
//    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void getMyRecruit(){
        OkhttpUtil.getMyRecruitList(handler);
    }

    private void handleMyRecruitList(Message msg){
        String result = msg.obj.toString();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(this,"错误","链接服务器失败").show();
            return ;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        list_myRecruitItem.clear();
        MyRecruitItem myRecruitItem;
        for(Recruit recruit : root.list_recruit){
            myRecruitItem = new MyRecruitItem();
            myRecruitItem.setId(recruit.id);
            myRecruitItem.setCompanyname(recruit.companyname);
            myRecruitItem.setUser_head_path(recruit.user_head_path);
            myRecruitItem.setUser_id(recruit.user_id);
            myRecruitItem.setDate(recruit.date);
            myRecruitItem.setRequirement(recruit.requirement);
            list_myRecruitItem.add(myRecruitItem);
        }

        myRecruitItemBaseAdapter  = new MyRecruitItemBaseAdapter(this,list_myRecruitItem);
        listView.setAdapter(myRecruitItemBaseAdapter);
        listView.setOnItemClickListener(this);

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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    //getNearbyRecruit();
                    getMyRecruit();
                    isloading = false;
                } else {
                    Toast.makeText(MyRecruitActivity.this, getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                            Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


    class Root{
        boolean success;
        String message;
        List<Recruit> list_recruit;
    }

    class Recruit{
        String id;
        String companyname;
        String date;
        String requirement;
        String user_id;
        String user_head_path;
    }
    class UpdateMyRecruitListReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            getMyRecruit();
        }
    }

}
