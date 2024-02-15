package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.common.domain.vo.response.ApiResult;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.utils.MyThreadLocal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

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
                .modifyNameChance(null)
                .build();
        return userInfo;
    }

    /**
     * 修改用户名
     * @param modifyNameDto
     * @return
     */
    @Override
    public void modify(ModifyNameDto modifyNameDto) {
        // 获取uid
        Long uid = MyThreadLocal.getUser().getUid();
        LambdaUpdateWrapper<User> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getName,modifyNameDto.getName())
                .eq(User::getId,uid);
        int count = userMapper.update(null, updateWrapper);
        if(count<1){
            System.out.println("修改失败");
        }
    }
}




