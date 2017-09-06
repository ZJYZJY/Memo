package com.donutcn.memo.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.donutcn.memo.utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.io.File;

/**
 * com.donutcn.memo.helper
 * Created by 73958 on 2017/8/8.
 */

public class ShareHelper {

    private Context mContext;

    public ShareHelper(Context context){
        this.mContext = context;
    }

    public void shareWechat(UMWeb web){
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener)
                .share();
    }

    public void shareWechatCirlce(UMWeb web){
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .share();
    }

    public void shareQQ(UMWeb web){
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.QQ)
                .setCallback(umShareListener)
                .share();
    }

    public void shareQzone(UMWeb web){
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.QZONE)
                .setCallback(umShareListener)
                .share();
    }

    public void shareWeibo(UMWeb web){
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.SINA)
                .setCallback(umShareListener)
                .share();
    }

    public void openShareBoard(String url, String title, String picUrl, String content){
        // set up share board style.
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);

        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        if(picUrl != null && !picUrl.equals("")){
            web.setThumb(new UMImage(mContext, picUrl));
        }
        web.setDescription(content);
        new ShareAction((Activity) mContext)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(umShareListener)
                .open(config);
    }

    public void shareWechat(Bitmap bitmap){
        UMImage image = new UMImage(mContext, bitmap);
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener)
                .share();
    }

    public void shareWechatCirlce(Bitmap bitmap){
        UMImage image = new UMImage(mContext, bitmap);
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener)
                .share();
    }

    public void shareQQ(Bitmap bitmap){
        UMImage image = new UMImage(mContext, bitmap);
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.QQ)
                .setCallback(umShareListener)
                .share();
    }

    public void shareQzone(Bitmap bitmap){
        UMImage image = new UMImage(mContext, bitmap);
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.QZONE)
                .setCallback(umShareListener)
                .share();
    }

    public void shareWeibo(Bitmap bitmap){
        UMImage image = new UMImage(mContext, bitmap);
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setPlatform(SHARE_MEDIA.SINA)
                .setCallback(umShareListener)
                .share();
    }

    public void openShareBoard(Bitmap bitmap, UMShareListener listener){
        // set up share board style.
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);

        UMImage image = new UMImage(mContext, bitmap);
        image.compressStyle = UMImage.CompressStyle.QUALITY;
        new ShareAction((Activity) mContext)
                .withMedia(image)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(listener)
                .open(config);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            ToastUtil.show(mContext, platform + " 分享成功啦");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.show(mContext, platform + " 分享失败啦");
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.show(mContext, platform + " 分享取消了");
        }
    };
}
