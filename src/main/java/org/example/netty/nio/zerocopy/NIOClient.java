package org.example.netty.nio.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 6666));
        String filename = "a.zip";
        FileChannel fileChannel = new FileInputStream(filename).getChannel();
        long start = System.currentTimeMillis();
        long perByte = 8 * 1024 * 1024;
        long res = 0;
        long count;
        long index = 0;
        do {
            count = fileChannel.transferTo(perByte * index, perByte, socketChannel);
            res += count;
            index++;
        } while (count > 0);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
        System.out.println("发生的总字节数为: " + res);
        fileChannel.close();
    }
}
