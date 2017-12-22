package com.allever.social.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.receiver.LongConnectionAlarmReceiver;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.ImageUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XM on 2016/4/20.
 * 发布动态界面
 */
public class AddNewsActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private GridView gridView1;              //网格显示缩略图
    private Button buttonPublish;            //发布按钮
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器

    private List<String> list_user_head_path;
    private static final int ADD_NEWS = 0;
    private static final int TAKE_PHOTO = 2;
    private Handler handler;

    private EditText et_content;
    private ImageButton btn_add_news;
    private ImageButton btn_location;
    private ImageButton btn_image;
    private ImageButton btn_take_photo;
    private String str_content;
    private String str_longitude;
    private String str_latitude;
    private String str_city;
    private int havedLocation = 0;
    private TextView tv_showCity;

    private Uri imageUri;
    private String imagepathTemp;

    //SharedPreferences locationSharedPreferences;
    //private SharedPreferences sessionSharedPreferences;
    private String result;
    private Root root;
    private Gson gson;

    private ProgressDialog progressDialog;

    private int gridviewClickItemPosition = 0;
    private String session_id;


    private RippleView rv_record;
    private TextView tv_record;
    private TextView tv_play_audio;
    private RippleView rv_play_audio;
    private ImageView iv_delete_audio;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private String audio_path = "";//语音文件保存路径
    private Handler handler_two;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_news_layout);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_AUTO_LOGIN:
                        handleAutoLogin(msg);
                        break;
                    case OkhttpUtil.MESSAGE_ADD_NEWS:
                        handleAddNews(msg);
                        break;
                }
            }
        };

        handler_two = new Handler();

        toolbar = (Toolbar)this.findViewById(R.id.id_add_news_toolbar);
        CommentUtil.initToolbar(this, toolbar, "发布动态");
        list_user_head_path = new ArrayList<>();

        et_content = (EditText)this.findViewById(R.id.id_add_news_et_content);
        btn_add_news = (ImageButton)this.findViewById(R.id.id_add_news_btn_add_news);
        btn_location = (ImageButton)this.findViewById(R.id.id_add_news_btn_location);
        btn_image = (ImageButton)this.findViewById(R.id.id_add_news_btn_choose_image);
        btn_take_photo = (ImageButton)this.findViewById(R.id.id_add_news_btn_take_photo);
        btn_add_news.setOnClickListener(this);
        btn_image.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        tv_showCity = (TextView)this.findViewById(R.id.id_add_news_tv_showcity);

        rv_record = (RippleView)this.findViewById(R.id.id_add_news_rv_record);
        rv_play_audio = (RippleView)this.findViewById(R.id.id_add_news_rv_play_audio);
        tv_record = (TextView)this.findViewById(R.id.id_add_news_tv_record);
        iv_delete_audio = (ImageView)this.findViewById(R.id.id_add_news_iv_delete_audio);
        tv_play_audio = (TextView)this.findViewById(R.id.id_add_news_tv_play_audio);

        if (havedLocation == 0){
            havedLocation = 1;
            tv_showCity.setVisibility(View.VISIBLE);
            tv_showCity.setText(SharedPreferenceUtil.getCity());
        }else if (havedLocation == 1){
            havedLocation = 0;
            tv_showCity.setVisibility(View.INVISIBLE);
        }

//        audio_path = Environment.getExternalStorageDirectory().getPath();
//        audio_path += "/audio_temp.arm";

        rv_record.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                audio_path = Environment.getExternalStorageDirectory().getPath();
                audio_path += "/audio_temp.arm";
                if (tv_record.getText().toString().equals("说几句")){
                    rv_record.setBackgroundColor(getResources().getColor(R.color.colorPink_300));
                    tv_record.setText("停止");
                    //录音操作
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mRecorder.setOutputFile(audio_path);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("AddNewsActivity", "prepare() failed");
                    }
                    mRecorder.start();

                }else if (tv_record.getText().toString().equals("停止")){
                    rv_play_audio.setVisibility(View.VISIBLE);
                    rv_record.setBackgroundColor(getResources().getColor(R.color.colorIndigo_300));
                    tv_record.setText("说几句");
                    //停止录音
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                }
            }
        });

        rv_play_audio.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (tv_play_audio.getText().toString().equals("播放录音")){
                    //播放录音
                    tv_play_audio.setText("停止播放");
                    mPlayer = new MediaPlayer();
                    try{
                        mPlayer.setDataSource(audio_path);
                        mPlayer.prepare();
                        mPlayer.start();

                        rv_record.setClickable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean flag = true;
                                try {
                                    while (flag){
                                        Thread.sleep(1000);
                                        if (mPlayer !=null){
                                            if (!mPlayer.isPlaying()) flag = false;
                                        }
                                    }

                                    handler_two.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            rv_record.setClickable(true);
                                            tv_play_audio.setText("播放录音");
                                        }
                                    });
                                }catch (InterruptedException e){
                                }
                            }
                        }).start();


                    }catch(IOException e){
                        Log.e("AddNewsActivity",audio_path);
                        Log.e("AddNewsActivity","播放失败");
                    }
                }else if (tv_play_audio.getText().toString().equals("停止播放")){
                    //停止播放
                    tv_play_audio.setText("播放录音");
                    mPlayer.stop();
                    //mPlayer.release();
                    //mPlayer = null;
                }

            }
        });

        iv_delete_audio.setOnClickListener(this);

        //华丽分割线1--------------------------------------------------------------------------------
        gridView1 = (GridView) findViewById(R.id.gridView1);

        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
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
                if(gridviewClickItemPosition==0 && imageItem.size() != 1){
                    dialog(position);
                    return;
                } else if (imageItem.size() == 7) { //第一张为默认图片
                   // dialog(position);
                    if(gridviewClickItemPosition != 6 ){
                        dialog(position);
                    }
                    Toast.makeText(AddNewsActivity.this, "图片数6张已满", Toast.LENGTH_SHORT).show();
                    return;
                } else if (gridviewClickItemPosition==(imageItem.size()-1)) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(AddNewsActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    return;
                    //通过onResume()刷新数据
                } else {
                    dialog(position);
                    return;

                }
            }
        });


        //华丽分割线1--------------------------------------------------------------------------------
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);//统计activity页面
    }


    //华丽分割线2--------------------------------------------------------------------------------
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case IMAGE_OPEN:
                break;
            case TAKE_PHOTO:
                break;
        }
        //打开图片
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
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
                list_user_head_path.add(pathImage);//保存图片地址
                if(list_user_head_path.size()==6){
                    Toast.makeText(AddNewsActivity.this,"图片已满" , Toast.LENGTH_SHORT).show();
                }
            }
        }  //end if 打开图片

        if(requestCode==TAKE_PHOTO && resultCode == RESULT_OK){

            pathImage = imagepathTemp;
            int degree = ImageUtil.readPictureDegree(pathImage);

            BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
            opts.inSampleSize=2;
            Bitmap cbitmap=BitmapFactory.decodeFile(pathImage,opts);
            /**
             * 把图片旋转为正的方向
             */
            Bitmap newbitmap = ImageUtil.rotaingImageView(degree, cbitmap);
            pathImage = ImageUtil.saveImage(newbitmap);
            Log.d("AcrivityResult", "in activityresult pathImage = " + pathImage);

            list_user_head_path.add(pathImage);//保存图片地址
            if(list_user_head_path.size()==6){
                Toast.makeText(AddNewsActivity.this,"图片已满" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer!=null){
            mPlayer.release();
            mPlayer = null;
        }
    }



    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
        if(!TextUtils.isEmpty(pathImage)){
            //Toast.makeText(this,pathImage,Toast.LENGTH_LONG).show();
            Log.d("onResume", "in onResume pathImage = " + pathImage);

            imageItem.ensureCapacity(imageItem.size()+1);

            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(gridviewClickItemPosition,map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
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
        }
    }
    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        if(position == 0){
           // return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewsActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
                list_user_head_path.remove(position);
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
    //华丽分割线2--------------------------------------------------------------------------------


    /*****?
     * 发布动态
      */
    private void addNews(){
        if(havedLocation == 1){
            OkhttpUtil.addNews(handler,str_content,
                    SharedPreferenceUtil.getLongitude(),
                    SharedPreferenceUtil.getLatitude(),
                    SharedPreferenceUtil.getCity(),
                    list_user_head_path,audio_path);
        }else{
            OkhttpUtil.addNews(handler,str_content,"","","",list_user_head_path,audio_path);
        }

    }


    /**
     *处理自动登录
     **/
    private void handleAutoLogin(Message msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("自动登录");
        builder.setContentTitle("Social");
        builder.setContentText("已自动登录");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, builder.build());
        addNews();

        //发广播通知MainActivity修改界面
        Intent intent = new Intent("com.allever.autologin");
        sendBroadcast(intent);


    }

    /**
     *处理发布动态
     **/
    private void handleAddNews(Message msg){
        result = msg.obj.toString();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        root = gson.fromJson(result, Root.class);

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

        closeProgressDialog();

        final Dialog dialog = new Dialog(AddNewsActivity.this,"提示","发布成功");


        Intent intent = new Intent("com.allever.social.refresh_nearby_news");
        sendBroadcast(intent);

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
        return;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_add_news_btn_add_news:
                str_content = et_content.getText().toString();
                if(str_content.equals("")){
                    new Dialog(this, "提示", "请输入内容").show();
                    return;
                }
//                getLocation();
//                getSession_id();
                showProgressDialog();;
                addNews();
                break;
            case R.id.id_add_news_btn_location:
                if (havedLocation == 0){
                    havedLocation = 1;
                    tv_showCity.setVisibility(View.VISIBLE);
                    tv_showCity.setText(SharedPreferenceUtil.getCity());
                }else if (havedLocation == 1){
                    havedLocation = 0;
                    tv_showCity.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.id_add_news_btn_choose_image:
                if (list_user_head_path.size()<6){
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                }
                break;
            case R.id.id_add_news_btn_take_photo:
                File outPutImage = new File(Environment.getExternalStorageDirectory(),new Date().toString() +".jpg");//cun chu pai zhao de zhao pian
                imagepathTemp = outPutImage.getPath();
                Log.d("onClick", "in onClick pathImage = " + imagepathTemp);
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                break;
            case R.id.id_add_news_iv_delete_audio:
                //播放显示为不可见
                audio_path = "";
                rv_play_audio.setVisibility(View.GONE);
                rv_record.setClickable(true);
                if (mPlayer!=null){
                    if (mPlayer.isPlaying()){
                        tv_play_audio.setText("播放录音");
                        mPlayer.release();
                        mPlayer = null;
                    }
                }

                break;
        }
    }


    /**
     *打开等待进度
     **/
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在发布");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    /**
     *关闭
     **/
    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }


    class Root{
        boolean success;
        String message;
        News news;
    }

    class News{
        String id;
        String content;
        String user_id;
        String date;
        String longitude;
        String latitude;
        String city;
        int is_commented;
        int commentcount;
        int lickcount;
        List<String> news_image_path;


    }

}
