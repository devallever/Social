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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.PostDetailActivity;
import com.allever.social.adapter.NearbyPostItemBaseAdapter;
import com.allever.social.pojo.NearByPostItem;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/21.
 */
public class NearbyPostFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {
    //private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;
    private PullToRefreshListView listView;
    private List<NearByPostItem> list_postitem;
    private NearbyPostItemBaseAdapter nearbyPostItemBaseAdapter;
    private int page = 1;
    private Handler handler;

    private IntentFilter intentFilter;
    private UpdateNearbyPostReceiver receiver;

    private String recruit_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_post_fragment_layout, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEARBY_POST:
                        handleNearbyPost(msg);
                        break;
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.updateNearbyPost");
        receiver = new UpdateNearbyPostReceiver();
        getActivity().registerReceiver(receiver, intentFilter);

        listView = (PullToRefreshListView)view.findViewById(R.id.id_nearby_post_fg_listview);
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
        listView.setOnItemClickListener(this);

        list_postitem = new ArrayList<NearByPostItem>();
        getNearbyPost();


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
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        getNearbyPost();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getNearbyPost();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("post_id",list_postitem.get(i-1).getId());
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void getNearbyPost(){
        OkhttpUtil.getNearbyPost(handler,page+"");
    }

    private void handleNearbyPost(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyPostFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            new Dialog(getActivity(),"Tips","无法获取附近招聘").show();
            return;
        }else{
            if (page == 1) list_postitem.clear();
            NearByPostItem nearByPostItem;
            for(Post post : root.list_post){
                nearByPostItem = new NearByPostItem();
                nearByPostItem.setId(post.id);
                nearByPostItem.setPostname(post.postname);
                nearByPostItem.setSalary(post.salary);
                nearByPostItem.setDistance(post.distance);
                nearByPostItem.setUser_head_path(post.user_head_path);
                nearByPostItem.setUser_id(post.user_id);
                nearByPostItem.setRecruit_id(post.recruit_id);
                nearByPostItem.setPhone(post.phone);
                nearByPostItem.setIs_owner(post.is_owner);
                list_postitem.add(nearByPostItem);
            }

            if (page == 1){
                nearbyPostItemBaseAdapter = new NearbyPostItemBaseAdapter(getActivity(),list_postitem);
                listView.setAdapter(nearbyPostItemBaseAdapter);
                listView.onRefreshComplete();
            }else{
                nearbyPostItemBaseAdapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }

        }
    }


//    @Override
//    public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
//                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
//                    getNearbyPost();
//                    isloading = false;
//
//                } else {
//                    Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
//                            Toast.LENGTH_SHORT).show();
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        }, 1000);
//    }


    class Root{
        boolean success;
        String message;
        List<Post> list_post;
    }

    class Post{
        String id;
        String postname;
        double distance;
        String salary;
        String user_id;
        String user_head_path;
        String recruit_id;
        int is_owner;
        String phone;
    }

    private class UpdateNearbyPostReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            page = 1;
            getNearbyPost();
        }
    }
}
