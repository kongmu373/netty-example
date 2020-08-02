package org.example.netty.nio.netty.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.netty.pojo.MyDataInfo;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Long> {

    /*
    读取客户端发来的消息
    ctx: 上下文对象, 管道pipeline, 通道channel(r/w),地址 ....
    msg: 客户端发送的数据，默认是Object对象
    */

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("Long: " + msg);
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
