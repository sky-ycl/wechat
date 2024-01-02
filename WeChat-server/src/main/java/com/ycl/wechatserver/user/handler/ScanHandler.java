package com.ycl.wechatserver.user.handler;

import com.ycl.wechatserver.user.service.WXMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class ScanHandler extends AbstractHandler {


    @Resource
    private WXMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {

        return wxMsgService.scan(wxMpXmlMessage);
    }

}
