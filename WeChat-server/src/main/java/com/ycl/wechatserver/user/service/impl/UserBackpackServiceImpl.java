package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.common.domain.enums.YesOrNoEnum;
import com.ycl.wechatserver.user.dao.UserBackpackDao;
import com.ycl.wechatserver.user.domain.entity.UserBackpack;
import com.ycl.wechatserver.user.domain.enums.IdempotentEnum;
import com.ycl.wechatserver.user.service.UserBackpackService;
import com.ycl.wechatserver.user.mapper.UserBackpackMapper;
import com.ycl.wechatserver.utils.AssertUtil;
import com.ycl.wechatserver.utils.RedissonUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 *
 */
@Service
public class UserBackpackServiceImpl extends ServiceImpl<UserBackpackMapper, UserBackpack>
        implements UserBackpackService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Resource
    private RedissonUtil redissonUtil;


    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 获取幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        redissonUtil.executeWithLock("acquire" + idempotent, () -> {
            UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
            if (Objects.nonNull(userBackpack)) {
                return;
            }
            // 发放物品
            UserBackpack backpack = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            userBackpackDao.save(backpack);
        });
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String s = String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
        return s;
    }

}




