package com.allever.social.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.RegistActivity;
import com.allever.social.ui.SocialMainActivity;
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

import org.greenrobot.eventbus.EventBus;

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

    private MaterialEditText mEtUsername;
    private MaterialEditText mEtPassword;

    private ButtonFlat mBtnLogin;
    private ButtonFlat mBtnForget;
    private ButtonFlat mBtnRegister;

    private String mUsername;
    private String mPassword;

    private ImageView mIvQqLogin;

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

        mBtnLogin = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_login);
        mBtnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mBtnRegister = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_regist);
        mBtnRegister.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mBtnForget = (ButtonFlat)this.findViewById(R.id.id_login_activity_btn_forget);
        mBtnForget.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mEtPassword = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_password);
        mEtUsername = (MaterialEditText)this.findViewById(R.id.id_login_activity_et_username);

        mIvQqLogin = (ImageView)this.findViewById(R.id.id_login_activity_iv_qq_login);
        mIvQqLogin.setOnClickListener(this);

        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {

        force_logout = getIntent().getStringExtra("force_logout");

        mTencent = Tencent.createInstance(QQ_APP_ID, MyApplication.mContext);

        loginListener = new QQLoginIUiListener();

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        if (sharedPreferences != null){
            mPassword = SharedPreferenceUtil.getPassword();
            mUsername = SharedPreferenceUtil.getUserName();
            mEtPassword.setText(mPassword);
            mEtUsername.setText(mUsername);
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
        mUsername = mEtUsername.getText().toString();
        mPassword = mEtPassword.getText().toString();
        mPresenter.login(this, mUsername, mPassword);
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
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        if (requestCode==REQUEST_CODE_SET_ACCOUNT && resultCode==RESULT_OK){
            //登录
            loginWithQQopenid();
        }

        if (requestCode == REQUEST_CODE_REGIST && resultCode ==RESULT_OK){
            SocialMainActivity.startSelf(this);
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
            mPassword = com.allever.social.utils.Constants.HX_DEFAULT_PASSWORD;
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
    public void showTipsDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void loginSuccess() {
        SocialMainActivity.startSelf(this);

        Intent intent = new Intent(ACTION_BROADCAST_AFTER_LOGIN);
        sendBroadcast(intent);

        EventBus.getDefault().post(com.allever.social.utils.Constants.EVENT_FINISH_ACTIVITY);
        finish();
    }
}
