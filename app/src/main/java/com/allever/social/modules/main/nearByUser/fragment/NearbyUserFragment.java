package com.allever.social.modules.main.nearByUser.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.foundModule.adapter.UserListBaseAdapter;
import com.allever.social.foundModule.bean.UserBeen;
import com.allever.social.listener.RecyclerItemClickListener;
import com.allever.social.listener.RecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allever on 17-6-19.
 */

public class NearbyUserFragment extends Fragment implements RecyclerViewScrollListener.OnRecycleRefreshListener {

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private UserListBaseAdapter userListBaseAdapter;
    private List<UserBeen> list_users = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewScrollListener recyclerViewScrollListener;

    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_user_fragment_layout_last,container,false);

        //initData();
        initView(view);
        showProgressDialog("正在加载");
        //getUserList();

        return view;
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void refresh() {

    }

    private void initView(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.id_user_list_fg_recycler_view);
        userListBaseAdapter = new UserListBaseAdapter(getActivity(), list_users);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(userListBaseAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),recyclerView,new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
                intent.putExtra("username", list_users.get(position).getUsername());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.id_user_list_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        recyclerViewScrollListener = new RecyclerViewScrollListener(this);

        recyclerView.addOnScrollListener(recyclerViewScrollListener);
        swipeRefreshLayout.setOnRefreshListener(recyclerViewScrollListener);

    }


    private void showProgressDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
