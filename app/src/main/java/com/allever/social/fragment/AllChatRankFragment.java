package com.allever.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.adapter.ChatRankItemBaseAdapter;
import com.allever.social.adapter.NearbyUserItemAdapter;
import com.allever.social.pojo.ChatRankItem;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/7/11.
 */
public class AllChatRankFragment extends Fragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2{

    private PullToRefreshListView listView;

    private List<ChatRankItem> list_chat_rank_item;

    private ChatRankItemBaseAdapter chatRankItemBaseAdapter;

    private int page = 1;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_chat_rank_fragment_lalyout, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_CHAT_RANK:
                        handleCharRank(msg);
                        break;
                }
            }
        };

        listView = (PullToRefreshListView)view.findViewById(R.id.id_all_chat_rank_fg_listview);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel(
                getString(R.string.pull_to_load));
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
                getString(R.string.loading));
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel(
                getString(R.string.release_to_load));
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);

        list_chat_rank_item = new ArrayList<>();

        listView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page=1;
        getAllCharRank();
    }

    //上拉
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page ++ ;
        getAllCharRank();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
        intent.putExtra("username", list_chat_rank_item.get(i-1).getUsername());
        startActivity(intent);
    }

    private void getAllCharRank(){
        OkhttpUtil.getChatRank(handler, page + "");
    }

    private void handleCharRank(Message msg){
        String result = msg.obj.toString();
        Log.d("NearbyUserFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            listView.onRefreshComplete();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }


        if (page == 1) list_chat_rank_item.clear();
        ChatRankItem chatRankItem;
        for (UserRank userRank: root.list_userrank){
            chatRankItem = new ChatRankItem();
            chatRankItem.setUsername(userRank.username);
            chatRankItem.setUser_head_path(userRank.user_head_path);
            chatRankItem.setNickname(userRank.nickname);
            chatRankItem.setChatcount(userRank.chatcount);
            list_chat_rank_item.add(chatRankItem);
        }

        if (page==1){
            chatRankItemBaseAdapter = new ChatRankItemBaseAdapter(getActivity(),list_chat_rank_item);
            listView.setAdapter(chatRankItemBaseAdapter);
            listView.onRefreshComplete();
        }else{
            chatRankItemBaseAdapter.notifyDataSetChanged();
            listView.onRefreshComplete();
        }
    }


    class Root{
        boolean success;
        String message;
        List<UserRank> list_userrank;
    }
    class UserRank{
        String username;
        String nickname;
        int rank;
        String user_head_path;
        int chatcount;
    }

}
