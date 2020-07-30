package org.example.netty.nio.netty.intro;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * request: 创建 TCP 服务:
 * <ul>
 *     <li>Netty port: 6668 </li>
 *     <li>client send msg to server</li>
 *     <li>server resend msg to client</li>
 * </ul>
 * <p>
 * destination: 对 Netty 模型有一个初步认识
 * <p>
 * solution:
 * <ol>
 *     <li>编写 server</li>
 *     <li>编写 client</li>
 *     <li>源码分析</li>
 * </ol>
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException {
        // 创建 BossGroup 和 WorkerGroup
        // explain:
        // 1. 创建两个线程组 bossGroup 和 workerGroup
        // 2. boss 只处理连接请求， worker 处理业务请求
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            // 服务器的配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) // set boss and worker
                .channel(NioServerSocketChannel.class) // Channel implementation as a server
                .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列等待连接的个数
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    // 创建一个通道初始化对象,给pipeline 设置处理器
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                }); // 给我们的 workerGroup 的 eventLoop 对应的管道设置处理器
            log.info("... server is ready.");
            log.info("主服务器读取线程: {}", Thread.currentThread().getName());
            ChannelFuture cf = bootstrap.bind(6668).sync();
            cf.addListener((ChannelFutureListener) future -> {
                if (cf.isSuccess()) {
                    System.out.println("监听端口 6668 成功");
                } else {
                    System.out.println("监听端口 6668 失败");
                }
            });
            // 对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
