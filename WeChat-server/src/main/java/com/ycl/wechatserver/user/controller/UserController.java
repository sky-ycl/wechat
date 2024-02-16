package com.ycl.wechatserver.user.controller;

import com.ycl.wechatserver.common.domain.dto.UserDto;
import com.ycl.wechatserver.common.domain.vo.response.ApiResult;
import com.ycl.wechatserver.common.interceptor.TokenInterceptor;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.UserService;
import com.ycl.wechatserver.utils.MyThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfo> getUserinfo(){
        UserInfo userinfo = userService.getUserinfo();
        return ApiResult.success(userinfo);
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public void modifyName(@Valid @RequestBody ModifyNameDto modifyNameDto){
        userService.modify(modifyNameDto);
    }
}
