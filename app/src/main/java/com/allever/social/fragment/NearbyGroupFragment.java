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
import com.allever.social.activity.GroupDataActivity;
import com.allever.social.activity.NearbyGroupWithRedPocketMapViewActivity;
import com.allever.social.adapter.GroupItemArrayAdapter;
import com.allever.social.pojo.GroupItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/12.
 */
public class NearbyGroupFragment extends Fragment implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2 {
    private PullToRefreshListView listView;
   // private NearbyGroupBaseAdapter nearbyGroupBaseAdapter;
    private GroupItemArrayAdapter groupItemArrayAdapter;
    private int page = 1;
    private List<GroupItem> list_group = new ArrayList<GroupItem>();
    private Handler handler;
    private Gson gson;
    private Root root;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;

    private IntentFilter intentFilter;
    private AddGroupReceiver receiver;
    private RefreshNearbyGroupReceiver  refreshNearbyGroupReceiver;
    private int position;

    private RippleView rv_redpocket_group;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_group_fragment_layout, container, false);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_NEARBY_GROUP:
                        handleNearbyGroup(msg);
                        break;
                }
            }
        };
        listView = (PullToRefreshListView)view.findViewById(R.id.id_nearby_group_fg_listview);
        //设置上拉下拉
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


        rv_redpocket_group = (RippleView)view.findViewById(R.id.id_nearby_group_fg_rv_red_pocket_group);
        rv_redpocket_group.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(getActivity(), NearbyGroupWithRedPocketMapViewActivity.class);
                intent.putExtra("is_first_page",1);
                startActivity(intent);
                //getActivity().finish();
            }
        });


        getNearbyGroupList();

//        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_nearby_group_fg_refresh);
//        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
//                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.refresh_group_list");
        intentFilter.addAction("com.allever.social.REFRESH_NEARBY_GROUP");
        receiver = new AddGroupReceiver();
        getActivity().registerReceiver(receiver,intentFilter);
        refreshNearbyGroupReceiver = new RefreshNearbyGroupReceiver();
        getActivity().registerReceiver(refreshNearbyGroupReceiver,intentFilter);

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
        getNearbyGroupList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getNearbyGroupList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(refreshNearbyGroupReceiver);
    }

//    @Override
//    public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
//                    //Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
//                    getNearbyGroupList();
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

    private void getNearbyGroupList(){
        OkhttpUtil.getNearbyGroup(handler,page+"");
    }

    private void handleNearbyGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        boolean is_success = root.success;
        if (!is_success){
            //closeProgressDialog();
            new Dialog(getActivity(),"Tips","无法获取附近群组").show();
            return;
        }else{
            if (page == 1)list_group.clear();
            GroupItem groupItem;
            for(Group group : root.group_list){
                groupItem = new GroupItem();
                groupItem.setId(group.id);
                groupItem.setWomen_count(group.women_count);
                groupItem.setPoint(group.point);
                groupItem.setMember_count(group.member_count);
                groupItem.setGroupname(group.groupname);
                groupItem.setAttention(group.attention);
                groupItem.setIs_member(group.is_member);
                groupItem.setHx_group_id(group.hx_group_id);
                groupItem.setGroup_bulider_path(group.group_bulider.headpath);
                groupItem.setGroup_img(group.group_img);
                groupItem.setGroup_type(group.group_type);
                String[] arr_member_head_path = new String[group.list_members.size()];
                for(int i=0;i<group.list_members.size();i++){
                    arr_member_head_path[i] = group.list_members.get(i).headpath;
                }
                groupItem.setList_members_path(arr_member_head_path);
                groupItem.setDistance(group.distance);
                list_group.add(groupItem);

                SharedPreferenceUtil.saveGroupDataFromHXgroupid(group.id,group.groupname,group.hx_group_id,WebUtil.HTTP_ADDRESS + group.group_img);
            }

            if (page==1){
                groupItemArrayAdapter = new GroupItemArrayAdapter(getActivity(),R.layout.nearby_group_item,list_group);
                listView.setAdapter(groupItemArrayAdapter);
                listView.onRefreshComplete();
            }else{
                groupItemArrayAdapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), GroupDataActivity.class);
        intent.putExtra("group_id",list_group.get(i-1).getId());
        startActivity(intent);
        position = i;
    }

    class Root {
        boolean success;
        String message;
        List<Group> group_list;
    }

    class Group {
        String id;
        String groupname;
        String group_img;
        User group_bulider;
        double distance;
        String point;
        int is_member;
        int member_count;
        int women_count;
        String hx_group_id;
        String attention;
        int group_type;
        List<User> list_members;
    }

    class User{
        String id;
        String username;
        String nickname;
        String headpath;
        int age;
        String sex;
    }

    private class AddGroupReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            page = 1;
            getNearbyGroupList();
            //listView.setSelection(position);
        }
    }

    private class RefreshNearbyGroupReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            page = 1;
            getNearbyGroupList();
            //listView.setSelection(position);
        }
    }
}
