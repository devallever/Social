package com.allever.social.activity;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
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
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

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
 * Created by XM on 2016/4/23.
 * 修改用户信息
 */
public class ModifyUserDataActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    private static final int REQUESTCODE_CUTTING = 1;
    private static final int REQUESTCODE_CHOOSE_OCCUPATION = 2;
    private static final int REQUESTCODE_CHOOSE_CONSTELLATION = 3;
    private static final int REQUESTCODE_CHOOSE_FIGURE = 5;
    private static final int REQUESTCODE_CHOOSE_EMOTION = 6;
    private Toolbar toolbar;
    private Button btn_save;
    private MaterialEditText et_nickname;
    private MaterialEditText et_city;
    private MaterialEditText et_signature;
   // private MaterialEditText et_phone;
    private MaterialEditText et_email;
    private MaterialEditText et_age;
    private MaterialEditText et_occupation;
    private MaterialEditText et_constellation;

    private MaterialEditText et_hight;
    private MaterialEditText et_weight;
    //private MaterialEditText et_figure;
    private TextView tv_figure;
    //private MaterialEditText et_emotion;
    private TextView tv_emotion;

    private RadioButton rb_man;
    private RadioButton rb_woman;

    private RelativeLayout rl_modity_user_head;
    private CircleImageView iv_head;

    private Handler handler;

    private String str_nickname;
    private String str_sex;
    private String str_city;
    private String str_signature;
   // private String str_phone;
    private String str_email;
    private int age;
    private String str_age;
    private String str_occupation;
    private String str_constellation;
    private String str_hight;
    private String str_weight;
    private String str_figure;
    private String str_emotion;

    private byte[] head_b;


    private String img_path;
    private Uri imageUri;

    private static final int TAKE_PHOTO = 4;

    //private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_user_data_layout);

        toolbar = (Toolbar)this.findViewById(R.id.id_modify_user_data_toolbar);
        CommentUtil.initToolbar(this, toolbar, "修改资料");

        initData();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_MODIFY_HEAD:
                        handleModifyHead(msg);
                        break;
                    case OkhttpUtil.MESSAGE_MODIFY_USER_DATA:
                        handleModifyUserData(msg);
                        break;
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;

                }
            }
        };
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CommentUtil.REQUEST_CODE_CHOOSE_PIC:
                if (resultCode == RESULT_OK){
                    String str_user_head_path = CommentUtil.getImageFilePath(this,data.getData());
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
                    //modifyHead(str_user_head_path);
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
            case REQUESTCODE_CHOOSE_OCCUPATION:
                if(resultCode == RESULT_OK){
                    et_occupation.setText(data.getStringExtra("occupation"));
                    str_occupation = data.getStringExtra("occupation");
                }
                break;
            case REQUESTCODE_CHOOSE_CONSTELLATION:
                if(resultCode == RESULT_OK){
                    et_constellation.setText(data.getStringExtra("constellation"));
                    str_constellation = data.getStringExtra("constellation");
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    //str_user_head_path = img_path;
                    startPhotoZoom(imageUri);
                }
                break;
            case REQUESTCODE_CHOOSE_FIGURE:
                if(resultCode == RESULT_OK){
                    tv_figure.setText(data.getStringExtra("figure"));
                    str_figure = data.getStringExtra("figure");
                }
                break;
            case REQUESTCODE_CHOOSE_EMOTION:
                if(resultCode == RESULT_OK){
                    tv_emotion.setText(data.getStringExtra("emotion"));
                    str_emotion = data.getStringExtra("emotion");
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
            modifyHead();
            //uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
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
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_modify_user_data_rl_modify_head:

                PopupMenu popup = new PopupMenu(this,view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.choose_photo_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(this);


                //CommentUtil.startPicChoiceIntent(this);
            break;
            case R.id.id_modify_user_data_btn_save:
                str_city = et_city.getText().toString();
                str_email = et_email.getText().toString();
                str_nickname = et_nickname.getText().toString();
               // str_phone = et_phone.getText().toString();
                str_signature = et_signature.getText().toString();
//                if (SharedPreferenceUtil.getSex().equals("男")) rb_man.setChecked(true);
//                else rb_woman.setChecked(true);
                if (rb_man.isChecked()) str_sex = "男";
                else str_sex = "女";
                str_age = et_age.getText().toString();
                str_occupation = et_occupation.getText().toString();
                str_constellation = et_constellation.getText().toString();
                str_hight = et_hight.getText().toString();
                str_weight = et_weight.getText().toString();
                str_figure = tv_figure.getText().toString();
                str_emotion = tv_emotion.getText().toString();

                modifyUserData();
                break;
            case R.id.id_modify_user_data_et_occupation:
                //Toast.makeText(this, "Occupation", Toast.LENGTH_SHORT).show();
                intent = new Intent(this,ChooseOccupationActivity.class);
                startActivityForResult(intent, REQUESTCODE_CHOOSE_OCCUPATION);
                break;
            case R.id.id_modify_user_data_et_constellation:
                intent = new Intent(this,ChooseConstellationActivity.class);
                startActivityForResult(intent,REQUESTCODE_CHOOSE_CONSTELLATION);
                //Toast.makeText(this, "constellation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_modify_user_data_tv_figure:
                intent = new Intent(this,ChoosFigureActivity.class);
                startActivityForResult(intent,REQUESTCODE_CHOOSE_FIGURE);
                break;
            case R.id.id_modify_user_data_tv_emotion:
                intent = new Intent(this,ChooseEmotionActivity.class);
                startActivityForResult(intent,REQUESTCODE_CHOOSE_EMOTION);
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

    public void initData(){
        btn_save = (ButtonFlat)this.findViewById(R.id.id_modify_user_data_btn_save);
        btn_save.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        et_nickname = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_nickname);
        et_city = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_city);
        et_signature = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_signature);
        //et_phone = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_phone);
        et_email = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_email);

        rb_man = (RadioButton)this.findViewById(R.id.id_modify_user_data_rb_man);
        rb_woman = (RadioButton)this.findViewById(R.id.id_modify_user_data_rb_woman);

        rl_modity_user_head = (RelativeLayout)this.findViewById(R.id.id_modify_user_data_rl_modify_head);
        iv_head = (CircleImageView)this.findViewById(R.id.id_modify_user_data_iv_userhead);

        rl_modity_user_head.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        et_age = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_age);
        et_occupation = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_occupation);
        et_constellation = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_constellation);
        et_hight = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_hight);
        et_weight = (MaterialEditText)this.findViewById(R.id.id_modify_user_data_et_weight);
        tv_figure = (TextView)this.findViewById(R.id.id_modify_user_data_tv_figure);
        tv_emotion = (TextView)this.findViewById(R.id.id_modify_user_data_tv_emotion);

        et_occupation.setOnClickListener(this);
        et_constellation.setOnClickListener(this);
        tv_figure.setOnClickListener(this);
        tv_emotion.setOnClickListener(this);



        setInitData();
    }

    private void setInitData(){
        et_nickname.setText(SharedPreferenceUtil.getNickname());
        et_city.setText(SharedPreferenceUtil.getCity());
        et_signature.setText(SharedPreferenceUtil.getSignature());
       // et_phone.setText(SharedPreferenceUtil.getPhone());
        et_email.setText(SharedPreferenceUtil.getEmail());
        et_age.setText(SharedPreferenceUtil.getAge()+"");
        et_occupation.setText(SharedPreferenceUtil.getOccupation());
        et_constellation.setText(SharedPreferenceUtil.getConstellation());
        et_hight.setText(SharedPreferenceUtil.getHight());
        et_weight.setText(SharedPreferenceUtil.getWeight());
        tv_figure.setText(SharedPreferenceUtil.getFigure());
        tv_emotion.setText(SharedPreferenceUtil.getEmotion());

        if (SharedPreferenceUtil.getSex().equals("男")) {rb_man.setChecked(true); rb_woman.setChecked(false);}
        else if (SharedPreferenceUtil.getSex().equals("女")) {rb_woman.setChecked(true); rb_man.setChecked(false);}
        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_head);
        //Picasso.with(this).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath()).into(iv_head);
    }

    private void setHead(byte[] b){
        Bitmap bitmap = CommentUtil.byteToBitmap(b, null);
        BitmapDrawable bd= new BitmapDrawable(getResources(), bitmap);
        iv_head.setImageDrawable(bd);
    }

    private void handleAutoLogin(Message msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("已自动登录");
        builder.setContentText("请重新操作...");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());

        String result = msg.obj.toString();
        Log.d("Setting", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);
        JPushInterface.setAlias(this, root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

    }


    private void handleModifyUserData(Message msg){
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ModifyUserDataRoot root = gson.fromJson(result, ModifyUserDataRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            if(root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }else{

        }

        SharedPreferenceUtil.setNickname(root.user.nickname);
        SharedPreferenceUtil.setSex(root.user.sex);
        SharedPreferenceUtil.setCity(root.user.city);
        SharedPreferenceUtil.setSignature(root.user.signature);
        SharedPreferenceUtil.setPhone(root.user.phone);
        SharedPreferenceUtil.setEmail(root.user.email);
        SharedPreferenceUtil.setAge(root.user.age);
        SharedPreferenceUtil.setOccupation(root.user.occupation);
        SharedPreferenceUtil.setConstellation(root.user.constellation);
        SharedPreferenceUtil.setHihgt(root.user.hight);
        SharedPreferenceUtil.setWeight(root.user.weight);
        SharedPreferenceUtil.setFigure(root.user.figure);
        SharedPreferenceUtil.setEmotion(root.user.emotion);
        Dialog dialog = new Dialog(this,"Tips","修改成功.");
        dialog.setCancelable(false);
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.show();

        //发广播通知SeetingFragment设置界面更新ui
        Intent intent = new Intent("com.allever.modifyUserData");
        intent.putExtra("nickname", root.user.nickname);
        sendBroadcast(intent);

        //发广播通知MainActivity修改界面
        Intent intent1 = new Intent("com.allever.autologin");
        sendBroadcast(intent1);

    }

    private void modifyUserData(){
        OkhttpUtil.modifyUserData(handler,str_nickname,str_sex,str_city,str_signature,str_email,str_age,str_occupation,str_constellation,str_hight,str_weight,str_figure,str_emotion);
    }

    private void modifyHead(){
        OkhttpUtil.modityHead(handler,head_b);
    }

    private void handleModifyHead(Message msg){
         String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        ModifyHeadRoot root = gson.fromJson(result, ModifyHeadRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.seccess){
            if(root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }else{
            Toast.makeText(this,"修改头像成功.",Toast.LENGTH_LONG).show();

            Intent intent = new Intent("com.allever.modifyUserHead");
            intent.putExtra("head_path", WebUtil.HTTP_ADDRESS + root.head_path);
            sendBroadcast(intent);

            //发广播通知MainActivity修改界面
            Intent intent1 = new Intent("com.allever.autologin");
            sendBroadcast(intent);

            Glide.with(this)
                    .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iv_head);
        }
    }

    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }


    class ModifyHeadRoot{
        boolean seccess;
        String message;
        String head_path;
    }

    class ModifyUserDataRoot{
        boolean seccess;
        String message;
        String session_id;
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
        int age;
        String occupation;
        String constellation;
        String hight;
        String weight;
        String figure;
        String emotion;
    }

}

