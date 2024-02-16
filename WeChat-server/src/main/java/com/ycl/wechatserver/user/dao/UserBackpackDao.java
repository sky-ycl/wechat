package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ycl.wechatserver.common.domain.enums.YesOrNoEnum;
import com.ycl.wechatserver.user.domain.entity.UserBackpack;
import com.ycl.wechatserver.user.domain.enums.ItemEnum;
import com.ycl.wechatserver.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserBackpackDao {

    @Resource
    private UserBackpackMapper userBackpackMapper;

    /**
     * 判断是否有重复的名字
     *
     * @param uid
     * @param itemId
     * @return
     */
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1");
        return userBackpackMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 使用物品
     *
     * @param userBackpack
     * @return
     */
    public Boolean useItem(UserBackpack userBackpack) {
        LambdaUpdateWrapper<UserBackpack> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .eq(UserBackpack::getUid, userBackpack.getUid());
        int count = userBackpackMapper.update(null, lambdaUpdateWrapper);
        return count == 1;
    }
}

