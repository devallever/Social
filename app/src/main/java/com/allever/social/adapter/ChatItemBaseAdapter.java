package com.allever.social.adapter;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allever.social.MyApplication;
import com.allever.social.R;
import com.allever.social.utils.SharedPreferenceUtil;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

/**
 * Created by XM on 2016/5/3.
 * 没用到
 */
public class ChatItemBaseAdapter extends BaseAdapter {
    private EMConversation conversation;
    private TextView textView;
    private ImageView iv_head;
    private String frined_id;
    private String friend_head_path;
    private Context context;

    public ChatItemBaseAdapter(Context context,EMConversation conversation, String friend_id, String friend_head_path){
        this.friend_head_path = friend_head_path;
        this.frined_id = friend_id;
        this.context = context;
        this.conversation = conversation;
    }

    @Override
    public int getCount() {
        return conversation.getAllMessages().size();
    }

    @Override
    public Object getItem(int i) {
        return conversation.getAllMessages().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        EMMessage emMessage = conversation.getAllMessages().get(i);
        EMMessageBody messageBody = emMessage.getBody();
        if (emMessage.direct() == EMMessage.Direct.RECEIVE){
            if (emMessage.getType() == EMMessage.Type.TXT){
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_receive, null);
                textView = (TextView)view.findViewById(R.id.id_chat_item_receive_text);
                textView.setText(messageBody.toString());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                        .into(iv_head);
                //Picasso.with(context).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath()).into(iv_head);
            }
        }else{
            if (emMessage.getType() == EMMessage.Type.TXT){
                view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.chat_item_send,null);
                textView = (TextView)view.findViewById(R.id.id_chat_item_send_head);
                textView.setText(messageBody.toString());
                Glide.with(context)
                        .load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath())
                        .into(iv_head);
                //Picasso.with(MyApplication.getContext()).load(WebUtil.HTTP_ADDRESS + SharedPreferenceUtil.getHeadpath()).into(iv_head);
            }
        }
        return view;
    }
}
