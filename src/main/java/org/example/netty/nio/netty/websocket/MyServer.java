package org.example.netty.nio.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * request: Http协议是无状态的,实现基于webSocket的长连接的全双工的交互
 */
public class MyServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO)) // 在bossGroup 增加一个日志处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 因为基于http协议, 使用http的编码和解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 是以块方式写的
                        pipeline.addLast(new ChunkedWriteHandler());
                        /*
                        1. http数据在传输过程中是分段的
                        2. 该处理器，可以将多个段聚合起来
                        3. 如果浏览器发送大量数据时，就会发出多次http请求
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        /*
                        1. 对应websocket, 它的数据是以 帧(frame)的形式传递的
                        2. 可以看到WebSocketFrame 下面有六个子类
                        3. 浏览器请求 ws;//localhost:7000/xxx 表示请求的uri
                        4. WebSocketServerProtocolHandler 核心功能是将 http协议升级为 ws 协议, 保持长连接
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        // 业务 handler
                        pipeline.addLast(new MyTextWebSocketFrameHandler());

                    }
                });
            ChannelFuture future = serverBootstrap.bind(7000).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
