package com.ycl.wechatserver.user.service;

public interface LoginService {

    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void refreshTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    String getToken(Long uid);

    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);

}