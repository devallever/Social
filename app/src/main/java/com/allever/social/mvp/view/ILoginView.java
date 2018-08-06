package com.allever.social.mvp.view;

public interface ILoginView {
    void showErrorMessageToast(String msg);
    void showTipsDialog(String msg);
    void loginSuccess();
}
