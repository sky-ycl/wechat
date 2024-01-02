package com.ycl.wechatserver.websocket.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WSLoginSuccess {
    private Long uid;
    private String avatar;
    private String token;
    private String name;
    //用户权限 0普通用户 1超管
    private Integer power;
}
