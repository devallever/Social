package com.allever.social.network.impl;

import android.util.Log;

import com.allever.social.network.NetResponse;
import com.allever.social.network.NetService;
import com.allever.social.network.listener.NetCallback;
import com.allever.social.network.util.OkhttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkHttpService implements NetService{

    private static final String TAG = "OkHttpService";

    @Override
    public void login(String username, String password, final NetCallback netCallback) {
        OkhttpUtil.getIns().login(username, password, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                netCallback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(),netCallback);
            }
        });
    }

    @Override
    public void autoLogin(final NetCallback netCallback) {
        Log.d(TAG, "autoLogin: ");
        OkhttpUtil.getIns().autoLogin(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                netCallback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(), netCallback);
            }
        });
    }

    @Override
    public void getUserList(String requestPage, final NetCallback netCallback) {
        OkhttpUtil.getIns().getUserList(requestPage, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                netCallback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(), netCallback);
            }
        });
    }

    @Override
    public void pullRefreshUser(final NetCallback netCallback) {
        OkhttpUtil.getIns().pullRefreshUser(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                netCallback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(), netCallback);
            }
        });
    }

    @Override
    public void getNewsList(String requestPage, final NetCallback netCallback) {
        OkhttpUtil.getIns().getNewsList(requestPage, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(), netCallback);
            }
        });
    }

    @Override
    public void likeNews(String newsId, final NetCallback netCallback) {
        OkhttpUtil.getIns().likeNews(newsId, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                netCallback.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                handleStringResponse(response.body().string(), netCallback);
            }
        });
    }

    @Override
    public void handleStringResponse(String result, NetCallback callback) {
        Log.d(TAG, "handleStringResponse: result = " + result);
        NetResponse netResponse = new NetResponse();
        netResponse.setString(result);
        callback.onSuccess(netResponse);
    }

    @Override
    public void handleBytesResponse(byte[] bytes, NetCallback callback) {
        NetResponse netResponse = new NetResponse();
        netResponse.setBytes(bytes);
        callback.onSuccess(netResponse);
    }
}
