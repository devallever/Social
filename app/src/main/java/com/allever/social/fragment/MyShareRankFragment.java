package com.allever.social.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.adapter.ChatRankItemBaseAdapter;
import com.allever.social.pojo.ChargeItem;
import com.allever.social.pojo.ChatRankItem;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;
import java.util.WeakHashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/7/11.
 */
public class MyShareRankFragment extends Fragment {

    private TextView tv_sharecount;
    private TextView tv_rank;

    private CircleImageView iv_head;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_share_rank_fragment_lalyout, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_MY_SHARE_RANK:
                        handleMyShareRank(msg);
                        break;
                }
            }
        };

        tv_sharecount = (TextView)view.findViewById(R.id.id_my_share_rank_fg_tv_sharecount);
        tv_rank = (TextView)view.findViewById(R.id.id_my_share_rank_fg_tv_rank);

        iv_head = (CircleImageView)view.findViewById(R.id.id_my_share_rank_fg_iv_head);

        if (OkhttpUtil.checkLogin()){
            getMyShareRank();
        }else{
            Toast.makeText(getActivity(),"未登录",Toast.LENGTH_LONG).show();
        }


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

    private void getMyShareRank(){
        OkhttpUtil.getMyShareRank(handler);
    }

    private void handleMyShareRank(Message msg){
        String result = msg.obj.toString();
        Log.d("MyShareRankFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (root.success == false){
            new Dialog(getActivity(),"错误",root.message).show();
        }

        tv_sharecount.setText(root.userrank.sharecount+"");
        if (root.userrank.sharecount==0){
            tv_rank.setText("未上榜");
        }else{
            tv_rank.setText("第 " + root.userrank.rank + " 名");
        }
        Glide.with(getActivity()).load(WebUtil.HTTP_ADDRESS + root.userrank.user_head_path).into(iv_head);



    }


    class Root{
        boolean success;
        String message;
        UserRank userrank;
    }
    class UserRank{
        String username;
        String nickname;
        int rank;
        String user_head_path;
        int sharecount;
    }

}
