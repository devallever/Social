package com.allever.social.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.WithdrawItemArrarAdapter;
import com.allever.social.pojo.WithdrawItem;
import com.allever.social.network.util.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/13.
 * 提现记录
 */
public class WithdrawLogActivity extends BaseActivity {
    private ListView listView;
    private WithdrawItemArrarAdapter withdrawItemArrarAdapter;
    private List<WithdrawItem> list_withdrawItem;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_log_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_WITHDRAW_LOG:
                        handleGetWithdrawLog(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("提现记录");

        initData();

        getWithdrawlog();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }

    private void initData(){
        listView = (ListView)this.findViewById(R.id.id_withdraw_log_activity_listview);
        list_withdrawItem = new ArrayList<>();
    }

    private void getWithdrawlog(){
        OkhttpUtil.getWithdrawlog(handler);
    }

    private void handleGetWithdrawLog(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        if (root.list_withdraw.size()==0) return;
        list_withdrawItem.clear();
        WithdrawItem withdrawItem;
        for (Withdraw withdraw : root.list_withdraw){
            withdrawItem = new WithdrawItem();
            withdrawItem.setDate(withdraw.date);
            withdrawItem.setMoney(withdraw.money);
            withdrawItem.setId(withdraw.id);
            withdrawItem.setState(withdraw.state);
            withdrawItem.setAccount(withdraw.account);
            list_withdrawItem.add(withdrawItem);
        }

        withdrawItemArrarAdapter = new WithdrawItemArrarAdapter(this,R.layout.withdraw_item,list_withdrawItem);
        listView.setAdapter(withdrawItemArrarAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class Root{
        boolean success;
        String message;
        List<Withdraw> list_withdraw;
    }
    class Withdraw{
        String id;
        String money;
        String date;
        String state;
        String account;
    }
}
