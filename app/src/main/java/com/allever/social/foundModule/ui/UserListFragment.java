package com.allever.social.foundModule.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.foundModule.adapter.UserListBaseAdapter;
import com.allever.social.foundModule.bean.UserBeen;
import com.allever.social.listener.RecyclerViewScrollListener;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.mvp.base.BaseMVPFragment;
import com.allever.social.mvp.presenter.UserListPresenter;
import com.allever.social.mvp.view.IUserListView;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Allever on 2016/12/2.
 */

public class UserListFragment extends BaseMVPFragment<IUserListView, UserListPresenter>
        implements RecyclerViewScrollListener.OnRecycleRefreshListener , IUserListView{

    private static final String TAG = "UserListFragment";

    private ProgressDialog mProgressDialog;

    private RecyclerView mRv;
    private UserListBaseAdapter mUserListBaseAdapter;

    private List<UserBeen> mUserList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerViewScrollListener recyclerViewScrollListener;

    private int mRequestPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.user_list_fragment_layout,container,false);

        initData();

        initView(view);

        mPresenter.createUserHeadDir();

        showLoadingProgressDialog("正在加载");

        mPresenter.getUserList(mRequestPage);

        return view;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mUserList = new ArrayList<>();
        mUserListBaseAdapter = new UserListBaseAdapter(getActivity(), mUserList);
        mProgressDialog = new ProgressDialog(getActivity());
    }

    @Override
    protected UserListPresenter createPresenter() {
        return new UserListPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView(View view){
        mRv = (RecyclerView)view.findViewById(R.id.id_user_list_fg_recycler_view);
        mRv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRv,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
                intent.putExtra("username", mUserList.get(position).getUsername());
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
        mRv.setAdapter(mUserListBaseAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_user_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        recyclerViewScrollListener = new RecyclerViewScrollListener(this);

        mRv.addOnScrollListener(recyclerViewScrollListener);

        mSwipeRefreshLayout.setOnRefreshListener(recyclerViewScrollListener);

    }

    private void pullRefreshUser(){
        mPresenter.pullRefreshUser();
    }

    @Override
    public void loadMore() {
        showLoadingProgressDialog("正在加载...");
        mRequestPage++;
        mPresenter.getUserList(mRequestPage);
    }

    @Override
    public void refresh() {
        mRequestPage = 1;
        mPresenter.getUserList(mRequestPage);
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
        if (mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void handleUserList(List<UserBeen> userBeens) {
        if (mRequestPage == 1){
            mUserList.clear();
        }

        mUserList.addAll(userBeens);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

                recyclerViewScrollListener.setLoadDataStatus(false);
                mUserListBaseAdapter.notifyDataSetChanged();

                if (SharedPreferenceUtil.getRefreshUserRefreshingState()==0){
                    //已超过一分钟 向其他用户推送
                    if (OkhttpUtil.checkLogin()){
                        pullRefreshUser();
                    }
                }

            }
        });
    }
}
