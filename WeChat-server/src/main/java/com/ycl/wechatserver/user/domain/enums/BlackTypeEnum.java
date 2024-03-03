package com.ycl.wechatserver.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BlackTypeEnum {

    UID(1,"UID"),
    IP(2,"IP");

    private final Integer Type;
    private final String desc;
}
