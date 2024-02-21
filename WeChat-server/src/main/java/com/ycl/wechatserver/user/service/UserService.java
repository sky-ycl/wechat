package com.ycl.wechatserver.user.service;

import com.ycl.wechatserver.common.domain.vo.response.ApiResult;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ycl.wechatserver.user.domain.vo.BadgesVO;
import com.ycl.wechatserver.user.domain.vo.UserInfo;

import java.util.List;

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

    /**
     * 获取用户信息
     * @return
     */
    UserInfo getUserinfo();

    /**
     * 修改用户名
     * @param modifyNameDto
     * @return
     */
    void modifyName(ModifyNameDto modifyNameDto);

    /**
     * 获取可选徽章
     * @return
     */
    List<BadgesVO> getBadges();

    /**
     * 佩戴徽章
     * @param itemId
     */
    void wearingBadge(Long badgeId);

}
