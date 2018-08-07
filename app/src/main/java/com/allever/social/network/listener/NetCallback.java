package com.allever.social.network.listener;

import com.allever.social.network.NetResponse;

public interface NetCallback {
    void  onSuccess(NetResponse response);
    void onFail(String msg);
}
