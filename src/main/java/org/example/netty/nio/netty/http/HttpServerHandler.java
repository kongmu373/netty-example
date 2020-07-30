package org.example.netty.nio.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.regex.Pattern;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final String PATTERN = ".*\\.ico$";

    // 创建 Pattern 对象
    private final Pattern R = Pattern.compile(PATTERN);

    private static ChannelGroup cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded: {} ", Thread.currentThread().getName());
        cg.add(ctx.channel());
    }

    // channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断 msg 是不是一个 httpRequest
        log.info("channelRead0: " + Thread.currentThread().getName());
        log.info("channelRead0: " + ctx.pipeline().hashCode());
        if (msg instanceof HttpRequest) {
            log.info("msg 类型: {}", msg.getClass());
            log.info("remote address: {}", ctx.channel().remoteAddress());
            HttpRequest request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            log.info(Thread.currentThread().getName());
            log.info("pipeline: {}", ctx.pipeline().hashCode());
            if (R.matcher(uri.toString()).find()) {
                log.info("忽略.ico文件");
                return;
            }
            // 回复信息给浏览器(遵循 http 协议)
            ByteBuf content = Unpooled.copiedBuffer("hello，我是掉毛", CharsetUtil.UTF_8);

            // httpResponse
            FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
