package com.ycl.wechatserver.user.service.impl;

import com.ycl.wechatserver.user.service.LoginService;
import com.ycl.wechatserver.utils.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.ycl.wechatserver.constant.RedisConstant.USER_TOKEN_KEY;
import static com.ycl.wechatserver.constant.RedisConstant.USER_TOKEN_TTL;


@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean verify(String token) {
        return false;
    }

    @Override
    @Async
    public void refreshTokenIfNecessary(String token) {
        // 获取uid
        Long uid = getValidUid(token);
        // 首先判断key是否是存在的
        Long expire = stringRedisTemplate.getExpire(USER_TOKEN_KEY, TimeUnit.DAYS);
        // 如果expire<=0的话 表示key是不存在的 或者该token过期了
        if (expire <= 0) {
            return;
        }
        // 刷新token
        stringRedisTemplate.expire(USER_TOKEN_KEY + uid, USER_TOKEN_TTL, TimeUnit.DAYS);
    }

    @Override
    public String getToken(Long uid) {
        String token = jwtUtil.createToken(uid);
        // 将生成的token存入到redis中
        stringRedisTemplate.opsForValue().set(USER_TOKEN_KEY + uid, token, USER_TOKEN_TTL, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        // 解析Token 获取uid
        Long uid = jwtUtil.getUidOrNull(token);
        if (uid == null) {
            return null;
        }
        // 从redis中获取token
        String redisToken = stringRedisTemplate.opsForValue().get(USER_TOKEN_KEY + uid);
        if (redisToken == null) {
            return null;
        }
        // 只有当传进来的token和redis缓存中的redis相同是才返回uid
        return Objects.equals(token, redisToken) ? uid : null;
    }
}
