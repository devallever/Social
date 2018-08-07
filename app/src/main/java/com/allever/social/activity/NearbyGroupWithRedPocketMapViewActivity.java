package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.pojo.RedPocketGroup;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/8/23.
 */
public class NearbyGroupWithRedPocketMapViewActivity  extends BaseActivity implements View.OnClickListener{

    private FloatingActionButton fab_home;

    private List<RedPocketGroup> infos;

    private MapView mapView;

    private BaiduMap mBaiduMap;

    private RelativeLayout rl_mark_info_container;

    private Handler handler;

    private int is_first_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.nearby_group_with_redpocket_mapview_activity_layout);

        is_first_page = getIntent().getIntExtra("is_first_page",0);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_RED_POCKET_GROUP:
                        handleRedpocketGroupList(msg);
                        break;
                }
            }
        };

        initView();

        getRedPocketGroupList();

    }

    private void getRedPocketGroupList(){
        OkhttpUtil.getRedPocketGroup(handler);
    }

    private void handleRedpocketGroupList(Message msg){
        String result = msg.obj.toString();
        Log.d("RedPocketGroupActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        infos.clear();
        RedPocketGroup redPocketGroup;
        for(Group group:root.group_list){
            redPocketGroup = new RedPocketGroup();
            redPocketGroup.setId(group.id);
            redPocketGroup.setAttention(group.attention);
            redPocketGroup.setDistance(group.distance);
            redPocketGroup.setGroup_bulider_path(group.group_bulider.headpath);
            redPocketGroup.setGroup_img(group.group_img);
            redPocketGroup.setGroupname(group.groupname);
            redPocketGroup.setHx_group_id(group.hx_group_id);
            redPocketGroup.setIs_member(group.is_member);
            redPocketGroup.setLongitude(group.longitude);
            redPocketGroup.setLatitude(group.latitude);
            redPocketGroup.setGroup_type(group.group_type);
            String[] arr_member_head_path = new String[group.list_members.size()];
            for(int i=0;i<group.list_members.size();i++){
                arr_member_head_path[i] = group.list_members.get(i).headpath;
            }
            redPocketGroup.setList_members_path(arr_member_head_path);
            redPocketGroup.setMember_count(group.member_count);
            redPocketGroup.setWomen_count(group.women_count);
            redPocketGroup.setPoint(group.point);
            infos.add(redPocketGroup);
        }

        addOverlay(infos);


    }

    private void initView(){
        infos = new ArrayList<RedPocketGroup>();

        fab_home = (FloatingActionButton)this.findViewById(R.id.id_nearby_group_with_redpocket_mapview_activity_fab_home);
        fab_home.setOnClickListener(this);

        if (is_first_page==1){
            fab_home.setVisibility(View.GONE);
        }else{
            fab_home.setVisibility(View.VISIBLE);
        }

        mapView = (MapView)this.findViewById(R.id.id_nearby_group_with_redpocket_mapview_activity_mapview);
        mBaiduMap  = mapView.getMap();

        rl_mark_info_container = (RelativeLayout)this.findViewById(R.id.id_nearby_group_with_redpocket_mapview_activity_mark_info_windows);
        rl_mark_info_container.setVisibility(View.INVISIBLE);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                rl_mark_info_container.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_nearby_group_with_redpocket_mapview_activity_fab_home:
                Intent intent = new Intent(this, SocialMainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
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
        mapView.onDestroy();
    }

    private void setMarkerInfo() {
        infos = new ArrayList<RedPocketGroup>();
        RedPocketGroup redPocketGroup_1 = new RedPocketGroup();
        redPocketGroup_1.setId("1");
        redPocketGroup_1.setAttention("注意");
        redPocketGroup_1.setDistance(0.10);
        redPocketGroup_1.setGroup_bulider_path("/images/head/xm.jpg");
        redPocketGroup_1.setGroup_img("/images/group/1024.jpg");
        redPocketGroup_1.setGroupname("平洲交友");
        redPocketGroup_1.setHx_group_id("1469497619236");
        redPocketGroup_1.setIs_member(1);
        redPocketGroup_1.setLongitude(113.202344);
        redPocketGroup_1.setLatitude(23.22656);
        redPocketGroup_1.setList_members_path(new String[]{"/images/head/xm.jpg", "/images/head/meimei.jpg", "images/head/xsx.jpg"});
        redPocketGroup_1.setMember_count(3);
        redPocketGroup_1.setWomen_count(1);
        redPocketGroup_1.setPoint("平南市场");
        //infos.add(redPocketGroup_1);
        RedPocketGroup redPocketGroup_2 = new RedPocketGroup();
        redPocketGroup_2.setId("1");
        redPocketGroup_2.setAttention("注意");
        redPocketGroup_2.setDistance(0.10);
        redPocketGroup_2.setGroup_bulider_path("/images/head/xm.jpg");
        redPocketGroup_2.setGroup_img("/images/group/1.jpg");
        redPocketGroup_2.setGroupname("平洲交友");
        redPocketGroup_2.setHx_group_id("1469497619236");
        redPocketGroup_2.setIs_member(1);
        redPocketGroup_2.setList_members_path(new String[]{"/images/head/xm.jpg", "/images/head/meimei.jpg", "images/head/xsx.jpg"});
        redPocketGroup_2.setMember_count(3);
        redPocketGroup_2.setWomen_count(1);
        redPocketGroup_2.setPoint("平西");
        redPocketGroup_2.setLongitude(113.502344);
        redPocketGroup_2.setLatitude(23.32656);
        //infos.add(redPocketGroup_2);
        RedPocketGroup redPocketGroup_3 = new RedPocketGroup();
        redPocketGroup_3.setId("1");
        redPocketGroup_3.setAttention("注意");
        redPocketGroup_3.setDistance(0.10);
        redPocketGroup_3.setGroup_bulider_path("/images/head/meimei.jpg");
        redPocketGroup_3.setGroup_img("/images/group/1.jpg");
        redPocketGroup_3.setGroupname("群3");
        redPocketGroup_3.setHx_group_id("1469497619236");
        redPocketGroup_3.setIs_member(1);
        redPocketGroup_3.setList_members_path(new String[]{"/images/head/xm.jpg", "/images/head/meimei.jpg", "images/head/xsx.jpg"});
        redPocketGroup_3.setMember_count(3);
        redPocketGroup_3.setWomen_count(1);
        redPocketGroup_3.setPoint("玉器街");
        redPocketGroup_3.setLongitude(113.302344);
        redPocketGroup_3.setLatitude(23.62656);
        //infos.add(redPocketGroup_3);
        //OkhttpUtil.downloadNewsVoice();
        //infos.add(new RedPocketGroup(117.176955,39.111345,"南开大学",R.drawable.nankai,"正式成立于1919年，是由严修、张伯苓秉承教育救国理念创办的综合性大学。"));
        //infos.add(new RedPocketGroup(117.174081,39.094994,"天津水上公园",R.drawable.shuishang,"天津水上公园原称青龙潭，1951年7月1日正式对游客开放，有北方的小西子之称。"));
    }

    //显示marker
    private void addOverlay(List<RedPocketGroup> infos2) {
        //清空地图
        mBaiduMap.clear();
        //创建marker的显示图标

        //Glide.with(this).load(in)
        //final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.bg_hb_loca_60);

        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;
        for(RedPocketGroup info:infos){

//            TextView textView = new TextView(this);
//            textView.setText("Title");
            CircleImageView circleImageView = new CircleImageView(this);
//            Glide.with(this).load(WebUtil.HTTP_ADDRESS+ "/images/head/xm.jpg").into(circleImageView);
            LinearLayout  view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.friend_location_mark,null);
            Glide.with(this).load(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg").into(circleImageView);
            view.addView(circleImageView);

            //CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.id_friend_location_mark_iv_head);
            //Glide.with(this).load(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg").into(circleImageView);
            //final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.bg_hb_loca_60);
            final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
            //获取经纬度
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            //设置marker
            options = new MarkerOptions()
                    .position(latLng)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            //添加marker
            marker = (Marker) mBaiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                final RedPocketGroup redPocketGroup = (RedPocketGroup)bundle.getSerializable("info");
                //Toast.makeText(NearbyGroupWithRedPocketMapViewActivity.this,redPocketGroup.getGroupname(),Toast.LENGTH_LONG).show();
                rl_mark_info_container.setVisibility(View.VISIBLE);
                rl_mark_info_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NearbyGroupWithRedPocketMapViewActivity.this,GroupDataActivity.class);
                        intent.putExtra("group_id", redPocketGroup.getId());
                        NearbyGroupWithRedPocketMapViewActivity.this.startActivity(intent);
                    }
                });
                ViewHolder viewHolder;
                if (rl_mark_info_container.getTag()==null){
                    viewHolder = new ViewHolder();

                    viewHolder.iv_group_img = (CircleImageView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_iv_group);
                    viewHolder.iv_member_1 = (CircleImageView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_iv_member_1);
                    viewHolder.iv_member_2 = (CircleImageView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_iv_member_2);
                    viewHolder.iv_member_3 = (CircleImageView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_iv_member_3);
                    viewHolder.btn_chat = (ButtonRectangle)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_btn_chat);
                    viewHolder.tv_groupname = (TextView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_tv_groupname);
                    viewHolder.tv_point = (TextView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_tv_point);
                    viewHolder.tv_distance = (TextView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_tv_distance);
                    viewHolder.tv_groupmember_count = (TextView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_tv_member_count);
                    viewHolder.tv_attention_desc = (TextView)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_tv_attention_desc);
                    viewHolder.rv_group_member_container = (RippleView)rl_mark_info_container.findViewById(R.id.id_near_by_group_rv_group_with_redpocket_mapview_activity_member_container);
                    viewHolder.btn_join = (ButtonRectangle)rl_mark_info_container.findViewById(R.id.id_near_by_group_with_redpocket_mapview_activity_btn_join);
                    rl_mark_info_container.setTag(viewHolder);
                }
                viewHolder = (ViewHolder)rl_mark_info_container.getTag();

                Glide.with(NearbyGroupWithRedPocketMapViewActivity.this)
                        .load(WebUtil.HTTP_ADDRESS + redPocketGroup.getGroup_img())
                        .into(viewHolder.iv_group_img);
                viewHolder.tv_groupname.setText(redPocketGroup.getGroupname());
                viewHolder.tv_point.setText(redPocketGroup.getPoint());
                viewHolder.tv_distance.setText(redPocketGroup.getDistance() + "");
                viewHolder.tv_attention_desc.setText(redPocketGroup.getAttention());
                viewHolder.tv_groupmember_count.setText("本群共" + redPocketGroup.getMember_count() + "(女生" + redPocketGroup.getWomen_count() + "人)");

                Glide.with(NearbyGroupWithRedPocketMapViewActivity.this)
                        .load(WebUtil.HTTP_ADDRESS + redPocketGroup.getGroup_bulider_path())
                        .into(viewHolder.iv_member_1);
                if (redPocketGroup.getMember_count()>=2){
                    Glide.with(NearbyGroupWithRedPocketMapViewActivity.this)
                            .load(WebUtil.HTTP_ADDRESS + redPocketGroup.getList_members_path()[1])
                            .into(viewHolder.iv_member_2);
                }
                if (redPocketGroup.getMember_count()>=3){
                    Glide.with(NearbyGroupWithRedPocketMapViewActivity.this)
                            .load(WebUtil.HTTP_ADDRESS + redPocketGroup.getList_members_path()[2])
                            .into(viewHolder.iv_member_3);
                }

                if (redPocketGroup.getIs_member()==1){
                    viewHolder.btn_chat.setVisibility(View.VISIBLE);
                    viewHolder.btn_join.setVisibility(View.INVISIBLE);
                }else{
                    viewHolder.btn_chat.setVisibility(View.INVISIBLE);
                    viewHolder.btn_join.setVisibility(View.VISIBLE);
                }


                viewHolder.btn_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //发请求
                        //joinGroup(list_group.get(position).getId() + "");
                        switch (redPocketGroup.getGroup_type()){
                            case 1:
                            case 2:
                                Toast.makeText(NearbyGroupWithRedPocketMapViewActivity.this, "私有群，只有群主可以邀请人；", Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(NearbyGroupWithRedPocketMapViewActivity.this, "已申请", Toast.LENGTH_LONG).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            EMClient.getInstance().groupManager().applyJoinToGroup(redPocketGroup.getHx_group_id(), "请求加入群组");//需异步处理
                                        }catch (HyphenateException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                break;
                            case 4:
                                break;
                        }

                    }
                });

                viewHolder.btn_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //进入聊天界面
                        //joinGroup(list_group.get(position).getId() + "");
                        Intent intent;
                        intent = new Intent(NearbyGroupWithRedPocketMapViewActivity.this,GroupChatActivity.class);
                        intent.putExtra("hx_group_id", redPocketGroup.getHx_group_id());
                        NearbyGroupWithRedPocketMapViewActivity.this.startActivity(intent);
                    }
                });

                viewHolder.rv_group_member_container.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent intent = new Intent(NearbyGroupWithRedPocketMapViewActivity.this, GroupMemberActivity.class);
                        intent.putExtra("group_id", redPocketGroup.getId());
                        intent.putExtra("hx_group_id", redPocketGroup.getHx_group_id());
                        NearbyGroupWithRedPocketMapViewActivity.this.startActivity(intent);
                    }
                });
                return false;
            }
        });

    }

    //显示InfoWindow
    private void showBaiduInwoWindows(){
        for(RedPocketGroup info:infos){
            LatLng pt = new LatLng(info.getLatitude(),info.getLongitude());
            View bubble = new View(this);
            bubble.setBackgroundResource(R.mipmap.infowindow_48);
//创建InfoWindow,传入view，地理坐标，y 轴偏移量
            InfoWindow mInfoWindow = new InfoWindow(bubble, pt, -47);
//显示InfoWindow
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
    }

    private class ViewHolder{
        CircleImageView iv_group_img;
        TextView tv_groupname;
        TextView tv_point;
        TextView tv_distance;
        TextView tv_groupmember_count;
        ButtonRectangle btn_join;
        ButtonRectangle btn_chat;
        TextView tv_attention_desc;
        CircleImageView iv_member_1;
        CircleImageView iv_member_2;
        CircleImageView iv_member_3;
        RippleView rv_group_member_container;

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
        double longitude;
        double latitude;
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
}
