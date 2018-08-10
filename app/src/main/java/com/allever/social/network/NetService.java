package com.allever.social.network;


import com.allever.social.network.listener.NetCallback;

public interface NetService {
    void handleStringResponse(String result, NetCallback callback);
    void handleBytesResponse(byte[] bytes, NetCallback callback);

    void login(String username, String password, NetCallback netCallback);
    void autoLogin(NetCallback netCallback);
    void getUserList(String requestPage, NetCallback netCallback);
    void pullRefreshUser(NetCallback netCallback);
    void getNewsList(String requestPage, NetCallback netCallback);
    void likeNews(String newsId, NetCallback netCallback);
}
