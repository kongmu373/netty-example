package org.example.netty.nio.netty.protocoltcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count = 0;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        String message = new String(msg.getContent(), StandardCharsets.UTF_8);
        System.out.println("服务器收的数据: " + message);
        System.out.println("服务器收到的数据长度为: " + msg.getLen());
        System.out.println("服务器接收道的消息量 = " + (++this.count));

        ctx.writeAndFlush(Unpooled.copiedBuffer(UUID.randomUUID().toString(), StandardCharsets.UTF_8));
    }
}
