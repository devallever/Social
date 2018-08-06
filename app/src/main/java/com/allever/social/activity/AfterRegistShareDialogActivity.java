package com.allever.social.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.view.MySquareImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by XM on 2016/10/7.
 */
public class AfterRegistShareDialogActivity extends BaseActivity implements View.OnClickListener{

    private int share_type = 1;

    private String username = "";

    private MySquareImageView iv_qzone;
    private MySquareImageView iv_wechat_moment;
    private MySquareImageView iv_sina;

    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_regist_share_dialog_activity_layout);

        username = getIntent().getStringExtra("username");

        ShareSDK.initSDK(this);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_SHARE_INFO:
                        handleGetShareInfo(msg);
                        break;
                }
            }
        };

        initView();

    }

    private void initView(){
        iv_qzone = (MySquareImageView)this.findViewById(R.id.id_after_regist_share_dialog_activity_iv_qzone);
        iv_wechat_moment = (MySquareImageView)this.findViewById(R.id.id_after_regist_share_dialog_activity_iv_wechat_moment);
        iv_sina = (MySquareImageView)this.findViewById(R.id.id_after_regist_share_dialog_activity_iv_sina);
        iv_qzone.setOnClickListener(this);
        iv_wechat_moment.setOnClickListener(this);
        iv_sina.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_after_regist_share_dialog_activity_iv_qzone:
                share_type = 1;
                getShareInfo();
                break;
            case R.id.id_after_regist_share_dialog_activity_iv_wechat_moment:
                share_type = 2;
                getShareInfo();
                break;
            case R.id.id_after_regist_share_dialog_activity_iv_sina:
                share_type = 3;
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
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        switch (share_type){
            case 1:
                shareQqone(root.content, root.url, root.img_url);
                break;
            case 2:
                shareWechatMoment(root.content, root.url, root.img_url);
                break;
            case 3:
                shareSina(root.content, root.url, root.img_url);
                break;
        }

    }

    private void shareQqone(String content,String url,String img_url){
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle(Constants.SHARE_TITLE + username);
        sp.setTitleUrl(url); // 标题的超链接
        sp.setText(content);
        sp.setImageUrl(img_url);
        sp.setSite(getString(R.string.app_name));
        sp.setSiteUrl(url);

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone. setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                AfterRegistShareDialogActivity.this.setResult(RESULT_OK);
                AfterRegistShareDialogActivity.this.finish();
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qzone.share(sp);
    }

    private void shareWechatMoment(String content,String url,String img_url){
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(Constants.SHARE_TITLE + username);
        sp.setText(content);
        sp.setImageUrl(img_url);
        sp.setUrl(url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wechat_moment = ShareSDK.getPlatform(WechatMoments.NAME);
        wechat_moment.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                AfterRegistShareDialogActivity.this.setResult(RESULT_OK);
                AfterRegistShareDialogActivity.this.finish();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(AfterRegistShareDialogActivity.this, "分享失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(AfterRegistShareDialogActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
        wechat_moment.share(sp);

    }

    private void shareSina(String content,String url,String img_url){
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(content);
        sp.setUrl(url);
        sp.setImageData(((BitmapDrawable) this.getResources().getDrawable(R.mipmap.share_red_pocket)).getBitmap());
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"分享成功",Toast.LENGTH_LONG).show();
                AfterRegistShareDialogActivity.this.setResult(RESULT_OK);
                AfterRegistShareDialogActivity.this.finish();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"分享失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(AfterRegistShareDialogActivity.this,"取消",Toast.LENGTH_LONG).show();
            }
        }); // 设置分享事件回调
// 执行图文分享
        weibo.share(sp);
    }



    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }

}
