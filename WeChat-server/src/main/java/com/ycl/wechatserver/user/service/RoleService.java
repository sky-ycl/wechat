package com.ycl.wechatserver.user.service;

import com.ycl.wechatserver.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ycl.wechatserver.user.domain.enums.RoleEnum;

/**
 *
 */
public interface RoleService extends IService<Role> {

    /**
     * 判断用户是否有权限
     * @param uid
     * @param roleEnum
     * @return
     */
    public boolean hasPower(Long uid, RoleEnum roleEnum);
}
