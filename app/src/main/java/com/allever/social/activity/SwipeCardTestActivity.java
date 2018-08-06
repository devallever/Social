package com.allever.social.activity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.allever.social.BaseActivity;
import com.allever.social.R;
import com.allever.social.adapter.SwipeCardItemBaseAdapter;
import com.allever.social.pojo.SwipeCardItem;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.lorentzos.swipecards.CardMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XM on 2016/10/14.
 */
public class SwipeCardTestActivity extends BaseActivity implements View.OnClickListener{
    private SwipeCardItemBaseAdapter swipeCardItemBaseAdapter;
    private List<SwipeCardItem> list_swipe_card_items;
    private SwipeFlingAdapterView swipeFlingAdapterView;
    private ImageView iv_like;
    private ImageView iv_dislike;
    private ImageView iv_user_info;

    private SwipeCardItem swipeCardItem_old;
    private SwipeCardItem swipeCardItem_present;

    private int i;
    private SwipeCardItem swipeCardItem_1;
    private SwipeCardItem swipeCardItem_2;
    private SwipeCardItem swipeCardItem_3;
    private SwipeCardItem swipeCardItem_4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_card_test_activity_layout);

        initView();
    }

    private void initView(){

        iv_like = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_right);
        iv_dislike = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_left);
        iv_user_info = (ImageView)this.findViewById(R.id.id_swipe_card_button_iv_info);


        swipeFlingAdapterView = (SwipeFlingAdapterView)this.findViewById(R.id.swipe_card_test_activity_swipe_fling_adapter_view);
        list_swipe_card_items = new ArrayList<>();

        final List<String> list_images = new ArrayList<>();
        list_images.add("/images/head/xm.jpg");
        list_images.add("/images/head/baobao.jpg");
        list_images.add("/images/head/xsx.jpg");
        list_images.add("/images/head/meimei.jpg");

        swipeCardItem_1 = new SwipeCardItem();
        swipeCardItem_1.setUsername("xm");
        swipeCardItem_1.setNickname("XM");
        swipeCardItem_1.setSex("女");
        swipeCardItem_1.setAge(21);
        swipeCardItem_1.setDistance("1.0");
        swipeCardItem_1.setSignature("没个性，不签名");
        swipeCardItem_1.setList_imgs(list_images);


        swipeCardItem_2 = new SwipeCardItem();
        swipeCardItem_2.setUsername("baobao");
        swipeCardItem_2.setNickname("Light and Heart");
        swipeCardItem_2.setSex("女");
        swipeCardItem_2.setAge(23);
        swipeCardItem_2.setDistance("3.0");
        swipeCardItem_2.setSignature("我是第二个卡片");
        swipeCardItem_2.setList_imgs(list_images);

        swipeCardItem_3 = new SwipeCardItem();
        swipeCardItem_3.setUsername("xsx");
        swipeCardItem_3.setNickname("淑得起，要开欣");
        swipeCardItem_3.setSex("女");
        swipeCardItem_3.setAge(21);
        swipeCardItem_3.setDistance("2.0");
        swipeCardItem_3.setSignature("我是第3个卡片");
        swipeCardItem_3.setList_imgs(list_images);


        swipeCardItem_4 = new SwipeCardItem();
        swipeCardItem_4.setUsername("meimei");
        swipeCardItem_4.setNickname("Lois");
        swipeCardItem_4.setSex("女");
        swipeCardItem_4.setAge(20);
        swipeCardItem_4.setDistance("4.0");
        swipeCardItem_4.setSignature("我是第四个卡片");
        swipeCardItem_4.setList_imgs(list_images);

        list_swipe_card_items.add(swipeCardItem_1);
        list_swipe_card_items.add(swipeCardItem_2);
        list_swipe_card_items.add(swipeCardItem_3);
        list_swipe_card_items.add(swipeCardItem_4);

        swipeCardItemBaseAdapter = new SwipeCardItemBaseAdapter(this,list_swipe_card_items);
        swipeFlingAdapterView.setAdapter(swipeCardItemBaseAdapter);

        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                swipeCardItem_old = list_swipe_card_items.get(0);
                list_swipe_card_items.remove(0);
                swipeCardItemBaseAdapter.notifyDataSetChanged();
                swipeCardItem_present = list_swipe_card_items.get(0);
                //Toast.makeText(SwipeCardTestActivity.this,"remove " + swipeCardItem_old.getNickname() + "\nThis is" + swipeCardItem_present.getNickname(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                makeToast(SwipeCardTestActivity.this, "不喜欢" + swipeCardItem_old.getNickname());
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                makeToast(SwipeCardTestActivity.this, "喜欢" + swipeCardItem_old.getNickname());
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                list_swipe_card_items.add(swipeCardItem_1);
                list_swipe_card_items.add(swipeCardItem_2);
                list_swipe_card_items.add(swipeCardItem_3);
                list_swipe_card_items.add(swipeCardItem_4);
                swipeCardItemBaseAdapter.notifyDataSetChanged();
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                try {
                    View view = swipeFlingAdapterView.getSelectedView();
                    view.findViewById(R.id.id_swipe_card_item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.id_swipe_card_item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_like.setOnClickListener(this);
        iv_dislike.setOnClickListener(this);
        iv_user_info.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.id_swipe_card_button_iv_right:
                right();
                break;
            case R.id.id_swipe_card_button_iv_left:
                left();
                break;
            case R.id.id_swipe_card_button_iv_info:
                break;
        }
    }

    static void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private void right() {
        swipeFlingAdapterView.getTopCardListener().selectRight();
    }

    private void left() {
        swipeFlingAdapterView.getTopCardListener().selectLeft();
    }
}
