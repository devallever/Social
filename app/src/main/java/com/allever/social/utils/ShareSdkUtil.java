package com.allever.social.utils;

import android.content.Context;

import com.allever.social.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by XM on 2016/7/1.
 */
public  class ShareSdkUtil {
    //调用shareSDK分享代码
    public void showShare(Context context) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("互信-点击下载");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(WebUtil.HTTP_ADDRESS + "/apk/social_0.10.04.apk");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("互信-点击下载\n" + WebUtil.HTTP_ADDRESS + "/apk/social_0.10.04.apk");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath();//确保SDcard下面存在此张图片
        //oks.setImageUrl(WebUtil.HTTP_ADDRESS + "/images/logo.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(WebUtil.HTTP_ADDRESS + "/apk/social_0.10.04.apk");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(WebUtil.HTTP_ADDRESS + "/apk/social_0.10.04.apk");
        oks.setTitleUrl(WebUtil.HTTP_ADDRESS + "/apk/social_0.10.04.apk");
// 启动分享GUI
        oks.show(context);
    }
}
