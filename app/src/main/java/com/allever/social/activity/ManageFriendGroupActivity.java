package com.allever.social.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.FriendGroupNameBaseAdapter;
import com.allever.social.pojo.FriendGroupNameItem;
import com.allever.social.utils.OkhttpUtil;
import com.andexert.library.RippleView;
import com.baidu.mobstat.StatService;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.conn.scheme.HostNameResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/6/15.
 * 分组管理界面
 */
public class ManageFriendGroupActivity extends BaseActivity implements SwipeMenuListView.OnMenuItemClickListener {

    private final static int REQUEST_CODE_ADD_FRIEND_GROUP = 1000;
    private final static int REQUEST_CODE_MODIFY_FRIEND_GROUP = 1001;

   // private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isloading;

    private SwipeMenuListView listView;
    private SwipeMenuCreator creator;
    private FriendGroupNameBaseAdapter friendGroupNameBaseAdapter;
    private List<FriendGroupNameItem> list_friendgroupnameItem = new ArrayList<>();
    private Handler handler;

    private RippleView rv_add_friendgroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_friendgroup_activity_layout);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_FRIEND_GROUP_NAME_LIST:
                        handleGetFriendGroupNameList(msg);
                        break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("分组管理");

        rv_add_friendgroup = (RippleView)this.findViewById(R.id.id_manage_friendgroup_activity_rv_add_friendgroup);
        rv_add_friendgroup.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(ManageFriendGroupActivity.this,AddFriendGroupActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_FRIEND_GROUP);
            }
        });

        listView = (SwipeMenuListView)this.findViewById(R.id.id_manage_friendgroup_activity_swipmenuListview);

        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem modifyItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                modifyItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                modifyItem.setWidth(dp2px(90));
                // set item title
                modifyItem.setTitle("修改");
                // set item title fontsize
                modifyItem.setTitleSize(18);
                // set item title font color
                modifyItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(modifyItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        //listView.setMenuCreator(creator);
       listView.setOnMenuItemClickListener(this);


        getFriendGroupNameList();
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
            case REQUEST_CODE_ADD_FRIEND_GROUP:
                if (resultCode == RESULT_OK) getFriendGroupNameList();
                break;
            case REQUEST_CODE_MODIFY_FRIEND_GROUP:
                if (resultCode == RESULT_OK) getFriendGroupNameList();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        switch (index){
            case 0:
                Intent intent = new Intent(this, ModifyFriendGroupActivity.class);
                intent.putExtra("old_friendgroup_name",list_friendgroupnameItem.get(position).getFriendgroup_name());
                intent.putExtra("friendgroup_id",list_friendgroupnameItem.get(position).getId());
                startActivityForResult(intent,REQUEST_CODE_MODIFY_FRIEND_GROUP);
                //Toast.makeText(this,"修改" + "id = " + list_friendgroupnameItem.get(position).getId(),Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this,"删除" +list_friendgroupnameItem.get(position).getFriendgroup_name(),Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    private void getFriendGroupNameList(){
        OkhttpUtil.getFriendGroupNameList(handler);
    }

    private void handleGetFriendGroupNameList(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(this,"服务器繁忙，请重试",Toast.LENGTH_LONG).show();
            return ;
        }

        if (!root.success){
            new Dialog(this,"Tips",root.message).show();
            return;
        }

        list_friendgroupnameItem.clear();
        FriendGroupNameItem friendGroupNameItem;
        for(FriendGroup friendGroup : root.list_friendgroup){
            friendGroupNameItem = new FriendGroupNameItem();
            friendGroupNameItem.setId(friendGroup.id);
            friendGroupNameItem.setFriendgroup_name(friendGroup.friendgroup_name);
            list_friendgroupnameItem.add(friendGroupNameItem);
        }

        friendGroupNameBaseAdapter = new FriendGroupNameBaseAdapter(this,list_friendgroupnameItem);
        listView.setAdapter(friendGroupNameBaseAdapter);
        listView.setMenuCreator(creator);
        System.out.print("");
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    class Root{
        boolean success;
        String message;
        List<FriendGroup> list_friendgroup;
    }

    class FriendGroup{
        String id;
        String friendgroup_name;
    }
}
