package org.example.netty.nio.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务器的配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) // set boss and worker
                .channel(NioServerSocketChannel.class) // Channel implementation as a server
                .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列等待连接的个数
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
//                .handler(null) // 该handler 对应的是 bossGroup,
                .childHandler(new HttpServerInitializer()); // 给我们的 workerGroup 的 eventLoop 对应的管道设置处理器
            log.info("... httpServer is ready.");
            ChannelFuture cf = bootstrap.bind(8080).sync();
            log.info("HttpServerInitializer: " + Thread.currentThread().getName());
            // 对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
