package com.allever.social;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.allever.social.activity.ChatActivity;
import com.allever.social.activity.FriendRequestDialogActivity;
import com.allever.social.activity.GroupChatActivity;
import com.allever.social.activity.GroupRequestDialogActivity;

import com.allever.social.ease.EmojiconExampleGroupData;
import com.allever.social.ease.QQFaceGroupData;
import com.allever.social.receiver.CallReceiver;
import com.allever.social.service.LongConnectionService;
import com.allever.social.utils.CommentUtil;
import com.allever.social.utils.OkhttpUtil;
import com.allever.social.utils.SharedPreferenceUtil;
import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.NetUtils;

import java.util.List;
import java.util.Map;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by XM on 2016/4/15.
 */
public class MyApplication extends Application {
    private static  Context context;
    private CallReceiver callReceiver;
    public static Context mContext;

    private EaseUI easeUI;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mContext = this;


        //初始化百度定位
        SDKInitializer.initialize(context);

       // RedPacket.getInstance().initContext(applicationContext);W
        //打开Log开关 正式发布时请关闭
       // RedPacket.getInstance().setDebugMode(true);

        String processName = CommentUtil.getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName .equals("com.allever.social");
            if (!defaultProcess) {
                //必要的初始化资源操作
                return;
            }
        }

        Log.e("Application", processName);


        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);



        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
////初始化
//        EMClient.getInstance().init(context, options);
////在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        EMClient.getInstance().setDebugMode(true);
//
        //EMOptions options2 = new EMOptions();
        //貌似不需要同时初始化
        EaseUI.getInstance().init(this, options);

        //0.15.06红包相关-----------------------------------------------------------------------------------------
//        RedPacket.getInstance().initContext(mContext);
//        //打开Log开关 正式发布时请关闭
//        RedPacket.getInstance().setDebugMode(true);
        //0.15.06红包相关-----------------------------------------------------------------------------------------

        easeUI  = EaseUI.getInstance();
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });


        //设置表情
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {
            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = QQFaceGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });


        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return SharedPreferenceUtil.getUserHeadPath(message.getFrom());
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return R.mipmap.logo;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, getApplicationContext());
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                //return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                EMMessage.ChatType chatType = message.getChatType();
                Intent intent;
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                //有电话时优先跳转到通话页面
                //if(isVideoCalling){
                //    intent = new Intent(getApplicationContext(), VideoCallActivityyy.class);
                //}else if(isVoiceCalling){
                //    intent = new Intent(getApplicationContext(), VoiceCallActivityyy.class);
                // }else{

                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", EaseConstant.CHATTYPE_SINGLE);
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                    intent.putExtra("userId", message.getTo());
                    if (chatType == EMMessage.ChatType.GroupChat) {
                        intent.putExtra("chatType", EaseConstant.CHATTYPE_GROUP);
                    } else {
                        intent.putExtra("chatType", EaseConstant.CHATTYPE_CHATROOM);
                    }

                    // }
                }
                return intent;
            }
        });



        EMMessageListener msgListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                Intent broadIntent = new Intent("com.allever.social.refresh_conversationlist");
                sendBroadcast(broadIntent);
                //收到消息
                Log.d("NewsMessage", "新消息");


                //messages.get(0).get
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("新消息");
                builder.setContentTitle(SharedPreferenceUtil.getUserNickname(messages.get(0).getUserName()));
                //builder.setContentTitle(db.getNickName(messages.get(0).getUserName()));
                builder.setContentText("发来消息，请查看");
                builder.setSmallIcon(R.mipmap.logo);
                Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setLights(0xff00ff00, 300, 1000);
                //builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                //long[] viberate = {0,1000,1000,1000};//震动两次
                long[] viberate = {0,1000,0,0};
                builder.setVibrate(viberate);

                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);

                //判断是单聊还是群聊
                EMMessage.ChatType chatType = messages.get(0).getChatType();

                //为排行榜添加记录
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    OkhttpUtil.addRankRecord(messages.get(0).getFrom());
                    Log.d("AutoReaction", "进入前 Onlinestate = " + SharedPreferenceUtil.getOnlineState());
                    if (SharedPreferenceUtil.getOnlineState().equals("忙碌")|| SharedPreferenceUtil.getOnlineState().equals("离线")){
                        //自动回复  1分钟回复一次
                        if (SharedPreferenceUtil.getIsOffline().equals("0")){
                            //发送自动回复
                            SharedPreferenceUtil.setIsOffline("1");
                            Log.d("AutoReaction", "发送前");
                            EMMessage message = EMMessage.createTxtSendMessage("[自动回复]"+SharedPreferenceUtil.getAutoReaction(), messages.get(0).getFrom());
                            //sendMessage(message);
                            EMClient.getInstance().chatManager().sendMessage(message);
                            Log.d("AutoReaction", "发送后");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000*60);
                                        SharedPreferenceUtil.setIsOffline("0");
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }

                    }
                }

                Intent intent;
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent= new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("friend_id", messages.get(0).getFrom());
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent= new Intent(getApplicationContext(), GroupChatActivity.class);
                    intent.putExtra("hx_group_id", messages.get(0).getTo());
//                    if (chatType == EMMessage.ChatType.GroupChat) {
//                        intent.putExtra("chatType", EaseConstant.CHATTYPE_GROUP);
//                    } else {
//                        intent.putExtra("chatType", EaseConstant.CHATTYPE_CHATROOM);
//                    }

                    // }
                }

                //Intent intent = new Intent(getContext(), ChatActivity.class);
               // intent.putExtra("friend_id", messages.get(0).getUserName());
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.notify(4, builder.build());

                //EaseUI.getInstance().getNotifier().viberateAndPlayTone(messages.get(0));
                //应用在后台，通知栏提示新消息
                if(!EaseUI.getInstance().hasForegroundActivies()){
                    //聊天界面不显示播
                    notificationManager.notify(4, builder.build());
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
                Log.d("cmdMessage", "收到穿透消息");
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) messages.get(0).getBody();
                String action = cmdMsgBody.action();
                Log.d("cmdMessage", "action = " + action);
                if(action.equals("REVOKE_FLAG")){
                    try {
                        String msgId = messages.get(0).getMsgId();
                        for (int i=0;i<messages.size();i++){
                            Log.d("cmdMessage","msgId = " + messages.get(i).getMsgId());
                        }
                        Log.d("cmdMessage","msgId = " + msgId);
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(messages.get(0).getFrom());
                       // --删除消息来表示撤回--
                        conversation.removeMessage(msgId);
                        // 如果需要，可以插入一条“XXX回撤一条消息”
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        //监听连接状态
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }
            @Override
            public void onDisconnected(int error) {
                if(error == EMError.USER_REMOVED){
                    // 显示帐号已经被移除
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    Intent broadIntent  = new Intent("com.allever.social.USER_LOGIN_ANOTHER_DEVICE");
                    sendBroadcast(broadIntent);
                } else {
                    if (NetUtils.hasNetwork(mContext)){
                        //连接不到聊天服务器
                    }else{
                        //当前网络不可用，请检查网络设置
                    }

                }
            }
        });

        //监听好友事件
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                Log.d("MyApplication", "好友请求被同意");
                //messages.get(0).get
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("添加好友");
                builder.setContentTitle(SharedPreferenceUtil.getUserNickname(username));
                //builder.setContentTitle(db.getNickName(messages.get(0).getUserName()));
                builder.setContentText("接受了你的好友请求.");
                builder.setSmallIcon(R.mipmap.logo);
                Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setLights(0xff00ff00, 300, 1000);
                //builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);

                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);
                //Intent intent = new Intent(getContext(), FriendRequestDialogActivity.class);
                //intent.putExtra("friend_id", username);
                //intent.putExtra("reason",reason);
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(6, builder.build());
                //OkhttpUtil.addFriendWithoutHandler(username);
                Intent broadIntent = new Intent("com.allever.updateFriend");
                sendBroadcast(broadIntent);
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                Log.d("MyApplication", "好友请求被拒绝");
                //messages.get(0).get
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("添加好友");
                builder.setContentTitle(SharedPreferenceUtil.getUserNickname(username));
                //builder.setContentTitle(db.getNickName(messages.get(0).getUserName()));
                builder.setContentText("拒绝了你好友的请求.");
                builder.setSmallIcon(R.mipmap.logo);
                builder.setLights(0xff00ff00, 300, 1000);
                //builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);

                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);
                //Intent intent = new Intent(getContext(), FriendRequestDialogActivity.class);
                //intent.putExtra("friend_id", username);
                //intent.putExtra("reason",reason);
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(6, builder.build());
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请//通知栏显示，点击弹出对话框activity
                Log.d("MyApplication", "收到好友邀请:" + reason);
                //messages.get(0).get
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("好友请求");
                builder.setContentTitle("好友请求");
                //builder.setContentTitle(db.getNickName(messages.get(0).getUserName()));
                builder.setContentText(SharedPreferenceUtil.getUserNickname(username) + ": " + reason);
                builder.setSmallIcon(R.mipmap.logo);
                builder.setLights(0xff00ff00, 300, 1000);

                //builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);
                Intent intent = new Intent(getContext(), FriendRequestDialogActivity.class);
                intent.putExtra("friend_id", username);
                intent.putExtra("reason", reason);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(5, builder.build());

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                Log.d("MyApplication", "被删除时回调此方法");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("您被删除了");
                builder.setContentTitle(SharedPreferenceUtil.getUserNickname(username));
                builder.setContentText("将您从联系人列表中移除.");
                builder.setSmallIcon(R.mipmap.logo);
                builder.setLights(0xff00ff00, 300, 1000);
                //builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);

                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);
                //Intent intent = new Intent(getContext(), FriendRequestDialogActivity.class);
                //intent.putExtra("friend_id", username);
                //intent.putExtra("reason",reason);
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(7, builder.build());
                //OkhttpUtil.deleteFriendWithoutHandler(username);
                Intent broadIntent = new Intent("com.allever.updateFriend");
                sendBroadcast(broadIntent);
            }

            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.d("MyApplication", "增加了联系人时回调此方法");
            }
        });

        //监听群租事件
        EMClient.getInstance().groupManager().addGroupChangeListener(new EMGroupChangeListener() {
            @Override
            public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {
                Log.d("MyApplication", "onAutoAcceptInvitationFromGroup");
            }

            @Override
            public void onUserRemoved(String groupId, String groupName) {
                //当前用户被管理员移除出群组
                Log.d("MyApplication", "当前用户被管理员移除出群组");
            }
            @Override
            public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
                //收到加入群组的邀请
                Log.d("MyApplication", "收到加入群组的邀请");
            }
            @Override
            public void onInvitationDeclined(String groupId, String invitee, String reason) {
                //群组邀请被拒绝
                Log.d("MyApplication", "群组邀请被拒绝");
            }
            @Override
            public void onInvitationAccpted(String groupId, String inviter, String reason) {
                //群组邀请被接受
                Log.d("MyApplication", "群组邀请被接受");
            }
            @Override
            public void onGroupDestroy(String groupId, String groupName) {
                //群组被创建者解散
                Log.d("MyApplication", "群组被创建者解散");
            }
            @Override
            public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
                //收到加群申请
                Log.d("MyApplication", "收到加群申请");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setTicker("群组请求");
                builder.setContentTitle("群组请求");
                //builder.setContentTitle(db.getNickName(messages.get(0).getUserName()));
                builder.setContentText(SharedPreferenceUtil.getUserNickname(applyer) + ": " + reason);
                builder.setSmallIcon(R.mipmap.logo);
                builder.setLights(0xff00ff00, 300, 1000);
                //默认系统设置
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);

                //builder.setContentInfo("This is content info");
                builder.setAutoCancel(true);
                Intent intent = new Intent(getContext(), GroupRequestDialogActivity.class);
                intent.putExtra("hx_group_id", groupId);
                intent.putExtra("reason", reason);
                intent.putExtra("groupName",groupName);
                intent.putExtra("applyer", applyer);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(8, builder.build());
            }
            @Override
            public void onApplicationAccept(String groupId, String groupName, String accepter) {
                //加群申请被同意
                Log.d("MyApplication", "加群申请被同意");
            }
            @Override
            public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
                // 加群申请被拒绝
                Log.d("MyApplication", "加群申请被拒绝");
            }
        });

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        registerReceiver(callReceiver, callFilter);


    }




    public static Context getContext(){
        return context;
    }

    public  EaseUser getUserInfo(String username){
        EaseUser easeUser = new EaseUser(username);

        easeUser.setNick(SharedPreferenceUtil.getUserNickname(username));
        easeUser.setAvatar(SharedPreferenceUtil.getUserHeadPath(username));
            return easeUser;
    }
}
