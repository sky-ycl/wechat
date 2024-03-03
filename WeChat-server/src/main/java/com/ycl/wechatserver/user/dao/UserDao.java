package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.enums.UserStatusEnum;
import com.ycl.wechatserver.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDao {

    @Resource
    private UserMapper userMapper;

    public Integer modifyName(Long uid, String name) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getName, name)
                .eq(User::getId, uid);
        return userMapper.update(null, updateWrapper);
    }

    public User getById(Long uid) {
        return userMapper.selectById(uid);
    }

    public Boolean wearingBadge(Long uid, Long badgeId) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(User::getItemId, badgeId)
                .eq(User::getId, uid);
        int count = userMapper.update(null, lambdaUpdateWrapper);
        return count == 1;
    }

    public Integer updateById(User user){
        return userMapper.updateById(user);
    }


    public Integer modifyStatus(User user) {
        LambdaUpdateWrapper<User> wrapper=new LambdaUpdateWrapper<>();
        wrapper.set(User::getStatus,UserStatusEnum.BLACK.getStatus())
                .eq(User::getId,user.getId());
        return userMapper.update(null,wrapper);
    }
}
