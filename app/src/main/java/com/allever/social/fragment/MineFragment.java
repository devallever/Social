package com.allever.social.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.activity.AboutActivity;
import com.allever.social.activity.ChooseOnlineStateActivity;
import com.allever.social.activity.FeedbackActivity;
import com.allever.social.activity.FollowFansCountActivity;
import com.allever.social.activity.GeneralActivity;
import com.allever.social.activity.LoginActivity;
import com.allever.social.activity.ModifyUserDataActivity;
import com.allever.social.activity.MyRecruitActivity;
import com.allever.social.activity.PocketActivity;
import com.allever.social.activity.SettingActivity;
import com.allever.social.activity.ShowNewsImageActivity;
import com.allever.social.activity.UserNewsActivity;
import com.allever.social.activity.VipCenterActivity;
import com.allever.social.activity.VisitedUserActivity;
import com.allever.social.activity.WebCollectionActivity;
import com.allever.social.adapter.ForwardUserBaseAdapter;
import com.allever.social.adapter.NewsImgAdapter;
import com.allever.social.utils.BlurTransformation;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.ImageUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.allever.social.view.MyGridView;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/15.
 */
public class MineFragment extends Fragment implements View.OnClickListener , RippleView.OnRippleCompleteListener{
    private static final int REQUEST_CODE_LOGIN = 1000;

    private ImageView iv_bg;

    private CircleImageView iv_head;
    private TextView tv_nickname;
    private TextView tv_news_count;
    private TextView tv_fans_count;
    private TextView tv_follow_count;
    private Handler handler;

    private RippleView rv_login;
    private RippleView rv_logout;

    private RippleView rv_account_and_secure;
    private RippleView rv_private;
    private RippleView rv_feedback;
    private RippleView rv_about;
    private RippleView rv_general_setting;
    private RippleView rv_visited_user;
    private RippleView rv_follow;
    private RippleView rv_fans;
    private RippleView rv_news;
    private RippleView rv_collection;
    private RippleView rv_online_state;
    private RippleView rv_exit;

    private ImageView iv_onlinestate;

    private NewsImgAdapter newsImgAdapter;

    private ButtonFlat btn_submit;
    private ButtonFlat btn_take_photo;
    private MyGridView gridView1;              //网格显示缩略图
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private List<String> list_image_path;
    private String imagePathTemp;
    private int gridviewClickItemPosition = 0;

    private List<String> list_photo_wall;


    private AfterModifyUserDataReceiver afterModifyUserDataReceiver;
    private AfterLoginaReceiver afterLoginReceiver;
    private UpdateSocialReceiver updateSocialReceiver;
    private UpdateOnlineStateReceiver updateOnlineStateReceiver;
    private ForceLogoutReceiver forceLogoutReceiver;
    private IntentFilter intentFilter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment_layout,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_LOGOUT:
                        handleLogout(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_PHOTO_WALL:
                        handleAddPhotoWall(msg);
                        break;
                    case OkhttpUtil.MESSAGE_PHOTO_WALL_LIST:
                        handlePhotoWallList(msg);
                        break;
                    case OkhttpUtil.MESSAGE_DELETE_PHOTO_WALL:
                        handleDeletPhotoWall(msg);
                        break;
                    case OkhttpUtil.MESSAGE_SOCIAL_COUNT:
                        handleGetSocialCount(msg);
                        break;
                    case OkhttpUtil.MESSAGE_GET_ONLINE_STATE:
                        handleGetOnlineState(msg);
                        break;
                }
            }
        };

        tv_fans_count = (TextView)view.findViewById(R.id.id_mine_fg_tv_fans_count);
        tv_follow_count = (TextView)view.findViewById(R.id.id_mine_fg_tv_follow_count);
        tv_news_count = (TextView)view.findViewById(R.id.id_mine_fg_tv_news_count);

//        rv_recruit = (RippleView)view.findViewById(R.id.id_mine_fg_rv_recruit);
//        if (OkhttpUtil.checkLogin()) rv_recruit.setOnRippleCompleteListener(this);
//        else Toast.makeText(getActivity(),"未登录",Toast.LENGTH_LONG).show();

        rv_account_and_secure = (RippleView)view.findViewById(R.id.id_mine_fg_rv_account_and_secure);
        if (OkhttpUtil.checkLogin()) rv_account_and_secure.setOnRippleCompleteListener(this);

        rv_private = (RippleView)view.findViewById(R.id.id_mine_fg_rv_private);
        if (OkhttpUtil.checkLogin()) rv_private.setOnRippleCompleteListener(this);

        rv_general_setting = (RippleView)view.findViewById(R.id.id_mine_fg_rv_gegeral_setting);
        rv_general_setting.setOnRippleCompleteListener(this);

        rv_feedback = (RippleView)view.findViewById(R.id.id_mine_fg_rv_feedback);
        rv_feedback.setOnRippleCompleteListener(this);

        rv_about = (RippleView)view.findViewById(R.id.id_mine_fg_rv_about);
        rv_about.setOnRippleCompleteListener(this);

        rv_visited_user = (RippleView)view.findViewById(R.id.id_mine_fg_rv_visited);
        if (OkhttpUtil.checkLogin())  rv_visited_user.setOnRippleCompleteListener(this);
        rv_logout = (RippleView)view.findViewById(R.id.id_mine_fg_rv_logout);
        rv_logout.setOnRippleCompleteListener(this);
        rv_login = (RippleView)view.findViewById(R.id.id_mine_fg_rv_login);
        rv_login.setOnRippleCompleteListener(this);

        rv_follow = (RippleView)view.findViewById(R.id.id_mine_fg_rv_follow);
        rv_follow.setOnRippleCompleteListener(this);
        rv_fans = (RippleView)view.findViewById(R.id.id_mine_fg_rv_fans);
        rv_fans.setOnRippleCompleteListener(this);
        rv_news = (RippleView)view.findViewById(R.id.id_mine_fg_rv_news);
        rv_news.setOnRippleCompleteListener(this);
        rv_collection = (RippleView)view.findViewById(R.id.id_mine_fg_rv_collection);
        rv_collection.setOnRippleCompleteListener(this);

        rv_online_state = (RippleView)view.findViewById(R.id.id_mine_fg_rv_onlinestate);
        rv_online_state.setOnRippleCompleteListener(this);

        rv_exit = (RippleView)view.findViewById(R.id.id_mine_fg_rv_exit);
        rv_exit.setOnRippleCompleteListener(this);

        iv_onlinestate = (ImageView)view.findViewById(R.id.id_mine_fg_iv_onlinestate);
        iv_head = (CircleImageView)view.findViewById(R.id.id_mine_fg_iv_head);
        iv_head.setOnClickListener(this);
        tv_nickname = (TextView)view.findViewById(R.id.id_mine_fg_tv_nickname);
        if (OkhttpUtil.checkLogin()){
            //tv_username.setText("账号：" + SharedPreferenceUtil.getUserName());
            tv_nickname.setText(SharedPreferenceUtil.getNickname()+"(" + SharedPreferenceUtil.getUserName() + ")");
            Glide.with(getActivity())
                    .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                    .into(iv_head);
            rv_online_state.setVisibility(View.VISIBLE);
        }else{
            rv_online_state.setVisibility(View.GONE);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.allever.modifyUserHead");
        intentFilter.addAction("com.allever.modifyUserData");
        intentFilter.addAction("com.allever.afterlogin");
        intentFilter.addAction("com.allever.social.UPDATE_SOCIAL_COUNT");
        intentFilter.addAction("com.allever.social.UPDATE_ONLINE_STATE");
        intentFilter.addAction("com.allever.social.USER_LOGIN_ANOTHER_DEVICE");
        afterModifyUserDataReceiver = new AfterModifyUserDataReceiver();
        afterLoginReceiver = new AfterLoginaReceiver();
        updateSocialReceiver = new UpdateSocialReceiver();
        updateOnlineStateReceiver = new UpdateOnlineStateReceiver();
        forceLogoutReceiver  = new ForceLogoutReceiver();
        getActivity().registerReceiver(afterLoginReceiver, intentFilter);
        getActivity().registerReceiver(afterModifyUserDataReceiver, intentFilter);
        getActivity().registerReceiver(updateSocialReceiver,intentFilter);
        getActivity().registerReceiver(updateOnlineStateReceiver,intentFilter);
        getActivity().registerReceiver(forceLogoutReceiver,intentFilter);

        RelativeLayout rl  = (RelativeLayout)view.findViewById(R.id.id_mine_fg_rl_gridview_container);
        rl.getBackground().setAlpha(100);

        //----------
        gridView1 = (MyGridView)view.findViewById(R.id.id_mine_fg_gv_photowall);
        list_image_path = new ArrayList<>();
        if(OkhttpUtil.checkLogin()){
            getPhotoWallList();
        }else{
        }

        //------------



        iv_bg = (ImageView)view.findViewById(R.id.id_mine_fg_iv_bg);
        RelativeLayout rl_head_bg =  (RelativeLayout)view.findViewById(R.id.id_mine_fg_rl_head_bg);
        //applyBlur();
        Glide.with(getActivity()).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                .transform(new BlurTransformation(getActivity(), 100))
                .crossFade()
                .into(iv_bg);


        if (OkhttpUtil.checkLogin()){
            rv_logout.setVisibility(View.VISIBLE);
            rv_login.setVisibility(View.INVISIBLE);
        }else{
            rv_login.setVisibility(View.VISIBLE);
            rv_logout.setVisibility(View.INVISIBLE);
        }


        //华丽分割线1--------------------------------------------------------------------------------
        //gridView1 = (GridView) findViewById(R.id.gridView1);

        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        imageItem = new ArrayList<HashMap<String, Object>>(8);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(getActivity(),
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                gridviewClickItemPosition = position;
                Log.d("AddRecruitAcrivity", "gridviewClickItemPosttion = " + gridviewClickItemPosition);
                Log.d("AddRecruitAcrivity", "imageItem.size() = " + (imageItem.size() - 1));
                if (gridviewClickItemPosition == (imageItem.size() - 1)) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(getActivity(), "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    return;
                } else {
                    //看大图
                    Intent intent = new Intent(getActivity(), ShowNewsImageActivity.class);
                    String[] list_path = new String[list_photo_wall.size()];
                    for (int i=0; i<list_photo_wall.size();i++){
                        list_path[i] =  WebUtil.HTTP_ADDRESS + list_photo_wall.get(i);
                    }
                    intent.putExtra("listpath",list_path);
                    intent.putExtra("position",gridviewClickItemPosition);
                    startActivity(intent);
                    return;
                }
            }
        });
        gridView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                gridviewClickItemPosition = position;
                Log.d("AddRecruitAcrivity", "gridviewClickItemPosttion = " + gridviewClickItemPosition);
                Log.d("AddRecruitAcrivity", "imageItem.size() = " + (imageItem.size() - 1));
                dialog(position);
                return true;
            }
        });


        //获取动态数 ，关注数， 粉丝数 用户状态
        if (OkhttpUtil.checkLogin()){
            getSocialCount();
            getOnlineState();
        }

        //华丽分割线1--------------------------------------------------------------------------------
        return view;
    }

    private void getOnlineState(){
        OkhttpUtil.getOnlineState(handler);
    }

    private void handleGetOnlineState(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        OnlineStateRoot root = gson.fromJson(result, OnlineStateRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        switch (root.onlinestate){
            case "在线":
                iv_onlinestate.setImageResource(R.mipmap.online_36);
                break;
            case "离线":
                iv_onlinestate.setImageResource(R.mipmap.offline_36);
                break;
            case "忙碌":
                iv_onlinestate.setImageResource(R.mipmap.busy_36);
                break;
        }
    }

    private void getSocialCount(){
        OkhttpUtil.getSocialCount(handler, SharedPreferenceUtil.getUserName());
    }

    private void handleGetSocialCount(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SocialCountRoot root = gson.fromJson(result, SocialCountRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        tv_news_count.setText(root.news_count+"");
        tv_fans_count.setText(root.fans_count+"");
        tv_follow_count.setText(root.follow_count+"");

    }



    private void getPhotoWallList(){
        OkhttpUtil.getPhotoWallList(handler, SharedPreferenceUtil.getUserName());
    }

    private void handlePhotoWallList(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        PhotoWallListRoot root = gson.fromJson(result, PhotoWallListRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                return;
            }
            if(root.message.equals("未登录")){
                new Dialog(getActivity(),"Tips","未登录").show();
                return;
            }
        }

        //to do
        list_photo_wall = root.photowalllist;

        if(list_photo_wall.size()>0){
            handleShowPhotoWall(WebUtil.HTTP_ADDRESS + list_photo_wall.get(list_photo_wall.size()-1),list_photo_wall.size()-1);
        }

    }

    private void handleShowPhotoWall(String path, final int position){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("user_id", SharedPreferenceUtil.getUserId())
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                .build();
        Log.d("Download Image", path);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                    byte[] result = response.body().bytes();
                    final Bitmap bitmap = CommentUtil.byteToBitmap(result, null);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("itemImage", bitmap);
                            imageItem.add(gridviewClickItemPosition, map);

                            if (imageItem != null && getActivity() != null)
                                simpleAdapter = new SimpleAdapter(getActivity(), imageItem, R.layout.griditem_addpic, new String[]{"itemImage"}, new int[]{R.id.imageView1});
                            else return;
                            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                @Override
                                public boolean setViewValue(View view, Object data,
                                                            String textRepresentation) {
                                    // TODO Auto-generated method stub
                                    if (view instanceof ImageView && data instanceof Bitmap) {
                                        ImageView i = (ImageView) view;
                                        i.setImageBitmap((Bitmap) data);
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            if ((position - 1) >= 0) {
                                handleShowPhotoWall(WebUtil.HTTP_ADDRESS + list_photo_wall.get(position - 1), (position - 1));
                            }

                            if ((imageItem.size() - 1) == list_photo_wall.size()) {
                                gridView1.setAdapter(simpleAdapter);
                                simpleAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode==getActivity().RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getActivity().getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                list_image_path.add(pathImage);//保存图片地址
                if(list_image_path.size()==8){
                    Toast.makeText(getActivity(),"图片已满" , Toast.LENGTH_SHORT).show();
                }
            }
        } else if(resultCode==getActivity().RESULT_OK && requestCode==REQUEST_CODE_LOGIN) {
            if (OkhttpUtil.checkLogin()){
                //tv_username.setText("账号：" + SharedPreferenceUtil.getUserName());
                tv_nickname.setText(SharedPreferenceUtil.getNickname()+"(" + SharedPreferenceUtil.getUserName() + ")");
                Glide.with(getActivity())
                        .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                        .into(iv_head);
                rv_login.setVisibility(View.INVISIBLE);
                rv_logout.setVisibility(View.VISIBLE);
                rv_online_state.setVisibility(View.VISIBLE);
            }else{
                rv_online_state.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        Intent intent;
        switch (id){
            case R.id.id_mine_fg_rv_login:
                intent = new Intent(getActivity(), LoginActivity.class);
                //startActivityForResult(intent,REQUEST_CODE_LOGIN);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.id_mine_fg_rv_gegeral_setting://通用设置
                intent = new Intent(getActivity(), GeneralActivity.class);
                //intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_fg_rv_about:
                intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_fg_rv_private:
                Toast.makeText(getActivity(),"隐私",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_feedback:
                Toast.makeText(getActivity(),"反馈",Toast.LENGTH_LONG).show();
                intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_fg_rv_account_and_secure:
                Toast.makeText(getActivity(), "账号与安全", Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_logout:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(MyApplication.mContext).clearDiskCache();
                    }
                }).start();
                logout();
                cleanLocation();
                getActivity().finish();
                intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_fg_rv_visited:
                intent = new Intent(getActivity(), VisitedUserActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_fg_rv_follow:
                intent = new Intent(getActivity(), FollowFansCountActivity.class);
                intent.putExtra("page_position", 0);
                intent.putExtra("username", SharedPreferenceUtil.getUserName());
                if (OkhttpUtil.checkLogin()) startActivity(intent);
                else Toast.makeText(getActivity(),"先登录",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_fans:
                intent = new Intent(getActivity(), FollowFansCountActivity.class);
                intent.putExtra("page_position", 1);
                intent.putExtra("username", SharedPreferenceUtil.getUserName());
                if (OkhttpUtil.checkLogin()) startActivity(intent);
                else Toast.makeText(getActivity(),"先登录",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_news:
                intent = new Intent(getActivity(), UserNewsActivity.class);
                intent.putExtra("user_id",SharedPreferenceUtil.getUserId());
                intent.putExtra("nickname",SharedPreferenceUtil.getNickname());
                intent.putExtra("username",SharedPreferenceUtil.getUserName());
                intent.putExtra("user_head_path", SharedPreferenceUtil.getHeadpath());
                if (OkhttpUtil.checkLogin()) startActivity(intent);
                else Toast.makeText(getActivity(),"先登录",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_collection:
                intent = new Intent(getActivity(), WebCollectionActivity.class);
                if (OkhttpUtil.checkLogin()) startActivity(intent);
                else Toast.makeText(getActivity(),"先登录",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_onlinestate:
                intent = new Intent(getActivity(), ChooseOnlineStateActivity.class);
                if (OkhttpUtil.checkLogin()) startActivity(intent);
                else Toast.makeText(getActivity(),"先登录",Toast.LENGTH_LONG).show();
                break;
            case R.id.id_mine_fg_rv_exit:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
        if(!TextUtils.isEmpty(pathImage)){
            try {
                imageItem.ensureCapacity(imageItem.size()+1);
                Bitmap addbmp= ImageUtil.revitionImageSize(pathImage);
                byte[] bytes = CommentUtil.Bitmap2Bytes(addbmp);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", addbmp);
                imageItem.add(gridviewClickItemPosition, map);

                //addPhowoWall
                addPhotoWall(gridviewClickItemPosition,bytes);
                simpleAdapter = new SimpleAdapter(getActivity(), imageItem, R.layout.griditem_addpic, new String[] { "itemImage"}, new int[] { R.id.imageView1});
                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data,
                                                String textRepresentation) {
                        // TODO Auto-generated method stub
                        if(view instanceof ImageView && data instanceof Bitmap){
                            ImageView i = (ImageView)view;
                            i.setImageBitmap((Bitmap) data);
                            return true;
                        }
                        return false;
                    }
                });
                gridView1.setAdapter(simpleAdapter);
                simpleAdapter.notifyDataSetChanged();
                //刷新后释放防止手机休眠后自动添加
                pathImage = null;
            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }

    private void addPhotoWall(int position, byte[] bytes){
        OkhttpUtil.addPhotoWall(handler, position, bytes);
    }

    private void handleAddPhotoWall(Message msg){
        String result = msg.obj.toString();
        Log.d("ContactFragment", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AddPhotoWallRoot root = gson.fromJson(result, AddPhotoWallRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        if (root.success == false){
            if (root.message.equals("无记录")){
                return;
            }
            if(root.message.equals("未登录")){
                new Dialog(getActivity(),"Tips","未登录").show();
                return;
            }
        }

        list_photo_wall.add(root.new_photo_path);

    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        if(position == 0){
            //return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
                deletePhotoWall(position);
                //list_image_path.remove(position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deletePhotoWall(int position){
        OkhttpUtil.deletePhotoWall(handler, position);
    }

    private void handleDeletPhotoWall(Message msg){

    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(afterModifyUserDataReceiver);
        getActivity().unregisterReceiver(afterLoginReceiver);
        getActivity().unregisterReceiver(updateSocialReceiver);
        getActivity().unregisterReceiver(forceLogoutReceiver);
        getActivity().unregisterReceiver(updateOnlineStateReceiver);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.id_mine_fg_iv_head:
                if (OkhttpUtil.checkLogin()){
                    intent = new Intent(getActivity(), ModifyUserDataActivity.class);
                    startActivity(intent);
                }else{
                }
                break;

        }
    }

    private void cleanLocation(){
        SharedPreferenceUtil.setState("0");
        SharedPreferenceUtil.setSessionId("");
    }

    private void logout(){
        OkhttpUtil.logout(handler);
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void handleAutoLogin(Message msg){
        //发广播通知MainActivity修改界面
        String result = msg.obj.toString();
        Log.d("Setting", result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LoginRoot root = gson.fromJson(result, LoginRoot.class);
        //登录成功后为每个用户设置别名：username
        JPushInterface.setAlias(getActivity(), root.user.username, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

        Intent intent = new Intent("com.allever.autologin");
        getActivity().sendBroadcast(intent);
        Glide.with(getActivity()).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath()).into(iv_head);
        tv_nickname.setText(SharedPreferenceUtil.getNickname()+"(" + SharedPreferenceUtil.getUserName() + ")");

        Intent i = new Intent(getActivity(),ModifyUserDataActivity.class);
        startActivity(i);
    }

    private class  AfterModifyUserDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getActivity(),"收到广播",Toast.LENGTH_LONG).show();
            String  action = intent.getAction();
            if(action.equals("com.allever.modifyUserData")){
                String nickname = intent.getStringExtra("nickname");
                //tv_nickname.setText(nickname);
                tv_nickname.setText(nickname + "(" + SharedPreferenceUtil.getUserName() + ")");
            }else if (action.equals("com.allever.modifyUserHead")){
                String head_path = intent.getStringExtra("head_path");
               // Picasso.with(getActivity()).load(head_path).into(iv_head);
                Glide.with(getActivity())
                        .load(head_path)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(iv_head);
                Glide.with(getActivity()).load(head_path)
                        .transform(new BlurTransformation(getActivity(), 100))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .crossFade()
                        .into(iv_bg);
            }
        }
    }

    private class  AfterLoginaReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getActivity(),"收到广播",Toast.LENGTH_LONG).show();
            String  action = intent.getAction();
            if(action.equals("com.allever.afterlogin")){
                getActivity().finish();
            }
        }
    }

    private class UpdateSocialReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action = intent.getAction();
            if (action.equals("com.allever.social.UPDATE_SOCIAL_COUNT")){
                if (OkhttpUtil.checkLogin()) getSocialCount();
            }
        }
    }

    private class UpdateOnlineStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action = intent.getAction();
            if (action.equals("com.allever.social.UPDATE_ONLINE_STATE")){
                if (OkhttpUtil.checkLogin()) getOnlineState();
            }
        }
    }

    private class ForceLogoutReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action = intent.getAction();
            if (action.equals("com.allever.social.USER_LOGIN_ANOTHER_DEVICE")){
                logout();
                cleanLocation();
                getActivity().finish();
                intent = new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("force_logout", "force_logout");
                startActivity(intent);
            }
        }
    }

    class AddPhotoWallRoot{
        boolean success;
        String message;
        String new_photo_path;
    }

    class PhotoWallListRoot{
        boolean success;
        String message;
        List<String> photowalllist;
    }


    class LoginRoot{
        boolean seccess;
        String message;
        String session_id;
        User user;
    }

    class User{
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
    }

    private void handleLogout(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        LogoutRoot  root = gson.fromJson(result, LogoutRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){

        }else{
            SharedPreferenceUtil.setState("");
            SharedPreferenceUtil.setSessionId("");
            logoutIMService();

        }
    }

    class LogoutRoot{
        public Boolean success;
        public String message;
    }

    private void logoutIMService(){
        //此方法为异步方法
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("SocialMainActivity", "成功退出环信服务器");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.d("SocialMainActivity", "还没退出环信服务器");
            }
        });
    }


    private static Bitmap stackblur(Bitmap sentBitmap,
                                    int radius) {

        Bitmap bitmap = null;
        try {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return sentBitmap;
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


    class SocialCountRoot{
        boolean success;
        String message;
        int fans_count;//粉丝数
        int follow_count;//关注数
        int news_count;//动态数
    }

    class OnlineStateRoot{
        boolean success;
        String message;
        String onlinestate;
    }
}
