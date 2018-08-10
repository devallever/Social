package com.allever.social.ui.fragment;

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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Allever on 2016/12/2.
 */

public class UserListFragment extends BaseMVPFragment<IUserListView, UserListPresenter>
        implements IUserListView{

    private static final String TAG = "UserListFragment";

    private ProgressDialog mProgressDialog;

    private RecyclerView mRv;

    private UserListBaseAdapter mUserListBaseAdapter;

    private List<UserBeen> mUserList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerViewScrollListener recyclerViewScrollListener;

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mRootView = inflater.inflate(R.layout.user_list_fragment_layout,container,false);

        initData();

        initView();

        mPresenter.createUserHeadDir();

        showLoadingProgressDialog("正在加载");

        mPresenter.getUserList();

        return mRootView;
    }

    @Override
    protected void initView() {
        mRv = (RecyclerView)mRootView.findViewById(R.id.id_user_list_fg_recycler_view);
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
                //Nothing to to
            }
        }));
        mRv.setAdapter(mUserListBaseAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.id_user_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);

        recyclerViewScrollListener = new RecyclerViewScrollListener(new RecyclerViewScrollListener.OnRecycleRefreshListener() {
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

        mRv.addOnScrollListener(recyclerViewScrollListener);

        mSwipeRefreshLayout.setOnRefreshListener(recyclerViewScrollListener);
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
    public void handleUserList(List<UserBeen> userBeenList) {
        mUserList.clear();
        mUserList.addAll(userBeenList);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerViewScrollListener.setLoadDataStatus(false);
                mUserListBaseAdapter.notifyDataSetChanged();
            }
        });
    }
}
