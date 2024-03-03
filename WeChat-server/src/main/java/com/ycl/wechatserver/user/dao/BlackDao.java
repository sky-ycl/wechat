package com.ycl.wechatserver.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycl.wechatserver.user.domain.entity.Black;
import com.ycl.wechatserver.user.domain.enums.BlackTypeEnum;
import com.ycl.wechatserver.user.mapper.BlackMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BlackDao {

    @Resource
    private BlackMapper blackMapper;

    public void save(Black black){
        blackMapper.insert(black);
    }

    public List<Black> getBlackByType(BlackTypeEnum blackTypeEnum){
        LambdaQueryWrapper<Black> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Black::getType,blackTypeEnum.getType());
        return blackMapper.selectList(wrapper);
    }

    public List<Black> list(){
        return blackMapper.selectList(null);
    }
}
