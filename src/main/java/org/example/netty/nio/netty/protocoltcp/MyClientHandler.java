package org.example.netty.nio.netty.protocoltcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count = 0;
    private static List<String> list = new ArrayList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
            String msg = String.format("今天的风儿有点喧嚣阿~ 编号: %d, %s", i, list.toString());
            byte[] content = msg.getBytes(StandardCharsets.UTF_8);
            int len = content.length;
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setContent(content);
            messageProtocol.setLen(len);
            ctx.writeAndFlush(messageProtocol);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

    }
}
