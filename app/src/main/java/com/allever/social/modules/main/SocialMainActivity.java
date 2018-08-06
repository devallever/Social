package com.allever.social.modules.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.AfterRegistShareDialogActivity;
import com.allever.social.fragment.RecommendFragment;
import com.allever.social.fragment.FriendFragment;
import com.allever.social.fragment.MainFragment;
import com.allever.social.fragment.MineFragment;
import com.allever.social.service.BDLocationService;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.Constants;
import com.allever.social.utils.FileUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * Created by XM on 2016/5/7.
 */
public class SocialMainActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_nearby;
    private TextView tv_chat;
    //private TextView txt_topbar;
    private TextView tv_hot;
    private TextView tv_mine;
    private FrameLayout ly_content;
    private RecommendFragment recommendFragment;//推荐
   // private SettingFragment settingFragment;
    private MineFragment mineFragment;//我的
    private MainFragment mainFragment;//附近
    private FriendFragment friendFragment;//聊天
    private FragmentManager fManager;

    private String apk_version_name;
    private Bundle saveInstanceState;
    private int position = 0;

    private RelativeLayout rl_msg_count_container;
    private TextView tv_msg_count;
    private int msg_count;

    private MyReceiver myReceiver;
    private IntentFilter intentFilter;


    //百度定位
//    private LocationClient mLocationClient = null;
//    private BDLocationListener myListener = new MyLocationListener();

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Context context = MyApplication.getContext();
//        SDKInitializer.initialize(context);
        //信鸽调试。发布时注释
        XGPushConfig.enableDebug(this, true);
        setContentView(R.layout.social_main_activity_layout);
        getSupportActionBar().hide();
        this.saveInstanceState = savedInstanceState;

//
        Intent intent = new Intent(this, BDLocationService.class);
        startService(intent);

        createSocialDir();


        //百度移动统计-----------------------------------------------------------------------------
        initMTJ();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_CHECK_VERSION:
                        handleVersion(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DOWNLOAD:
                        handleDownload(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        //handleADSetting(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGOUT:
                        handleLogout(msg);
                        break;
                }
            }
        };


        //信鸽推送------------------------------------------------------------------------------
        XGPushManager.registerPush(MyApplication.mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.d("SocialMain","注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.d("SocialMain","注册失败");
            }
        });
        //信鸽推送------------------------------------------------------------------------------



        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.update_msg_count");
        intentFilter.addAction("com.allever.social.receiver_msg");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver,intentFilter);

        initData();

        fManager = getSupportFragmentManager();
        if (saveInstanceState == null ) {//模拟一次点击，既进去后选择第一项
            //Toast.makeText(this,"saveInstanceState = null \nposition = " + position,Toast.LENGTH_LONG).show();
            tv_nearby.performClick();
        } else {
            //hideAllFragment(fManager.beginTransaction());
            position = saveInstanceState.getInt("position");
            //Toast.makeText(this,"saveInstanceState != null \nposition = " + position,Toast.LENGTH_LONG).show();
            switch (position){
                case 0:
                    tv_nearby.performClick();
                    break;
                case 1:
                    tv_chat.performClick();
                    break;
                case 2:
                    tv_hot.performClick();
                    break;
                case 3:
                    tv_mine.performClick();
                    break;
            }
            return;
        }


        //检查更新
        //checkVersion();

    }

    private void createSocialDir(){
        String dirPath = Environment.getExternalStorageDirectory() + "/social/";
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        checkVersion();
        //shareRemind 分享提醒
        shareRemind();


    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void shareRemind(){
        String today = CommentUtil.getDate();
        String date= SharedPreferenceUtil.getShareRemindDate();
        if (today.equals(date)){
            //不操作
        }else{
            int rest_count = SharedPreferenceUtil.getShareRemindCount();
            if (rest_count > 0){
                //不操作
            }else{
                //提示框
                Dialog dialog = new Dialog(this,"分享",Constants.SHARE_REMIND_TITLE);
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(SocialMainActivity.this, ShareDialogActivity.class);
//                        startActivity(intent);
                        Intent intent = new Intent(SocialMainActivity.this, AfterRegistShareDialogActivity.class);
                        intent.putExtra("username",SharedPreferenceUtil.getUserName());
                        startActivity(intent);
                    }
                });
                if (OkhttpUtil.checkLogin()){
                    dialog.show();
                }
                SharedPreferenceUtil.setShareRemindRestCount(today, Constants.SHARE_REMIND_SPACE);
                //Toast.makeText(this,"已重置提醒日期\n"+ "上一次提醒：" + SharedPreferenceUtil.getShareRemindDate() + "\n剩余天数：" + SharedPreferenceUtil.getShareRemindCount(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initData() {
        tv_nearby = (TextView) findViewById(R.id.id_social_main_tv_nearby);
        tv_chat = (TextView) findViewById(R.id.id_social_main_tv_chat);
        tv_hot = (TextView) findViewById(R.id.id_social_main_tv_hot);
        tv_mine = (TextView) findViewById(R.id.id_social_main_tv_mine);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        rl_msg_count_container = (RelativeLayout)findViewById(R.id.id_social_main_rl_msg_count_container);
        tv_msg_count = (TextView)findViewById(R.id.id_social_main_tv_msg_count);
        msg_count = SharedPreferenceUtil.getMsgCount();
        if (msg_count!=0){
            rl_msg_count_container.setVisibility(View.VISIBLE);
            tv_msg_count.setText(msg_count+"");
        }

        tv_nearby.setOnClickListener(this);
        tv_chat.setOnClickListener(this);
        tv_hot.setOnClickListener(this);
        tv_mine.setOnClickListener(this);

    }

    //百度移动统计
    private void  initMTJ(){
        //StatService.setAppKey("");//已在manifest设置
        // StatService.setAppChannel(this,"",false);//已在manifest设置
        //StatService.setOn(this,StatService.EXCEPTION_LOG);//已在manifest设置
        StatService.setLogSenderDelayed(10);
        // StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START,1,false);//已在manifest设置
        StatService.setSessionTimeOut(30);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void checkVersion(){
        PackageInfo packageInfo;
        try{
            packageInfo = getPackageManager().getPackageInfo(this.getPackageName(),0);
            OkhttpUtil.checkVersion(handler, String.valueOf(packageInfo.versionCode));

        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();;
        }

        //OkhttpUtil.checkVersion(handler, );
    }

    private void handleVersion(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        final Root  root = gson.fromJson(result, Root.class);
        if (root==null ||!root.success){
//            new Dialog(this,"Tips",root.message).show();
//            if(root.message.equals("未登录")){
//                if(!SharedPreferenceUtil.getSessionId().equals("")) OkhttpUtil.autoLogin();
//            }
            return ;
        }else{
            final Dialog dialog = new Dialog(this,"Tips","发现新版本"+root.version.version_name+"\n"+root.version.description);
            apk_version_name = root.version.version_name;
            dialog.setCancelable(true);
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SocialMainActivity.this, "Downloading", Toast.LENGTH_LONG).show();
                    FileInputStream fin = null;
                    String filename = Environment.getExternalStorageDirectory().getPath()+"/social"+apk_version_name +".apk";
                    Log.d("Mainactivity", filename);
                    try {
                        fin = new FileInputStream(filename);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (fin == null) {
                        downloadUpdateVersion(root.version.app_path);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(SocialMainActivity.this);
                        builder.setTicker("正在下载");
                        builder.setContentTitle("Social.apk");
                        builder.setContentText("正在下载");
                        builder.setSmallIcon(R.mipmap.logo);
                        //builder.setContentInfo("This is content info");
                        builder.setAutoCancel(true);

                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(3, builder.build());
                    } else {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(SocialMainActivity.this);
                        builder.setTicker("下载完成");
                        builder.setContentTitle("Social.apk");
                        builder.setContentText("下载完成");
                        builder.setSmallIcon(R.mipmap.logo);
                        //builder.setContentInfo("This is content info");
                        builder.setAutoCancel(true);
                        Intent intent =  FileUtil.getFileIntent(new File(filename));
                        PendingIntent pendingIntent = PendingIntent.getActivity(SocialMainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(3, builder.build());
                        //打开文件
                        startActivity(intent);
                    }

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //okhttp下载文件
//
//                            //下载管理器方式下载文件
////                            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
////                            String apkUrl = WebUtil.HTTP_ADDRESS + root.version.app_path;
////                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
////                            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath(), "social.apk");
////                            //request.setDestinationInExternalFilesDir(getEx,"social.apk");
////                            request.setTitle("Social");
////                            request.setDescription("正在下载...");
////                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
////                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
////                            //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
////                            request.setMimeType("application/vnd.android");
////                            request.setAllowedOverRoaming(true);
////                            long downloadId = downloadManager.enqueue(request);
//                        }
//                    }).start();
                }
            });
            dialog.show();
        }
    }

    private void downloadUpdateVersion(String path){
        OkhttpUtil.downloadUpdateVersion(handler,path);
    }

    private void handleDownload(Message msg){
        byte[] b = (byte[])msg.obj;
        FileOutputStream fos;
        String filePath= "";
        try{
            filePath = Environment.getExternalStorageDirectory().getPath() + "/social" + apk_version_name+ ".apk";
            System.out.println("path = " + filePath);
            fos = new FileOutputStream(filePath);
            fos.write(b);
            fos.close();
        }catch (Exception e){

        }

        //通知栏显示
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("下载完成");
        builder.setContentTitle("Social.apk");
        builder.setContentText("下载完成");
        builder.setSmallIcon(R.mipmap.logo);
        //builder.setContentInfo("This is content info");
        builder.setAutoCancel(true);

        Intent intent =  FileUtil.getFileIntent(new File(filePath));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(3, builder.build());
        //打开文件
        startActivity(intent);

        //打开文件
//        Intent intent1 = FileUtil.getFileIntent(new File(filePath));

    }


    @Override
    public void onClick(View view) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (view.getId()) {
            case R.id.id_social_main_tv_nearby:
                position = 0;
                setSelected();
                tv_nearby.setSelected(true);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    if (saveInstanceState ==null) fTransaction.add(R.id.ly_content, mainFragment,"mainFragment");
                    else {
                        mainFragment = (MainFragment)fManager.findFragmentByTag("mainFragment");
                        if(mainFragment==null){
                            mainFragment = new MainFragment();
                            fTransaction.add(R.id.ly_content, mainFragment,"mainFragment");
                        }else{
                            fTransaction.show(mainFragment);
                        }
                    }
                } else {
                    fTransaction.show(mainFragment);
                }
                break;
            case R.id.id_social_main_tv_chat:
                position =1;
                setSelected();
                tv_chat.setSelected(true);
                if (friendFragment == null) {
                    friendFragment = new FriendFragment();
                    if (saveInstanceState ==null) fTransaction.add(R.id.ly_content, friendFragment,"friendFragment");
                    else {
                        friendFragment = (FriendFragment)fManager.findFragmentByTag("friendFragment");
                        if(friendFragment==null){
                            friendFragment = new FriendFragment();
                            fTransaction.add(R.id.ly_content, friendFragment,"friendFragment");
                        }else{
                            fTransaction.show(friendFragment);
                        }
                    }
                } else {
                    fTransaction.show(friendFragment);
                }
                break;
            case R.id.id_social_main_tv_hot:
                position = 2;
                setSelected();
                tv_hot.setSelected(true);
                if (recommendFragment == null) {
                    recommendFragment = new RecommendFragment();
                    if (saveInstanceState ==null) fTransaction.add(R.id.ly_content, recommendFragment,"recommendFragment");
                    else {
                        recommendFragment = (RecommendFragment)fManager.findFragmentByTag("recommendFragment");
                        if(recommendFragment ==null){
                            recommendFragment = new RecommendFragment();
                            fTransaction.add(R.id.ly_content, recommendFragment,"recommendFragment");
                        }else{
                            fTransaction.show(recommendFragment);
                        }
                    }
                } else {
                    fTransaction.show(recommendFragment);
                }
                break;
            case R.id.id_social_main_tv_mine:
                position = 3;
                setSelected();
                tv_mine.setSelected(true);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    if (saveInstanceState ==null) fTransaction.add(R.id.ly_content, mineFragment,"mineFragment");
                    else {
                        mineFragment = (MineFragment)fManager.findFragmentByTag("mineFragment");
                        if(mineFragment==null){
                            mineFragment = new MineFragment();
                            fTransaction.add(R.id.ly_content, mineFragment,"mineFragment");
                        }else{
                            fTransaction.show(mineFragment);
                        }
                    }
                } else {
                    fTransaction.show(mineFragment);
                }
                break;
        }
        fTransaction.commit();
    }

    //重置所有文本的选中状态
    private void setSelected(){
        tv_nearby.setSelected(false);
        tv_chat.setSelected(false);
        tv_hot.setSelected(false);
        tv_mine.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        //if(mainFragment != null)fragmentTransaction.hide(mainFragment);
        if(mainFragment != null)fragmentTransaction.hide(mainFragment);
        if(friendFragment != null)fragmentTransaction.hide(friendFragment);
        if(recommendFragment != null)fragmentTransaction.hide(recommendFragment);
        if(mineFragment != null)fragmentTransaction.hide(mineFragment);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putInt("position", position);
        //finish();
        //FragmentTransaction fTransaction = fManager.beginTransaction();
        //hideAllFragment(fTransaction);
    }

//    private void initLocation(){
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        mLocationClient.setLocOption(option);
//    }

    //百度定位-------------------------------------------------------------------------------------


    private void handleLogout(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        final Root  root = gson.fromJson(result, Root.class);
        if (!root.success){

        }else{
            logoutIMService();
        }
    }

    private void logoutIMService(){
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("SocialMainActivity", "成功退出环信服务器");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.d("SocialMainActivity", "还没退出环信服务器");
            }
        });
    }

    class Root{
        boolean success;
        int code;
        String message;
        Version version;
    }

    class Version{
        String id;
        int version_code;
        String version_name;
        String description;
        String app_path;
    }

//    class ADSettingRoot{
//        boolean success;
//        String message;
//        ADSetting ad_setting;
//    }
//
//    class ADSetting{
//        String id;
//        int day_space;
//        int count;
//        int isshow;
//    }

    class LogoutRoot{
        public Boolean success;
        public String message;
    }


    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.update_msg_count":
                    msg_count = 0;
                    rl_msg_count_container.setVisibility(View.GONE);
                    break;
                case "com.allever.social.receiver_msg":
                    String msg_type = intent.getStringExtra("msg_type");
                    if (msg_type==null) return;
                    if (msg_type.equals("add_news")){
                        rl_msg_count_container.setVisibility(View.VISIBLE);
                        msg_count++;
                        tv_msg_count.setText(msg_count + "");
                    }

                    break;
            }
        }
    }

    public static void startSelf(Context context){
        Intent intent = new Intent(context, SocialMainActivity.class);
        context.startActivity(intent);
    }
}
