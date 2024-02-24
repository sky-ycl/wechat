package com.ycl.wechatserver.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ycl.wechatserver.common.domain.vo.response.IpResult;
import com.ycl.wechatserver.user.dao.UserDao;
import com.ycl.wechatserver.user.domain.entity.IpDetail;
import com.ycl.wechatserver.user.domain.entity.IpInfo;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.user.service.IpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class IpServiceImpl implements IpService {

    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500),
            new NamedThreadFactory("refresh-ipDetail", false));

    @Resource
    private UserDao userDao;


    @Resource
    private UserMapper userMapper;

    /**
     * 异步解析用户ip详情
     *
     * @param uid
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        executor.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (ipInfo == null) return;

            // 如果存在满足条件的 IP，则返回 null，表示不需要刷新；否则返回 updateIp，表示需要刷新
            String ip = ipInfo.needRefreshIp();
            if (ip == null) {
                return;
            }
            IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes(ip);
            if (ipDetail != null) {
                ipInfo.refreshIpDetail(ipDetail);

                // 更新用户信息
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userMapper.updateById(user);
            }
        });
    }

    /**
     * 获取IpDetail失败 进行3次重试
     * @param ip
     * @return
     */
    private static IpDetail TryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {}, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.execute(() -> {
                IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes("113.90.36.126");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (date.getTime() - begin.getTime())));
                }
            });
        }
    }
}
