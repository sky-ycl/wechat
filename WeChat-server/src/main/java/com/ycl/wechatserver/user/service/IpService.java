package com.ycl.wechatserver.user.service;

public interface IpService {

    /**
     * 异步解析用户ip详情
     * @param id
     */
    void refreshIpDetailAsync(Long id);
}
