package com.ycl.wechatserver.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    NORMAL(0,"正常状态"),
    BLACK(1,"被拉黑");


    private final Integer status;
    private final String desc;


}
