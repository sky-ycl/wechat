package com.ycl.wechatserver.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(1l,"超级管理员"),
    CHAT_MANAGER(2l,"群聊管理员");
    private final Long id;
    private final String desc;

}
