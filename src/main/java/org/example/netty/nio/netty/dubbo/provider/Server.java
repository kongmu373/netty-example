package org.example.netty.nio.netty.dubbo.provider;

import org.example.netty.nio.netty.dubbo.netty.NettyServer;

public class Server {
    public static void main(String[] args) {
        NettyServer.startServer(7000);
    }
}
