package org.example.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 目的:验证BIO机制,来一个请求就新建一个线程与之通信
 * <p>
 * Note: 使用线程池优化
 */
public class BIOServer {
    public static void main(String[] args) throws IOException {
        // 1. create a thread pool
        // 2. if client connect to sever, create a thread to communicate it.

        ExecutorService pool = Executors.newCachedThreadPool();

        final ServerSocket socket = new ServerSocket(6666);
        System.out.println("server was started...");
        while (true) {
            System.out.println("Waiting for client to connect...");
            final Socket accept = socket.accept();
            System.out.println("Connect to a client...");
            pool.execute(() -> handler(accept));
        }
    }

    private static void handler(Socket socket) {
        try (final InputStream inputStream = socket.getInputStream()) {
            System.out.println("thread id : " + Thread.currentThread().getId()
                                   + "thread name: " + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            while (read != -1) {
                System.out.println("thread id : " + Thread.currentThread().getId()
                                       + "thread name: " + Thread.currentThread().getName());
                System.out.println(new String(bytes, 0, read));
                System.out.println("wait read...");
                read = inputStream.read(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("inputStream close...");
        }
    }
}
