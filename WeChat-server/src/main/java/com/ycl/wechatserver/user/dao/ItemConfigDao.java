package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycl.wechatserver.user.domain.entity.ItemConfig;
import com.ycl.wechatserver.user.mapper.ItemConfigMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemConfigDao {


    @Resource
    private ItemConfigMapper itemConfigMapper;

    /**
     * 通过type查询物品
     * @param type
     * @return
     */
    public List<ItemConfig> getItemByType(Long type) {
        LambdaQueryWrapper<ItemConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ItemConfig::getType,type);
        return itemConfigMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 通过id来查询徽章类型
     * @param id
     * @return
     */
    public ItemConfig getItemById(Long id){
        LambdaQueryWrapper<ItemConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ItemConfig::getId,id);
        return itemConfigMapper.selectOne(lambdaQueryWrapper);
    }
}
