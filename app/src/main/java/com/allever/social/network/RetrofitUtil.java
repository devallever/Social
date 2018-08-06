package com.allever.social.network;



/**
 * Created by allever on 17-6-19.
 */

public class RetrofitUtil {
//    private static final String BASE_URL = "http://192.168.43.235:8080/SocialServer/";
//    private SocialApiService mService;
//    private Retrofit mRetrofit;
//    private RetrofitUtil(){
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(5000, TimeUnit.SECONDS)
//                .build();
//        mRetrofit = new Retrofit.Builder()
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .baseUrl(BASE_URL)
//                .build();
//        mService = mRetrofit.create(SocialApiService.class);
//    }
//    private static class Holder{
//        private static final RetrofitUtil INS = new RetrofitUtil();
//    }
//
//    public static RetrofitUtil getIns(){
//        return Holder.INS;
//    }
//
//
//    public void callLogin(String username,
//                          String password,
//                          String str_longitude,
//                          String str_latitude,
//                          Callback<Response<User>> callback){
//        mService.callLogin(username, password, str_longitude,str_latitude).enqueue(callback);
//    }
//
//    public void login(String username,
//                      String password,
//                      String str_longitude,
//                      String str_latitude,
//                      Observer<Response<User>> observer){
//        mService.login(username,password,str_longitude,str_latitude)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io())
//                .subscribe(observer);
//    }
//
//    public void getUserData(String username,
//                            String str_longitude,
//                            String str_latitude,
//                            Observer<UserBeen> observer){
//        mService.getUserDataWithPost(username,str_longitude, str_latitude)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//    }

}
