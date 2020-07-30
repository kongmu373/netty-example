package org.example.netty.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOServer {
    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(6666);
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket socket = serverSocketChannel.socket();
            socket.bind(address);

            ByteBuffer buffer = ByteBuffer.allocate(4096);

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                int readCount = socketChannel.read(buffer);
                while (readCount != -1) {

                    buffer.clear();
                    readCount = socketChannel.read(buffer);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
