package com.ycl.wechatserver.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemTypeEnum {
    MODIFY_NAME_CARD(1l, "改名卡"),
    BADGE(2l, "徽章"),
    ;

    private final Long type;
    private final String desc;
}
