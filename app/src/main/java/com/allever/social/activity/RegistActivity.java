package com.allever.social.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.bean.Response;
import com.allever.social.utils.CommentUtil;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.rengwuxian.materialedittext.MaterialEditText;


import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;


import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/4/18.
 * 注册界面
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener , PopupMenu.OnMenuItemClickListener{;
    private static final int REGIST = 0;
    private static final int REQUESTCODE_CUTTING = 1;
    private static final int TAKE_PHOTO = 2;

    private static final int REQUEST_CODE_REGIST_COMPLETE = 1000;

    private ButtonFlat btn_regist;

    private CircleImageView iv_head;

    private MaterialEditText et_username;
    private MaterialEditText et_password;
    private MaterialEditText et_ensure_password;
    private MaterialEditText et_recommend_name;
    private MaterialEditText et_age;

    private RadioButton rb_man;
    private RadioButton rb_woman;

    private String str_username;
    private String str_password;
    private String str_ensure_password;
    private String str_user_head_path;
    private String str_recommend_name;
    private String sex;
    private String age;

    private Handler handler;
    private String result;
    private Response root;
    private Gson gson;
    private String img_path;
    private Uri imageUri;
    private String phone;

    private ProgressDialog progressDialog;

    private byte[] head_b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);

        phone = getIntent().getStringExtra("phone");
        if (phone == null) phone = "13800138000";

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_REGIST:
                        handleRegist(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGIN:
                        handleLogin(msg);
                        break;
                }
            }
        };


        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("注册");
        initData();

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
                if (str_user_head_path == null ||str_user_head_path.equals("") || str_user_head_path.length()==0){
                    new Dialog(this,"提示","请选择头像").show();
                    return super.onOptionsItemSelected(item);
                }
                str_username = et_username.getText().toString();
                System.out.println(CommentUtil.isChinese(str_username));
                if (CommentUtil.isChinese(str_username)){
                    new Dialog(this,"Tips","互信号不能含有中文").show();
                    return super.onOptionsItemSelected(item);
                }

                if(!str_username.matches("[a-z0-9_]+")){
                    new Dialog(this,"Tips","账号只能由小写字母或数字或下划线组成").show();
                    return super.onOptionsItemSelected(item);
                }



                str_password = et_password.getText().toString();
                str_ensure_password = et_ensure_password.getText().toString();
                if (!str_password.equals(str_ensure_password)){
                    new Dialog(this,"提示","密码不一致").show();
                    return super.onOptionsItemSelected(item);
                }

                str_recommend_name = et_recommend_name.getText().toString();

                age = et_age.getText().toString();
                if (age.equals("")||age==null) {
                    Toast.makeText(this,"请输入年龄",Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }

                if (rb_man.isChecked()) sex = "男";
                if (rb_woman.isChecked()) sex  = "女";


                showProgressDialog();
                regist(str_username,str_password,str_user_head_path,str_recommend_name);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CommentUtil.REQUEST_CODE_CHOOSE_PIC:
                if (resultCode == RESULT_OK){
                    str_user_head_path = CommentUtil.getImageFilePath(this,data.getData());
                    byte[] mContent = null;
                    ContentResolver resolver = getContentResolver();
                    try {
                        mContent  = CommentUtil.inputStramToByte(resolver.openInputStream(Uri.parse(data.getData().toString())));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //setHead(mContent);
                    startPhotoZoom(data.getData());
                }
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    //str_user_head_path = CommentUtil.getImageFilePath(this,data.getData());
                    setPicToView(data);
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    str_user_head_path = img_path;
                    startPhotoZoom(imageUri);
                }
                break;
            case REQUEST_CODE_REGIST_COMPLETE:
                if (resultCode == RESULT_OK){
                    EventBus.getDefault().post(com.allever.social.utils.Constants.EVENT_FINISH_ACTIVITY);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            iv_head.setImageDrawable(drawable);
            head_b = Bitmap2Bytes(photo);
            //uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }


    private void initData(){
        btn_regist = (ButtonFlat)this.findViewById(R.id.id_regist_btn_regist);
        btn_regist.setOnClickListener(this);
        btn_regist.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        rb_man = (RadioButton)this.findViewById(R.id.id_regist_rb_man);
        rb_woman = (RadioButton)this.findViewById(R.id.id_regist_rb_woman);

        et_age = (MaterialEditText)this.findViewById(R.id.id_regist_et_age);
        et_username = (MaterialEditText)this.findViewById(R.id.id_regist_et_username);
        et_password = (MaterialEditText)this.findViewById(R.id.id_regist_et_password);
        et_ensure_password = (MaterialEditText)this.findViewById(R.id.id_regist_et_ensurepassword);
        et_recommend_name = (MaterialEditText)this.findViewById(R.id.id_regist_et_recommend_name);
        iv_head  = (CircleImageView)this.findViewById(R.id.id_regist_iv_head);
        iv_head.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_regist_btn_regist:

                if (str_user_head_path == null ||str_user_head_path.equals("") || str_user_head_path.length()==0){
                    new Dialog(this,"提示","请选择头像").show();
                    return;
                }

                str_username = et_username.getText().toString();
                System.out.println(CommentUtil.isChinese(str_username));
                if (CommentUtil.isChinese(str_username)){
                    new Dialog(this,"Tips","互信号不能含有中文").show();
                    return;
                }


                if(!str_username.matches("[a-z0-9_]+")){
                    new Dialog(this,"Tips","账号只能由小写字母或数字或下划线组成").show();
                    return;
                }

                str_password = et_password.getText().toString();
                str_ensure_password = et_ensure_password.getText().toString();
                if (!str_password.equals(str_ensure_password)){
                    new Dialog(this,"提示","密码不一致").show();
                    return;
                }

                str_recommend_name = et_recommend_name.getText().toString();

                age = et_age.getText().toString();
                if (age.equals("")||age==null) {
                    Toast.makeText(this,"请输入年龄",Toast.LENGTH_LONG).show();
                    return;
                }

                if (rb_man.isChecked()) sex = "男";
                if (rb_woman.isChecked()) sex  = "女";


                showProgressDialog();
                regist(str_username,str_password,str_user_head_path,str_recommend_name);
                break;
            case R.id.id_regist_iv_head:
                View view1 = new View(this);
                PopupMenu popup = new PopupMenu(this,view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.choose_photo_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(this);
                //CommentUtil.startPicChoiceIntent(this);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.id_choose_photo_menu_choose_pic:
                CommentUtil.startPicChoiceIntent(this);
                break;
            case R.id.id_choose_photo_menu_take_photo:
                File outPutImage = new File(Environment.getExternalStorageDirectory(),new Date().toString() +".jpg");//cun chu pai zhao de zhao pian
                img_path = outPutImage.getPath();
                Log.d("onClick", "in onClick pathImage = " + img_path);
                //take_photo_path = outPutImage.getPath();
                try{
                    if(outPutImage.exists()){
                        outPutImage.delete();
                    }
                    outPutImage.createNewFile();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
                imageUri = Uri.fromFile(outPutImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);


                break;
        }
        return false;
    }

    private void setHead(byte[] b){
        Bitmap bitmap = CommentUtil.byteToBitmap(b, null);
        BitmapDrawable bd= new BitmapDrawable(getResources(), bitmap);
        iv_head.setImageDrawable(bd);
    }

    private void regist(String username, String password,String str_user_head_path,String recommend_name){
        //OkhttpUtil.regist(handler, username, password, str_user_head_path);
        OkhttpUtil.registWithByte(handler, username, password, recommend_name, phone, head_b, age, sex);
    }

    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void handleRegist(Message msg){
        result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Response.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.isSuccess()){
            new Dialog(this,"提示",root.getMessage()).show();
            if(progressDialog.isShowing()){
                //progressDialog.cancel();
                closeProgressDialog();
            }
            return ;
        }

        closeProgressDialog();

        //start RegistComleteDialogActivity
//        Intent intent = new Intent(this,RegistCompleteDialogActivity.class);
//        intent.putExtra("username",root.user.username);
//        startActivityForResult(intent,REQUEST_CODE_REGIST_COMPLETE);

        //自动登录
        login();


        //------------------旧代码0.17.05------------------------------------------------
//        final Dialog dialog = new Dialog(RegistActivity.this,"注册成功","您注册的账号为: " + root.user.username);
//
//        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        dialog.setOnCancelButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // RegistActivity.this.finish();
//            }
//        });
//        dialog.show();
        //------------------旧代码 0.17.05------------------------------------------------
        return;
    }

    private void login(){
        OkhttpUtil.login(handler, str_username, str_password);
    }

    private void handleLogin(Message msg){
        result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            new Dialog(this,"错误",root.message).show();
            return ;
        }

        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("JPush", i + "");
            }
        });

        //登录app服务器成功后登录环信服务器
        EMClient.getInstance().login(root.user.username, str_password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("LoninFragment", "登陆聊天服务器成功！");
                    }
                }).start();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("LoninFragment", "登陆聊天服务器失败！");
            }
        });


        SharedPreferenceUtil.setUserId(root.user.id);
        SharedPreferenceUtil.setUsername(root.user.username);
        SharedPreferenceUtil.setNickname(root.user.nickname);
        SharedPreferenceUtil.setPassword(str_password);
        SharedPreferenceUtil.setHeadpath(root.user.user_head_path);
        SharedPreferenceUtil.setSignature(root.user.signature);
        SharedPreferenceUtil.setState("1");
        SharedPreferenceUtil.setSessionId(root.session_id);
        SharedPreferenceUtil.setCity(root.user.city);
        SharedPreferenceUtil.setSex(root.user.sex);
        SharedPreferenceUtil.setPhone(root.user.phone);
        SharedPreferenceUtil.setEmail(root.user.email);
        SharedPreferenceUtil.setAge(root.user.age);
        SharedPreferenceUtil.setOccupation(root.user.occupation);
        SharedPreferenceUtil.setConstellation(root.user.constellation);
        SharedPreferenceUtil.setHihgt(root.user.hight);
        SharedPreferenceUtil.setWeight(root.user.weight);
        SharedPreferenceUtil.setFigure(root.user.figure);
        SharedPreferenceUtil.setEmotion(root.user.emotion);
        SharedPreferenceUtil.setVip(root.user.is_vip + "");
        SharedPreferenceUtil.setRecommend(root.user.is_recommended + "");
        SharedPreferenceUtil.setAutoReaction(root.user.autoreaction);
        SharedPreferenceUtil.setOnlineState(root.user.onlinestate);
        SharedPreferenceUtil.setOpenid(root.user.qq_open_id);


        Intent intent = new Intent(this,RegistCompleteDialogActivity.class);
        intent.putExtra("username", root.user.username);
        startActivityForResult(intent, REQUEST_CODE_REGIST_COMPLETE);

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

    class Root{
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
    }

    private class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        LoginUser user;
    }

    private class LoginUser{
        String id;
        String username;
        String nickname;
        String imagepath;
        double longitude;
        double latiaude;
        String phone;
        String email;
        String user_head_path;
        String signature;
        String city;
        String sex;
        int age;
        String occupation;
        String constellation;
        String hight;
        String weight;
        String figure;
        String emotion;
        int is_vip;
        int is_recommended;
        String autoreaction;
        String onlinestate;
        String qq_open_id;
    }

}
