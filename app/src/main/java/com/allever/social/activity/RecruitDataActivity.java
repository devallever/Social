package com.allever.social.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.PostBaseAdapter;
import com.allever.social.pojo.PostItem;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.WebUtil;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.flashview.FlashView;
import com.gc.flashview.constants.EffectConstants;
import com.gc.flashview.listener.FlashViewListener;
import com.gc.materialdesign.widgets.Dialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/21.
 * 招聘详情界面
 */
public class RecruitDataActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private final static int MODIFY_RECRUIT = 1;

    private TextView tv_companyname;
    private TextView tv_distance;
    private TextView tv_link;
    private TextView tv_address;
    private TextView tv_requirement;
//    private ButtonRectangle btn_dail;
//    private ButtonRectangle btn_chat;
//    private ButtonRectangle btn_add_post;
//    private ButtonRectangle btn_delete_recruit;
    private FloatingActionButton fab_chat;
    private FloatingActionButton fab_dail;
    private FloatingActionButton fab_add_post;
    private FloatingActionButton fab_delete_recruit;
    private FloatingActionButton fab_modify_recruit;
    private CircleImageView iv_head;
    private ListView listView;

    private String recruit_id;
    private String username;
    private String phone;

    private Handler handler;
    private PostBaseAdapter postBaseAdapter;
    private List<PostItem> list_postitem = new ArrayList<>();

    private Recruit recruit;


    private FlashView flashView;

    private List<String> imageUrls;
    private String[] arr_recruit_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recruit_data_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_RECRUIT_DATA:
                        handleRecruitData(msg);
                        break;
                    case OkhttpUtil.MESSAGE_POST_LIST:
                        handlePostList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_RECRUIT:
                        handleDeleteRecruit(msg);
                        break;
                }
            }
        };

        recruit_id = getIntent().getStringExtra("recruit_id");

        ActionBar ab = this.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("招聘详情");

        initData();

        getRecruitData();
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
            case MODIFY_RECRUIT:
                if (resultCode == RESULT_OK){
                    finish();
                }
                break;
        }
    }

    private void initData(){
        tv_address = (TextView)this.findViewById(R.id.id_recruit_data_activity_tv_address);
        tv_companyname = (TextView)this.findViewById(R.id.id_recruit_data_activity_tv_companyname);
        tv_distance = (TextView)this.findViewById(R.id.id_recruit_data_activity_tv_distance);
        tv_link = (TextView)this.findViewById(R.id.id_recruit_data_activity_tv_link);
        tv_requirement = (TextView)this.findViewById(R.id.id_recruit_data_activity_tv_requirement);

//        btn_add_post = (ButtonRectangle)this.findViewById(R.id.id_recruit_data_activity_btn_add_post);
//        btn_dail = (ButtonRectangle)this.findViewById(R.id.id_recruit_data_activity_btn_dail);
//        btn_delete_recruit = (ButtonRectangle)this.findViewById(R.id.id_recruit_data_activity_btn_delete_recruit);
//        btn_chat = (ButtonRectangle)this.findViewById(R.id.id_recruit_data_activity_btn_chat);

        fab_chat = (FloatingActionButton)this.findViewById(R.id.id_recruit_data_activity_fab_chat);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_chat.setColorNormalResId(R.color.colorGreen_300);
        fab_chat.setColorPressedResId(R.color.colorGreen700);
        fab_chat.setIcon(R.mipmap.ic_sms_white_24dp);
        fab_chat.setStrokeVisible(false);
        fab_chat.setOnClickListener(this);

        fab_dail = (FloatingActionButton)this.findViewById(R.id.id_recruit_data_activity_fab_dail);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_dail.setColorNormalResId(R.color.colorGreen_300);
        fab_dail.setColorPressedResId(R.color.colorGreen700);
        fab_dail.setIcon(R.mipmap.ic_call_white_24dp);
        fab_dail.setStrokeVisible(false);
        fab_dail.setOnClickListener(this);

        fab_add_post = (FloatingActionButton)this.findViewById(R.id.id_recruit_data_activity_fab_add_post);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_add_post.setColorNormalResId(R.color.colorIndigo_300);
        fab_add_post.setColorPressedResId(R.color.colorIndigo_700);
        fab_add_post.setIcon(R.mipmap.ic_add_48);
        fab_add_post.setStrokeVisible(false);
        fab_add_post.setOnClickListener(this);


        fab_modify_recruit = (FloatingActionButton)this.findViewById(R.id.id_recruit_data_activity_fab_modify_recruit);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_modify_recruit.setColorNormalResId(R.color.colorPrimary);
        fab_modify_recruit.setColorPressedResId(R.color.colorPrimaryDark);
        fab_modify_recruit.setIcon(R.mipmap.ic_mode_edit_white_24dp);
        fab_modify_recruit.setStrokeVisible(false);
        fab_modify_recruit.setOnClickListener(this);

        fab_delete_recruit = (FloatingActionButton)this.findViewById(R.id.id_recruit_data_activity_fab_delete_recruit);
        //fab_chat.setSize(FloatingActionButton.SIZE_MINI);
        fab_delete_recruit.setColorNormalResId(R.color.colorPink_300);
        fab_delete_recruit.setColorPressedResId(R.color.colorPink_700);
        fab_delete_recruit.setIcon(R.mipmap.trashbin_24);
        fab_delete_recruit.setStrokeVisible(false);
        fab_delete_recruit.setOnClickListener(this);


        iv_head = (CircleImageView)this.findViewById(R.id.id_recruit_data_activity_iv_head);
        listView = (ListView)this.findViewById(R.id.id_recruit_data_activity_listview);
        listView.setOnItemClickListener(this);

        flashView = (FlashView)this.findViewById(R.id.id_recruit_data_activity_flash_view);
        imageUrls = new ArrayList<String>();
//        flashView.setImageUris(imageUrls);
//        flashView.setEffect(EffectConstants.ACCORDTION_EFFECT);//更改图片切换的动画效果
        flashView.setOnPageClickListener(new FlashViewListener() {
            @Override
            public void onClick(int position) {
               // Toast.makeText(getApplicationContext(), "你的点击的是第"+(position+1)+"张图片！", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RecruitDataActivity.this, ShowNewsImageActivity.class);
                if(arr_recruit_img.length>=1){

                }else if(arr_recruit_img.length==0){
                    arr_recruit_img[0] = WebUtil.HTTP_ADDRESS + recruit.head_path;
                }
                intent.putExtra("listpath", arr_recruit_img);
                intent.putExtra("position", (position));
                RecruitDataActivity.this.startActivity(intent);

            }});

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("post_id",list_postitem.get(i).getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_recruit_data_activity_fab_add_post:
                intent = new Intent(this,AddPostActivity.class);
                intent.putExtra("recruit_id",recruit_id);
                startActivity(intent);
                break;
            case R.id.id_recruit_data_activity_fab_chat:
                intent = new Intent(this,ChatActivity.class);
                intent.putExtra("friend_id", username);
                startActivity(intent);
                break;
            case R.id.id_recruit_data_activity_fab_dail:
                intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
                break;
            case R.id.id_recruit_data_activity_fab_delete_recruit:

                final Dialog dialog = new Dialog(this, "提示", "你确定要删除该招聘吗？");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //deleteNews();
                        Dialog dialog = new Dialog(RecruitDataActivity.this, "提示", "重要的事情说三遍\n您真的要狠心删除么?123");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteRecruit();
                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
                break;
            case R.id.id_recruit_data_activity_fab_modify_recruit:
                intent = new Intent(this, ModifyRecruitActivity.class);
                intent.putExtra("recruit_id", recruit.id);
                intent.putExtra("companyname", recruit.companyname);
                intent.putExtra("link", recruit.link);
                intent.putExtra("phone", recruit.phone);
                intent.putExtra("address", recruit.address);
                intent.putExtra("requirement", recruit.requirement);
                startActivityForResult(intent,MODIFY_RECRUIT);
                break;
        }
    }

    /**
     * 删除招聘
     * **/
    private void deleteRecruit(){
        OkhttpUtil.deleteRecruit(handler, recruit_id);
    }

    private void handleDeleteRecruit(Message msg){
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

        Intent intent1 = new Intent("com.allever.social.updateMyRecruitList");
        sendBroadcast(intent1);
        finish();
    }


    /**
     * 获取招聘信息
     * **/
    private void getRecruitData(){
        OkhttpUtil.getRecruitData(handler,recruit_id);
    }

    private void handleRecruitData(Message msg){
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

        username = root.recruit.user_name;
        phone = root.recruit.phone;
        recruit = root.recruit;
        tv_companyname.setText(recruit.companyname);
        tv_distance.setText("距离 " + recruit.distance + " km");
        tv_link.setText("联系人：" + recruit.link);
        tv_address.setText("地址：" + recruit.address);
        tv_requirement.setText("补充：" + recruit.requirement);


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
            arr_recruit_img[0] = WebUtil.HTTP_ADDRESS + root.recruit.head_path;
            imageUrls.add(arr_recruit_img[0]);
        }

        flashView.setImageUris(imageUrls);
        flashView.setEffect(EffectConstants.ACCORDTION_EFFECT);//更改图片切换的动画效果

        if (recruit.is_owner == 1){
            fab_delete_recruit.setVisibility(View.VISIBLE);
            fab_add_post.setVisibility(View.VISIBLE);
            fab_modify_recruit.setVisibility(View.VISIBLE);
            fab_dail.setVisibility(View.GONE);
            fab_chat.setVisibility(View.GONE);
        }else{
            fab_chat.setVisibility(View.VISIBLE);
            fab_dail.setVisibility(View.VISIBLE);
            fab_delete_recruit.setVisibility(View.GONE);
            fab_add_post.setVisibility(View.GONE);
            fab_modify_recruit.setVisibility(View.GONE);
        }
//
//        btn_add_post.setOnClickListener(this);

        Glide.with(this)
                .load(WebUtil.HTTP_ADDRESS + recruit.head_path)
                .into(iv_head);

        getPostList();
    }

    private void getPostList(){
        OkhttpUtil.getPostList(handler,recruit_id);
    }

    private void handlePostList(Message msg){
        String result = msg.obj.toString();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PostRoot root = gson.fromJson(result, PostRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            new Dialog(this,"错误",root.message).show();
        }

        list_postitem.clear();
        PostItem postItem;
        for (Post post : root.list_post){
            postItem = new PostItem();
            postItem.setId(post.id);
            postItem.setPostname(post.postname);
            postItem.setSalary(post.salary);
            list_postitem.add(postItem);
        }

        postBaseAdapter = new PostBaseAdapter(this,list_postitem);
        listView.setAdapter(postBaseAdapter);

        setListViewHeightBasedOnChildren(listView);
        CommentUtil.setListViewHeightBasedOnChildren(listView);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
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
        Recruit recruit;
    }
    class Recruit{
        String id;
        String companyname;
        double distance;
        String link;
        String phone;
        String address;
        String requirement;
        String user_name;
        String head_path;
        int is_owner;
        List<String> list_recruit_img;

    }

    class PostRoot{
        boolean success;
        String message;
        List<Post> list_post;
    }

    class Post{
        String id;
        String postname;
        String salary;
    }
}
