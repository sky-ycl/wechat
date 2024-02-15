package com.ycl.wechatserver.common.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.common.domain.vo.response.ApiResult;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.LoginService;
import com.ycl.wechatserver.utils.MyThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Order(-2)
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";

    @Resource
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户登录token
        String token = getToken(request);
        Long uid = loginService.getValidUid(token);

        // 未登录状态
        if (uid == null && !isPublicURI(request.getRequestURI())) {
            response.setStatus(401);
            response.setContentType(ContentType.JSON.toString(StandardCharsets.UTF_8));
            response.getWriter().write(JSONUtil.toJsonStr(ApiResult.fail(401,"登录失效，请重新登录")));
            return false;
        }

        // 登录状态
        request.setAttribute(ATTRIBUTE_UID, uid);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MyThreadLocal.removeUser();
    }

    /**
     * 判断是不是公共方法，可以未登录访问的
     *
     * @param requestURI
     */
    private boolean isPublicURI(String requestURI) {
        String[] split = requestURI.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    /**
     * 获取token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }
}
