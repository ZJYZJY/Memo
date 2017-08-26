package com.donutcn.memo.activity;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.entity.ChatMessage;
import com.donutcn.memo.entity.ChatUser;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ChatView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener;
import cn.jiguang.imui.chatinput.listener.OnClickEditTextListener;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;

public class ChatActivity extends AppCompatActivity implements ChatView.OnKeyboardChangedListener,
        ChatView.OnSizeChangedListener, View.OnTouchListener {

    private final int RC_RECORD_VOICE = 0x0001;
    private final int RC_CAMERA = 0x0002;
    private final int RC_PHOTO = 0x0003;

    private ChatView mChatView;
    private MsgListAdapter<ChatMessage> mAdapter;
    private List<ChatMessage> mData;
    private ChatUser mMyself, mOther;

    private InputMethodManager mImm;
    private Window mWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        EventBus.getDefault().register(this);
        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();

        mMyself = new ChatUser(UserStatus.getCurrentUser().getUsername(),
                UserStatus.getCurrentUser().getName(),
                UserStatus.getCurrentUser().getIconUrl());
        mOther = new ChatUser(getIntent().getStringExtra("username"),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("avatar"));

        mChatView = (ChatView) findViewById(R.id.chat_view);
        mChatView.initModule();
        mChatView.setTitle(mOther.getDisplayName());
        initMsgAdapter();
//        mAdapter.notifyDataSetChanged();

        mChatView.setKeyboardChangedListener(this);
        mChatView.setOnSizeChangedListener(this);
        mChatView.setOnTouchListener(this);
        mChatView.setMenuClickListener(new OnMenuClickListener() {
            @Override
            public boolean onSendTextMessage(CharSequence input) {
                if (input.length() == 0) {
                    return false;
                }
                //创建一条文本消息
                EMMessage msg = EMMessage.createTxtSendMessage(input.toString(), mOther.getId());
                LogUtil.d("toUsername:" + mOther.getId() + "\nmessage:" + input.toString());
                ToastUtil.show(ChatActivity.this, "toUsername:" + mOther.getId() + "\nmessage:" + input.toString());
                //发送消息
                String avatar = mMyself.getAvatarFilePath();
                String name = mMyself.getDisplayName();
                msg.setAttribute("avatar", mMyself.getAvatarFilePath());
                msg.setAttribute("name", mMyself.getDisplayName());
                EMClient.getInstance().chatManager().sendMessage(msg);
                ChatMessage message = new ChatMessage(input.toString(), IMessage.MessageType.SEND_TEXT, mMyself);
                mAdapter.addToStart(message, true);
                BriefMessage briefMessage = new BriefMessage(msg.getUserName(),
                        mOther.getAvatarFilePath(), mOther.getDisplayName(), input.toString(),
                        msg.getMsgTime(), 0);
                EventBus.getDefault().postSticky(briefMessage);
                return true;
            }

            @Override
            public void onSendFiles(List<FileItem> list) {
//                if (list == null || list.isEmpty()) {
//                    return;
//                }
//
//                ChatMessage message;
//                for (FileItem item : list) {
//                    if (item.getType() == FileItem.Type.Image) {
//                        message = new ChatMessage(null, IMessage.MessageType.SEND_IMAGE);
//
//                    } else if (item.getType() == FileItem.Type.Video) {
//                        message = new ChatMessage(null, IMessage.MessageType.SEND_VIDEO);
//                        message.setDuration(((VideoItem) item).getDuration());
//
//                    } else {
//                        throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video");
//                    }
//
//                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
////                    message.setTimeString("2017-8-26");
//                    message.setMediaFilePath(item.getFilePath());
//                    message.setUserInfo(new ChatUser("1", "Ironman", "R.drawable.ironman"));
//
//                    final ChatMessage fMsg = message;
//                    ChatActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mAdapter.addToStart(fMsg, true);
//                        }
//                    });
//                }
            }

            @Override
            public boolean switchToMicrophoneMode() {
                String[] perms = new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

//                if (!EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
//                    EasyPermissions.requestPermissions(ChatActivity.this,
//                            getResources().getString(R.string.rationale_record_voice),
//                            RC_RECORD_VOICE, perms);
//                }
                return true;
            }

            @Override
            public boolean switchToGalleryMode() {
                String[] perms = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };

//                if (!EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
//                    EasyPermissions.requestPermissions(ChatActivity.this,
//                            getResources().getString(R.string.rationale_photo),
//                            RC_PHOTO, perms);
//                }
                return true;
            }

            @Override
            public boolean switchToCameraMode() {
                String[] perms = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                };

//                if (!EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
//                    EasyPermissions.requestPermissions(ChatActivity.this,
//                            getResources().getString(R.string.rationale_camera),
//                            RC_CAMERA, perms);
//                } else {
                File rootDir = getFilesDir();
                String fileDir = rootDir.getAbsolutePath() + "/photo";
                mChatView.setCameraCaptureFile(fileDir, new SimpleDateFormat("yyyy-MM-dd-hhmmss",
                        Locale.getDefault()).format(new Date()));
//                }
                return true;
            }
        });

        mChatView.setRecordVoiceListener(new RecordVoiceListener() {
            @Override
            public void onStartRecord() {
                // set voice file path, after recording, audio file will save here
                String path = Environment.getExternalStorageDirectory().getPath() + "/voice";
                File destDir = new File(path);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                mChatView.setRecordVoiceFile(destDir.getPath(), DateFormat.format("yyyy-MM-dd-hhmmss",
                        Calendar.getInstance(Locale.CHINA)) + "");
            }

            @Override
            public void onFinishRecord(File voiceFile, int duration) {
                ChatMessage message = new ChatMessage(null, IMessage.MessageType.SEND_VOICE, mMyself);
                message.setMediaFilePath(voiceFile.getPath());
                message.setDuration(duration);
//                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                mAdapter.addToStart(message, true);
            }

            @Override
            public void onCancelRecord() {

            }
        });

        mChatView.setOnCameraCallbackListener(new OnCameraCallbackListener() {
            @Override
            public void onTakePictureCompleted(String photoPath) {
                final ChatMessage message = new ChatMessage(null, IMessage.MessageType.SEND_IMAGE, mMyself);
//                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                message.setMediaFilePath(photoPath);
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addToStart(message, true);
                    }
                });
            }
            @Override
            public void onStartVideoRecord() {}
            @Override
            public void onFinishVideoRecord(String videoPath) {}
            @Override
            public void onCancelVideoRecord() {}
        });

        mChatView.setOnTouchEditTextListener(new OnClickEditTextListener() {
            @Override
            public void onTouchEditText() {
//                mAdapter.getLayoutManager().scrollToPosition(0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
//    }

    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .placeholder(R.mipmap.user_default_icon)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(avatarImageView);
            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .override(400, Target.SIZE_ORIGINAL)
                        .into(imageView);
            }
        };

        mData = new ArrayList<>();
        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);
        // If you want to customise your layout, try to create custom ViewHolder:
        // holdersConfig.setSenderTxtMsg(CustomViewHolder.class, layoutRes);
        // holdersConfig.setReceiverTxtMsg(CustomViewHolder.class, layoutRes);
        // CustomViewHolder must extends ViewHolders defined in MsgListAdapter.
        // Current ViewHolders are TxtViewHolder, VoiceViewHolder.

        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<ChatMessage>() {
            @Override
            public void onMessageClick(ChatMessage message) {
                // do something
                if (message.getType() == IMessage.MessageType.RECEIVE_VIDEO
                        || message.getType() == IMessage.MessageType.SEND_VIDEO) {
                    if (!TextUtils.isEmpty(message.getMediaFilePath())) {
//                        Intent intent = new Intent(ChatActivity.this, VideoActivity.class);
//                        intent.putExtra(VideoActivity.VIDEO_PATH, message.getMediaFilePath());
//                        startActivity(intent);
                    }
                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "点击了消息",
//                            Toast.LENGTH_SHORT).show();
                    ToastUtil.show(ChatActivity.this, message.getFromUser().getId());
                }
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<ChatMessage>() {
            @Override
            public void onMessageLongClick(ChatMessage message) {
                Toast.makeText(getApplicationContext(),
                        "长按了消息",
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<ChatMessage>() {
            @Override
            public void onAvatarClick(ChatMessage message) {
                ChatUser userInfo = (ChatUser) message.getFromUser();
                Toast.makeText(getApplicationContext(),
                        "点击了头像",
                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setMsgResendListener(new MsgListAdapter.OnMsgResendListener<ChatMessage>() {
            @Override
            public void onMessageResend(ChatMessage message) {
                // resend message here
            }
        });

        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
//                if (totalCount <= mData.size()) {
//                    Log.i("MessageListActivity", "Loading next page");
//                    loadNextPage();
//                }
            }
        });
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mOther.getId());
        if(conversation != null){
            List<EMMessage> messages = conversation.getAllMessages();
            if(messages.size() < 5){
                messages.addAll(0, conversation.loadMoreMsgFromDB(messages.get(0).getMsgId(), 10));
            }
            mData = convertMessages(messages);
            Collections.reverse(mData);
        }
        mAdapter.addToEnd(mData);
        mChatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addToEnd(mData);
            }
        }, 1000);
    }

    @Override
    public void onKeyBoardStateChanged(int state) {
        switch (state) {
            case ChatInputView.KEYBOARD_STATE_INIT:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (mImm != null) {
                    mImm.isActive();
                }
                if (chatInputView.getMenuState() == View.INVISIBLE
                        || (!chatInputView.getSoftInputState()
                        && chatInputView.getMenuState() == View.GONE)) {

                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    chatInputView.dismissMenuLayout();
                }
                break;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (oldh - h > 300) {
            mChatView.setMenuHeight(oldh - h);
        }
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();

                if (view.getId() == chatInputView.getInputView().getId()) {

                    if (chatInputView.getMenuState() == View.VISIBLE
                            && !chatInputView.getSoftInputState()) {
                        chatInputView.dismissMenuAndResetSoftMode();
                        return false;
                    } else {
                        return false;
                    }
                }
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                if (chatInputView.getSoftInputState()) {
                    View v = getCurrentFocus();

                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        chatInputView.setSoftInputState(false);
                    }
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onBack(View view){
        finish();
    }

    private List<ChatMessage> convertMessages(List<EMMessage> messages){
        List<ChatMessage> list = new ArrayList<>();
        String msg = "";
        IMessage.MessageType type = null;
        ChatUser user= null;
        for(EMMessage message : messages){
            switch (message.getType()){
                case TXT:
                    msg = ((EMTextMessageBody)message.getBody()).getMessage();
                    if(message.direct() == EMMessage.Direct.SEND){
                        type = IMessage.MessageType.SEND_TEXT;
                    }  else {
                        type = IMessage.MessageType.RECEIVE_TEXT;
                    }
                    user = new ChatUser(message.getUserName(),
                            message.getStringAttribute("name", ""),
                            message.getStringAttribute("avatar", ""));
                    break;
            }
            list.add(new ChatMessage(msg, type, user, message.getMsgTime()));
        }
        return list;
    }

    @Subscribe
    public void onReceiveChatMsg(ChatMessage message){
        mAdapter.addToStart(message, true);
    }

//    @Subscribe(sticky = true)
//    public void onReceiveBriefMessage(BriefMessage event){
//        if(PublishType.getType(event.getType()) == null){
//            EventBus.getDefault().cancelEventDelivery(event);
//        }
//    }
}
