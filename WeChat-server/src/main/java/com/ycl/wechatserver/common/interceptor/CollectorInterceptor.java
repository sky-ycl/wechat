package com.ycl.wechatserver.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.utils.MyThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Order(1)
@Component
@Slf4j
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUid(Optional.ofNullable(request.getAttribute(TokenInterceptor.ATTRIBUTE_UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null));
        userDto.setIp(ServletUtil.getClientIP(request));
        MyThreadLocal.saveUser(userDto);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MyThreadLocal.removeUser();
    }
}
