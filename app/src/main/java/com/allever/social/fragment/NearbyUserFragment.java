package com.allever.social.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.activity.SelectedNearbyUserDialogActivity;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.UserDataActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.adapter.NewsItemAdapter;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.pojo.NewsItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/9.
 */
public class NearbyUserFragment extends Fragment implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2 {

    private final static int REQUEST_CODE_SELECTED_NEARBY_USER = 1000;

    private PullToRefreshListView listView;
    private NearbyUserItemAdapter nearbyUserItemAdapter;
    private List<NearByUserItem> list_user = new ArrayList<>();
    private int page = 1;

    private RippleView rv_video_call;
    private RippleView rv_selected;

    private ProgressDialog progressDialog;

    private String video_fee;

   private ADBarFragment adBarFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private IntentFilter intentFilter;

    private Handler handler;
    private Gson gson;
    private NearbyUserRoot root;

    private boolean isloading;
    private boolean haveMoreData = true;

    //private CharacterParser characterParser;
    //private PinyinComparator pinyinComparator;

    private LinearLayout ll_toolbar;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.nearby_user_fragment_layout,container,false);

        ll_toolbar = (LinearLayout)container.findViewById(R.id.id_main_fragment_ll_toolbar);

        rv_video_call = (RippleView)view.findViewById(R.id.id_nearby_user_rv_video_call);
        rv_selected = (RippleView)view.findViewById(R.id.id_nearby_user_rv_selected);

        listView = (PullToRefreshListView)view.findViewById(R.id.id_near_by_user_fg_listview);
        listView.setOnItemClickListener(this);

        listView.setMode(Mode.BOTH);
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

        showProgressDialog();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });


        rv_video_call.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //开启视频聊天
                OkhttpUtil.saveVideoFeeSetting(handler,"1",video_fee);
                rv_video_call.setVisibility(View.GONE);
                //page = 1;
                //getNearbyUser();
            }
        });

        rv_selected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //打开shaixua筛选对话框
                Intent intent = new Intent(getActivity(), SelectedNearbyUserDialogActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SELECTED_NEARBY_USER);
            }
        });

        fragmentManager = this.getChildFragmentManager();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEARBY_USER:
                        handleNearbyUser(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        handleADSetting(msg);
                        break;
                    case OkhttpUtil.MESSAGE_CHECK_VIDEO_CALL:
                        handleCheckVideoCall(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SAVE_VIDEO_FEE_SETTING:
                        handleSaveVideoCall(msg);
                        break;
                    case OkhttpUtil.MESSAGE_REFRESH_NEARBY_USER:
                        handleRefreshNearbyUser(msg);
                        break;
                    case OkhttpUtil.MESSAGE_PULL_REFRESH_USER:
                        handlePullRefreshUser(msg);
                        break;

                }
            }
        };
        getNearbyUser();

        if (OkhttpUtil.checkLogin()) checkVideoCall();

        return view;
    }


    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SELECTED_NEARBY_USER:
                if (resultCode==getActivity().RESULT_OK){
                    //刷新附近人
                    list_user.clear();
                    nearbyUserItemAdapter.notifyDataSetChanged();
                    page = 1;
                    refreshNearbyUser();
                }
                break;
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



    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page=1;
        //getNearbyUser();
        refreshNearbyUser();
        if (OkhttpUtil.checkLogin()) checkVideoCall();
    }

    //上拉加载
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getNearbyUser();
    }

    private void checkVideoCall(){
        OkhttpUtil.checkVideoCall(handler);
    }

    private void handleCheckVideoCall(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CheckVideoCallRoot root = gson.fromJson(result, CheckVideoCallRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        video_fee = root.video_fee + "";
    }


    private void getNearbyUser(){
        OkhttpUtil.getNearbyUser(handler,page+"");
    }

    private void handleNearbyUser(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, NearbyUserRoot.class);

        if (root == null){
            closeProgressDialog();
            listView.onRefreshComplete();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            closeProgressDialog();
            listView.onRefreshComplete();
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            closeProgressDialog();
            listView.onRefreshComplete();
            new Dialog(getActivity(),"Tips","无法获取附近人").show();
            return;
        }else{
            if (page == 1) list_user.clear();
            NearByUserItem nearByUserItem;
            for (User user : root.user_list){
                nearByUserItem = new NearByUserItem();
                nearByUserItem.setUser_id(user.id);
                nearByUserItem.setNickname(user.nickname);
                nearByUserItem.setUsername(user.username);
                nearByUserItem.setSex(user.sex);
                nearByUserItem.setAge(user.age);
                nearByUserItem.setDistance(user.distance);
                nearByUserItem.setSignature(user.signature);
                nearByUserItem.setUser_head_path(user.user_head_path);
                nearByUserItem.setOccupation(user.occupation);
                nearByUserItem.setConstellation(user.constellation);
                nearByUserItem.setIs_vip(user.is_vip);
                nearByUserItem.setAccept_video(user.accetp_video);
                nearByUserItem.setVideo_fee(user.video_fee);
                list_user.add(nearByUserItem);
                SharedPreferenceUtil.saveUserData(user.username, user.nickname, WebUtil.HTTP_ADDRESS + user.user_head_path);
            }
            if (page==1){
                nearbyUserItemAdapter = new NearbyUserItemAdapter(getActivity(),R.layout.near_by_user_item,list_user);
                listView.setAdapter(nearbyUserItemAdapter);
                listView.onRefreshComplete();
            }else{
                nearbyUserItemAdapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }
        }
        closeProgressDialog();
    }

    private void refreshNearbyUser(){
        OkhttpUtil.refreshNearbyUser(handler, page + "");
    }

    private void handleRefreshNearbyUser(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, NearbyUserRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            //closeProgressDialog();
            new Dialog(getActivity(),"Tips","无法获取附近人").show();
            return;
        }else{
            //closeProgressDialog();
            if (page == 1) list_user.clear();
            NearByUserItem nearByUserItem;
            for (User user : root.user_list){
                nearByUserItem = new NearByUserItem();
                nearByUserItem.setUser_id(user.id);
                nearByUserItem.setNickname(user.nickname);
                nearByUserItem.setUsername(user.username);
                nearByUserItem.setSex(user.sex);
                nearByUserItem.setAge(user.age);
                nearByUserItem.setDistance(user.distance);
                nearByUserItem.setSignature(user.signature);
                nearByUserItem.setUser_head_path(user.user_head_path);
                nearByUserItem.setOccupation(user.occupation);
                nearByUserItem.setConstellation(user.constellation);
                nearByUserItem.setIs_vip(user.is_vip);
                nearByUserItem.setAccept_video(user.accetp_video);
                nearByUserItem.setVideo_fee(user.video_fee);
                list_user.add(nearByUserItem);
                SharedPreferenceUtil.saveUserData(user.username, user.nickname, WebUtil.HTTP_ADDRESS + user.user_head_path);
            }
            if (page==1){
                nearbyUserItemAdapter = new NearbyUserItemAdapter(getActivity(),R.layout.near_by_user_item,list_user);
                listView.setAdapter(nearbyUserItemAdapter);
                listView.onRefreshComplete();
            }else{
                nearbyUserItemAdapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }

            if (SharedPreferenceUtil.getRefreshUserRefreshingState()==0){
                //已超过一分钟 向其他用户推送
                if (OkhttpUtil.checkLogin()){
                    pullRefreshUser();
                }
            }
        }
    }

    private void pullRefreshUser(){
        OkhttpUtil.pullRefreshUser(handler);
    }

    private void handlePullRefreshUser(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, NearbyUserRoot.class);

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
        intent.putExtra("username", list_user.get(i-1).getUsername());
        startActivity(intent);
    }

    class NearbyUserRoot{
        boolean success;
        String message;
        List<User> user_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       ///getActivity().unregisterReceiver(updateVideoCallReceiver);
    }

    class User{
        String id;
        String username;
        String nickname;
        String sex;
        String distance;
        String user_head_path;
        String signature;
        int age;
        String occupation;
        String constellation;
        int is_vip;
        int video_fee;
        int accetp_video;

    }


    private void getADSetting(){
        OkhttpUtil.getADSetting(handler);
    }
    private void handleADSetting(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADSettingRoot  root = gson.fromJson(result, ADSettingRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }
    }


    private void handleSaveVideoCall(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        VideoCallSettingRoot root = gson.fromJson(result, VideoCallSettingRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        page = 1;
        getNearbyUser();
    }



    class ADSettingRoot{
        boolean success;
        String message;
        ADSetting ad_setting;
    }

    class ADSetting{
        String id;
        int day_space;
        int count;
        int isshow;
    }

    class CheckVideoCallRoot{
        boolean success;
        String message;
        int accept_video;
        int video_fee;
    }

    class VideoCallSettingRoot{
        boolean success;
        String message;
    }

}
