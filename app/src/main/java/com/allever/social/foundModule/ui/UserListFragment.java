package com.allever.social.foundModule.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.foundModule.adapter.UserListBaseAdapter;
import com.allever.social.foundModule.bean.UserBeen;
import com.allever.social.listener.RecyclerViewScrollListener;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.modules.main.nearByUser.event.DownloadHeadFinishEvent;
import com.allever.social.utils.FileUtil;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Allever on 2016/12/2.
 */

public class UserListFragment extends Fragment implements RecyclerViewScrollListener.OnRecycleRefreshListener {

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private UserListBaseAdapter userListBaseAdapter;
    private List<UserBeen> list_users = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewScrollListener recyclerViewScrollListener;

    private Handler handler;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_fragment_layout,container,false);

        createUserHeadDir();


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_USER_LIST:
                        handleUserList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_PULL_REFRESH_USER:
                        handlePullRefreshUser(msg);
                        break;
                }
            }
        };

        //initData();
        initView(view);
        showProgressDialog("正在加载");
        getUserList();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void createUserHeadDir(){
        String dirPath = FileUtil.USER_HEAD_DIR;
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
    }


    private void initView(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.id_user_list_fg_recycler_view);
        userListBaseAdapter = new UserListBaseAdapter(getActivity(), list_users);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(userListBaseAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
                intent.putExtra("username", list_users.get(position).getUsername());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_user_list_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        recyclerViewScrollListener = new RecyclerViewScrollListener(this);

        recyclerView.addOnScrollListener(recyclerViewScrollListener);
        swipeRefreshLayout.setOnRefreshListener(recyclerViewScrollListener);

    }

    private void getUserList(){
        OkhttpUtil.getUserList(handler,page+"");
    }

    private void handleUserList(Message msg){
        final String result = msg.obj.toString();
        Log.d("UserListFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        final UserRoot root = gson.fromJson(result, UserRoot.class);
        swipeRefreshLayout.setRefreshing(false);
        dismissProgressDialog();
        if (root == null){
            //dismissProgressDialog();
            //new Dialog(this,"错误","链接服务器失败").show();
            //listView.onRefreshComplete();
            //swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            //listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            //dismissProgressDialog();
            //listView.onRefreshComplete();
            //swipeRefreshLayout.setRefreshing(false);
            //new Dialog(getActivity(),"错误",root.message).show();
            return;
        }

        if (page == 1){
            list_users.clear();
        }
        UserBeen userBeen;
        for (User user: root.user_list){
            userBeen = new UserBeen();
            userBeen.setUsername(user.username);
            userBeen.setNickname(user.nickname);
            userBeen.setHead_path(WebUtil.HTTP_ADDRESS + user.user_head_path);
            userBeen.setSex(user.sex);
            userBeen.setAge(user.age);
            userBeen.setIs_accept_video(user.accetp_video);
            userBeen.setLogin_time(user.login_time);
            userBeen.setOccupation(user.occupation);
            list_users.add(userBeen);
            SharedPreferenceUtil.saveUserData(user.username, user.nickname, WebUtil.HTTP_ADDRESS + user.user_head_path);
        }
        recyclerViewScrollListener.setLoadDataStatus(false);
        userListBaseAdapter.notifyDataSetChanged();

        if (SharedPreferenceUtil.getRefreshUserRefreshingState()==0){
            //已超过一分钟 向其他用户推送
            if (OkhttpUtil.checkLogin()){
                pullRefreshUser();
            }
        }

    }

    private void pullRefreshUser(){
        OkhttpUtil.pullRefreshUser(handler);
    }

    private void handlePullRefreshUser(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        RefresuUserRoot root = gson.fromJson(result, RefresuUserRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        //推送成功,修改SharePreference 为 1
        SharedPreferenceUtil.setRefreshUserRefreshingState(1);
        //新建线程1分钟后修改为 0
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*60);
                    SharedPreferenceUtil.setRefreshUserRefreshingState(0);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initData(){
        UserBeen userBeen1 = new UserBeen();
        userBeen1.setNickname("Allever");
        userBeen1.setSex("女");
        userBeen1.setAge(23);
        userBeen1.setIs_accept_video(0);
        userBeen1.setHead_path(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg");
        userBeen1.setUsername("xm");
        userBeen1.setLogin_time("3分钟前");
        userBeen1.setOccupation("学生");

        UserBeen userBeen2 = new UserBeen();
        userBeen2.setNickname("宝宝");
        userBeen2.setSex("女");
        userBeen2.setAge(22);
        userBeen2.setIs_accept_video(0);
        userBeen2.setHead_path(WebUtil.HTTP_ADDRESS + "/images/head/baobao.jpg");
        userBeen2.setUsername("baobao");
        userBeen2.setLogin_time("5分钟前");
        userBeen2.setOccupation("模特");

        UserBeen userBeen3 = new UserBeen();
        userBeen3.setNickname("妍纯");
        userBeen3.setSex("女");
        userBeen3.setAge(19);
        userBeen3.setIs_accept_video(0);
        userBeen3.setHead_path(WebUtil.HTTP_ADDRESS + "/images/head/yc_been.jpg");
        userBeen3.setUsername("yc_been");
        userBeen3.setLogin_time("9分钟前");
        userBeen3.setOccupation("幼师");

        UserBeen userBeen4 = new UserBeen();
        userBeen4.setNickname("输得起，要开心");
        userBeen4.setSex("女");
        userBeen4.setAge(23);
        userBeen4.setIs_accept_video(0);
        userBeen4.setHead_path(WebUtil.HTTP_ADDRESS + "/images/head/xsx.jpg");
        userBeen4.setUsername("xsx");
        userBeen4.setLogin_time("21分钟前");
        userBeen4.setOccupation("销售");

        UserBeen userBeen5 = new UserBeen();
        userBeen5.setNickname("女人何必楚楚动人");
        userBeen5.setSex("女");
        userBeen5.setAge(23);
        userBeen5.setIs_accept_video(0);
        userBeen5.setHead_path(WebUtil.HTTP_ADDRESS + "/images/head/mfj.jpg");
        userBeen5.setUsername("mfj");
        userBeen5.setLogin_time("32分钟前");
        userBeen5.setOccupation("妈妈");

        list_users.add(userBeen1);
        list_users.add(userBeen2);
        list_users.add(userBeen3);
        list_users.add(userBeen4);
        list_users.add(userBeen5);
        list_users.add(userBeen2);
        list_users.add(userBeen1);
        list_users.add(userBeen3);
        list_users.add(userBeen4);
        list_users.add(userBeen1);
        list_users.add(userBeen5);
        list_users.add(userBeen4);
        list_users.add(userBeen2);
        list_users.add(userBeen3);
//        list_users.add(userBeen4);
//        list_users.add(userBeen5);
//        list_users.add(userBeen3);
//        list_users.add(userBeen2);
//        list_users.add(userBeen4);
//        list_users.add(userBeen1);
    }

    @Override
    public void loadMore() {
        showProgressDialog("正在加载...");
        //Toast.makeText(getActivity(),"正在加载...",Toast.LENGTH_LONG).show();
        page ++;
        getUserList();
    }

    @Override
    public void refresh() {
        page = 1;
        getUserList();
    }


    private void showProgressDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    class UserRoot{
        boolean success;
        String message;
        List<User> user_list;
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
        int video_fee;
        int accetp_video;
        String login_time;
    }

    class RefresuUserRoot{
        boolean success;
        String message;
        String[] arr_username;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notificateDownloadFinish(DownloadHeadFinishEvent downloadHeadFinishEvent){
        userListBaseAdapter.notifyDataSetChanged();
    }

}
