package com.allever.social.mvp.presenter;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.allever.social.bean.News;
import com.allever.social.bean.Response;
import com.allever.social.foundModule.bean.NewsBeen;
import com.allever.social.mvp.base.BasePresenter;
import com.allever.social.mvp.view.INewsListView;
import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.impl.OkHttpService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewsListPresenter extends BasePresenter<INewsListView> {

    private static final String TAG = "NewsListPresenter";

    private int mPageCount = 1;

    private List<NewsBeen> mNewsList = new ArrayList<>();

    private NetService mNetService;

    public NewsListPresenter(){
        mNetService = new OkHttpService();
    }

    public void getNewsList(){
        mNetService.getNewsList(mPageCount+"", new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) {
                String result = response.getString();
                Log.d(TAG, "onSuccess: result = " + result);
                //mViewRef.get().handleUserList(result);
                mViewRef.get().hideLoadingProgressDialog();

                handleNewsList(result);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }


    public void likeNews(String newsId) {
        mNetService.likeNews(newsId, new NetCallback() {
            @Override
            public void onSuccess(NetResponse response) { }
            @Override
            public void onFail(String msg) { }
        });
    }

    private void handleNewsList(String result) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Response<List<News>>>() {}.getType();
        Response<List<com.allever.social.bean.News>> root = gson.fromJson(result,type);

        mViewRef.get().hideLoadingProgressDialog();

        if (root == null){
            return;
        }

        if (root.isSuccess() == false){
            return;
        }

        if (mPageCount == 1){
            mNewsList.clear();
        }

        NewsBeen newsBeen;
        for (News news: root.getData()){
            newsBeen = new NewsBeen();
            newsBeen.setId(news.getId());
            newsBeen.setContent(news.getContent());
            newsBeen.setIsLiked(news.getIsLiked());
            newsBeen.setLickCount(news.getLickcount());
            newsBeen.setNewsimg_list(news.getNews_image_path());
            newsBeen.setUser_head_path(WebUtil.HTTP_ADDRESS + news.getUser_head_path());
            newsBeen.setNickname(news.getNickname());
            newsBeen.setTime(news.getDate());
            mNewsList.add(newsBeen);
            SharedPreferenceUtil.saveUserData(news.getUsername(),news.getNickname(),WebUtil.HTTP_ADDRESS + news.getUser_head_path());
        }

        mViewRef.get().handleNewsList(mNewsList);
    }


    public void createNewsImageDir(){
        String dirPath = Environment.getExternalStorageDirectory() + "/social/news/";
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
    }

    public void getMoreUserList() {
        mPageCount ++;
        getNewsList();
    }

    public void refreshUserList() {
        mPageCount = 1;
        getNewsList();
    }

}
