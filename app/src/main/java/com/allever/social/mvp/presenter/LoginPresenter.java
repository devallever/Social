package com.allever.social.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.allever.social.activity.SetAccountActivity;
import com.allever.social.bean.QQ;
import com.allever.social.bean.Response;
import com.allever.social.bean.User;
import com.allever.social.mvp.base.BasePresenter;
import com.allever.social.mvp.view.ILoginView;
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.utils.Constants;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.allever.social.ui.activity.LoginActivity.REQUEST_CODE_SET_ACCOUNT;

public class LoginPresenter extends BasePresenter<ILoginView> {

    private static final String TAG = "LoginPresenter";

    private String mOpenId;

    private NetService mNetService;

    public LoginPresenter(){

        mNetService = new OkHttpService();

    }

    public void loginWithQQ(Activity activity, IUiListener iUiListener, Tencent tencent){
        tencent.login(activity, "all", iUiListener);
    }

    public void login(final Context context,
                      String username,
                      final String pwd){
        mNetService.login(username, pwd, new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) {
                Log.d(TAG, "onSuccess: ");
                String result = response.getString();
                if (result != null){
                    handleLogin(context, result, pwd);
                    mViewRef.get().loginSuccess();
                }
            }

            @Override
            public void onFail(String msg) {
                mViewRef.get().showTipsDialog(msg);
            }
        });

    }

    private void handleLogin(Context context, String result, String pwd) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Response<User>>() {}.getType();
        Response<User> root = gson.fromJson(result,type);

        if (root == null){
            mViewRef.get().showErrorMessageToast(Constants.MSG_SERVER_ERROR);
            return;
        }

        if (!root.isSuccess()){
            mViewRef.get().showTipsDialog(Constants.MSG_ERROR);
            return ;
        }

        //登录成功后为每个用户设置别名：username
        User user = root.getData();
        JPushInterface.setAlias(context, root.getData().getUsername(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("JPush", i + "");
            }
        });

        //登录app服务器成功后登录环信服务器
        EMClient.getInstance().login(user.getUsername(), pwd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("LoninFragment", "登陆聊天服务器成功！");
                    }
                }).start();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("LoninFragment", "登陆聊天服务器失败！");
            }
        });

        SharedPreferenceUtil.setUserId(user.getId());
        SharedPreferenceUtil.setUsername(user.getUsername());
        SharedPreferenceUtil.setNickname(user.getNickname());
        SharedPreferenceUtil.setPassword(pwd);
        SharedPreferenceUtil.setHeadpath(user.getUser_head_path());
        SharedPreferenceUtil.setSignature(user.getSignature());
        SharedPreferenceUtil.setState("1");
        SharedPreferenceUtil.setSessionId(root.getSession_id());
        SharedPreferenceUtil.setCity(user.getCity());
        SharedPreferenceUtil.setSex(user.getSex());
        SharedPreferenceUtil.setPhone(user.getPhone());
        SharedPreferenceUtil.setEmail(user.getEmail());
        SharedPreferenceUtil.setAge(user.getAge());
        SharedPreferenceUtil.setOccupation(user.getOccupation());
        SharedPreferenceUtil.setConstellation(user.getConstellation());
        SharedPreferenceUtil.setHihgt(user.getHight());
        SharedPreferenceUtil.setWeight(user.getWeight());
        SharedPreferenceUtil.setFigure(user.getFigure());
        SharedPreferenceUtil.setEmotion(user.getEmotion());
        SharedPreferenceUtil.setVip(user.getIs_vip() + "");
        SharedPreferenceUtil.setRecommend(user.getIs_recommended() + "");
        SharedPreferenceUtil.setAutoReaction(user.getAutoreaction());
        SharedPreferenceUtil.setOnlineState(user.getOnlinestate());
        SharedPreferenceUtil.setOpenid(user.getQq_open_id());
    }

    public void checkQQOpenId(final Activity activity,
                              final String openid,
                              final String access_token,
                              final String expires) {
        OkhttpUtil.getIns().checkQQOpenId(openid, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Type type = new TypeToken<Response<QQ>>() {}.getType();
                Response<QQ> root = gson.fromJson(result,type);


                if (root == null){
                    mViewRef.get().showErrorMessageToast("服务器繁忙，请重试");
                    return;
                }

                if (!root.isSuccess()){
                    mViewRef.get().showTipsDialog(root.getMessage());
                    return ;
                }

                if (root.getData().getExist() == 0){
                    //无记录
                    // 注册操作
                    // 获取QQ用户信息 昵称 地址 头像url
                    //设置互信号
                    Intent intent = new Intent(activity,SetAccountActivity.class);
                    intent.putExtra("openid",openid);
                    intent.putExtra("access_token",access_token);
                    intent.putExtra("expires",expires);
                    activity.startActivityForResult(intent,REQUEST_CODE_SET_ACCOUNT);

                }else{
                    //有记录 登录操作
                    loginWithQQopenid(activity);
                }
            }
        });
    }

    public void loginWithQQopenid(final Activity activity){
        OkhttpUtil.getIns().loginWithQQopenid(mOpenId, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                //NOT UI Thread
                String result = response.body().string();
                handleLogin(activity, result, Constants.HX_DEFAULT_PASSWORD);
                mViewRef.get().loginSuccess();
            }
        });
    }

    public void handleLoginQQSuccess(Object response, Tencent tencent, Activity activity){
        System.out.println(response);
        //看服务器是否有该
        try {
            mOpenId = ((JSONObject)response).getString("openid");
            String access_token = ((JSONObject)response).getString("access_token");
            String expires = ((JSONObject)response).getString("expires_in");
            Log.d("QQLogin","openid = " + mOpenId);
            Log.d("QQLogin", "access_token = " + access_token);
            Log.d("QQLogin", "expires_in = " + expires);
            tencent.setOpenId(mOpenId);
            tencent.setAccessToken(access_token, expires);

            SharedPreferenceUtil.setOpenid(mOpenId);

            checkQQOpenId(activity, mOpenId, access_token, expires);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
