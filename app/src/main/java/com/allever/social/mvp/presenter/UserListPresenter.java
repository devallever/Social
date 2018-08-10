package com.allever.social.mvp.presenter;

import android.util.Log;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.bean.Response;
import com.allever.social.bean.User;
import com.allever.social.foundModule.bean.UserBeen;
import com.allever.social.mvp.base.BasePresenter;
import com.allever.social.mvp.view.IUserListView;
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.FileUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserListPresenter extends BasePresenter<IUserListView>{

    private static final String TAG = "UserListPresenter";

    private NetService mNetService;

    private int mRequestPage = 1;

    private List<UserBeen> mUserList = new ArrayList<>();

    public UserListPresenter(){
        mNetService = new OkHttpService();
    }


    public void createUserHeadDir(){
        String dirPath = FileUtil.USER_HEAD_DIR;
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
    }

    public void refreshUserList(){
        mRequestPage = 1;
        getUserList();
    }

    public void getMoreUserList(){
        mRequestPage++;
        getUserList();
    }

    public void getUserList(){
        mNetService.getUserList(mRequestPage + "", new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) {
                String result = response.getString();
                Log.d(TAG, "onSuccess: result = " + result);
                //mViewRef.get().handleUserList(result);
                mViewRef.get().hideLoadingProgressDialog();

                handleUserList(result);
            }

            @Override
            public void onFail(String msg) {
                mViewRef.get().hideLoadingProgressDialog();
            }
        });
    }

    public void pullRefreshUser(){
        mNetService.pullRefreshUser(new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) {
                Log.d(TAG, "onSuccess: ");
                //推送成功,修改SharePreference 为 1
                SharedPreferenceUtil.setRefreshUserRefreshingState(1);
                //新建线程1分钟后修改为 0
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000*60);
                            SharedPreferenceUtil.setRefreshUserRefreshingState(0);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onFail(String msg) {
                Log.d(TAG, "onFail: ");
            }
        });
    }

    private void handleUserList(String result) {
        Log.d(TAG, "handleUserListData: ");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Response<List<User>>>() {}.getType();
        Response<List<com.allever.social.bean.User>> root = gson.fromJson(result,type);

        if (root == null){
            return;
        }

        if (root.isSuccess() == false){
            return;
        }

        if (mRequestPage == 1){
            mUserList.clear();
        }

        UserBeen userBeen;
        for (com.allever.social.bean.User user: root.getData()){
            userBeen = new UserBeen();
            userBeen.setUsername(user.getUsername());
            userBeen.setNickname(user.getNickname());
            userBeen.setHead_path(WebUtil.HTTP_ADDRESS + user.getUser_head_path());
            userBeen.setSex(user.getSex());
            userBeen.setAge(user.getAge());
            userBeen.setIs_accept_video(user.getAccetp_video());
            userBeen.setLogin_time(user.getLogin_time());
            userBeen.setOccupation(user.getOccupation());
            mUserList.add(userBeen);
            SharedPreferenceUtil.saveUserData(user.getUsername(), user.getNickname(), WebUtil.HTTP_ADDRESS + user.getUser_head_path());
        }

        mViewRef.get().handleUserList(mUserList);

        if (SharedPreferenceUtil.getRefreshUserRefreshingState()==0){
            //已超过一分钟 向其他用户推送
            if (OkhttpUtil.checkLogin()){
                pullRefreshUser();
            }
        }
    }
}


