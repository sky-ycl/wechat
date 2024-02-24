package com.ycl.wechatserver.user.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
public class IpInfo implements Serializable {
    //注册时的ip
    private String createIp;
    //注册时的ip详情
    private IpDetail createIpDetail;
    //最新登录的ip
    private String updateIp;
    //最新登录的ip详情
    private IpDetail updateIpDetail;

    /**
     * 需要刷新的ip，这里判断更新ip就够，初始化的时候ip也是相同的，只需要设置的时候多设置进去就行
     *
     * @return
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();

        // 如果存在满足条件的 IP，则返回 null，表示不需要刷新；否则返回 updateIp，表示需要刷新
        return notNeedRefresh ? null : updateIp;
    }

    /**
     * 刷新ip
     * @param ip
     */
    public void refreshIP(String ip) {
        if(StringUtils.isBlank(ip)){
            return;
        }
        if (StringUtils.isBlank(createIp)) {
            createIp = ip;
        }
        updateIp=ip;
    }

    /**
     * 刷新ip详细信息
     * @param ipDetail
     */
    public void refreshIpDetail(IpDetail ipDetail) {
        if(Objects.equals(createIp,ipDetail.getIp())){
            createIpDetail=ipDetail;
        }
        if(Objects.equals(updateIp,ipDetail.getIp())){
            updateIpDetail=ipDetail;
        }
    }
}
