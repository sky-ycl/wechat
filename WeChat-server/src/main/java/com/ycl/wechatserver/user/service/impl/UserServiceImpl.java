package com.ycl.wechatserver.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.common.domain.enums.YesOrNoEnum;
import com.ycl.wechatserver.user.dao.ItemConfigDao;
import com.ycl.wechatserver.user.dao.UserBackpackDao;
import com.ycl.wechatserver.user.dao.UserDao;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.entity.ItemConfig;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.entity.UserBackpack;
import com.ycl.wechatserver.user.domain.enums.ItemEnum;
import com.ycl.wechatserver.user.domain.enums.ItemTypeEnum;
import com.ycl.wechatserver.user.domain.vo.BadgesVO;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.user.mapper.UserMapper;
import com.ycl.wechatserver.utils.AssertUtil;
import com.ycl.wechatserver.utils.MyThreadLocal;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Resource
    private ItemConfigDao itemConfigDao;


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
    public void modifyName(ModifyNameDto modifyNameDto) {
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

    /**
     * 获取可选徽章
     * @return
     */
    @Override
    public List<BadgesVO> getBadges() {
        // 获取所有徽章
        List<ItemConfig> badgesList = itemConfigDao.getItemByType(ItemTypeEnum.BADGE.getType());

        // 查询用户所用的徽章
        List<Long> itemIdList = badgesList.stream().map(ItemConfig::getId).collect(Collectors.toList());
        // 获取用户id
        Long uid = MyThreadLocal.getUser().getUid();
        List<UserBackpack> userBackpacks=userBackpackDao.getItemById(uid,itemIdList);

        // 查询用户所使用的徽章
        User user = userDao.getById(uid);

        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        Set<Long> obtainItemSet = userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return badgesList.stream().map(a -> {
                    BadgesVO badgesVO = new BadgesVO();
                    BeanUtil.copyProperties(a, badgesVO);
                    badgesVO.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    badgesVO.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return badgesVO;
                }).sorted(Comparator.comparing(BadgesVO::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgesVO::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 佩戴徽章
     * @param badgeId
     */
    @Override
    public void wearingBadge(Long badgeId) {
        // 获取用户id
        Long uid = MyThreadLocal.getUser().getUid();
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, badgeId);
        AssertUtil.isNotEmpty(userBackpack,"您没有这个徽章哦，快去达成条件获取吧");
        // 确保物品类型是徽章
        ItemConfig item = itemConfigDao.getItemById(badgeId);
        AssertUtil.equal(item.getType(),ItemTypeEnum.BADGE.getType(),"只有徽章才能佩戴");
        // 佩戴徽章
        Boolean isWearSuccess = userDao.wearingBadge(uid, badgeId);
    }
}




