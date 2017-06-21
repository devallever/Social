package com.allever.social.network;

import java.util.concurrent.TimeUnit;


/**
 * Created by allever on 17-6-19.
 */

public class RetrofitUtil {

/*    private static final String TAG = "RetrofitUtil";

    private Retrofit retrofit;
    private SocialApiService socialApiService;
    private static final String BASE_URL = "http://39.108.9.138:8080/SocialServer/";

    private RetrofitUtil(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        socialApiService = retrofit.create(SocialApiService.class);
    }

    public static RetrofitUtil getInstance(){
        return RetrofitHolder.INSTANCE;
    }
*//*
    public void getBingImageList(Subscriber<BingRoot> subscriber, String page){
        bingApiService.getBingImageList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }*//*


    private static class RetrofitHolder{
        private static final RetrofitUtil INSTANCE = new RetrofitUtil();
    }*/


}
