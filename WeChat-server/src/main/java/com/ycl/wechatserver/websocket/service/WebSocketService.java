package com.ycl.wechatserver.websocket.service;

import com.ycl.wechatserver.websocket.domain.vo.response.WSBaseResp;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;


public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    /**
     * 用户扫码成功
     * @param code
     * @param uid
     */
    void scanLoginSuccess(Integer code, Long uid);

    /**
     * 用户等待授权
     * @param code
     */
    void waitAuthorize(Integer code);

    /**
     * 登录认证  防止用户刷新后需要重新进行websocket连接
     * @param channel
     * @param token
     */
    void authorize(Channel channel, String token);

    /**
     * 发送消息给所有人
     * @param resp
     */
    void sendMsgToAll(WSBaseResp<?> resp);
}
