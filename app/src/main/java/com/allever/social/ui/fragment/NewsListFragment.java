package com.allever.social.ui.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.AddCommentDialogActivity;
import com.allever.social.activity.NewsDetailActivity;
import com.allever.social.foundModule.adapter.NewsListBaseAdapter;
import com.allever.social.foundModule.bean.NewsBeen;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.listener.RecyclerViewScrollListener;
import com.allever.social.mvp.base.BaseMVPFragment;
import com.allever.social.mvp.presenter.NewsListPresenter;
import com.allever.social.mvp.view.INewsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allever on 2016/12/3.
 */

public class NewsListFragment extends BaseMVPFragment<INewsListView, NewsListPresenter> implements
        INewsListView {

    private final static int REQUEST_CODE_UPDATE = 1000;
    private final static int REQUEST_CODE_UPDATE_COMMENT_COUNT = 1001;

    private ProgressDialog mProgressDialog;

    private List<NewsBeen> mNewsBeenList = new ArrayList<>();
    private NewsListBaseAdapter mNewsListBaseAdapter;
    private RecyclerView mRv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewScrollListener mRecyclerViewScrollListener;

    private View mRootView;

    private int selected_position;//点赞记录所选位置
    private MyReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);

        mRootView = inflater.inflate(R.layout.news_list_fragment_layout,container,false);

        initView();

        mPresenter.createNewsImageDir();

        initBroadcast();


        mPresenter.getNewsList();

        return mRootView;
    }

    private void initBroadcast() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.social.refresh_nearby_news");
        intentFilter.addAction("com.allever.action_update_like_news");
        intentFilter.addAction("com.allever.action_update_comment_news");
        mReceiver = new MyReceiver();
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void initView() {
        mProgressDialog = new ProgressDialog(getActivity());

        mRv = (RecyclerView)mRootView.findViewById(R.id.id_news_list_fg_recycler_view);
        mNewsListBaseAdapter = new NewsListBaseAdapter(getActivity(), mNewsBeenList);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRv.setAdapter(mNewsListBaseAdapter);

        mRv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRv,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("news_id", mNewsBeenList.get(position).getId());
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
                switch (view.getId()){
                    case R.id.id_news_item_tv_content:
                        Toast.makeText(getActivity(), mNewsBeenList.get(position).getContent(),Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        mSwipeRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.id_news_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        mRecyclerViewScrollListener = new RecyclerViewScrollListener(new RecyclerViewScrollListener.OnRecycleRefreshListener() {
            @Override
            public void refresh() {
                mPresenter.refreshUserList();
            }

            @Override
            public void loadMore() {
                showLoadingProgressDialog("正在加载...");
                mPresenter.getMoreUserList();
            }
        });

        mRv.addOnScrollListener(mRecyclerViewScrollListener);
        mSwipeRefreshLayout.setOnRefreshListener(mRecyclerViewScrollListener);
    }

    @Override
    protected void initData() { }

    @Override
    protected NewsListPresenter createPresenter() {
        return new NewsListPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_UPDATE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (data.getStringExtra("result_type").equals("like")) {
                        int position = data.getIntExtra("position", 0);
                        int like_count = data.getIntExtra("like_count", 0);
                        boolean islike = data.getBooleanExtra("islike", false);
                        mNewsBeenList.get(position).setLickCount(like_count);
                        if (islike) mNewsBeenList.get(position).setIsLiked(1);
                        else mNewsBeenList.get(position).setIsLiked(0);
                        mNewsListBaseAdapter.notifyDataSetChanged();
                    }
                    //可有可无
                    if (data.getStringExtra("result_type").equals("comment")) {
                        int position = data.getIntExtra("position", 0);
                        int comment_count = data.getIntExtra("comment_count", 0);
                        mNewsBeenList.get(position).setCommentCount(comment_count + "");
                        mNewsListBaseAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void showLoadingProgressDialog(String msg) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        }
    }

    @Override
    public void hideLoadingProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void handleNewsList(List<NewsBeen> mNewsList) {
        mNewsBeenList.clear();
        mNewsBeenList.addAll(mNewsList);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                mRecyclerViewScrollListener.setLoadDataStatus(false);
                mNewsListBaseAdapter.notifyDataSetChanged();
            }
        });
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "com.allever.social.refresh_nearby_news":
//                    //Toast.makeText(getActivity(),"s收到刷新动态广播",Toast.LENGTH_LONG).show();
//                    mPageCount = 1;
//                    getNewsList();
                    break;
                case "com.allever.action_update_like_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("nearby_news")){
                            selected_position = intent.getIntExtra("position", 0);
                            mPresenter.likeNews(mNewsBeenList.get(selected_position).getId());
                        }
                    }
                    break;
                case "com.allever.action_update_comment_news":
                    if (intent.getStringExtra("news_from")!=null){
                        if (intent.getStringExtra("news_from").equals("nearby_news")){
                            selected_position = intent.getIntExtra("position", 0);
                            Intent intent_activity = new Intent(getActivity(),AddCommentDialogActivity.class);
                            intent_activity.putExtra("news_id", mNewsBeenList.get(selected_position).getId());
                            intent_activity.putExtra("position", selected_position);
                            startActivityForResult(intent_activity, REQUEST_CODE_UPDATE_COMMENT_COUNT);
                        }
                    }
                    break;
            }
        }
    }

}
