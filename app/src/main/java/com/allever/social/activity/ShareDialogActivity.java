package com.allever.social.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.view.MySquareImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by XM on 2016/8/4.
 */
public class ShareDialogActivity extends BaseActivity implements View.OnClickListener{

    private int share_type = 1;
    private boolean is_first_share = false;

    private MySquareImageView iv_qzone;
    private MySquareImageView iv_wechat_moment;
    private MySquareImageView iv_sina;
    private MySquareImageView iv_qq;
    private MySquareImageView iv_wechat;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_dialog_activity_layout);

        is_first_share = getIntent().getBooleanExtra("is_first_share",false);

        ShareSDK.initSDK(this);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_SHARE_RECORD:
                        handleAddShareRecord(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GIVE_VIP:
                        handleGiveVip(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GET_SHARE_INFO:
                        handleGetShareInfo(msg);
                        break;
                }
            }
        };

        initView();

    }

    private void initView(){
        iv_qzone = (MySquareImageView)this.findViewById(R.id.id_share_dialog_activity_iv_qzone);
        iv_wechat_moment = (MySquareImageView)this.findViewById(R.id.id_share_dialog_activity_iv_wechat_moment);
        iv_sina = (MySquareImageView)this.findViewById(R.id.id_share_dialog_activity_iv_sina);
        iv_qq = (MySquareImageView)this.findViewById(R.id.id_share_dialog_activity_iv_qq);
        iv_wechat = (MySquareImageView)this.findViewById(R.id.id_share_dialog_activity_iv_wechat);
        iv_qzone.setOnClickListener(this);
        iv_wechat_moment.setOnClickListener(this);
        iv_sina.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_wechat.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_share_dialog_activity_iv_qzone:
                share_type = 1;
                getShareInfo();
                break;
            case R.id.id_share_dialog_activity_iv_wechat_moment:
                share_type = 2;
                getShareInfo();
                break;
            case R.id.id_share_dialog_activity_iv_sina:
                share_type = 3;
                getShareInfo();
                break;
            case R.id.id_share_dialog_activity_iv_qq:
                share_type = 4;
                getShareInfo();
                break;
            case R.id.id_share_dialog_activity_iv_wechat:
                share_type = 5;
                getShareInfo();
                break;
        }
    }

    private void getShareInfo(){
        OkhttpUtil.getShareInfo(handler);
    }
    private void handleGetShareInfo(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ShareInfoRoot root = gson.fromJson(result, ShareInfoRoot.class);

        if (root == null){
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        switch (share_type){
            case 1:
                shareQqone(root.content,root.url,root.img_url);
                break;
            case 2:
                shareWechatMoment(root.content,root.url,root.img_url);
                break;
            case 3:
                shareSina(root.content,root.url,root.img_url);
                break;
            case 4:
                shareQQ(root.content,root.url,root.img_url);
                break;
            case 5:
                shareWechat(root.content,root.url,root.img_url);
                break;
        }

    }

    private void shareSina(String content,String url,String img_url){
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(content);
        //sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);
        sp.setUrl(url);
        sp.setImageData(((BitmapDrawable) this.getResources().getDrawable(R.mipmap.share_red_pocket)).getBitmap());
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (is_first_share){
                    ShareDialogActivity.this.setResult(RESULT_OK);
                    ShareDialogActivity.this.finish();
                }else{
                    //分享成功 分享数加1
                    addShareRecord();
                    //开通一个月会员
                    giveVip();
                    Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(ShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(ShareDialogActivity.this,"取消",Toast.LENGTH_LONG).show();
            }
        }); // 设置分享事件回调
// 执行图文分享
        weibo.share(sp);
    }

    private void shareWechat(String content,String url,String img_url){
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setTitle("我的互信号是：" + SharedPreferenceUtil.getUserName());
        sp.setText(content);
        //sp.setImageUrl(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath());
        //sp.setImageData(this.getResources().getDrawable(R.mipmap.logo)).getBitmap());
        //sp.imageData = ((BitmapDrawable)this.getResources().getDrawable(R.mipmap.share_red_pocket)).getBitmap();
        sp.setImageUrl(img_url);
        sp.setUrl(url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (is_first_share){
                    ShareDialogActivity.this.setResult(RESULT_OK);
                    ShareDialogActivity.this.finish();
                }else{
                    //分享成功 分享数加1
                    addShareRecord();
                    //开通一个月会员
                    giveVip();
                    Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(ShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(ShareDialogActivity.this,"取消",Toast.LENGTH_LONG).show();
            }
        });
        wechat.share(sp);

    }

    private void shareWechatMoment(String content,String url,String img_url){
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle("我的互信号是：" + SharedPreferenceUtil.getUserName());
        sp.setText(content);
        //sp.setImageUrl(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath());
        //sp.setImageData(this.getResources().getDrawable(R.mipmap.logo)).getBitmap());
        //sp.imageData = ((BitmapDrawable)this.getResources().getDrawable(R.mipmap.share_red_pocket)).getBitmap();
        sp.setImageUrl(img_url);
        sp.setUrl(url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wechat_moment = ShareSDK.getPlatform(WechatMoments.NAME);
        wechat_moment.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (is_first_share){
                    ShareDialogActivity.this.setResult(RESULT_OK);
                    ShareDialogActivity.this.finish();
                }else{
                    //分享成功 分享数加1
                    addShareRecord();
                    //开通一个月会员
                    giveVip();
                    Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(ShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(ShareDialogActivity.this,"取消",Toast.LENGTH_LONG).show();
            }
        });
        wechat_moment.share(sp);

    }

    private void shareQQ(String content,String url,String img_url){
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setTitle("我的互信号是：" + SharedPreferenceUtil.getUserName());
        sp.setTitleUrl(url); // 标题的超链接
        sp.setText(content);
        sp.setImageUrl(img_url);
        sp.setSite("互信");
        sp.setSiteUrl(url);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq. setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (is_first_share){
                    ShareDialogActivity.this.setResult(RESULT_OK);
                    ShareDialogActivity.this.finish();
                }else{
                    //分享成功 分享数加1
                    addShareRecord();
                    //开通一个月会员
                    giveVip();
                    Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(ShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qq.share(sp);
    }

    private void shareQqone(String content,String url,String img_url){
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle("我的互信号是：" + SharedPreferenceUtil.getUserName());
        sp.setTitleUrl(url); // 标题的超链接
        sp.setText(content);
        sp.setImageUrl(img_url);
        sp.setSite(SharedPreferenceUtil.getUserName());
        sp.setSiteUrl(url);

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone. setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (is_first_share){
                    ShareDialogActivity.this.setResult(RESULT_OK);
                    ShareDialogActivity.this.finish();
                }else{
                    //分享成功 分享数加1
                    addShareRecord();
                    //开通一个月会员
                    giveVip();
                    Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(ShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qzone.share(sp);
    }

    private void giveVip(){
        OkhttpUtil.giveVip(handler);
    }

    private void handleGiveVip(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataDetail", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        GiviVipRoot root = gson.fromJson(result, GiviVipRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        Toast.makeText(ShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
        SharedPreferenceUtil.setVip(root.isVip + "");
        this.setResult(RESULT_OK);
        this.finish();

    }

    private void addShareRecord(){
        OkhttpUtil.addShareRecord(handler);
    }

    private void handleAddShareRecord(Message msg){
        String result = msg.obj.toString();
        Log.d("UserDataDetail", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddShareRecordRoot root = gson.fromJson(result, AddShareRecordRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success==false) return;

        //this.finish();

    }

    class AddShareRecordRoot{
        boolean success;
        String message;
    }

    class GiviVipRoot{
        boolean success;
        String message;
        int isVip;
    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }

}
