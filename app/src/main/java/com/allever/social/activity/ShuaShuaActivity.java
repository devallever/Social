package com.allever.social.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.adapter.SwipeCardItemBaseAdapter;
import com.allever.social.pojo.SwipeCardItem;
import com.allever.social.network.util.OkhttpUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/10/14.
 */
public class ShuaShuaActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_RECOMMEND_GROUP = 1000;

    private SwipeCardItemBaseAdapter swipeCardItemBaseAdapter;
    private List<SwipeCardItem> list_swipe_card_items;
    private SwipeFlingAdapterView swipeFlingAdapterView;
    private ImageView iv_like;
    private ImageView iv_dislike;
    private ImageView iv_user_info;

    private SwipeCardItem swipeCardItem_old;
    private SwipeCardItem swipeCardItem_present;

    private int i;
    private SwipeCardItem swipeCardItem_1;
    private SwipeCardItem swipeCardItem_2;
    private SwipeCardItem swipeCardItem_3;
    private SwipeCardItem swipeCardItem_4;

    private boolean isFirst = false;

    private Handler handler;

    private int is_first_login = 0;
    private String recommend_group_json_data = "";
    private Group groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuashua_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_SHUASHUA_USER_LIST:
                        handleGetShuashuaUser(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GET_RECOMMEND_GROUP:
                        handleGetRecommendGroup(msg);
                        break;
                }
            }
        };

        isFirst = getIntent().getBooleanExtra("is_first",false);

        initView();

        getRecommendGroup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(this, SocialMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void getRecommendGroup(){
        OkhttpUtil.getRecommendGroup(handler);
    }

    private void handleGetRecommendGroup(Message msg){
        String result = msg.obj.toString();
        Log.d("ShuaShuaActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        RecommendGroupRoot root = gson.fromJson(result, RecommendGroupRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        is_first_login = root.is_first_login;
        if (root.group != null){
            recommend_group_json_data = result;
            groupData = root.group;
        }


    }

    private void initView(){

        iv_like = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_right);
        iv_dislike = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_left);
        iv_user_info = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_info);


        swipeFlingAdapterView = (SwipeFlingAdapterView)this.findViewById(R.id.id_shuashua_activity_swipe_fling_adapter_view);
        list_swipe_card_items = new ArrayList<>();

        swipeCardItemBaseAdapter = new SwipeCardItemBaseAdapter(this,list_swipe_card_items);
        swipeFlingAdapterView.setAdapter(swipeCardItemBaseAdapter);

        if (OkhttpUtil.checkLogin()) getShuashuaUserData();
        else{
            makeToast(this,"未登录");
        }


        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                swipeCardItem_old = list_swipe_card_items.get(0);
                list_swipe_card_items.remove(0);
                swipeCardItemBaseAdapter.notifyDataSetChanged();
                if (list_swipe_card_items.size()!= 0) swipeCardItem_present = list_swipe_card_items.get(0);
                //Toast.makeText(SwipeCardTestActivity.this,"remove " + swipeCardItem_old.getNickname() + "\nThis is" + swipeCardItem_present.getNickname(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //makeToast(ShuaShuaActivity.this, "不喜欢" + swipeCardItem_old.getNickname());
                updateShuashua(swipeCardItem_old.getUsername(), 0);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //makeToast(ShuaShuaActivity.this, "喜欢" + swipeCardItem_old.getNickname());
                updateShuashua(swipeCardItem_old.getUsername(), 1);
                followUser(swipeCardItem_old.getUsername());
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //makeToast(ShuaShuaActivity.this,"已经刷完了");
                //getShuashuaUserData();
//                list_swipe_card_items.add(swipeCardItem_1);
//                list_swipe_card_items.add(swipeCardItem_2);
//                list_swipe_card_items.add(swipeCardItem_3);
//                list_swipe_card_items.add(swipeCardItem_4);
//                swipeCardItemBaseAdapter.notifyDataSetChanged();
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                try {
                    View view = swipeFlingAdapterView.getSelectedView();
                    view.findViewById(R.id.id_swipe_card_item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.id_swipe_card_item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_like.setOnClickListener(this);
        iv_dislike.setOnClickListener(this);
        iv_user_info.setOnClickListener(this);
        swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent intent = new Intent(ShuaShuaActivity.this,UserDataDetailActivity.class);
                intent.putExtra("username",list_swipe_card_items.get(itemPosition).getUsername());
                startActivity(intent);
            }
        });


    }

    private void followUser(String follow_username){
        OkhttpUtil.followUser(handler,follow_username);
    }

    private void updateShuashua(String other_username,int flag){
        OkhttpUtil.updateShuashua(handler, other_username, flag + "");
    }

    private void getShuashuaUserData(){
        OkhttpUtil.getShuashuaUserList(handler);
    }

    private void handleGetShuashuaUser(Message msg){
        String result = msg.obj.toString();
        Log.d("ShuaShuaActivity", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        list_swipe_card_items.clear();
        SwipeCardItem swipeCardItem;
        for (ShuaShuaUser shuaShuaUser: root.list_shuashuaUser){
            swipeCardItem = new SwipeCardItem();
            swipeCardItem.setUsername(shuaShuaUser.username);
            swipeCardItem.setNickname(shuaShuaUser.nickanme);
            swipeCardItem.setSex(shuaShuaUser.sex);
            swipeCardItem.setOccupation(shuaShuaUser.occupation);
            swipeCardItem.setAge(shuaShuaUser.age);
            swipeCardItem.setDistance(shuaShuaUser.distance + "");
            swipeCardItem.setSignature(shuaShuaUser.signature);
            swipeCardItem.setList_imgs(shuaShuaUser.photo_wall_img_urls);
            list_swipe_card_items.add(swipeCardItem);
        }

        swipeCardItemBaseAdapter.notifyDataSetChanged();
        if (list_swipe_card_items.size() > 0) swipeCardItem_present = list_swipe_card_items.get(0);
        else Toast.makeText(this,"已刷完",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_swipe_card_button_iv_right:
                if (list_swipe_card_items.size() != 0) right();
                else makeToast(this,"已刷完");
                break;
            case R.id.id_swipe_card_button_iv_left:
                if (list_swipe_card_items.size() != 0) left();
                else makeToast(this,"已刷完");
                break;
            case R.id.id_swipe_card_button_iv_info:
                if (list_swipe_card_items.size() != 0){
                    Intent intent = new Intent(this,UserDataDetailActivity.class);
                    intent.putExtra("username", swipeCardItem_present.getUsername());
                    startActivity(intent);
                }else {
                    makeToast(this,"已刷完");
                }

                break;
        }
    }

    static void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private void right() {

        swipeFlingAdapterView.getTopCardListener().selectRight();
    }

    private void left() {
        swipeFlingAdapterView.getTopCardListener().selectLeft();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isFirst){
            //Intent intent = new Intent(this, SocialMainActivity.class);
            //startActivity(intent);
            if (is_first_login == 1){
                if (groupData!=null){
                    //启动 RecommendGroupDialogActivity
                    Intent intent = new Intent(this,RecommendGroupDialogActivity.class);
                    intent.putExtra("group_data",recommend_group_json_data);
                    startActivityForResult(intent, REQUEST_CODE_RECOMMEND_GROUP);
                    //super.onBackPressed();
                }else{
                    super.onBackPressed();
                    Intent intent = new Intent(this, SocialMainActivity.class);
                    startActivity(intent);
                }
            }else{
                super.onBackPressed();
                Intent intent = new Intent(this, SocialMainActivity.class);
                startActivity(intent);
            }
        }

    }


    class Root{
        boolean success;
        String message;
        List<ShuaShuaUser> list_shuashuaUser;
    }

    class ShuaShuaUser{
        String username;
        String nickanme;
        String sex;
        String occupation;
        int age;
        double distance;
        String signature;
        List<String> photo_wall_img_urls;
    }


    class RecommendGroupRoot{
        boolean success;
        String message;
        int is_first_login;
        Group group;
    }

    class Group{
        String id;
        String groupname;
        String group_img;
        double distance;
        int member_count;
        int women_count;
        String hx_group_id;
    }
}
