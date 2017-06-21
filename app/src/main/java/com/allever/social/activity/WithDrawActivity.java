package com.allever.social.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by XM on 2016/6/13.
 * 提现界面
 */
public class WithDrawActivity extends BaseActivity {
    private Handler handler;
    private TextView tv_credit;
    private TextView tv_money;

    private EditText et_money;
    private EditText et_account;
    private RippleView rv_withdraw_log;

    private String money;
    private String account;
    private int credit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_CREDIT:
                        handleCredit(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_WITHDRAW:
                        handleAddWithdraw(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("提现");

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

    private void  initData(){
        tv_credit = (TextView)this.findViewById(R.id.id_withdraw_activity_tv_credit);
        tv_money = (TextView)this.findViewById(R.id.id_withdraw_activity_tv_money);

        et_account  = (EditText)this.findViewById(R.id.id_withdraw_activity_et_account);
        et_money = (EditText)this.findViewById(R.id.id_withdraw_activity_et_money);

        rv_withdraw_log = (RippleView)this.findViewById(R.id.id_withdraw_activity_rv_withdraw_log);
        rv_withdraw_log.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(WithDrawActivity.this,WithdrawLogActivity.class);
                startActivity(intent);
            }
        });

    }


    private void getCredit(){
        OkhttpUtil.getCredit(handler);
    }

    private void handleCredit(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        tv_money.setText(root.credit+"");
        tv_credit.setText(root.credit + "");
        credit = root.credit;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_save:
                money = et_money.getText().toString();
                account = et_account.getText().toString();
                if (credit<100){
                    Toast.makeText(this,"信用不足，请充值",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }

                if (money.length()==0) {
                    Toast.makeText(this,"请输入提现金额",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                if (Integer.valueOf(money) < 100) {
                    Toast.makeText(this,"提现金额必须大于100",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                if(account.length()==0){
                    Toast.makeText(this,"请输入提现账号",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }

                addWithdraw();



                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addWithdraw(){
        OkhttpUtil.addWithdraw(handler,money,account);
    }

    private void handleAddWithdraw(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        //Toast.makeText(this,"已提交",Toast.LENGTH_LONG).show();
        new Dialog(this,"提示","已提交,预计1-3个工作日内到账").show();
        et_account.setText("");
        et_money.setText("");
        tv_credit.setText(root.credit+"");
        tv_money.setText(root.credit+"");
        credit = root.credit;
    }


    class Root{
        boolean success;
        String message;
        int credit;
    }
}
