package org.example.netty.nio.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class NettyByteBuf01 {
    public static void main(String[] args) {
//        test01();
        test02();

    }

    private static void test02() {
        // create buf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", StandardCharsets.UTF_8);

        // 常用方法
        if (byteBuf.hasArray()) { // true
            byte[] content = byteBuf.array();

            // content 转为字符串
            System.out.println(new String(content, StandardCharsets.UTF_8));
            System.out.println(byteBuf.getCharSequence(0, 4, StandardCharsets.UTF_8));
            System.out.println("byteBuf = " + byteBuf);

        }
    }

    private static void test01() {
        // create a ByteBuf, buffer 该对象包含一个数组 arr, 是一个byte[10]
        ByteBuf buffer = Unpooled.buffer(10);
        // writeBytes
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.writeByte(i);
        }
        buffer.resetReaderIndex();
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.getByte(i));
        }
    }
}
