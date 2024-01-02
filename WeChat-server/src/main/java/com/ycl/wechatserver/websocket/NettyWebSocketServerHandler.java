package com.ycl.wechatserver.websocket;


import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ycl.wechatserver.websocket.domain.enums.WSReqTypeEnum;
import com.ycl.wechatserver.websocket.domain.vo.request.WSBaseReq;
import com.ycl.wechatserver.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private WebSocketService webSocketService;

    // 当客户与服务端建立刚连接的时候
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务端建立成功");
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("握手完成");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                userOffLine(ctx.channel());
            }

            //TODO 用户下线
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取文本
        String text = msg.text();
        log.info("发送的消息为:" + text);
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case AUTHORIZE:
                break;
            case LOGIN:
                log.info("请求二维码 = " + msg.text());
                webSocketService.handleLoginReq(ctx.channel());
                break;
            case HEARTBEAT:
                break;
            default:
                log.info("未知类型");
        }
    }

    /**
     * 用户下线
     *
     * @param channel
     */
    private void userOffLine(Channel channel) {
        // 移除用户的连接
        webSocketService.remove(channel);
    }

}
