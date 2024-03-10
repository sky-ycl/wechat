package com.ycl.wechatserver.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycl.wechatserver.user.domain.entity.Contact;
import com.ycl.wechatserver.user.service.ContactService;
import com.ycl.wechatserver.user.mapper.ContactMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact>
    implements ContactService{

}




