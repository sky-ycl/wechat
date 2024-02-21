package com.ycl.wechatserver.utils;

import com.ycl.wechatserver.common.exception.BusinessException;
import com.ycl.wechatserver.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedissonUtil {

    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean isSuccess = lock.tryLock(waitTime, leaseTime, timeUnit);
        if (!isSuccess) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUnit, Runnable runnable) {
        return executeWithLock(key, waitTime,-1, TimeUnit.MILLISECONDS, () -> {
            runnable.run();
            return null;
        });
    }


    public <T> T executeWithLock(String key, Runnable runnable) {
        return executeWithLock(key, -1,-1, TimeUnit.MILLISECONDS, () -> {
            runnable.run();
            return null;
        });
    }

}
