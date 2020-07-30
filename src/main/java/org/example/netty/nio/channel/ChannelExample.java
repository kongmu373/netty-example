package org.example.netty.nio.channel;

import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 关于 buffer 和 Channel 的 练习.
 */
public class ChannelExample {
    public static void main(String[] args) throws Exception {
//        test01();
//        test02();
//        test03();
//        test04();
//        test05();
//        test06();
//        test07();
        test08();
    }

    /**
     * request: 利用 buffer 和 channel 将"Hello world!" 写入到 file01.txt, 文件不存在，则创建
     */
    public static void test01() throws IOException {
        String str = "Hello word!";
        FileOutputStream fileOutputStream = new FileOutputStream("file01.txt");
        FileChannel channel = fileOutputStream.getChannel();
        /*
         另一种方案:
         ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
         byteBuffer.put(str.getBytes());
         byteBuffer.flip()
         */
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        channel.write(byteBuffer);
        channel.close();
    }

    /**
     * 将 file "file01.txt" 读入到程序，并控制台输出.
     */
    public static void test02() throws Exception {
        FileInputStream inputStream = new FileInputStream("file01.txt");
        FileChannel channel = inputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        channel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        channel.close();
    }

    /**
     * 使用 FileChannel的read,write 方法，完成文件的拷贝
     */
    public static void test03() throws Exception {
        File src = new File("file01.txt");
        File dst = new File("file02.txt");
        if (!dst.exists()) {
            dst.createNewFile();
        }
        FileChannel dstChannel = FileChannel.open(dst.toPath(), WRITE);
        FileChannel srcChannel = FileChannel.open(src.toPath());
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        while (true) {
            byteBuffer.clear();
            int read = srcChannel.read(byteBuffer);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            dstChannel.write(byteBuffer);
        }
        srcChannel.close();
        dstChannel.close();
    }

    /**
     * 使用 TransferFrom 拷贝 文件
     */
    public static void test04() throws Exception {
        File src = new File("file01.txt");
        FileChannel srcChannel = FileChannel.open(src.toPath());
        File dst = new File("file03.txt");
        if (!dst.exists()) {
            dst.createNewFile();
        }
        FileChannel dstChannel = FileChannel.open(dst.toPath(), WRITE);
        dstChannel.transferFrom(srcChannel, 0, src.length());
        srcChannel.close();
        dstChannel.close();
    }

    /**
     * ByteBuffer的get和put的数据类型应该是同一种 ，否则可能抛出异常:
     * Exception in thread "main" java.nio.BufferUnderflowException
     */
    public static void test05() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putInt(Integer.MAX_VALUE);
        byteBuffer.flip();
        System.out.println(byteBuffer.getLong());
    }

    /**
     * 将普通buffer 转变成 只读buffer.
     */
    public static void test06() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        // class java.nio.HeapByteBuffer
        System.out.println(buffer.getClass());
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }

        buffer.flip();

        // change
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        // class java.nio.HeapByteBufferR
        System.out.println(readOnlyBuffer.getClass());
        while (readOnlyBuffer.hasRemaining()) {
            System.out.println(readOnlyBuffer.get());
        }
//        Exception in thread "main" java.nio.ReadOnlyBufferException
        readOnlyBuffer.put((byte) 2323);
    }

    /**
     * MappedByteBuffer example:
     * 1. 可以让文件直接在内存(堆外内存)修改
     */
    public static void test07() throws Exception {
        RandomAccessFile rw = new RandomAccessFile("file01.txt", "rw");
        FileChannel channel = rw.getChannel();
        /**
         * FileChannel.MapMode.READ_WRITE 使用的读写模式
         * 0 : position 指针起始位置
         * 5 : size, 映射到内存的大小
         */
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        map.put(0, (byte) 'h');
        map.put(3, (byte) '9');
        channel.close();
    }

    /**
     * Scattering and Gathering
     * Scattering: 将数据写入到buffer时，可以采用buffer数组，依次写入
     * Gathering:  将数据从buffer写入到channel时,可以采用buffer数组,依次写入
     * demo: 使用ServerSocketChannel与 SocketChannel
     */
    public static void test08() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(7000);
        serverSocketChannel.socket().bind(address);

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        SocketChannel socketChannel = serverSocketChannel.accept();
        int len = 8; // 假定从客户端接收8个字节
        // 循环的读取
        while (true) {
            int byteRead = 0;
            while (byteRead < len) {
                System.out.println("开始读取...");
                long l = socketChannel.read(byteBuffers);
                byteRead += l;
                System.out.println("byteRead: " + byteRead);
                Arrays.stream(byteBuffers)
                    .map(byteBuffer -> "position: " + byteBuffer.position() + ", limit: " + byteBuffer.limit())
                    .forEach(System.out::println);
            }
            Arrays.stream(byteBuffers).forEach(Buffer::flip);
            long byteWrite = 0;
            while (byteWrite < len) {
                long l = socketChannel.write(byteBuffers);
                byteWrite += l;
            }
            Arrays.stream(byteBuffers).forEach(Buffer::clear);
            System.out.println("byteRead: " + byteRead + " byteWrite: " + byteWrite + " byteLength: " + len);
        }
    }
}
