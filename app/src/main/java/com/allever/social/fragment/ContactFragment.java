package com.allever.social.fragment;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.FriendItemAdapter;
import com.allever.social.pojo.FriendItem;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.sortlistview.CharacterParser;
import com.allever.social.view.sortlistview.PinyinComparator;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/4/16.
 * 联系人界面
 */
public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener , SwipeRefreshLayout.OnRefreshListener{
    private ListView listView;
    private Handler handler;
    private List<FriendItem> list_friend;
    private FriendItemAdapter ad;
    private Root root;
    private Gson gson;
    private Activity activity;
    private ContactFragmentListener contactFragmentListener;

    private AfterUpdateFriendsReceiver afterUpdateFriendsReceiver;
    private IntentFilter intentFilter;

    private FrameLayout.LayoutParams lp_Left_Bottom;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;

    private ADBarFragment adBarFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private CloseADBarReceiver closeADBarReceiver;


    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            contactFragmentListener = (ContactFragmentListener)activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.contact_fragment_layout,container,false);

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //handleFriendList
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_FRIEND_LIST:
                        handleFriendList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        handleADSetting(msg);
                        break;
                }

            }
        };

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_contact_fg_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);


        fragmentManager = this.getChildFragmentManager();

        list_friend = new ArrayList<>();

        listView = (ListView)view.findViewById(R.id.id_contact_fg_listview);
        listView.setOnItemClickListener(this);

        getFriendList();//如果自动登录的就不用先判断是否登录

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.updateFriend");
        afterUpdateFriendsReceiver = new AfterUpdateFriendsReceiver();
        getActivity().registerReceiver(afterUpdateFriendsReceiver,intentFilter);

        intentFilter.addAction("com.allever.social.broadcast_close_ad_bar");
        closeADBarReceiver = new CloseADBarReceiver();
        getActivity().registerReceiver(closeADBarReceiver,intentFilter);

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
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (listView.getFirstVisiblePosition() == 0 && !isloading) {
                   // Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    getFriendList();
                    isloading = false;

                } else {
                    Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                            Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(afterUpdateFriendsReceiver);
        getActivity().unregisterReceiver(closeADBarReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
        intent.putExtra("username", list_friend.get(i).getUsername());
        startActivity(intent);
    }

    private void handleAutoLogin(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot  root = gson.fromJson(result, LoginRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.seccess){
            getFriendList();
        }

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(getActivity(), root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

    }

    private void handleFriendList(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                list_friend.clear();
                ad = new FriendItemAdapter(getActivity(),R.layout.friend_item,list_friend);
                listView.setAdapter(ad);
                return;
            }
            if(root.message.equals("未登录")){
                new Dialog(getActivity(),"Tips","未登录").show();
                return;
            }

        }

        list_friend.clear();
        FriendItem friendItem;
        if(root.friends_list== null || root.friends_list.size()==0){
            //new Dialog(getActivity(),"",root.message).show();
            return ;
        }
        for (Friend friend : root.friends_list){
            friendItem = new FriendItem();
            friendItem.setUser_id(String.valueOf(friend.id));
            friendItem.setSignature(friend.signature);
            friendItem.setNickname(friend.nickname);
            friendItem.setUsername(friend.username);
            friendItem.setUser_head_path(friend.head_path);
            list_friend.add(friendItem);

            SharedPreferenceUtil.saveUserData(friend.username,friend.nickname,WebUtil.HTTP_ADDRESS+ friend.head_path);

        }
        ad = new FriendItemAdapter(getActivity(),R.layout.friend_item,list_friend);
        listView.setAdapter(ad);

    }


    private List<FriendItem> filledData(List<FriendItem> list_friendItem){

        for(int i=0; i<list_friendItem.size(); i++){
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(list_friendItem.get(i).getNickname());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                list_friendItem.get(i).setSortLetters(sortString.toUpperCase());
            }else{
                list_friendItem.get(i).setSortLetters("#");
            }
        }
        return list_friendItem;

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

        int count = SharedPreferenceUtil.getADcount("ad_bar");
        //联网后
        boolean isshow = SharedPreferenceUtil.getADshow("ad_bar");
        if((root.ad_setting.isshow==1) && isshow){
            if(count != 0){

                adBarFragment = new ADBarFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.id_near_by_user_fg_fragment_ad_bar_container,adBarFragment);
                fragmentTransaction.commit();
                SharedPreferenceUtil.updateADcount((count - 1), "ad_bar");
            }else{
                SharedPreferenceUtil.updateADshow(false,"ad_bar");
            }
        }

    }


    private void getFriendList(){
        OkhttpUtil.getFriendList(handler);
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

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }

    class User{
        String id;
        String username;
        String nickname;
        String imagepath;
        double longitude;
        double latiaude;
        String phone;
        String email;
        String user_head_path;
        String signature;
        String city;
        String sex;
    }


    public interface ContactFragmentListener{
        void autoLogin();
    }

    private class AfterUpdateFriendsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            getFriendList();
        }
    }

    class CloseADBarReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(adBarFragment);
            fragmentTransaction.commit();
        }
    }

}
