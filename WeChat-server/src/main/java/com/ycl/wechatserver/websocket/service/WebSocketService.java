package com.ycl.wechatserver.websocket.service;

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
}
