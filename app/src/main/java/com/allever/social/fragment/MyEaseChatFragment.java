package com.allever.social.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.ChatActivity;
import com.allever.social.activity.ChooseForwardUserActivity;
import com.allever.social.activity.GroupDataActivity;
import com.allever.social.activity.ShareDialogActivity;
import com.allever.social.activity.UserDataDetailActivity;
import com.allever.social.activity.VoiceeCallActivity;
import com.allever.social.ease.QQFaceGroupData;
import com.allever.social.emdemo.Constant;
import com.allever.social.emdemo.ContextMenuActivity;
import com.allever.social.network.util.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.hyphenate.easeui.ui.EaseGroupRemoveListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.util.List;

/**
 * Created by XM on 2016/5/4.
 */
public class MyEaseChatFragment extends EaseBaseFragment{
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;


    private static final int REQUEST_CODE_SHARE_DIALOG_ACTIVITY = 1000;
    private static final int REQUEST_CODE_CONTEXT_MENU = 1001;

    //red packet code : 红包功能使用的常量
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    private static final int REQUEST_CODE_SEND_RED_PACKET = 15;
    private static final int ITEM_RED_PACKET = 16;


    /**
     * 传入fragment的参数
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUsername;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;
    protected String hx_group_id;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    private String forward_msg_id;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected EMMessage contextMenuMessage;

    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;
    static final int ITEM_VOICE_CALL = 4;

    protected int[] itemStrings = { com.hyphenate.easeui.R.string.attach_take_pic, com.hyphenate.easeui.R.string.attach_picture, com.hyphenate.easeui.R.string.attach_location };
    protected int[] itemdrawables = { com.hyphenate.easeui.R.drawable.ease_chat_takepic_selector, com.hyphenate.easeui.R.drawable.ease_chat_image_selector,com.hyphenate.easeui.R.drawable.ease_chat_location_selector };
    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
    private EMChatRoomChangeListener chatRoomChangeListener;
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    //private SocialDBAdapter db;
    private EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            Log.d("MyEaseChatFragmetn", "新消息！！！！！！！！！");
            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }

                // 如果是当前会话的消息，刷新聊天页面
                Log.d("MyEaseChatFragment", "username = "  + username + "\n" + "tochatUser = "+ toChatUsername);
                if (username.equals(toChatUsername)) {
                    messageList.refreshSelectLast();
                } else {
                    // 如果消息不是和当前聊天ID的消息
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                    builder.setTicker("新消息");
                    builder.setContentTitle(SharedPreferenceUtil.getUserNickname(message.getUserName()));
                    builder.setContentText("发来消息，请查看");
                    builder.setSmallIcon(R.mipmap.logo);
                    builder.setLights(0xff00ff00, 300, 1000);
                    //默认系统设置
                    builder.setDefaults(NotificationCompat.DEFAULT_ALL);

                    //builder.setContentInfo("This is content info");
                    builder.setAutoCancel(true);
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("friend_id", messages.get(0).getUserName());
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(4, builder.build());

                    //EaseUI.getInstance().getNotifier().viberateAndPlayTone(messages.get(0));
                    //应用在后台，通知栏提示新消息
                    if(!EaseUI.getInstance().hasForegroundActivies()){
                    }

                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            Toast.makeText(getActivity(),"收到穿透消息",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            if(isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            if(isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if(isMessageListInited) {
                messageList.refresh();
            }
        }
    };
    private int messageCount;
    private int isVip;
    private Handler this_handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {;
        this_handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OkhttpUtil.MESSAGE_GET_MESSAGE_COUNT:
                        handleGetMessageCount(msg);
                        break;
                    case OkhttpUtil.MESSAGE_UPDATE_MESSAGE_COUNT:
                        handleUpdateMessageCount(msg);
                        break;
                }
            }
        };
        //getMessageCount();//获取剩余发送条数
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    private void getMessageCount(){
        //OkhttpUtil.getMessageCount(this_handler);
    }

    private void handleGetMessageCount(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        MessageCountRoot root = gson.fromJson(result, MessageCountRoot.class);

        if (root == null){
            //new Dialog(this,"错误","链接服务器失败").show();
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }

        Log.d("MessageCount", result);
        messageCount = root.messageCount;
        isVip = root.isVip;

    }

    private void handleUpdateMessageCount(Message msg){
        String result = msg.obj.toString();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        MessageCountRoot root = gson.fromJson(result, MessageCountRoot.class);

        if (root == null){
            Toast.makeText(getActivity(), "服务器繁忙，请重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (!root.success){
            new Dialog(getActivity(),"Tips",root.message).show();
            return;
        }

        messageCount = root.messageCount;
    }


    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);//统计Fragment页面
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        fragmentArgs = getArguments();
        // 判断单聊还是群聊
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // 会话人或群组id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);

        hx_group_id = fragmentArgs.getString("hx_group_id");

        //forward_msg_id = fragmentArgs.getString("forward_msg_id");
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * init view
     */
    protected void initView() {

        //super.hideTitleBar();
        // 按住说话录音控件
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);

        // 消息列表layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        messageList.init(toChatUsername, chatType, new CustomChatRowProvider());
        if(chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        // 发送语音消息
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                //发送大表情(动态表情)
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * 设置属性，监听等
     */
    protected void setUpView() {
        titleBar.setTitle(toChatUsername);
        titleBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (chatType == EaseConstant.CHATTYPE_SINGLE) { // 单聊
            // 设置标题
            if(EaseUserUtils.getUserInfo(toChatUsername) != null){
                titleBar.setTitle(EaseUserUtils.getUserInfo(toChatUsername).getNick());
            }
            titleBar.setRightImageResource(com.hyphenate.easeui.R.drawable.ic_delete_white_24dp);
        } else {//群聊
            titleBar.setRightImageResource(com.hyphenate.easeui.R.drawable.ease_to_group_details_normal);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                // 群聊
                EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
                if (group != null)
                    titleBar.setTitle(group.getGroupName());
                // 监听当前会话的群聊解散被T事件
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
                onChatRoomViewCreation();
            }

        }
        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        // 设置标题栏点击事件
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    emptyHistory();
                } else {
                    Log.d("MyEaseChatFragment", "即将进入群组信息界面");
                    //-------
                    EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
                    Intent intent = new Intent(getActivity(), GroupDataActivity.class);
                    String group_id;
                    if (group != null)
                        group_id = SharedPreferenceUtil.getGroupid(group.getGroupId());
                    else group_id = SharedPreferenceUtil.getGroupid(hx_group_id);

                    intent.putExtra("group_id", group_id);
                    startActivity(intent);
                    //-------
                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();

        String forward_msg_id = fragmentArgs.getString("forward_msg_id");
        if (forward_msg_id != null) {
            // 发送要转发的消息
            forwardMessage(forward_msg_id);
            getActivity().finish();
        }


        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(QQFaceGroupData.getData());
    }

    /**
     * 注册底部菜单扩展栏item; 覆盖此方法时如果不覆盖已有item，item的id需大于3
     */
    protected void registerExtendMenuItem(){
        for(int i = 0; i < itemStrings.length; i++){
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }

        if(chatType == Constant.CHATTYPE_SINGLE){
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.mipmap.em_chat_voice_call_normal, ITEM_VOICE_CALL, extendMenuItemClickListener);
            //inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }
    }


    protected void onConversationInit(){
        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    protected void onMessageListInit(){
        messageList.init(toChatUsername,chatType,new CustomChatRowProvider());
        //设置list item里的控件的点击事件
        setListItemClickListener();
        messageList.getListView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }



    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {

                //点击头像
                Intent intent = new Intent(getActivity(), UserDataDetailActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                if(chatFragmentListener != null){
                    chatFragmentListener.onAvatarClick(username);
                }
            }

            @Override
            public void onResendClick(final EMMessage message) {
                new EaseAlertDialog(getActivity(), com.hyphenate.easeui.R.string.resend, com.hyphenate.easeui.R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        resendMessage(message);
                    }
                }, true).show();
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                //Toast.makeText(getActivity(),"消息框长按时间",Toast.LENGTH_LONG).show();
                startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message), REQUEST_CODE_CONTEXT_MENU);
                if(chatFragmentListener != null){
                    chatFragmentListener.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if(chatFragmentListener != null){
                    return chatFragmentListener.onMessageBubbleClick(message);
                }
                return false;
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.unable_to_get_loaction, 0).show();
                }

            } else if(requestCode == REQUEST_CODE_SHARE_DIALOG_ACTIVITY){
                isVip = 1;
                messageCount = 10;
            }else if(requestCode == REQUEST_CODE_SEND_RED_PACKET){
            }
        }


        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
                    clipboard.setText(((EMTextMessageBody) contextMenuMessage.getBody()).getMessage());
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // 转发消息
                    Toast.makeText(getActivity(),"转发",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), ChooseForwardUserActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    break;
                case ContextMenuActivity.RESULT_CODE_WITHDRAW:
                    //Toast.makeText(getActivity(),"撤回",Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(),"msgId = " + contextMenuMessage.getMsgId(),Toast.LENGTH_LONG).show();
                    withdrawMsg();
                    break;

                default:
                    break;
            }
        }
    }

    private void addRedPocketGroupCount(){
        OkhttpUtil.addRedPocketGroupCount(this_handler,hx_group_id);
    }

    private void withdrawMsg(){
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);//统计Fragment页面
        if(isMessageListInited)
            messageList.refresh();
       EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        Log.d("MyEaseChatFragment", "onResume ->MessageListener do  work!!!!");
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);

        Log.d("MyEaseChatFragment", "onStop ->MessageListener do not  work!!!!");

        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        db.close();
        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }
        if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
        }

        if(chatRoomChangeListener != null){
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }
    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                        if (room != null) {
                            titleBar.setTitle(room.getName());
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                        EMLog.d(TAG, "join room success : " + room.getName());
                        addChatRoomChangeListenr();
                        onConversationInit();
                        onMessageListInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }


    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    showChatroomToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showChatroomToast("member : " + participant + " join the room : " + roomId);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
            }

            @Override
            public void onMemberKicked(String roomId, String roomName, String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
                        getActivity().finish();
                    }else{
                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                    }
                }
            }

        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    protected void showChatroomToast(final String toastContent){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 扩展菜单栏item点击事件
     *
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener{

        @Override
        public void onClick(int itemId, View view) {
            if(chatFragmentListener != null){
                if(chatFragmentListener.onExtendMenuItemClick(itemId, view)){
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE: // 拍照
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal(); // 图库选择图片
                    break;
                case ITEM_LOCATION: // 位置
                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;
                case ITEM_VOICE_CALL: //音频通话
                    startVoiceCall();
                    break;
                case ITEM_RED_PACKET:
                    break;

                default:
                    break;
            }
        }

    }


    //发送消息方法
    //==========================================================================
    protected void sendTextMessage(String content) {
        System.out.println(content);
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        sendMessage(message);
    }

    protected void sendBigExpressionMessage(String name, String identityCode){
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message){
        if (message == null) {
            return;
        }
        if(chatFragmentListener != null){
            //设置扩展属性
            chatFragmentListener.onSetMessageAttributes(message);
        }
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == EaseConstant.CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        //刷新ui
        if(isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }


    public void resendMessage(EMMessage message){
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageList.refresh();
    }

    //===================================================================================


    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * 根据uri发送文件
     * @param uri
     */
    protected void sendFileByUri(Uri uri){
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.File_does_not_exist, 0).show();
            return;
        }
        //大于10M不让发送
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.The_file_is_not_greater_than_10_m, 0).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * 照相获取图片
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isExitsSdcard()) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.sd_card_does_not_exist, 0).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * 拨打语音电话
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, 0).show();
        } else {
            Intent intent = new Intent(getActivity(),VoiceeCallActivity.class);
            intent.putExtra("username", toChatUsername);
            intent.putExtra("isComingCall", false);
            startActivity(intent);
            inputMenu.hideExtendMenuContainer();
        }
    }



    /**
     * 点击清空聊天记录
     *
     */
    protected void emptyHistory() {
        String msg = getResources().getString(com.hyphenate.easeui.R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(),null, msg, null,new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if(confirmed){
                    // 清空会话

                    EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);
                    messageList.refresh();
                }
            }
        }, true).show();;
    }

    /**
     * 点击进入群组详情
     *
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                return;
            }
            if(chatFragmentListener != null){
                chatFragmentListener.onEnterToChatDetails();
            }
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            if(chatFragmentListener != null){
                chatFragmentListener.onEnterToChatDetails();
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 转发消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if(forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                }else{
                    // 获取消息内容，发送消息
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // 发送图片
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 不存在大图发送缩略图
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if(forward_msg.getChatType() == EMMessage.ChatType.ChatRoom){
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * 监测群组解散或者被T事件
     *
     */
    class GroupListener extends EaseGroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.you_are_group, 1).show();
                        getActivity().finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.the_current_group, 1).show();
                        getActivity().finish();
                    }
                }
            });
        }

    }


    protected EaseChatFragmentListener chatFragmentListener;
    public void setChatFragmentListener(EaseChatFragmentListener chatFragmentListener){
        this.chatFragmentListener = chatFragmentListener;
    }

    public interface EaseChatFragmentListener{
        /**
         * 设置消息扩展属性
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * 进入会话详情
         */
        void onEnterToChatDetails();

        /**
         * 用户头像点击事件
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * 消息气泡框点击事件
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * 消息气泡框长按事件
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * 扩展输入栏item点击事件,如果要覆盖EaseChatFragment已有的点击事件，return true
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * 设置自定义chatrow提供者
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

    private void showVipDialog(){
        Dialog dialog = new Dialog(getActivity(),"提示","邀请好友免费开通VIP会员");
        dialog.setCancelable(false);
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出分享对话框
                //Toast.makeText(getActivity(), "弹出分享对话框", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), ShareDialogActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SHARE_DIALOG_ACTIVITY);
            }
        });
        dialog.show();
    }

    class MessageCountRoot{
        boolean success;
        String message;
        int messageCount;
        int isVip;
    }

    private final class CustomChatRowProvider implements EaseCustomChatRowProvider{
        @Override
        public int getCustomChatRowTypeCount() {
            return 8;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
        return null;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if(message.getType() == EMMessage.Type.TXT){
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
            }
            return 0;
        }
    }

}
