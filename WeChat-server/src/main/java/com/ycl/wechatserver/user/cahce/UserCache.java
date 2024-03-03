package com.ycl.wechatserver.user.cahce;

import com.ycl.wechatserver.user.dao.UserRoleDao;
import com.ycl.wechatserver.user.domain.entity.UserRole;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Resource
    private UserRoleDao userRoleDao;


    @Cacheable(cacheNames = "user", key = "'role'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoleList = userRoleDao.selectList(uid);
        return userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
