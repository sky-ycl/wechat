package com.ycl.wechatserver.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlPath;
import cn.hutool.core.net.url.UrlQuery;
import com.ycl.wechatserver.utils.NettyUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

public class MyHandshakeHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);

            // 如果token存在
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            // 移除后面拼接的所有参数
            request.setUri(urlBuilder.getPath().toString());
            // 获取用户ip(判断nginx是否有ip)
            String ip = request.headers().get("X-Real-IP");
            if(StringUtils.isBlank(ip)){
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip  = address.getAddress().getHostAddress();
            }
            // 保存到channel附件中
            NettyUtil.setAttr(ctx.channel(),NettyUtil.IP,ip) ;
            // 处理器只需要处理一次
            ctx.pipeline().remove(this);
        }
        super.channelRead(ctx, msg);
    }
}
