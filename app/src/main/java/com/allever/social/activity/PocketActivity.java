package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.ChargeItemArrayAdapter;
import com.allever.social.pojo.ChargeItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/5.
 * 我的钱包界面
 */
public class PocketActivity extends BaseActivity {
    private ListView listView;
    private ChargeItemArrayAdapter chargeItemArrayAdapter;
    private List<ChargeItem> list_charge;
    private ChargeItem chargeItem;

    private Handler handler;
    private TextView tv_balance;
    private ImageView iv_head;
    private TextView tv_nickname;
    private RippleView rv_withdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pocket_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_CREDIT:
                        handleCredit(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("我的钱包");



        initData();

        getCredit();


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
        listView = (ListView)this.findViewById(R.id.id_pocket_activity_listview);
        list_charge = new ArrayList<>();
        chargeItem = new ChargeItem();
        chargeItem.setCredit("1");
        chargeItem.setMoney("1");
        list_charge.add(chargeItem);
        chargeItem = new ChargeItem();
        chargeItem.setCredit("10");
        chargeItem.setMoney("10");
        list_charge.add(chargeItem);
        chargeItem = new ChargeItem();
        chargeItem.setCredit("30");
        chargeItem.setMoney("30");
        list_charge.add(chargeItem);
        chargeItem = new ChargeItem();
        chargeItem.setCredit("60");
        chargeItem.setMoney("60");
        list_charge.add(chargeItem);
        chargeItem = new ChargeItem();
        chargeItem.setCredit("99");
        chargeItem.setMoney("99");
        list_charge.add(chargeItem);

        chargeItemArrayAdapter = new ChargeItemArrayAdapter(this,R.layout.charge_item,list_charge);
        listView.setAdapter(chargeItemArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PocketActivity.this, PayActivity.class);
                startActivity(intent);
            }
        });

        tv_nickname = (TextView)this.findViewById(R.id.id_pocket_activity_tv_nickname);
        tv_nickname.setText(SharedPreferenceUtil.getNickname());

        tv_balance = (TextView)this.findViewById(R.id.id_pocket_activity_tv_balance);
        iv_head = (ImageView)this.findViewById(R.id.id_pocket_activity_iv_head);
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_head);

        rv_withdraw = (RippleView)this.findViewById(R.id.id_pocket_activity_rv_withdraw);
        rv_withdraw.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(PocketActivity.this, WithDrawActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 获取当前信用值
     * **/
    private void getCredit(){
        OkhttpUtil.getCredit(handler);
    }

    private void handleCredit(Message msg){
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

        tv_balance.setText("当前信用：" + root.credit);
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
        int credit;
    }
}
