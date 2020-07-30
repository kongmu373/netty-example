package org.example.netty.nio.buffer;

import java.nio.IntBuffer;

/**
 * example: buffer 与 程序之间的通信
 */
public class BasicBuffer {
    public static void main(String[] args) {
        //  创建 buffer , 并分配空间
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity() - 1; i++) {
            intBuffer.put(i * 2);
        }
        // 读写切换
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
