package org.example.netty.nio.netty.protobuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.netty.pojo.MyDataInfo;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {

    /*
    读取客户端发来的消息
    ctx: 上下文对象, 管道pipeline, 通道channel(r/w),地址 ....
    msg: 客户端发送的数据，默认是Object对象
    */

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

        log.info("channelRead ctx: {}", ctx);
        MyDataInfo.MyMessage.DataType dataType = msg.getDataType();
        if(dataType == MyDataInfo.MyMessage.DataType.StudentType) {
            MyDataInfo.Student student = msg.getStudent();
            System.out.println("客户端发送的数据 id= " + student.getId()
                                   + " 名字=" + student.getName());
            return;
        }

        if (dataType == MyDataInfo.MyMessage.DataType.WorkerType) {
            MyDataInfo.Worker worker = msg.getWorker();
            System.out.println("客户端发送的数据 id= " + worker.getName()
                                   + " 名字=" + worker.getName());
            return;
        }

        System.out.println("传输的类型不正确");
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
