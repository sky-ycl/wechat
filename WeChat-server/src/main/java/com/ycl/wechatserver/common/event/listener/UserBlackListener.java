package com.ycl.wechatserver.common.event.listener;

import com.ycl.wechatserver.common.event.UserBlackEvent;
import com.ycl.wechatserver.user.cahce.BlackCache;
import com.ycl.wechatserver.user.dao.UserDao;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.websocket.service.WebSocketService;
import com.ycl.wechatserver.websocket.service.adapter.WebSocketAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单拦截
 */
@Component
public class UserBlackListener {

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserDao userDao;

    @Resource
    private BlackCache blackCache;

    /**
     * 拉黑用户  将用户id发送其他所有用户
     *
     * @param event
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void sendMag(UserBlackEvent event) {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    /**
     * 改变用户状态
     * @param event
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void changUserStatus(UserBlackEvent event) {
        User user = event.getUser();
        userDao.modifyStatus(user);
    }

    /**
     * 清空缓存
     * @param event
     */
    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void clearCache(UserBlackEvent event) {
        blackCache.clearBlackMap();
    }
}
