package org.example.netty.nio.netty.gcsys;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义一个 channel 组, 管理所有的channel
    // GlobalEventExecutor.INSTANCE 是全局的事件执行器,是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static ConcurrentMap<ChannelId, String> uniqueUsername = new ConcurrentHashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        channelGroup.add(ch);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        channelGroup.writeAndFlush(
            "[萌新]" + uniqueUsername.get(ctx.channel().id()) + ": 忍不了了,退群了!\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        uniqueUsername.remove(ctx.channel().id());
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        if (msg == null) {
            channel.writeAndFlush("兄弟,消息为空哦");
            return;
        }

        if (msg.startsWith("username:")) {
            String username = msg.split(":")[1];
            uniqueUsername.put(channel.id(), username);
            channelGroup.writeAndFlush("[萌新]" + username + ": 进群,大家开揍!\n");
            channel.writeAndFlush(0);
            return;
        }
        // 转发给全部
        if (msg.startsWith("0-")) {
            channelGroup.stream().filter(ch -> ch != channel)
                .forEach(ch -> ch.writeAndFlush(broadcast(msg, ch)));
            return;
        }
        // 转发给某人
        if (msg.contains("-")) {
            String[] split = msg.split("-");
            if (split.length == 2) {
                String username = split[0];
                uniqueUsername.entrySet().stream()
                    .filter(entry -> entry.getValue().trim().equals(username))
                    .map(entry -> channelGroup.find(entry.getKey()))
                    .forEach(ch -> sendMsgToSpec(split[1], channel, ch));
                return;
            }
        }

        // 格式不对
        channel.writeAndFlush("兄弟,输入的格式不对哦");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    private void sendMsgToSpec(String msg, Channel src, Channel dst) {
        dst.writeAndFlush("[用户]" + src.remoteAddress()
                              + "-"
                              + uniqueUsername.get(src.id()) + "对你说:"
                              + msg);
    }

    private String broadcast(String msg, Channel src) {
        return "[用户]" + src.remoteAddress()
                   + "-"
                   + uniqueUsername.get(src.id())
                   + "对所有人说: " + msg;
    }
}
