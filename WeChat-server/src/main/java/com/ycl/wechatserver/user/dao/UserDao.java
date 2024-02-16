package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDao {

    @Resource
    private UserMapper userMapper;

    public Integer modifyName(Long uid,String name){
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getName,name)
                .eq(User::getId,uid);
        return userMapper.update(null, updateWrapper);
    }
}
