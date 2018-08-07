package com.allever.social.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.adapter.FriendLocationRecyclerViewAdapter;
import com.allever.social.pojo.FriendLocationItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Allever on 2016/11/5.
 */

public class FriendLocationActivity extends BaseActivity {

    private List<FriendLocationItem> list_friend_locationItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendLocationRecyclerViewAdapter friendLocationRecyclerViewAdapter;
    private int position = 0;

    private Handler handler;

    private MapView mapView;
    private BaiduMap baiduMap;

    private RippleView rv_add_friend;

    private AcceptFriendLocationReceiver acceptFriendLocationReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(MyApplication.mContext);
        setContentView(R.layout.friend_location_activity_layout);

        intentFilter = new IntentFilter("com.allever.social.update_friend_location");
        acceptFriendLocationReceiver = new AcceptFriendLocationReceiver();
        this.registerReceiver(acceptFriendLocationReceiver,intentFilter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_LOCATION_LIST:
                        handleGetFriendLocationList(msg);
                        break;
                }
            }
        };


        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("定位");


        initView();

        getFriendLocationList();
    }


    private void initView(){
        recyclerView = (RecyclerView)this.findViewById(R.id.id_friend_location_activity_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                position = i;
                addOverlay();
            }

            @Override
            public void onItemLongClick(View view, int i) {
            }
        }));


        mapView = (MapView)this.findViewById(R.id.id_friend_location_activity_map_view);
        baiduMap = mapView.getMap();

        rv_add_friend = (RippleView)this.findViewById(R.id.id_friend_location_activity_rv_add_friend);
        rv_add_friend.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(FriendLocationActivity.this, ChooseRequestFriendLocationActivity.class);
                startActivity(intent);

            }
        });


    }

    private void getFriendLocationList(){
        OkhttpUtil.getFriendLocationList(handler);
    }

    private void handleGetFriendLocationList(Message msg){
        String result = msg.obj.toString();
        Log.d("FriendLocationActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        list_friend_locationItems.clear();

        FriendLocationItem friendLocationItem;
        for (Friend friend: root.friends_list){
            friendLocationItem = new FriendLocationItem();
            friendLocationItem.setUser_id(friend.id);
            friendLocationItem.setUsername(friend.username);
            friendLocationItem.setUser_head_path(friend.head_path);
            friendLocationItem.setNickname(friend.nickname);
            friendLocationItem.setLongitude(friend.longitude);
            friendLocationItem.setLatitude(friend.latitude);
            friendLocationItem.setAddress(friend.address);
            list_friend_locationItems.add(friendLocationItem);
        }

        friendLocationRecyclerViewAdapter = new FriendLocationRecyclerViewAdapter(this,list_friend_locationItems);
        recyclerView.setAdapter(friendLocationRecyclerViewAdapter);

        addOverlay();

    }

    private void addOverlay(){
        //清空地图
        baiduMap.clear();
        //创建marker的显示图标
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;



        FriendLocationItem friendLocationItem = list_friend_locationItems.get(position);
        //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_red_48);
        View view = LayoutInflater.from(this).inflate(R.layout.friend_location_mark,null);
        TextView textView = (TextView) view.findViewById(R.id.id_friend_location_mark_tv_address);
        CircleImageView imageView = (CircleImageView)view.findViewById(R.id.id_friend_location_mark_iv_head);
        Glide.with(this).load(WebUtil.HTTP_ADDRESS+friendLocationItem.getUser_head_path()).into(imageView);
        textView.setText(friendLocationItem.getAddress());
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);

        //获取经纬度
        latLng = new LatLng(friendLocationItem.getLatitude(),friendLocationItem.getLongitude());
        //设置marker
        options = new MarkerOptions()
                .position(latLng)//设置位置
                .icon(bitmap)//设置图标样式
                .zIndex(9) // 设置marker所在层级
                .draggable(true); // 设置手势拖拽;
        //添加marker
        marker = (Marker) baiduMap.addOverlay(options);
        //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
        Bundle bundle = new Bundle();
        //info必须实现序列化接口
        bundle.putSerializable("info", friendLocationItem);
        marker.setExtraInfo(bundle);

        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.setMapStatus(msu);

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
        if (mapView!=null){
            mapView.onDestroy();
        }
        unregisterReceiver(acceptFriendLocationReceiver);
        super.onDestroy();
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


    class Root{
        boolean success;
        String message;
        List<Friend> friends_list;
    }

    class Friend{
        String id;
        String nickname;
        String username;
        String head_path;
        String signature;
        double longitude;
        double latitude;
        String address;
    }

    class AcceptFriendLocationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            getFriendLocationList();
        }
    }
}
