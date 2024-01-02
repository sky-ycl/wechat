package com.ycl.wechatserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ycl.wechatserver.user.mapper")
public class WeChatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeChatServerApplication.class, args);
	}
}
