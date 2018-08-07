package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.ShowBigImageActvity;
import com.allever.social.activity.VideoCallActivity;
import com.allever.social.pojo.NearByUserItem;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/9.
 * 附近用户适配器
 */
public class NearbyUserItemAdapter extends ArrayAdapter<NearByUserItem> {
    private Context context;
    private int nearbyUserItemResId;
    private Handler handler;

    public NearbyUserItemAdapter(Context context, int nearbyUserItemResId, List<NearByUserItem> nearByUserItemList){
        super(context,nearbyUserItemResId,nearByUserItemList);
        this.context = context;
        this.nearbyUserItemResId = nearbyUserItemResId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NearByUserItem nearByUserItem = (NearByUserItem)getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(nearbyUserItemResId, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_near_by_user_item_circle_iv_userhead);
            viewHolder.iv_vip = (ImageView)view.findViewById(R.id.id_near_by_user_item_circle_iv_vip_logo);
            viewHolder.tv_nickname = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_nickname);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_distance);
            viewHolder.ll_sex = (LinearLayout)view.findViewById(R.id.id_near_by_user_item_ll_sex);
            viewHolder.tv_sex = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_sex);
            viewHolder.tv_age = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_age);
            viewHolder.tv_signature = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_signature);
            viewHolder.tv_occupation = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_occupation);
            viewHolder.tv_constellation = (TextView)view.findViewById(R.id.id_near_by_user_item_tv_constellation);
            viewHolder.ll_occupation = (LinearLayout)view.findViewById(R.id.id_near_by_user_item_ll_occupation);
            viewHolder.ll_constellation = (LinearLayout)view.findViewById(R.id.id_near_by_user_item_ll_constellation);
            viewHolder.rv_videocall = (RippleView)view.findViewById(R.id.id_near_by_user_item_rv_videocall);



            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_CREDIT:
                        handleGetCredit(msg);
                        break;
                    case 1000:
                        new Dialog(context,"提示","您的信用不足，请充值。").show();
                        break;
                }
            }
        };

        viewHolder.tv_nickname.setText(nearByUserItem.getNickname());
        if(nearByUserItem.getIs_vip()==1){
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.colorRed_500));
            viewHolder.iv_vip.setVisibility(View.VISIBLE);
        } else{
            viewHolder.tv_nickname.setTextColor(context.getResources().getColor(R.color.black_deep));
            viewHolder.iv_vip.setVisibility(View.GONE);
        }
        viewHolder.tv_distance.setText(nearByUserItem.getDistance()+" km");
        viewHolder.tv_sex.setText(nearByUserItem.getSex());
        viewHolder.tv_age.setText(nearByUserItem.getAge()+"");
        viewHolder.tv_signature.setText(nearByUserItem.getSignature());

        viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, UserDataActivity.class);
//                intent.putExtra("friend_id", nearByUserItem.getUsername());
//                context.startActivity(intent);
                Intent intent;
                intent = new Intent(context,ShowBigImageActvity.class);
                intent.putExtra("image_path",WebUtil.HTTP_ADDRESS + nearByUserItem.getUser_head_path());
                context.startActivity(intent);
            }
        });

        if (nearByUserItem.getSex().equals("男")){
            viewHolder.ll_sex.setBackgroundResource(R.drawable.color_indigo_bg_round);
        }else{
            viewHolder.ll_sex.setBackgroundResource(R.drawable.color_pink_bg_round);
        }

        if (nearByUserItem.getAccept_video()==1){
            viewHolder.rv_videocall.setVisibility(View.VISIBLE);
            viewHolder.tv_signature.getLayoutParams().width = view.getWidth() - viewHolder.iv_head.getWidth()-viewHolder.rv_videocall.getWidth();
        }else{
            viewHolder.rv_videocall.setVisibility(View.INVISIBLE);
            viewHolder.tv_signature.getLayoutParams().width = view.getWidth() - viewHolder.iv_head.getWidth();
        }

        viewHolder.rv_videocall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
//                Toast.makeText(context,"VideoCallActivity",Toast.LENGTH_LONG).show();
//                try {
//                    EMClient.getInstance().callManager().makeVideoCall(nearByUserItem.getUsername());
//                } catch (EMServiceNotReadyException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                if (!EMClient.getInstance().isConnected())
                    Toast.makeText(context, R.string.not_connect_to_server, Toast.LENGTH_LONG).show();
                else {
                    final Dialog dialog = new Dialog(context,"提示","发起视频聊天\n需要向 " + nearByUserItem.getNickname() + " \n支付 " + nearByUserItem.getVideo_fee() + " 信用/分钟");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            OkHttpClient okHttpClient = new OkHttpClient();
                            // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
                            RequestBody formBody = new FormEncodingBuilder()
                                    .add("user_id", SharedPreferenceUtil.getUserId())
                                    .build();
                            Request request = new Request.Builder()
                                    .url(WebUtil.HTTP_ADDRESS + "/GetCreditServlet")
                                    .post(formBody)
                                    .addHeader("Cookie", "JSESSIONID=" + SharedPreferenceUtil.getSessionId())
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    //NOT UI Thread
                                    if (response.isSuccessful()) {
                                        System.out.println(response.code());
                                        String result = response.body().string();
                                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                                        Root root = gson.fromJson(result, Root.class);

                                        if(root==null){
                                            //new Dialog(context,"错误","链接服务器失败").show();
                                            return ;
                                        }

                                        if (!root.success){
                                            //new Dialog(context,"Tips",root.message).show();
                                            return;
                                        }

                                        if (root.credit >= nearByUserItem.getVideo_fee()){
                                            context.startActivity(new Intent(context, VideoCallActivity.class).putExtra("username", nearByUserItem.getUsername())
                                                    .putExtra("isComingCall", false));
                                        }else{
                                            Message message = new Message();
                                            message.what = 1000;
                                            message.obj = result;
                                            message.arg1 = -1;
                                            handler.sendMessage(message);
                                            System.out.println(result);
                                        }

                                    }
                                }
                            });

                        }
                    });
                    dialog.show();
                }
            }
        });

        viewHolder.tv_occupation.setText(nearByUserItem.getOccupation());
        switch (nearByUserItem.getOccupation()){
            case "学生":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("学生");
                break;
            case "IT":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("IT");
                break;
            case "农业":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("保险");
                break;
            case "制造":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("制造");
                break;
            case "商业":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_blue_bg_round);
                //viewHolder.tv_occupation.setText("商务");
                break;
            case "模特":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("交通");
                break;
            case "文化":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_purple_bg_round);
                //viewHolder.tv_occupation.setText("传媒");
                break;
            case "教育":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("教育");
                break;
            case "医疗":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_pink_bg_round);
                //viewHolder.tv_occupation.setText("娱乐");
                break;
            case "艺术":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_green_bg_round);
                //viewHolder.tv_occupation.setText("公共");
                break;
            case "金融":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_orange_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "行政":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_brown_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "空姐":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_red_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
            case "法律":
                viewHolder.ll_occupation.setBackgroundResource(R.drawable.color_indigo_bg_round);
                //viewHolder.tv_occupation.setText("金融");
                break;
        }

//        switch (nearByUserItem.getConstellation()){
//            case "白羊座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorGray_300));
//                viewHolder.tv_constellation.setText("白羊");
//                break;
//            case "金牛座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("金牛");
//                break;
//            case "双子座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorRed_300));
//                viewHolder.tv_constellation.setText("双子");
//                break;
//            case "巨蟹座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("巨蟹");
//                break;
//            case "狮子座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("狮子");
//                break;
//            case "处女座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorPink_300));
//                viewHolder.tv_constellation.setText("处女");
//                break;
//            case "天秤座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorGreen_300));
//                viewHolder.tv_constellation.setText("天秤");
//                break;
//            case "天蝎座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorPurple_300));
//                viewHolder.tv_constellation.setText("天蝎");
//                break;
//            case "射手座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorBlue_300));
//                viewHolder.tv_constellation.setText("射手");
//                break;
//            case "魔蝎座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorIndigo_300));
//                viewHolder.tv_constellation.setText("魔蝎");
//                break;
//            case "水瓶座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorBlue_300));
//                viewHolder.tv_constellation.setText("水瓶");
//                break;
//            case "双鱼座":
//                viewHolder.ll_constellation.setBackgroundColor(context.getResources().getColor(R.color.colorOrange_300));
//                viewHolder.tv_constellation.setText("双鱼");
//                break;
//
//        }

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + nearByUserItem.getUser_head_path())
                .into(viewHolder.iv_head);
        //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + nearByUserItem.getUser_head_path()).into(viewHolder.iv_head);
        return view;
    }

    private void handleGetCredit(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Root root = gson.fromJson(result, Root.class);

        if(root==null){
            new Dialog(context,"错误","链接服务器失败").show();
            return ;
        }

        if (!root.success){
            new Dialog(context,"Tips",root.message).show();
            return;
        }

        if (root.credit > 0){

        }else{

        }


    }


    class Root{
        boolean success;
        String message;
        int credit;
    }

    private class ViewHolder{
        private CircleImageView iv_head;
        private ImageView iv_vip;
        private TextView tv_nickname;
        private TextView tv_distance;
        private LinearLayout ll_sex;
        private TextView tv_sex;
        private TextView tv_age;
        private TextView tv_signature;
        private TextView tv_occupation;
        private TextView tv_constellation;
        private LinearLayout ll_occupation;
        private LinearLayout ll_constellation;
        private RippleView rv_videocall;
    }
}
