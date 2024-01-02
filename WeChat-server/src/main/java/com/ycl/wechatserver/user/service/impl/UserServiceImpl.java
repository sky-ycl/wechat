package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 用户进行注册
     * @param user
     * @return
     */
    @Override
    public Integer registered(User user) {
        int count = userMapper.insert(user);
        return count;
    }
}




