package com.allever.social.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.FansItemBaseAdapter;
import com.allever.social.adapter.FollowItemBaseAdapter;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.pojo.FansUserItem;
import com.allever.social.pojo.FollowUserItem;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/7/28.
 */
@SuppressLint("ValidFragment")
public class FansUserFragment extends Fragment implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2{

    private PullToRefreshListView listView;
    private FansItemBaseAdapter fansItemBaseAdapter;
    private List<FansUserItem> list_user = new ArrayList<>();

    private int page = 1;
    private Handler handler;

    private String username;

    public FansUserFragment(){

    }

    public FansUserFragment(String username){
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fans_user_fragment_layout,container,false);

        handler  = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_FANS_USER:
                        handleGetFansUser(msg);
                        break;
                }
            }
        };

        listView = (PullToRefreshListView)view.findViewById(R.id.id_fans_user_fg_listview);
        listView.setOnItemClickListener(this);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel(
                getString(R.string.pull_to_load));
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
                getString(R.string.loading));
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel(
                getString(R.string.release_to_load));
        listView.setOnRefreshListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        Log.i("ListScroll", "用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                        Glide.with(MyApplication.mContext).pauseRequests();
                        //刷新
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.i("ListScroll", "视图已经停止滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.i("ListScroll", "手指没有离开屏幕，视图正在滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        getFansUser();

        //followItemBaseAdapter = new FollowItemBaseAdapter(getActivity(),list_user);
        //listView.setAdapter(followItemBaseAdapter);

        return view;
    }

    private void getFansUser(){
        OkhttpUtil.getFansUser(handler, page + "",username);
    }

    private void handleGetFansUser(Message msg){
        String result = msg.obj.toString();
        Log.d("FansUserFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root  root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        if (page == 1) list_user.clear();
        FansUserItem fansUserItem;
        for (User user : root.list_fans_user){
            fansUserItem = new FansUserItem();
            fansUserItem.setUser_id(user.id);
            fansUserItem.setNickname(user.nickname);
            fansUserItem.setUsername(user.username);
            fansUserItem.setSex(user.sex);
            fansUserItem.setAge(user.age);
            fansUserItem.setDistance(user.distance + "");
            fansUserItem.setSignature(user.signature);
            fansUserItem.setUser_head_path(user.user_head_path);
            fansUserItem.setOccupation(user.occupation);
            fansUserItem.setIs_vip(user.is_vip);
            list_user.add(fansUserItem);
            SharedPreferenceUtil.saveUserData(user.username, user.nickname, WebUtil.HTTP_ADDRESS + user.user_head_path);
        }
        //list_user = filledData(list_user);
        //Collections.sort(list_user, pinyinComparator);
        if (page==1){
            fansItemBaseAdapter = new FansItemBaseAdapter(getActivity(),list_user);
            listView.setAdapter(fansItemBaseAdapter);
            listView.onRefreshComplete();
        }else{
            fansItemBaseAdapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
        //
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(getActivity(), UserDataActivity.class);
//        intent.putExtra("friend_id", list_user.get(i).getUsername());
//        startActivity(intent);
        Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
        intent.putExtra("username", list_user.get(i - 1).getUsername());
        startActivity(intent);
    }


    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page=1;
        getFansUser();
        //if (OkhttpUtil.checkLogin()) checkVideoCall();
    }

    //上拉加载
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getFansUser();
    }


    class Root{
        boolean success;
        String message;
        List<User> list_fans_user;
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
        String occupation;
        int is_vip;
    }

}
