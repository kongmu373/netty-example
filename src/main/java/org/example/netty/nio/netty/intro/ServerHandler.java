package org.example.netty.nio.netty.intro;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /*
    读取客户端发来的消息
    ctx: 上下文对象, 管道pipeline, 通道channel(r/w),地址 ....
    msg: 客户端发送的数据，默认是Object对象
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead ctx: {}", ctx);
        // 将 msg 转成一个 ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        log.info("客户端发送的消息是:, {}", buf.toString(CharsetUtil.UTF_8));
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();
        log.info("客户端地址 : {} ", channel.remoteAddress());
    }

    /**
     * 数据读取完毕后
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("服务器读取线程: {}", Thread.currentThread().getName());
        log.info("channelReadComplete");
        ctx.channel().eventLoop().execute(() -> {

            try {
                Thread.sleep(10 * 1000);
                log.info("延迟发送的 服务器读取线程 10秒的: {}", Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("延迟发送", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        ctx.channel().eventLoop().execute(() -> {

            try {
                Thread.sleep(5 * 1000);
                log.info("延迟发送的 服务器读取线程 5秒的: {}", Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("延迟发送", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 自定义任务 -> scheduleTaskQueue
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(2 * 1000);
                log.info("延迟发送的 服务器读取线程 2秒的: {}", Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("延迟发送", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2, TimeUnit.SECONDS);

        // write and flush
        // 要对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, world!", CharsetUtil.UTF_8));
    }


    /**
     * Handle exception
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}
