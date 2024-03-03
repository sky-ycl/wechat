package com.ycl.wechatserver.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.common.exception.HttpErrorEnum;
import com.ycl.wechatserver.user.cahce.BlackCache;
import com.ycl.wechatserver.user.cahce.UserCache;
import com.ycl.wechatserver.user.domain.enums.BlackTypeEnum;
import com.ycl.wechatserver.utils.MyThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Order(2)
@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Resource
    private BlackCache blackCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> map = blackCache.getBlackMap();
        UserDto userDto = MyThreadLocal.getUser();
        if (inBlackList(userDto.getUid(), map.get(BlackTypeEnum.UID.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
//        if (inBlackList(userDto.getIp(), map.get(BlackTypeEnum.IP.getType()))) {
//            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
//            return false;
//        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> blackSet) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(blackSet)) {
            return false;
        }
        return blackSet.contains(target.toString());
    }
}
