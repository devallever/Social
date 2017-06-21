package com.allever.social.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * Created by Allever on 2016/11/6.
 */

public class BDLocationService extends Service {

    //百度定位
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        //super.onDestroy();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
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
            Log.d("location", sb.toString());
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

                Log.d("location", sb.toString());
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
                //Toast.makeText(MyApplication.mContext, sb.toString(),Toast.LENGTH_LONG).show();
                Log.d("SocialMainActivity", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                //Toast.makeText(MyApplication.mContext, sb.toString(), Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                //Toast.makeText(MyApplication.mContext, sb.toString(),Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                //Toast.makeText(MyApplication.mContext, sb.toString(),Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                //Toast.makeText(MyApplication.mContext, sb.toString(),Toast.LENGTH_LONG).show();
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
            Log.i("location", sb.toString());
            SharedPreferenceUtil.setLocation(String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getCity()) + String.valueOf(location.getDistrict()),
                    location.getAddrStr());

            //定位成功------------------------------------------------------------------------------
            //OkhttpUtil.pollServive(new Handler());
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
}
