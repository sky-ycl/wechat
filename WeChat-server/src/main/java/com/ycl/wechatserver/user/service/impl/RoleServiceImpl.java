package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.user.cahce.UserCache;
import com.ycl.wechatserver.user.domain.entity.Role;
import com.ycl.wechatserver.user.domain.enums.RoleEnum;
import com.ycl.wechatserver.user.service.RoleService;
import com.ycl.wechatserver.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

    @Resource
    private UserCache userCache;


    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {//超级管理员无敌的好吧，后期做成权限=》资源模式
        Set<Long> roleSet = userCache.getRoleSet(uid);
        boolean contains = roleSet.contains(roleEnum.getId());
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }
}




