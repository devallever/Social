package com.allever.social.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.allever.social.MyApplication;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.HttpVersion;
//import org.apache.http.NameValuePair;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.CoreProtocolPNames;
//import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/4/18.
 */
public class WebUtil {
    //public static String HTTP_ADDRESS = "http://172.16.98.95:8080/SocialServer";//
    //public static String HTTP_ADDRESS = "http://192.168.56.1:8080/SocialServer";//V-BOX ipv4
    //public static String HTTP_ADDRESS = "http://192.168.23.1:8080/SocialServer";//本机开wifi
    //public static String HTTP_ADDRESS = "http://39.108.9.138:8080/SocialServer";//阿里云

    public static String HTTP_ADDRESS = "http://10.42.0.1:8080/SocialServer";//Linux 本机开wifi

   // public static String HTTP_ADDRESS = "http://192.168.0.106:8080/SocialServer";//Tenda27.54.249.252
   // public static String HTTP_ADDRESS = "http://27.54.249.252:8080/SocialServer";//远程服务器   27.54.249.252
    //public static String HTTP_ADDRESS = "http://192.168.1.100:8080/SocialServer";//宿舍

    //http://27.54.249.252:8080/SocialServer/apk/social_0.16.00.apk
    public static String APK_ADDRESS = "http://a.app.qq.com/o/simple.jsp?pkgname=com.allever.social";//APK下载地址1
    public static String APK_ADDRESS_2 = "http://27.54.249.252:8080/SocialServer" + "/apk/social_0.13.04.apk";//APK下载地址1
    public static String NEWS_TYPE_NEARBY = "0";
    public static String NEWS_TYPE_HOT = "1";
    public static String LONGITUDE = "113.111";
    public static String LATITUDE = "22.1";
    public static String CITY = "广州";




//    public static  String getNearbyNewsList(String url,String longitude, String latitude){
//        HttpClient httpclient = new DefaultHttpClient();
//        //设置HTTP协议版本
//        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//        HttpPost post = new HttpPost(url);//使用POST方法
//
//        List<NameValuePair> ll = new ArrayList<NameValuePair>();
//        ll.add(new BasicNameValuePair("longitude", longitude));
//        ll.add(new BasicNameValuePair("latitude", latitude));
//        //采用“名/值”对的方式对数据进行预处理，并将数据打包在发送给服务端程序的请求数据包中发送到服务端程序；
//        //在这种格式的数据体中，如果设置Content-Type为“application/x-www-form-urlencoded”，
//        //服务端程序会使用简单表单形式对数据进行预处理，进而使得应用程序可以方便的从请求数据中获得相应参数的值。
//        UrlEncodedFormEntity uefe = null;
//        try {
//            uefe = new UrlEncodedFormEntity(ll, "utf-8");
//            uefe.setContentType("application/x-www-form-urlencoded");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //调用HttpPost.setEntity方法将数据封装到HttpPost中；
//        post.setEntity(uefe);
//
//        HttpResponse response = null;
//
//        try {
//            //调用HttpClient.execute方法执行请求，并获得该方法返回的HttpResponse对象；
//            response = httpclient.execute(post);
//
//        } catch (ClientProtocolException e1) {
//            e1.printStackTrace();
//            return null;
//        } catch (IOException e1) {
//            e1.printStackTrace();
//            return null;
//        }
//        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
//            return null;
//
//        HttpEntity entity = response.getEntity();
//        String result = null;
//        try {
//            result = EntityUtils.toString(entity);
//            System.out.println(result);
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.out.println(result);
//        return result;
//    }
//
//    private boolean checkNetworkState() {
//        ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(MyApplication.CONNECTIVITY_SERVICE);
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if ((ni == null) || (ni.isConnected() == false)) {
//            return false;
//        }
//        return true;
//    }

}
