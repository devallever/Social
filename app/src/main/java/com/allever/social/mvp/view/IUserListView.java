package com.allever.social.mvp.view;

import com.allever.social.foundModule.bean.UserBeen;

import java.util.List;

public interface IUserListView {
    void showLoadingProgressDialog(String msg);
    void hideLoadingProgressDialog();
    void handleUserList(List<UserBeen> userBeens);
}
