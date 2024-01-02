package com.ycl.wechatserver.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
public class RedisUtil {

    private static StringRedisTemplate stringRedisTemplate;

    static {
        RedisUtil.stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }
}
