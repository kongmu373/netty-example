package org.example.netty.nio.netty.gcsys;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * request: 基于Netty的群聊系统
 *  <ol>
 *      <li>实现服务器端与客户端的简单通讯</li>
 *      <li>实现多人群聊</li>
 *      <li>对于服务器: 可以检测用户上线，离线,并实现消息转发功能</li>
 *      <li>对于客户端: 发送消息给其它在线的用户，及接收其他用户发送的用户</li>
 *  </ol>
 * <p>
 * destination:  进一步理解Netty的网络编程机制
 */
@Slf4j
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    // 编写 run 方法
    public void run() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup workers = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, workers)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast(new ServerHandler());
                    }
                });
            log.info("Server is running ... ");
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Server(8080).run();
    }
}
