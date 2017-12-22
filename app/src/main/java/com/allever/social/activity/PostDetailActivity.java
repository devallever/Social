package com.allever.social.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.gc.flashview.FlashView;
import com.gc.flashview.constants.EffectConstants;
import com.gc.flashview.listener.FlashViewListener;
import com.gc.materialdesign.widgets.Dialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/5/20.
 * 职位详情页面
 */
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private final static int MODIFY_POST = 1;

    private String post_id;
    private Handler handler;

    private TextView tv_postname;
    private TextView tv_companyname;
    private TextView tv_distance;
    private TextView tv_salary;
    private TextView tv_date;
    private TextView tv_location;
    private TextView tv_description;
    private TextView tv_requirement;
    private TextView tv_link;
    private TextView tv_phone;
    private TextView tv_address;

//    private ButtonRectangle btn_dail;
//    private ButtonRectangle btn_chat;
//    private ButtonRectangle btn_add_post;
//    private ButtonRectangle btn_delete_post;
    private FloatingActionButton fab_chat;
    private FloatingActionButton fab_dail;
    private  FloatingActionButton fab_add_post;
    private  FloatingActionButton fab_delete_post;
    private FloatingActionButton fab_modify_post;

    private String phone;
    private String recruit_id;
    private String username;

    private Post post;

    private FlashView flashView;

    private List<String> imageUrls;
    private String[] arr_recruit_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                   case  OkhttpUtil.MESSAGE_POST_DATA:
                       handlePostData(msg);
                       break;
                    case OkhttpUtil.MESSAGE_DELETE_POST:
                        handleDeletePost(msg);
                        break;

                }
            }
        };

        post_id = getIntent().getStringExtra("post_id");

        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("职位详情");

        ininData();

        getPostDetail();
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
            case MODIFY_POST:
                if (resultCode == RESULT_OK){
                    finish();
                }
                break;
        }
    }

    private void ininData(){
        tv_postname = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_postname);
        tv_companyname = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_companyname);
        tv_distance = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_distance);
        tv_salary = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_salary);
        tv_date = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_date);
        tv_description = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_description);
        tv_requirement = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_requirement);
        tv_link = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_link);
        tv_phone = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_phone);
        tv_address = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_address);
        tv_location = (TextView)this.findViewById(R.id.id_post_detail_activity_tv_location);

//        btn_dail = (ButtonRectangle)this.findViewById(R.id.id_post_detail_activity_btn_dail);
//        btn_chat = (ButtonRectangle)this.findViewById(R.id.id_post_detail_activity_btn_chat);
//        btn_add_post = (ButtonRectangle)this.findViewById(R.id.id_post_detail_activity_btn_add_post);
//        btn_delete_post = (ButtonRectangle)this.findViewById(R.id.id_post_detail_activity_btn_delete_post);
        fab_chat = (FloatingActionButton)this.findViewById(R.id.id_post_detail_activity_fab_chat);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_chat.setColorNormalResId(R.color.colorGreen_300);
        fab_chat.setColorPressedResId(R.color.colorGreen700);
        fab_chat.setIcon(R.mipmap.ic_sms_white_24dp);
        fab_chat.setStrokeVisible(false);
        fab_chat.setOnClickListener(this);

        fab_dail = (FloatingActionButton)this.findViewById(R.id.id_post_detail_activity_fab_dail);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_dail.setColorNormalResId(R.color.colorGreen_300);
        fab_dail.setColorPressedResId(R.color.colorGreen700);
        fab_dail.setIcon(R.mipmap.ic_call_white_24dp);
        fab_dail.setStrokeVisible(false);
        fab_dail.setOnClickListener(this);

        fab_add_post = (FloatingActionButton)this.findViewById(R.id.id_post_detail_activity_fab_add_post);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_add_post.setColorNormalResId(R.color.colorIndigo_300);
        fab_add_post.setColorPressedResId(R.color.colorIndigo_700);
        fab_add_post.setIcon(R.mipmap.ic_add_48);
        fab_add_post.setStrokeVisible(false);
        fab_add_post.setOnClickListener(this);


        fab_modify_post = (FloatingActionButton)this.findViewById(R.id.id_post_detail_activity_fab_modify_post);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_modify_post.setColorNormalResId(R.color.colorPrimary);
        fab_modify_post.setColorPressedResId(R.color.colorPrimaryDark);
        fab_modify_post.setIcon(R.mipmap.ic_mode_edit_white_24dp);
        fab_modify_post.setStrokeVisible(false);
        fab_modify_post.setOnClickListener(this);

        fab_delete_post = (FloatingActionButton)this.findViewById(R.id.id_post_detail_activity_fab_delete_post);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_delete_post.setColorNormalResId(R.color.colorPink_300);
        fab_delete_post.setColorPressedResId(R.color.colorPink_700);
        fab_delete_post.setIcon(R.mipmap.trashbin_24);
        fab_delete_post.setStrokeVisible(false);
        fab_delete_post.setOnClickListener(this);

//        btn_dail.setOnClickListener(this);
//        btn_chat.setOnClickListener(this);
//        btn_add_post.setOnClickListener(this);
//        btn_delete_post.setOnClickListener(this);


        flashView = (FlashView)this.findViewById(R.id.id_post_detail_activity_flash_view);
        imageUrls = new ArrayList<String>();
//        imageUrls.add(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg");
//        imageUrls.add(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg");
//        imageUrls.add(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg");
//        imageUrls.add(WebUtil.HTTP_ADDRESS + "/images/head/xm.jpg");
//
//        flashView.setImageUris(imageUrls);
//        flashView.setEffect(EffectConstants.ACCORDTION_EFFECT);//更改图片切换的动画效果

        flashView.setOnPageClickListener(new FlashViewListener() {
            @Override
            public void onClick(int position) {
                // Toast.makeText(getApplicationContext(), "你的点击的是第"+(position+1)+"张图片！", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(PostDetailActivity.this, ShowNewsImageActivity.class);
                if(arr_recruit_img.length>=1){

                }else if(arr_recruit_img.length==0){
                    //arr_recruit_img[0] = WebUtil.HTTP_ADDRESS + recruit.head_path;
                }
                intent.putExtra("listpath", arr_recruit_img);
                intent.putExtra("position", (position));
                PostDetailActivity.this.startActivity(intent);

            }});

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int id  = view.getId();
        switch (id){
            case R.id.id_post_detail_activity_fab_dail:
                 intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
                break;
            case R.id.id_post_detail_activity_fab_chat:
                intent = new Intent(this,ChatActivity.class);
                intent.putExtra("friend_id", username);
                startActivity(intent);
                break;
            case R.id.id_post_detail_activity_fab_add_post:
                intent = new Intent(this,AddPostActivity.class);
                intent.putExtra("recruit_id", recruit_id);
                startActivity(intent);
                break;
            case R.id.id_post_detail_activity_fab_delete_post:

                final Dialog dialog = new Dialog(this, "提示", "你确定要删除该职位吗？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //deleteNews();
                        Dialog dialog = new Dialog(PostDetailActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deletePost();
                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
                break;

            case R.id.id_post_detail_activity_fab_modify_post:
                intent = new Intent(this, ModifyPostActivity.class);
                intent.putExtra("post_id",post.id);
                intent.putExtra("postname",post.postname);
                intent.putExtra("salary",post.salary);
                intent.putExtra("description",post.description);
                intent.putExtra("requirement",post.requirement);
                startActivityForResult(intent,MODIFY_POST);
                break;
        }
    }


    /**
     * 删除职位信息
     * **/
    private void deletePost(){
        OkhttpUtil.deletePost(handler,post_id);
    }

    private void handleDeletePost(Message msg){
        String  result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            if (root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }
        Intent intent = new Intent("com.allever.social.updateNearbyPost");
        sendBroadcast(intent);
        finish();
    }


    /**
     * 获取职位信息
     * **/
    private void getPostDetail(){
        OkhttpUtil.getPostData(handler, post_id);
    }

    private void handlePostData(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this, "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

         post = root.post;
        Recruit recruit =  root.recruit;
        tv_postname.setText(post.postname);
        tv_companyname.setText(recruit.companyname);
        tv_distance.setText("距离 " + recruit.distance + " km");
        tv_location.setText(recruit.address);
        tv_salary.setText(post.salary);
        tv_date.setText(recruit.date + "");
        tv_description.setText(post.description);
        tv_requirement.setText(post.requirement);
        tv_link.setText("联系人：" + recruit.link);
        tv_phone.setText("电  话：" + recruit.phone);
        tv_address.setText("地  址：" + recruit.address);

        if (recruit.is_owner==1){
            fab_dail.setVisibility(View.GONE);
            fab_chat.setVisibility(View.GONE);
            fab_add_post.setVisibility(View.VISIBLE);
            fab_delete_post.setVisibility(View.VISIBLE);
            fab_modify_post.setVisibility(View.VISIBLE);
        }else{
            fab_add_post.setVisibility(View.GONE);
            fab_delete_post.setVisibility(View.GONE);
            fab_modify_post.setVisibility(View.GONE);
            fab_dail.setVisibility(View.VISIBLE);
            fab_chat.setVisibility(View.VISIBLE);
        }

        username = recruit.username;
        recruit_id = recruit.id;
        phone = recruit.phone;

        arr_recruit_img = new String[root.recruit.list_recruit_img.size()];
        imageUrls.clear();

        if(root.recruit.list_recruit_img.size()>=1){
            for(int i=0; i<root.recruit.list_recruit_img.size();i++){
                arr_recruit_img[i] = WebUtil.HTTP_ADDRESS + root.recruit.list_recruit_img.get(i);
                imageUrls.add(arr_recruit_img[i]);
            }
        }else{
            //flashView.setVisibility(View.GONE);
            arr_recruit_img = new String[1];
            arr_recruit_img[0] = WebUtil.HTTP_ADDRESS + recruit.user_head_path;
            imageUrls.add(arr_recruit_img[0]);
        }

        flashView.setImageUris(imageUrls);
        flashView.setEffect(EffectConstants.ACCORDTION_EFFECT);//更改图片切换的动画效果

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
        Post post;
        Recruit recruit;
    }

    class Post{
        String id;
        String postname;
        String salary;
        String requirement;
        String description;
    }

    class Recruit{
        String id;
        String companyname;
        String date;
        double distance;
        String longitude;
        String latitude;
        String phone;
        String link;
        String address;
        int is_owner;
        String username;
        String user_head_path;
        List<String> list_recruit_img;
    }

}
