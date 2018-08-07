package com.allever.social.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.foke.NativeRuntime;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.service.AdvertiseService;
import com.allever.social.service.BDLocationService;
import com.allever.social.ui.activity.FirstActivity;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.Constants;
import com.allever.social.utils.FileUtils;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by XM on 2016/6/1.
 * 欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    private static final int REQUEST_CODE_RED_POCKET_DIALOG = 1000;

    //百度定位
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private Handler handler;

    private ImageView iv_ad;
    private List<AdDetail> list_addetail = new ArrayList<>();

    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = MyApplication.getContext();
        setContentView(R.layout.welcome_activity_layout);

//        //百度移动统计-----------------------------------------------------------------------------
        initMTJ();

        NetService mNetService = new OkHttpService();
        mNetService.autoLogin(new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) {
                Log.d(TAG, "onSuccess: response = " + response.getString());
            }
            @Override
            public void onFail(String msg) { }
        });



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AD_DETAIL:
                        handleADDetail(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DOWNLOAD:
                        //handleDownload(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AD_SETTING:
                        handleADSetting(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGOUT:
                        //handleLogout(msg);
                        break;
                }
            }
        };


        initData();

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(this, SharedPreferenceUtil.getUserName(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

        getAdDetail();

        //百度定位-----------------------------------------------------------------------------
        //locate();
        Intent intentService = new Intent(this, BDLocationService.class);
        startService(intentService);
        //百度定位-----------------------------------------------------------------------------


        getADSetting();

        //第一次启动
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean("first_lanch",true);
        if(b){
            //Toast.makeText(this,"第一次启动",Toast.LENGTH_LONG).show();;
            sharedPreferences.edit().putBoolean("first_lanch", false).commit();
            SharedPreferenceUtil.initADSharepreference();
            SharedPreferenceUtil.setLocation("113.220583", "23.117193", "广州", "广东省广州市");//设置模拟位置，广州
            SharedPreferenceUtil.setADReceiver();

            //设置分享提示
            //获取当前日期，保存到SharePreference
            String date = CommentUtil.getDate();
            SharedPreferenceUtil.setShareRemindRestCount(date, Constants.SHARE_REMIND_SPACE);

            //show First Activity
            FirstActivity.startSelf(this);
            finish();
        }else{
            if (OkhttpUtil.checkLogin()) beginTimeOut();
            else {
                FirstActivity.startSelf(this);
                WelcomeActivity.this.finish();
            }
        }
            String executable = "libhelper.so";
            String aliasfile = "helper";
            String parafind = "/data/data/" + getPackageName() + "/" + aliasfile;
            NativeRuntime.getInstance().RunExecutable(getPackageName(), executable, aliasfile, getPackageName() + "/com.allever.social.foke.FokeService");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeRuntime.getInstance().startService(getPackageName() + "/com.allever.social.foke.FokeService", FileUtils.createRootPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //开启广告定时服务
        Intent intentService_ad = new Intent(this, AdvertiseService.class);
        startService(intentService_ad);

    }

    private void locate(){
        //发布代码
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        //调用前判断网络连接状态
        try{
            mLocationClient.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_RED_POCKET_DIALOG:
                if (resultCode == RESULT_OK){

                    SocialMainActivity.startSelf(WelcomeActivity.this);
                    WelcomeActivity.this.finish();
                }
                break;
        }
    }

    private void beginTimeOut(){
        //计时线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if(flag){
                        SocialMainActivity.startSelf(WelcomeActivity.this);

                        WelcomeActivity.this.finish();
                    }

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }




    //百度移动统计
    private void  initMTJ(){
        StatService.setLogSenderDelayed(10);
        StatService.setSessionTimeOut(30);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        JPushInterface.onPause(this);
    }

    private void initData(){
        iv_ad = (ImageView)this.findViewById(R.id.id_welcome_activity_iv_ad);
        iv_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list_addetail.size()>0) {
                    //Toast.makeText(WelcomeActivity.this,list_addetail.get(0).url,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(WelcomeActivity.this, WebViewActivity.class);
                    intent.putExtra("url",list_addetail.get(0).url);
                    intent.putExtra("type","1");
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                    flag = false;
                }
            }
        });
    }

    private void getAdDetail(){
        OkhttpUtil.getAdDdtail(handler, "1");
    }

    private void handleADDetail(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADDetailRoot  root = gson.fromJson(result, ADDetailRoot.class);


        if (root == null){
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        list_addetail = root.addetail_list;

        if (list_addetail.size()>0) Glide.with(this).load(WebUtil.HTTP_ADDRESS+list_addetail.get(0).ad_path).into(iv_ad);
        else iv_ad.setImageResource(R.mipmap.welcome_ad);
    }


    private void getADSetting(){
        OkhttpUtil.getADSetting(handler);
    }

    private void handleADSetting(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ADSettingRoot  root = gson.fromJson(result, ADSettingRoot.class);

        if (root == null){
            return;
        }

        if (!root.success){
            return;
        }

        int count = SharedPreferenceUtil.getADcount("ad_screen");
        boolean isshow = SharedPreferenceUtil.getADshow("ad_screen");
        if((root.ad_setting.isshow==1) && isshow){
            if(count != 0){
                SharedPreferenceUtil.updateADcount((count-1),"ad_screen");
            }else if(count == 0){
                SharedPreferenceUtil.updateADshow(false,"ad_screen");
            }
        }


    }


    //百度定位-------------------------------------------------------------------------------------
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d("BaiduMapTestActivity", "error code : " + location.getLocType());
            //navigateTo(location);
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            StringBuffer sb_points = new StringBuffer();
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            SharedPreferenceUtil.setLocation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()), "未知", "未知");
            Log.d("SocialMainActivity", sb.toString());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                SharedPreferenceUtil.setLocation(String.valueOf(location.getLongitude()),
                        String.valueOf(location.getLatitude()),
                        String.valueOf(location.getCity())+String.valueOf(location.getDistrict()),location.getAddrStr());

                Log.d("SocialMainActivity", sb.toString());
                //Toast.makeText(SocialMainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                //保存位置
                SharedPreferenceUtil.setLocation(String.valueOf(location.getLongitude()),
                        String.valueOf(location.getLatitude()),
                        String.valueOf(location.getCity())+String.valueOf(location.getDistrict()),location.getAddrStr());
                //Toast.makeText(WelcomeActivity.this, sb.toString(),Toast.LENGTH_LONG).show();
                Log.d("SocialMainActivity", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                //Toast.makeText(WelcomeActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                //Toast.makeText(WelcomeActivity.this, sb.toString(),Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                //Toast.makeText(WelcomeActivity.this, sb.toString(),Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                //Toast.makeText(WelcomeActivity.this, sb.toString(),Toast.LENGTH_LONG).show();
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    sb_points.append(p.getName()+"_");
                }

                SharedPreferenceUtil.setPoints(sb_points.toString());
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            SharedPreferenceUtil.setLocation(String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getCity()) + String.valueOf(location.getDistrict()),
                    location.getAddrStr());

            //OkhttpUtil.pollServive(handler);
            //Toast.makeText(WelcomeActivity.this,"SharedPreferenceUtil.getAddress() = " + SharedPreferenceUtil.getAddress(), Toast.LENGTH_LONG).show();
            Log.d("location", "address = " + SharedPreferenceUtil.getAddress());
            //定位成功------------------------------------------------------------------------------
        }
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
    //百度定位-------------------------------------------------------------------------------------


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

    class ADDetailRoot{
        boolean success;
        String message;
        List<AdDetail> addetail_list;
    }

    class AdDetail{
        String id;
        String ad_path;
        String url;
    }

}
