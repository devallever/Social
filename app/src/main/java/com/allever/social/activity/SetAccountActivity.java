package com.allever.social.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.FileUtil;
import com.allever.social.utils.ImageUtil;
import com.allever.social.utils.MyConstants;
import com.allever.social.utils.OkhttpUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by XM on 2016/8/13.
 */
public class SetAccountActivity extends BaseActivity {

    private static final int REQUEST_CODE_REGIST_WITH_QQ = 1000;

    private String account;
    private EditText et_account;

    private Handler handler;

    private QQToken qqToken;
    private UserInfo userInfo;
    private Tencent mTencent;
    private String openid;
    private String access_token;
    private String expires;

    private String nickname;
    private String city;
    private String sex;
    private String head_url;
    private String filePath= "";
    private byte[] head_byte;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_account_activity_layout);

        openid = getIntent().getStringExtra("openid");
        access_token = getIntent().getStringExtra("access_token");
        expires = getIntent().getStringExtra("expires");

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_CHECK_USERNAME:
                        handleCheckUsername(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DOWNLOAD_QQ_HEAD:
                        handleDownloadQQhead(msg);
                        break;
                    case OkhttpUtil.MESSAGE_REGIST_WITH_QQ_OPEN_ID:
                        handleRegistWithQQopenid(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置账号");

        et_account = (EditText)this.findViewById(R.id.id_set_account_activity_et_account);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_REGIST_WITH_QQ:
                if (resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
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
                account = et_account.getText().toString();
                if (account.equals("")) {
                    Toast.makeText(this, "请输入账号", Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                if (CommentUtil.isChinese(account)){
                    new Dialog(this,"Tips","互信号不能含有中文").show();
                    return super.onOptionsItemSelected(item);
                }

                if(!account.matches("[a-z0-9_]+")){
                    new Dialog(this,"Tips","账号只能由小写字母或数字或下划线组成").show();
                    return super.onOptionsItemSelected(item);
                }

                showProgressDialog();
                checkAccount();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAccount(){
        OkhttpUtil.checkUsername(handler, account);
    }

    private void handleCheckUsername(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CheckUsernameRoot root = gson.fromJson(result, CheckUsernameRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"错误",root.message).show();
            return ;
        }

        if (root.isExist==0){
            //无记录 可注册
            //获取QQ用户信息
            mTencent = Tencent.createInstance(MyConstants.QQ_APP_ID, MyApplication.mContext);
            mTencent.setOpenId(openid);
            mTencent.setAccessToken(access_token, expires);
            qqToken = mTencent.getQQToken();
            userInfo = new UserInfo(MyApplication.mContext,qqToken);
            userInfo.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object object) {
                    Log.d("QQinfo", "获取成功");
                    JSONObject jsonObject = (JSONObject) object;
                    System.out.println(jsonObject);
                    try {
                        nickname = jsonObject.getString("nickname");
                        Log.d("QQinfo", "nickname = " + nickname);
                        sex = jsonObject.getString("gender");
                        Log.d("QQinfo", "sex = " + sex);
                        city = jsonObject.getString("province") + jsonObject.getString("city");
                        Log.d("QQinfo", "city = " + city);
                        head_url = jsonObject.getString("figureurl_qq_2");
                        Log.d("QQinfo", "head_url = " + head_url);
                        //下载头像到sd卡

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downloadQQhead();
                            }
                        }).start();
                        //OkhttpUtil.downloadQQhead(handler, head_url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });

        }else{
            //有记录 账号不可用 重写
        }

    }

    private void downloadQQhead(){
        //OkhttpUtil.downloadQQhead(handler,head_url);
        OkHttpClient client = new OkHttpClient();
        String url = head_url;
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            head_byte = CommentUtil.Bitmap2Bytes(bm);
            FileOutputStream fos;
            filePath = Environment.getExternalStorageDirectory().getPath() + "/" + openid+".jpg";
            fos = new FileOutputStream(filePath);
            fos.write(head_byte);
            fos.close();
            //imageView.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //注册
        //showProgressDialog();
        registWithQQopenid();


    }

    private void registWithQQopenid(){
        OkhttpUtil.registWithByteWithQQopenid(handler, account,nickname, head_byte, sex, city,openid);
    }

    private void handleRegistWithQQopenid(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        RegistRoot root = gson.fromJson(result, RegistRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            new Dialog(this,"提示",root.message).show();
            return ;
        }
//
//        //注册成功 返回上级菜单 登录
//        //set RESULT
//        closeProgressDialog();
//        setResult(RESULT_OK);
//        finish();

        //注册成功，弹出分享界面
        closeProgressDialog();

        Intent intent = new Intent(this,RegistCompleteDialogActivity.class);
        intent.putExtra("username",root.user.username);
        startActivityForResult(intent,REQUEST_CODE_REGIST_WITH_QQ);

//        Intent intent = new Intent(this, AfterRegistShareDialogActivity.class);
//        intent.putExtra("username", root.user.username);
//        startActivityForResult(intent,REQUEST_CODE_REGIST_WITH_QQ);


    }



    private void handleDownloadQQhead(Message msg){
        byte[] b = (byte[])msg.obj;
        FileOutputStream fos;

        File file = null;
        try {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/" + openid+".jpg";
            file = new File(filePath);
//            if  (!file .exists()  && !file .isDirectory()) {
//                System.out.println("//不存在");
//                file .mkdir();
//            } else{
//                System.out.println("//目录存在");
//            }

            //
            fos = new FileOutputStream(filePath);
            fos.write(b);
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
            Log.d("SetAccount", "filepath = " + filePath);
        }
    }



    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在注册，请稍后");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }

    class CheckUsernameRoot{
        boolean success;
        String message;
        int isExist;
    }


    class RegistRoot{
        boolean seccess;
        String message;
        //String session_id;
        User user;
    }

    class User{
        String id;
        String username;
        String nickname;
        double longitude;
        double latiaude;
        String phone;
        String user_head_path;
        String email;
        String signature;
        String city;
        String sex;
        String occupation;
        String constellation;
        String qq_open_id;
    }


}
