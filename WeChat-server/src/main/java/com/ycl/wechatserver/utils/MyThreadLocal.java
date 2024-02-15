package com.ycl.wechatserver.utils;

import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.user.domain.vo.UserInfo;

public class MyThreadLocal {
    private static final ThreadLocal<UserDto> tl = new ThreadLocal<>();

    public static void saveUser(UserDto user){
        tl.set(user);
    }

    public static UserDto getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
