package com.ycl.wechatserver.user.controller;

import com.ycl.wechatserver.common.domain.vo.response.ApiResult;
import com.ycl.wechatserver.user.domain.dto.ModifyNameDto;
import com.ycl.wechatserver.user.domain.dto.WearingBadgeDTO;
import com.ycl.wechatserver.user.domain.vo.BadgesVO;
import com.ycl.wechatserver.user.domain.vo.UserInfo;
import com.ycl.wechatserver.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameDto modifyNameDto){
        userService.modifyName(modifyNameDto);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgesVO>> getBadges(){
        List<BadgesVO> BadgesList=userService.getBadges();
        return ApiResult.success(BadgesList);
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeDTO wearingBadgeDTO){
        userService.wearingBadge(wearingBadgeDTO.getBadgeId());
        return ApiResult.success();
    }
}
