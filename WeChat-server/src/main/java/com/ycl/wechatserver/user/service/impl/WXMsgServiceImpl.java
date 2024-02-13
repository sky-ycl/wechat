package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ycl.wechatserver.config.WxMpProperties;
import com.ycl.wechatserver.user.builder.TextBuilder;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.user.service.WXMsgService;
import com.ycl.wechatserver.websocket.service.WebSocketService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.RegEx;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class WXMsgServiceImpl implements WXMsgService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private WxMpService wxMpService;

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private WxMpProperties wxMpProperties;

    /**
     * openid和登录code的关系
     */
    private static final ConcurrentMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);

        // 如果code为空表示出现异常
        if (code == null) {
            return null;
        }

        // 通过openId获取用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId, openId);
        User user = userMapper.selectOne(wrapper);

        // 用户已经注册并授权成功
        if (user != null && user.getAvatar() != null) {
            //用户登录逻辑
            webSocketService.scanLoginSuccess(code, user.getId());
            return null;
        } else {
            // 用户未注册需要对用户进行注册
            User user1 = new User();
            user1.setOpenId(openId);
            userService.registered(user1);
            // 推送链接让用户授权
            WAIT_AUTHORIZE_MAP.put(openId, code);
            // 用户等待授权
            webSocketService.waitAuthorize(code);
        }
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        String url = getURL(appId);
        // 扫码事件处理
        return new TextBuilder().build("请点击登录:<a href=\"" + url + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getName, userInfo.getNickname())
                .set(User::getAvatar, userInfo.getHeadImgUrl())
                .eq(User::getOpenId, userInfo.getOpenid());
        userMapper.update(null, updateWrapper);
        // 当用户授权完需要移除WAIT_AUTHORIZE_MAP对应的openid
        Integer code = WAIT_AUTHORIZE_MAP.remove(userInfo.getOpenid());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 通过openid获取用户id
        wrapper.eq(User::getOpenId, userInfo.getOpenid());
        User user = userMapper.selectOne(wrapper);
        webSocketService.scanLoginSuccess(code, user.getId());
    }

    public Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            eventKey = eventKey.replace("grscene_", "");
            return Integer.parseInt(eventKey);
        } catch (Exception e) {
            return null;
        }
    }

    private String getURL(String appid) {
        String callback = wxMpProperties.getCallback();
        System.out.println(callback);
        try {
            String redirectUri = URLEncoder.encode(callback, "UTF-8");

            String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" +
                    "" + redirectUri + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
            return URL;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
