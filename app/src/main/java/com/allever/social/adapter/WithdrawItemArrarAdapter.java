package com.allever.social.adapter;

import android.app.WallpaperInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allever.social.R;
import com.allever.social.pojo.WithdrawItem;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.andexert.library.RippleView;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

/**
 * Created by XM on 2016/6/13.
 */
public class WithdrawItemArrarAdapter extends ArrayAdapter<WithdrawItem> {
    private Context context;
    private int res_id;
    private List<WithdrawItem> list_withdrawitem;
    private Handler handler;

    public WithdrawItemArrarAdapter(Context context, int item_resid, List<WithdrawItem> list_item){
        super(context,item_resid,list_item);
        this.context =context;
        this.res_id = item_resid;
        this.list_withdrawitem = list_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final WithdrawItem withdrawItem = list_withdrawitem.get(position);
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(res_id, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_date  = (TextView)view.findViewById(R.id.id_withdraw_item_tv_date);
            viewHolder.tv_money = (TextView)view.findViewById(R.id.id_withdraw_item_tv_money);
            viewHolder.tv_state = (TextView)view.findViewById(R.id.id_withdraw_item_tv_state);
            viewHolder.tv_account = (TextView)view.findViewById(R.id.id_withdraw_item_tv_account);
            viewHolder.rv_cancle = (RippleView)view.findViewById(R.id.id_withdraw_item_rv_cancle);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        handler = new Handler();
//        handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what){
//
//                }
//            }
//        };

        if (withdrawItem.getState().equals("-1")) viewHolder.tv_state.setText("已取消");
        if (withdrawItem.getState().equals("0")) viewHolder.tv_state.setText("处理中");
        if (withdrawItem.getState().equals("1")) viewHolder.tv_state.setText("已到账");

        if (withdrawItem.getState().equals("0")) viewHolder.rv_cancle.setVisibility(View.VISIBLE);
        else viewHolder.rv_cancle.setVisibility(View.INVISIBLE);

        viewHolder.tv_date.setText(withdrawItem.getDate());
        viewHolder.tv_money.setText(withdrawItem.getMoney());
        viewHolder.tv_account.setText(withdrawItem.getAccount());

        viewHolder.rv_cancle.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                //TODO SOMETHING
                OkHttpClient okHttpClient = new OkHttpClient();
                // okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(MyApplication.getContext()),CookiePolicy.ACCEPT_ALL));
                RequestBody formBody = new FormEncodingBuilder()
                        .add("user_id", SharedPreferenceUtil.getUserId())
                        .add("withdraw_id", withdrawItem.getId())
                        .build();
                Request request = new Request.Builder()
                        .url(WebUtil.HTTP_ADDRESS + "/CancleWithdrawServlet")
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
                               // new Dialog(context,"错误","链接服务器失败").show();
                                return ;
                            }

                            if (!root.success){
                               // new Dialog(context,"Tips",root.message).show();
                                return;
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.rv_cancle.setVisibility(View.INVISIBLE);
                                    viewHolder.tv_state.setText("已取消");
                                }
                            });


//                            Message message = new Message();
//                            message.what = MESSAGE_GET_WITHDRAW_LOG;
//                            message.obj = result;
//                            handler.sendMessage(message);
//                            System.out.println(result);
                        }
                    }
                });



            }
        });

        return view;
    }

    class ViewHolder{
        TextView tv_money;
        TextView tv_date;
        TextView tv_state;
        RippleView rv_cancle;
        TextView tv_account;
    }


    class Root{
        boolean success;
        String message;
    }
}
