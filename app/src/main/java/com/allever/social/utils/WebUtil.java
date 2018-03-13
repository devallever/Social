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
    //public static String HTTP_ADDRESS = "http://192.168.23.1:8080/SocialServer";//本机开wifi
    //public static String HTTP_ADDRESS = "http://39.108.9.138:8080/SocialServer";//阿里云

    public static String HTTP_ADDRESS = "http://10.42.0.1:8080/SocialServer";//Linux 本机开wifi
    //public static String HTTP_ADDRESS = "http://192.168.43.235:8080/SocialServer";//Linux 本机开wifi

   // public static String HTTP_ADDRESS = "http://27.54.249.252:8080/SocialServer";//远程服务器   27.54.249.252

    //http://27.54.249.252:8080/SocialServer/apk/social_0.16.00.apk
    public static String APK_ADDRESS = "http://a.app.qq.com/o/simple.jsp?pkgname=com.allever.social";//APK下载地址1
    public static String APK_ADDRESS_2 = "http://27.54.249.252:8080/SocialServer" + "/apk/social_0.13.04.apk";//APK下载地址1
    public static String NEWS_TYPE_NEARBY = "0";
    public static String NEWS_TYPE_HOT = "1";
    public static String LONGITUDE = "113.111";
    public static String LATITUDE = "22.1";
    public static String CITY = "广州";

}
