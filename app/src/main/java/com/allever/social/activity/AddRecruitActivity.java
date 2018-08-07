package com.allever.social.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.utils.ImageUtil;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XM on 2016/5/19.
 * 新建招聘界面
 */
public class AddRecruitActivity extends BaseActivity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 2;

    private ButtonFlat btn_submit;
    private ButtonFlat btn_take_photo;
    private GridView gridView1;              //网格显示缩略图
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private List<String> list_image_path;
    private String imagePathTemp;
    private int gridviewClickItemPosition = 0;

    private String companyname;
    private String link;
    private String phone;
    private String requirement;
    private String address;

    private MaterialEditText et_companyname;
    private MaterialEditText et_link;
    private MaterialEditText et_phone;
    private MaterialEditText et_requitement;
    private MaterialEditText et_address;


    private ProgressDialog progressDialog;

    private Uri imageUri;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recruit_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_ADD_RECRUIT:
                        handleAddRecruit(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新建招聘");


        initData();

        //华丽分割线1--------------------------------------------------------------------------------
        //gridView1 = (GridView) findViewById(R.id.gridView1);

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
                Log.d("AddRecruitAcrivity","gridviewClickItemPosttion = " + gridviewClickItemPosition);
                Log.d("AddRecruitAcrivity","imageItem.size() = " + (imageItem.size()));
                if(gridviewClickItemPosition==0 && imageItem.size() != 1){
                    dialog(position);
                    return;
                } else if (imageItem.size() == 7) { //第一张为默认图片
                    //dialog(position);
                    if(gridviewClickItemPosition != 6 ){
                        dialog(position);
                    }
                    Toast.makeText(AddRecruitActivity.this, "图片数6张已满", Toast.LENGTH_SHORT).show();
                    return;
                } else if (gridviewClickItemPosition==(imageItem.size()-1)) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(AddRecruitActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    return;
                    //通过onResume()刷新数据
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




    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_add_recruit_activity_btn_submit:
                companyname = et_companyname.getText().toString();
                if (companyname.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入公司名称.");
                    dialog.show();
                    return;
                }
                link = et_link.getText().toString();
                if (link.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入联系人.");
                    dialog.show();
                    return;
                }

                requirement = et_requitement.getText().toString();
                if (requirement.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入您的要求.");
                    dialog.show();
                    return;
                }

                address = et_address.getText().toString();
                if (address.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入您公司地址.");
                    dialog.show();
                    return;
                }

                phone = et_phone.getText().toString();
                if (phone.equals("")) {
                    Dialog dialog = new Dialog(this,"Tips","请输入联系人.");
                    dialog.show();
                    return;
                }

                if(phone.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){

                }else if(phone.matches("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")){

                }else{
                    new Dialog(this,"Tips","请输入正确的手机号..").show();
                    return;
                }

                addRecruit();
                showProgressDialog();

                break;
            case R.id.id_add_recruit_activity_btn_take_photo:

                File outPutImage = new File(Environment.getExternalStorageDirectory(),new Date().toString() +".jpg");//cun chu pai zhao de zhao pian
                imagePathTemp = outPutImage.getPath();
                Log.d("onClick", "in onClick pathImage = " + imagePathTemp);
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
    }

    /**
     * 新建招聘
     * **/
    private void addRecruit(){
        OkhttpUtil.addRecruit(handler,companyname,link,phone,requirement,address,list_image_path);
    }

    /**
     * 处理新建招聘
     * **/
    private void handleAddRecruit(Message msg){
       String  result = msg.obj.toString();
       Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if (!root.success){
            if (root.message.equals("未登录")){
                OkhttpUtil.autoLogin(handler);
                return;
            }
            new Dialog(this,"提示",root.message).show();
            return ;
        }

        closeProgressDialog();

        Intent intent = new Intent(this,AddPostActivity.class);
        intent.putExtra("recruit_id", root.recruit_id);
        startActivity(intent);

        Intent broadIntent = new Intent("com.allever.social.updateMyRecruitList");
        sendBroadcast(broadIntent);
        this.finish();

    }

    private void initData(){
        btn_submit = (ButtonFlat)this.findViewById(R.id.id_add_recruit_activity_btn_submit);
        btn_submit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_submit.setOnClickListener(this);

        btn_take_photo = (ButtonFlat)this.findViewById(R.id.id_add_recruit_activity_btn_take_photo);
        btn_take_photo.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn_take_photo.setOnClickListener(this);

        gridView1 = (GridView) findViewById(R.id.gridView1);

        list_image_path = new ArrayList<>();

        et_phone = (MaterialEditText)this.findViewById(R.id.id_add_recruit_activity_et_phone);
        et_companyname = (MaterialEditText)this.findViewById(R.id.id_add_recruit_activity_et_companyname);
        et_link = (MaterialEditText)this.findViewById(R.id.id_add_recruit_activity_et_link);
        et_requitement = (MaterialEditText)this.findViewById(R.id.id_add_recruit_activity_et_requirement);
        et_address = (MaterialEditText)this.findViewById(R.id.id_add_recruit_activity_et_address);
        et_address.setText(SharedPreferenceUtil.getAddress());
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

    //华丽分割线2--------------------------------------------------------------------------------
    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                list_image_path.add(pathImage);//保存图片地址
                if(list_image_path.size()==6){
                    Toast.makeText(AddRecruitActivity.this,"图片已满" , Toast.LENGTH_SHORT).show();
                }
            }
        }  //end if 打开图片

        if(requestCode==TAKE_PHOTO && resultCode == RESULT_OK){

            pathImage = imagePathTemp;
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

            list_image_path.add(pathImage);//保存图片地址
            if(list_image_path.size()==6){
                Toast.makeText(AddRecruitActivity.this,"图片已满" , Toast.LENGTH_SHORT).show();
            }
        }//end if

    }



    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//统计activity页面
        if(!TextUtils.isEmpty(pathImage)){

            imageItem.ensureCapacity(imageItem.size()+1);

            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(gridviewClickItemPosition, map);

            simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[] { "itemImage"}, new int[] { R.id.imageView1});
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
            //return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecruitActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
                list_image_path.remove(position);
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


    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在发布");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null) progressDialog.dismiss();
    }

    class Root{
        boolean success;
        String message;
        String recruit_id;
    }


}
