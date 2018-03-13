package com.allever.social.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.adapter.RecruitItemBaseAdapter;
import com.allever.social.pojo.NearByRecruitItem;
import com.allever.social.pojo.PostItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/18.
 */
public class NearbyRecruitFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;
    private ListView listView;
    private RecruitItemBaseAdapter recruitItemBaseAdapter;

    private List<NearByRecruitItem> list_recruit;
    private Handler handler;
    private Root root;
    private Gson gson;


    private IntentFilter intentFilter;
    private AddRecruitReceiver recruitReceiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_recruit_fragment_layout, container, false);
        listView = (ListView)view.findViewById(R.id.id_nearby_recruit_fg_listview);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_nearby_recruit_fg_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        list_recruit = new ArrayList<>();

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.updateNearbyRecruit");
        recruitReceiver = new AddRecruitReceiver();
        getActivity().registerReceiver(recruitReceiver,intentFilter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_FLING:
                        Log.i("ListScroll","用户在手指离开屏幕之前，由于滑了一下，视图仍然依靠惯性继续滑动");
                        Glide.with(MyApplication.mContext).pauseRequests();
                        //刷新
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.i("ListScroll", "视图已经停止滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.i("ListScroll","手指没有离开屏幕，视图正在滑动");
                        Glide.with(MyApplication.mContext).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEARBY_RECRUIT:
                        handleNearbyRecruit(msg);
                        break;
                }
            }
        };

        getNearbyRecruit();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(recruitReceiver);
    }

    private void getNearbyRecruit(){
        OkhttpUtil.getNearbyRecruit(handler);
    }

    private void handleNearbyRecruit(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyRecruitFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            //closeProgressDialog();
            new Dialog(getActivity(),"Tips","无法获取附近招聘").show();
            return;
        }else{

            list_recruit.clear();
            NearByRecruitItem nearByRecruitItem;

            for(Recruit recruit : root.list_recruit){
                nearByRecruitItem = new NearByRecruitItem();
                nearByRecruitItem.setHead_img(recruit.user_head_path);
                nearByRecruitItem.setId(recruit.id);
                nearByRecruitItem.setDistance(recruit.distance);
                nearByRecruitItem.setCommanyname(recruit.companyname);
                nearByRecruitItem.setRequirement(recruit.requirement);
                nearByRecruitItem.setLink(recruit.link);
                nearByRecruitItem.setIs_owner(recruit.is_owner);
                nearByRecruitItem.setPhone(recruit.phone);

                List<PostItem> postItemList = new ArrayList<>();
                PostItem postItem;
                for(Post post : recruit.list_post){
                    postItem = new PostItem();
                    postItem.setId(post.id);
                    postItem.setSalary(post.salary);
                    postItem.setPostname(post.postname);
                    postItemList.add(postItem);
                }

                nearByRecruitItem.setListPostItem(postItemList);
                nearByRecruitItem.setList_recruit_img(recruit.recruit_image_path);

                list_recruit.add(nearByRecruitItem);
            }

            recruitItemBaseAdapter = new RecruitItemBaseAdapter(getActivity(),list_recruit);
            listView.setAdapter(recruitItemBaseAdapter);
            //
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    getNearbyRecruit();
                    isloading = false;

                } else {
                    Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                            Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    class Root{
        boolean success;
        String message;
        List<Recruit> list_recruit;

    }

    class Recruit{
        String id;
        String companyname;
        double distance;
        String requirement;
        String date;
        String user_head_path;
        String link;
        String phone;
        int is_owner;
        List<String> recruit_image_path;
        List<Post> list_post;
    }

    class Post{
        String id;
        String postname;
        String salary;
    }

    private class AddRecruitReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            getNearbyRecruit();
        }
    }

}
