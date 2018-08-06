package com.allever.social.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.Constants;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by XM on 2016/10/2.
 */
public class RedPocketDialogActivity extends BaseActivity {

    private static final int REQUEST_CODE_SHARE_DIALOG_ACTIVITY = 1000;

    private RippleView rv_invite;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_pocket_dialog_activity_layout);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SHARE_DIALOG_ACTIVITY:
                if (resultCode == RESULT_OK){
                    RedPocketDialogActivity.this.setResult(RESULT_OK);
                    RedPocketDialogActivity.this.finish();
                }
                break;
        }
    }

    private void initView(){
        rv_invite = (RippleView)this.findViewById(R.id.id_red_pocket_dialog_activity_rv_invite_friend);
        rv_invite.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(RedPocketDialogActivity.this,ShareDialogActivity.class);
                intent.putExtra("is_first_share",true);
                startActivityForResult(intent,REQUEST_CODE_SHARE_DIALOG_ACTIVITY);
            }
        });
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

        showShare(root.content, root.url, root.img_url);

    }

    //调用shareSDK分享代码
    private void showShare(String content,String url,String img_url) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(Constants.SHARE_TITLE + SharedPreferenceUtil.getUserName());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content + ":" + url);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath();//确保SDcard下面存在此张图片
        //oks.setImageUrl(WebUtil.HTTP_ADDRESS + "/images/logo.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(content);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("sharecallback", "成功" + platform.getName());
                Toast.makeText(RedPocketDialogActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                //addShareRecord();
                RedPocketDialogActivity.this.setResult(RESULT_OK);
                RedPocketDialogActivity.this.finish();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("sharecallback", "错误，失败");
                Toast.makeText(RedPocketDialogActivity.this, "失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("sharecallback", "取消");
                Toast.makeText(RedPocketDialogActivity.this, "取消", Toast.LENGTH_LONG).show();
            }
        });
// 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onBackPressed() {

    }

    class ShareInfoRoot{
        boolean success;
        String message;
        String content;
        String url;
        String img_url;
    }
}
