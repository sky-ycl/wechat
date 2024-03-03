package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycl.wechatserver.user.domain.entity.UserRole;
import com.ycl.wechatserver.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserRoleDao {

    @Resource
    private UserRoleMapper userRoleMapper;

    public List<UserRole> selectList(Long uid){
        LambdaQueryWrapper<UserRole> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUid,uid);
        return userRoleMapper.selectList(wrapper);
    }
}
