package com.ycl.wechatserver.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ycl.wechatserver.common.config.ThreadPoolConfig;
import com.ycl.wechatserver.common.event.UserOnlineEvent;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.enums.RoleEnum;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.user.service.IpService;
import com.ycl.wechatserver.user.service.LoginService;
import com.ycl.wechatserver.user.service.RoleService;
import com.ycl.wechatserver.utils.NettyUtil;
import com.ycl.wechatserver.websocket.domain.dto.WSChannelExtraDTO;
import com.ycl.wechatserver.websocket.domain.vo.response.WSBaseResp;
import com.ycl.wechatserver.websocket.service.WebSocketService;
import com.ycl.wechatserver.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Resource
    @Lazy
    private WxMpService wxMpService;

    /**
     * 管理所有用户的连接(登录态/游客)
     */
    private static final ConcurrentMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final int MAXIMUM_SIZE = 1000;

    public static final Duration DURATION = Duration.ofHours(1);

    @Resource
    private UserMapper userMapper;

    @Resource
    private LoginService loginService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoleService roleService;

    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 临时保存登录code和channel的关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {

        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());

    }


    @Override
    public void handleLoginReq(Channel channel) {

        try {
            // 生成随机code
            Integer code = getLoginCode(channel);
            // 找微信申请带参二维码
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
            // 把二维码推送给前端
            sendMessage(channel, WebSocketAdapter.buildLoginUrlResp(wxMpQrCodeTicket));

        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除用户的连接
     *
     * @param channel
     */
    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        // 确定连接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        User user = userMapper.selectById(uid);
        // 获取登录模块获取token
        String token = loginService.getToken(uid);
        // 用户登录成功
        loginSuccess(channel, user, token);
    }

    /**
     * 用户等待授权
     *
     * @param code
     */
    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMessage(channel, WebSocketAdapter.buildWaitAuthorize());
    }

    /**
     * 登录认证  防止用户刷新后需要重新进行websocket连接
     *
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (uid != null) {
            User user = userMapper.selectById(uid);
            // 用户登录成功
            loginSuccess(channel, user, token);
        } else {
            sendMessage(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMsgToAll(WSBaseResp<?> resp) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            threadPoolTaskExecutor.execute(() -> sendMessage(channel, resp));
        });
    }

    private void sendMessage(Channel channel, WSBaseResp<?> request) {
        // 将消息发送给前端
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(request)));
    }

    /**
     * 获取登录code
     *
     * @param channel
     * @return
     */
    private Integer getLoginCode(Channel channel) {
        Integer code = RandomUtil.randomInt(Integer.MAX_VALUE);
        System.out.println(code);
        while (WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel) != null) {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }
        return code;
    }

    /**
     * 用户登录成功逻辑
     *
     * @param channel
     * @param user
     * @param token
     */
    private void loginSuccess(Channel channel, User user, String token) {
        // 保存用户channel的对应uid
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO(user.getId()));
        // 将用户信息发送给前端
        sendMessage(channel, WebSocketAdapter.buildLoginResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        // 用户登录上线事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }
}
