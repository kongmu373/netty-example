package org.example.netty.nio.netty.codec;

import static io.netty.util.CharsetUtil.UTF_8;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.netty.pojo.MyDataInfo;

import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Long> {

    /**
     * 当通道就绪的时候，就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive: ctx: {}", ctx);
        long random = new Random().nextLong();
        ctx.writeAndFlush(random);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("msg :" + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}
