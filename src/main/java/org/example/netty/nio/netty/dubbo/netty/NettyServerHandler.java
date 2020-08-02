package org.example.netty.nio.netty.dubbo.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.netty.nio.netty.dubbo.provider.HelloServiceImpl;

import java.util.concurrent.Callable;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    private ChannelHandlerContext ctx;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("msg = " + msg);
        if (msg.startsWith("HelloService#hello#")) {
            String message = new HelloServiceImpl().sendMessage(msg.substring(msg.lastIndexOf("#") + 1));
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


}
