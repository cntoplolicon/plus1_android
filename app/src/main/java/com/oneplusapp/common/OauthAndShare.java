package com.oneplusapp.common;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.oneplusapp.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class OauthAndShare {
    public static final UMSocialService umSocialService = UMServiceFactory.getUMSocialService("com.umeng.share");
    public static final String UMENG_SHARE_CONTENT = "每天一个新活动，给生活带来不一样的乐趣";

    public static void addCustomPlatForms(final Context context, int postId, String imageUrl, String text) {
        addWXPlatform(context);
        addQQQZonePlatform(context);
        addSinaPlatForm();
        setShareContent(context, postId, imageUrl, text);

        umSocialService.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
        umSocialService.registerListener(new SocializeListeners.SnsPostListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "分享失败" + eCode, Toast.LENGTH_SHORT).show();
                }
            }
        });
        umSocialService.openShare((Activity) context, false);
    }

    public static void addWXPlatform(Context context) {
        String appSecret = "d4624c36b6795d1d99dcf0547af5443d";
        String appId = "wx8722fc0d2579fb13";

        UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
        wxHandler.setRefreshTokenAvailable(false);
        wxHandler.addToSocialSDK();

        UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    public static void addQQQZonePlatform(Context context) {
        String qqID = "1104950070";
        String qqKey = "qBwAJcdsf58q3PNf";

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) context, qqID, qqKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) context, qqID, qqKey);
        qZoneSsoHandler.addToSocialSDK();
    }


    public static void addSinaPlatForm() {
        umSocialService.getConfig().setDefaultShareLocation(false);
        umSocialService.getConfig().setSsoHandler(new SinaSsoHandler());

    }

    public static void setShareContent(Context context, int postId, String imageUrl, String text) {
        umSocialService.setShareContent(UMENG_SHARE_CONTENT);
        umSocialService.setShareMedia(new UMImage(context, RestClient.getInstance().buildShareUrl(postId)));

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(text);
        weixinContent.setTitle(UMENG_SHARE_CONTENT);
        weixinContent.setTargetUrl(RestClient.getInstance().buildShareUrl(postId));
        shareImageUrl(context, weixinContent, imageUrl);
        umSocialService.setShareMedia(weixinContent);

        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent("加一");
        circleMedia.setTitle(UMENG_SHARE_CONTENT);
        circleMedia.setTargetUrl(RestClient.getInstance().buildShareUrl(postId));
        shareImageUrl(context, circleMedia, imageUrl);
        umSocialService.setShareMedia(circleMedia);

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle(UMENG_SHARE_CONTENT);
        qqShareContent.setShareContent(text);
        qqShareContent.setTargetUrl(RestClient.getInstance().buildShareUrl(postId));
        shareImageUrl(context, qqShareContent, imageUrl);
        umSocialService.setShareMedia(qqShareContent);

        QZoneShareContent qZone = new QZoneShareContent();
        qZone.setShareContent(text);
        qZone.setTargetUrl(RestClient.getInstance().buildShareUrl(postId));
        shareImageUrl(context, qZone, imageUrl);
        qZone.setTitle(UMENG_SHARE_CONTENT);
        umSocialService.setShareMedia(qZone);
    }

    public static void shareImageUrl(Context context, BaseShareContent shareContent, String shareImageUrl) {
        if (shareImageUrl == null) {
            shareContent.setShareMedia(new UMImage(context, R.drawable.share_image));
        } else {
            shareContent.setShareMedia(new UMImage(context, shareImageUrl));
        }
    }
}
