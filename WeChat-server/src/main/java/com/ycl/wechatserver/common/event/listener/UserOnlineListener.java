package com.ycl.wechatserver.common.event.listener;


import com.ycl.wechatserver.common.event.UserOnlineEvent;
import com.ycl.wechatserver.user.dao.UserDao;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.enums.ChatActiveStatusEnum;
import com.ycl.wechatserver.user.service.IpService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserOnlineListener {


    @Resource
    private UserDao userDao;

    @Resource
    private IpService ipService;

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent userOnlineEvent){
        User user = userOnlineEvent.getUser();
        user.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        // 更新用户(更新ipInfo  更新最后一次上下时间)
        userDao.updateById(user);
        // 用户ip详情的解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
