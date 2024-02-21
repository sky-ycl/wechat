package com.ycl.wechatserver.user.cahce;

import com.ycl.wechatserver.user.dao.ItemConfigDao;
import com.ycl.wechatserver.user.domain.entity.ItemConfig;
import com.ycl.wechatserver.user.domain.enums.ItemTypeEnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigDao itemConfigDao;


    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getItemByType(){
        List<ItemConfig> badgesList=itemConfigDao.getItemByType(ItemTypeEnum.BADGE.getType());
        return badgesList;
    }

    @Cacheable(cacheNames = "item", key = "'items:'+#itemId")
    public ItemConfig getItemById(Long itemId){
        ItemConfig itemConfig = itemConfigDao.getItemById(itemId);
        return itemConfig;
    }
}
