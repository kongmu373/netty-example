package org.example.netty.nio.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向管道加入处理器

        // 得到管道
        ChannelPipeline pipeline = ch.pipeline();
        log.info("HttpServerInitializer: " + Thread.currentThread().getName());
        log.info("HttpServerInitializer: " + pipeline.hashCode());
        // netty 提供的 httpServerCodec  (http解释器)
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        // 增加一个自定义handler
        pipeline.addLast("MyCustomHttpHandler", new HttpServerHandler());
        System.out.println();
    }
}
