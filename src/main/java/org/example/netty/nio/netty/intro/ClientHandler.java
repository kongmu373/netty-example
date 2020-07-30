package org.example.netty.nio.netty.intro;

import static io.netty.util.CharsetUtil.UTF_8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪的时候，就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive: ctx: {}", ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,server!", UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead: ctx: {}", ctx);
        log.info("channelRead: msg: {}", msg);
        ByteBuf buf = (ByteBuf) msg;
        log.info("server's msg: {}", buf.toString(UTF_8));
        log.info("server's remote address: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}
