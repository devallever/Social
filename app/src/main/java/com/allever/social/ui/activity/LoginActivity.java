package com.allever.social.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.RegistActivity;
import com.allever.social.activity.ShuaShuaActivity;
import com.allever.social.modules.main.SocialMainActivity;
import com.allever.social.mvp.base.BaseMVPActivity;
import com.allever.social.mvp.presenter.LoginPresenter;
import com.allever.social.mvp.view.ILoginView;
import com.allever.social.service.BDLocationService;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import static com.allever.social.utils.Constants.ACTION_BROADCAST_AFTER_LOGIN;

/**
 * Created by XM on 2016/4/17.
 */
public class LoginActivity extends BaseMVPActivity<ILoginView, LoginPresenter> implements
        View.OnClickListener,
        ILoginView{
    private static final String TAG = "LoginActivity";

    public final static int REQUEST_CODE_SET_ACCOUNT = 1000;
    private final static int REQUEST_CODE_REGIST = 1001;

    private String force_logout;

    private MaterialEditText et_username;
    private MaterialEditText et_password;
    private ButtonFlat btn_login;
    private ButtonFlat btn_forget;
    private ButtonFlat btn_regist;
    private String username;
    private String password;

    private ImageView iv_qq_login;

    private Tencent mTencent;
    private IUiListener loginListener;

    private static final String QQ_APP_ID = "1105431865";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initView();

        initData();

        if (force_logout!=null){
            Dialog dialog = new Dialog(this,"提示","该账号在其他设备登录");
            dialog.show();
        }

        startLocationService();
    }

    @Override
    public void initView(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("登录");

        btn_login = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_login);
        btn_login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_regist = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_regist);
        btn_regist.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_forget = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_forget);
        btn_forget.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        et_password = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_password);
        et_username = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_username);

        iv_qq_login = (ImageView)this.findViewById(R.id.id_login_activity_iv_qq_login);
        iv_qq_login.setOnClickListener(this);

        btn_regist.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void initData() {

        force_logout = getIntent().getStringExtra("force_logout");

        mTencent = Tencent.createInstance(QQ_APP_ID, MyApplication.mContext);

        loginListener = new QQLoginIUiListener();

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        if (sharedPreferences != null){
            password = SharedPreferenceUtil.getPassword();
            username = SharedPreferenceUtil.getUserName();
            et_password.setText(password);
            et_username.setText(username);
        }
    }

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    private void startLocationService(){
        Intent intent = new Intent(this, BDLocationService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_login_activity_btn_regist:
                Intent intent = new Intent(this, RegistActivity.class);
                startActivityForResult(intent,REQUEST_CODE_REGIST);
                break;
            case R.id.id_login_activity_btn_login:
                login();
                break;
            case R.id.id_login_activity_iv_qq_login:
                qqLogin();
                break;
            default:
                break;
        }
    }

    private void qqLogin(){
        mPresenter.loginWithQQ(this,loginListener, mTencent);
    }

    private void login(){
        username = et_username.getText().toString();
        password = et_password.getText().toString();
        mPresenter.login(this, username,password);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("QQLogin", "回调111111");
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        if (requestCode==REQUEST_CODE_SET_ACCOUNT && resultCode==RESULT_OK){
            //登录
            loginWithQQopenid();
        }

        if (requestCode == REQUEST_CODE_REGIST && resultCode ==RESULT_OK){
            Intent intent = new Intent(this,ShuaShuaActivity.class);
            intent.putExtra("is_first",true);
            startActivity(intent);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithQQopenid(){
        mPresenter.loginWithQQopenid(this);
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


    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    private class QQLoginIUiListener implements  IUiListener{
        @Override
        public void onComplete(Object response) {
            Log.d("QQLogin", "QQ登录成功0000");
            password = com.allever.social.utils.Constants.HX_DEFAULT_PASSWORD;
            mPresenter.handleLoginQQSuccess(response, mTencent, LoginActivity.this);
        }

        @Override
        public void onCancel() {
            Log.d("QQLogin","取消登录");
        }

        @Override
        public void onError(UiError uiError) {
            Log.d("QQLogin","QQ登录出错\n" + uiError);
        }
    }

    @Override
    public void showErrorMessageToast(String msg) {
        showToast(msg);
    }

    @Override
    public void showTipsDialog(String msg) {
        new Dialog(this, msg,msg).show();
    }

    @Override
    public void loginSuccess() {
        Log.d(TAG, "loginSuccess: ");

        SocialMainActivity.startSelf(this);

        Intent intent = new Intent(ACTION_BROADCAST_AFTER_LOGIN);
        sendBroadcast(intent);
        setResult(RESULT_OK);
        finish();
    }
}
