package com.allever.social.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/13.
 */
public class AddGroupActivity extends BaseActivity implements View.OnClickListener{
    private static final  int REQUESTCODE_CHOOSE_POINT = 2;
    private CircleImageView iv_group_img;
    private byte[] group_img_b;
    private static final int REQUESTCODE_CUTTING = 1;
    private String str_group_img_head_path;

    private MaterialEditText et_groupname;
    private MaterialEditText et_group_description;
    private TextView tv_choose_point;
    private ButtonRectangle btn_submit;
    private String groupname;
    private String description;
    private String point;

    private Handler handler;
    private String hx_group_id;

    private ProgressDialog progressDialog;

    private int group_type;
    private RadioButton rb_private_1;
    private RadioButton rb_private_2;
    private RadioButton rb_public_1;
    private RadioButton rb_public_2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_GROUP:
                        handleAddGroup(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新建群组");

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CommentUtil.REQUEST_CODE_CHOOSE_PIC:
                if (resultCode == RESULT_OK){
                    str_group_img_head_path = CommentUtil.getImageFilePath(this,data.getData());
                    byte[] mContent = null;
                    ContentResolver resolver = getContentResolver();
                    try {
                        mContent  = CommentUtil.inputStramToByte(resolver.openInputStream(Uri.parse(data.getData().toString())));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    startPhotoZoom(data.getData());
                }
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case REQUESTCODE_CHOOSE_POINT:
                tv_choose_point.setText(data.getStringExtra("point"));
                point = data.getStringExtra("point");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData(){
        iv_group_img = (CircleImageView)this.findViewById(R.id.id_add_group_iv_group_img);
        iv_group_img.setOnClickListener(this);
        et_group_description = (MaterialEditText)this.findViewById(R.id.id_add_group_et_description);
        et_groupname = (MaterialEditText)this.findViewById(R.id.id_add_group_et_groupname);
        tv_choose_point = (TextView)this.findViewById(R.id.id_add_group_tv_choose_point);
        btn_submit = (ButtonRectangle)this.findViewById(R.id.id_add_group_btn_add);
        btn_submit.setOnClickListener(this);
        tv_choose_point.setOnClickListener(this);

        rb_private_1 = (RadioButton)this.findViewById(R.id.id_add_group_rb_private_1);
        rb_private_2 = (RadioButton)this.findViewById(R.id.id_add_group_rb_private_2);
        rb_public_1 = (RadioButton)this.findViewById(R.id.id_add_group_rb_public_1);
        rb_public_2 = (RadioButton)this.findViewById(R.id.id_add_group_rb_public_2);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_add_group_iv_group_img:
                CommentUtil.startPicChoiceIntent(this);
                break;
            case R.id.id_add_group_btn_add:
                groupname = et_groupname.getText().toString();
                if (groupname.equals("")) {new Dialog(this,"Tips","请输入群名称").show(); return;}
                description = et_group_description.getText().toString();
                if (description.equals("")) {new Dialog(this,"Tips","请输入群介绍").show(); return; }
                point = tv_choose_point.getText().toString();
                if (point.equals("请选择")) {new Dialog(this,"Tips","请选择群组地点").show(); return;}
                if (rb_private_1.isChecked()){
                    group_type = 1;
                }else if (rb_private_2.isChecked()){
                    group_type = 2;
                }else if (rb_public_1.isChecked()){
                    group_type = 3;
                }else if (rb_public_2.isChecked()){
                    group_type = 4;
                }

                showProgressDialog();

                try {
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 200;
                    switch (group_type){
                        case 1:
                            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;//私有群，群成员也能邀请人进群；
                            break;
                        case 2:
                            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;//私有群，只能群主邀请人进群；
                            break;
                        case 3:
                            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;//公开群，加入此群除了群主邀请，只能通过申请加入此群；；
                            break;
                        case 4:
                            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin ;//公开群，任何人都能加入此群。；
                            break;
                    }

                    //option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;//私有群，群成员也能邀请人进群；
                    //option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;//私有群，只能群主邀请人进群；
                    //option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;//公开群，加入此群除了群主邀请，只能通过申请加入此群；；
                    //option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin ;//公开群，任何人都能加入此群。；
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(groupname, description, new String[]{SharedPreferenceUtil.getUserName()}, "reason", option);
                    Log.d("AddGroupActivity", "group_id = " + group.getGroupId() + "\n" + "group_name = " + group.getGroupName()
                            + "\n" + "group_desc = " + group.getDescription() + "\n" + "group_owner = " + group.getOwner());
                    hx_group_id = group.getGroupId();
                }catch (HyphenateException e){
                    e.printStackTrace();
                }

               addGroup();
                break;
            case R.id.id_add_group_tv_choose_point:
                Intent intent = new Intent(this,ChoosePointActivity.class);
                startActivityForResult(intent,REQUESTCODE_CHOOSE_POINT);
                break;
        }
    }

    private void addGroup(){
        if (OkhttpUtil.checkLogin()){
            Log.d("AddGroupAcitivty", "groupname  = " + groupname);
            OkhttpUtil.addGroup(handler,groupname,description,point,group_img_b,hx_group_id,group_type+"");
        }else{
            new Dialog(this,"Tips","请先登录").show();
            return;
        }
       //
    }

    private void handleAddGroup(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(this,"提示",root.message).show();
            return ;
        }

        closeProgressDialog();


        Dialog dialog = new Dialog(this,"Tipx","提交成功.");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发广播通知修改界面
                Intent intent = new Intent("com.allever.social.refresh_group_list");
                sendBroadcast(intent);
                AddGroupActivity.this.finish();
            }
        });
        dialog.show();
        //CommentUtil.closeProgressDialog();
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
            iv_group_img.setImageDrawable(drawable);
            group_img_b = Bitmap2Bytes(photo);
        }
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在提交，请稍后");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }

    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
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

    private class Root{
        boolean success;
        String message;
    }


}
