package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.user.dao.UserBackpackDao;
import com.ycl.wechatserver.user.dao.UserDao;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.entity.UserBackpack;
import com.ycl.wechatserver.user.domain.enums.ItemEnum;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.utils.AssertUtil;
import com.ycl.wechatserver.utils.MyThreadLocal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDao userDao;

    @Resource
    private UserBackpackDao userBackpackDao;

    /**
     * 用户进行注册
     * @param user
     * @return
     */
    @Override
    public Integer registered(User user) {
        int count = userMapper.insert(user);
        return count;
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public UserInfo getUserinfo() {
        // 从线程池中取出UserDto
        UserDto userDto = MyThreadLocal.getUser();
        User user = userMapper.selectById(userDto.getUid());
        // TODO 修改名字的次数还未完成
        UserInfo userInfo = UserInfo.builder()
                .sex(user.getSex())
                .id(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .modifyNameChance(1)
                .build();
        return userInfo;
    }

    /**
     * 修改用户名
     * @param modifyNameDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(ModifyNameDto modifyNameDto) {
        // 获取uid
        Long uid = MyThreadLocal.getUser().getUid();
        String name = modifyNameDto.getName();

        // 查询有没有相同的用户名  有的话会报TooManyResultsException
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getName,name);
        User user = userMapper.selectOne(lambdaQueryWrapper);

        AssertUtil.isEmpty(user,"名字已经被抢占了，请换一个");
        // 判断改名卡够不够用
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(userBackpack,"改名次数不够了，等后续活动送改名卡哦");

        // 使用物品
        Boolean isUse=userBackpackDao.useItem(userBackpack);
        if(isUse){
            // 修改用户名称
            Integer count = userDao.modifyName(uid, name);

        }
    }
}




