package com.ycl.wechatserver.user.service;

import com.ycl.wechatserver.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ycl.wechatserver.user.domain.enums.IdempotentEnum;

/**
 *
 */
public interface UserBackpackService extends IService<UserBackpack> {

    /**
     * 用户发放一个物品
     * @param uid 用户id
     * @param itemId 物品id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum,String businessId);
}
