package com.ycl.wechatserver.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlPath;
import cn.hutool.core.net.url.UrlQuery;
import com.ycl.wechatserver.utils.NettyUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

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

            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            request.setUri(urlBuilder.getPath().toString());

        }
        super.channelRead(ctx, msg);
    }
}
