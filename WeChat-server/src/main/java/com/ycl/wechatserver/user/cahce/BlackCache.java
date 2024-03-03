package com.ycl.wechatserver.user.cahce;

import com.ycl.wechatserver.user.dao.BlackDao;
import com.ycl.wechatserver.user.domain.entity.Black;
import com.ycl.wechatserver.user.domain.enums.BlackTypeEnum;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlackCache {

    @Resource
    private BlackDao blackDao;

    @Cacheable(cacheNames = "black", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    @CacheEvict(cacheNames = "black", key = "'blackList'")
    public Map<Integer, Set<String>> clearBlackMap(){
        return null;
    }

    public Set<String> getBlackOfType(List<Black> blackList) {
        return blackList.stream()
                .map(black -> String.valueOf(black.getTarget()))
                .collect(Collectors.toSet());
    }
}
