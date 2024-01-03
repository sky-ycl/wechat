package com.ycl.wechatserver;

import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.user.service.LoginService;
import com.ycl.wechatserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ycl.wechatserver.constant.RedisConstant.USER_TOKEN_KEY;

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
	public void testMapper(){
		List<User> users = userMapper.selectList(null);
		System.out.println(users);
	}

	@Test
	public void testCreateToken(){
		String token = jwtUtil.createToken(1l);
		System.out.println(token);
	}

	@Test
	public void testRedis(){
		stringRedisTemplate.opsForValue().set("name","呆瓜");
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
	public void testJwt(){
		String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjMsImNyZWF0ZVRpbWUiOjE3MDQyMDk5MTB9.V2qQ35w9trANuSUa-YEHVKMas4fHBzH8GIJQ6SUB1Ls";
		Long uid = loginService.getValidUid(token);
		System.out.println(uid);
	}

	@Test
	public void testRedisExpire(){
		Long expire = stringRedisTemplate.getExpire(USER_TOKEN_KEY + 3,TimeUnit.DAYS);
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
		threadPoolTaskExecutor.execute(()->{
			if(1==1){
				log.error("123");
				throw new RuntimeException("1234");
			}
		});
		Thread.sleep(200);
	}
}
