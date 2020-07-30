package org.example.netty.nio.selector.groupchatsystem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6666;

    public Server() {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException {
        while (true) {
            int keyCount = selector.select(2000);
            if (keyCount == 0) {
                System.out.println("waiting...");
                continue;
            }
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();

                if (selectionKey.isAcceptable()) {
                    SocketChannel channel = listenChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println(channel.getRemoteAddress() + "上线了");
                    forwardMsg(channel.getRemoteAddress() + "上线了", channel);
                }

                if (selectionKey.isReadable()) {
                    readData(selectionKey);
                }

                keyIterator.remove();
            }
        }
    }

    public void readData(SelectionKey selectionKey) {
        String remoteAddress = "";
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) selectionKey.channel();
            remoteAddress = channel.getRemoteAddress().toString();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            StringBuilder sb = new StringBuilder();
            while (count != 0) {
                sb.append(new String(buffer.array()));
                buffer.clear();
                count = channel.read(buffer);
            }
            System.out.println("from 客户端: " + sb);
            forwardMsg(sb.toString(), channel);
        } catch (IOException e) {
            forwardMsg(remoteAddress + ",离线了...", channel);
            selectionKey.cancel();
            System.out.println(remoteAddress + ",离线了...");
        }
    }

    public void forwardMsg(String msg, SocketChannel self) {
        System.out.println("开始转发消息");
        List<SelectableChannel> list = selector.keys().stream()
                                           .map(SelectionKey::channel)
                                           .filter(key -> !key.equals(self))
                                           .collect(Collectors.toList());

        for (SelectableChannel channel : list) {
            if (channel instanceof SocketChannel) {
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                try {
                    ((SocketChannel) channel).write(buffer);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.listen();
    }
}
