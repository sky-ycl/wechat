package com.ycl.wechatserver.user.service;

import com.ycl.wechatserver.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户进行注册
     * @param user
     * @return
     */
    Integer registered(User user);
}
