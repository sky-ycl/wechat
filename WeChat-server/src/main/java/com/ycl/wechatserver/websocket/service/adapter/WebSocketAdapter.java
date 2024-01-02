package com.ycl.wechatserver.websocket.service.adapter;

import com.ycl.wechatserver.user.domain.entity.User;
import com.ycl.wechatserver.websocket.domain.enums.WSReqTypeEnum;
import com.ycl.wechatserver.websocket.domain.enums.WSRespTypeEnum;
import com.ycl.wechatserver.websocket.domain.vo.response.WSBaseResp;
import com.ycl.wechatserver.websocket.domain.vo.response.WSLoginSuccess;
import com.ycl.wechatserver.websocket.domain.vo.response.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {

    public static WSBaseResp<?> buildLoginUrlResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> request = new WSBaseResp<>();
        request.setType(WSReqTypeEnum.LOGIN.getType());
        request.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return request;
    }


    public static WSBaseResp<?> buildLoginResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> request=new WSBaseResp<>();
        request.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .build();
        request.setData(wsLoginSuccess);
        return request;
    }

    public static WSBaseResp<?> buildWaitAuthorize() {
        WSBaseResp<WSLoginUrl> request = new WSBaseResp<>();
        request.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return request;
    }
}
