package com.allever.social.mvp.view;

import com.allever.social.foundModule.bean.NewsBeen;

import java.util.List;

public interface INewsListView {
    void showLoadingProgressDialog(String msg);
    void hideLoadingProgressDialog();
    void handleNewsList(List<NewsBeen> newsBeenList);
}
