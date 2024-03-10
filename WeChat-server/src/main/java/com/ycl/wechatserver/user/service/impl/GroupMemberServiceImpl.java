package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.user.domain.entity.GroupMember;
import com.ycl.wechatserver.user.service.GroupMemberService;
import com.ycl.wechatserver.user.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember>
    implements GroupMemberService{

}




