package com.ycl.wechatserver;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycl.wechatserver.common.exception.CommonErrorEnum;
import com.ycl.wechatserver.user.cahce.BlackCache;
import com.ycl.wechatserver.user.dao.BlackDao;
import com.ycl.wechatserver.user.dao.ItemConfigDao;
import com.ycl.wechatserver.user.domain.entity.Black;
import com.ycl.wechatserver.user.domain.entity.ItemConfig;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.enums.BlackTypeEnum;
import com.ycl.wechatserver.user.domain.enums.IdempotentEnum;
import com.ycl.wechatserver.user.domain.enums.ItemEnum;
import com.ycl.wechatserver.user.mapper.ItemConfigMapper;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.user.service.LoginService;
import com.ycl.wechatserver.user.service.UserBackpackService;
import com.ycl.wechatserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ycl.wechatserver.common.constant.RedisConstant.USER_TOKEN_KEY;

@SpringBootTest
@Slf4j
class WeChatServerApplicationTests {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ItemConfigMapper itemConfigMapper;

    @Resource
    private BlackDao blackDao;

    @Test
    public void testGetAccessToken() throws WxErrorException {
        String accessToken = wxMpService.getAccessToken();
        System.out.println(accessToken);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

    @Test
    public void testMapper() {
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

    @Test
    public void testCreateToken() {
        String token = jwtUtil.createToken(1l);
        System.out.println(token);
    }

    @Test
    public void testRedis() {
        stringRedisTemplate.opsForValue().set("name", "呆瓜");
    }

    @Test
    public void testRedisson() throws InterruptedException {
        RLock lock = redissonClient.getLock("123");
        boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
        if (isLock) {
            try {
                System.out.println("获取成功");
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }

    @Resource
    private LoginService loginService;

    @Test
    public void testJwt() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjMsImNyZWF0ZVRpbWUiOjE3MDQyMDk5MTB9.V2qQ35w9trANuSUa-YEHVKMas4fHBzH8GIJQ6SUB1Ls";
        Long uid = loginService.getValidUid(token);
        System.out.println(uid);
    }

    @Test
    public void testRedisExpire() {
        Long expire = stringRedisTemplate.getExpire(USER_TOKEN_KEY + 3, TimeUnit.DAYS);
        System.out.println(expire);
        Long expire1 = stringRedisTemplate.getExpire(USER_TOKEN_KEY + 4, TimeUnit.DAYS);
        System.out.println(expire1);
        Long expire2 = stringRedisTemplate.getExpire("name");
        System.out.println(expire2);
    }

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void testThread() throws InterruptedException {
        threadPoolTaskExecutor.execute(() -> {
            if (1 == 1) {
                log.error("123");
                throw new RuntimeException("1234");
            }
        });
        Thread.sleep(200);
    }

    @Test
    public void testMap() {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, null);
        Object o = map.get(1);
        System.out.println(o);
        map.put(1, 1);
        System.out.println(map.get(1));
    }

    @Test
    public void testEnum() {
        Integer code = CommonErrorEnum.SYSTEM_ERROR.getCode();
        System.out.println(code);
    }

    @Resource
    private ItemConfigDao itemConfigDao;

    @Test
    public void testItemConfig() {
        List<ItemConfig> itemByType = itemConfigDao.getItemByType(2l);
        System.out.println(itemByType);
    }

    @Resource
    private UserBackpackService userBackpackService;

    private Long UID = 4l;

    @Test
    public void testIdempotent() {
        long itemId = 3l;
        for (long i = itemId; i <= 6l; i++) {
            userBackpackService.acquireItem(UID, i, IdempotentEnum.UID, UID + "");
        }
        Set<String> set = new HashSet<>();
        Map<String, String> map = new HashMap<>();
        ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<>();
    }


    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(1l);
        user.setName("456");
        userMapper.updateById(user);
    }

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(5l);
        System.out.println(user);
    }

    @Test
    public void testStream() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<Integer> nameLengths = names.stream()
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println(nameLengths);
    }


    @Test
    public void testGetBlackByType() {
        List<Black> blackList1 = blackDao.getBlackByType(BlackTypeEnum.IP);
        List<Black> blackList2 = blackDao.getBlackByType(BlackTypeEnum.UID);
        Map<Integer, Set<String>> map = new HashMap<>();
        map.put(BlackTypeEnum.UID.getType(), getBlackOfType(blackList2));
        map.put(BlackTypeEnum.IP.getType(), getBlackOfType(blackList1));
        System.out.println(map);
    }

    @Resource
    private BlackCache blackCache;

    @Test
    public void testBlackCache(){
        Map<Integer, Set<String>> map = blackCache.getBlackMap();
        System.out.println(map);
    }
    public Set<String> getBlackOfType(List<Black> blackList) {
        return blackList.stream()
                .map(black -> String.valueOf(black.getTarget()))
                .collect(Collectors.toSet());
    }
}
