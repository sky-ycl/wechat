package com.ycl.wechatserver.websocket;


import cn.hutool.json.JSONUtil;
import com.ycl.wechatserver.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            log.info("握手完成");
        }else if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state()== IdleState.READER_IDLE){
                System.out.println("读空闲");
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取文本
        String text = msg.text();
        log.info("发送的消息为:"+text);
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        if(wsBaseReq.getType()==1){
            System.out.println("登录成功");
        }else{
            System.out.println("登录失败");
        }
        // 发送消息给所有客户端
        String message="奥特曼打怪兽  打呀打呀";
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
        channelFuture.addListener(future -> {
            if(future.isSuccess()){
                log.info("消息发送成功");
            }else{
                log.info("消息发送失败");
            }
        });
    }
}