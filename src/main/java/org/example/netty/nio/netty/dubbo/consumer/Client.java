package org.example.netty.nio.netty.dubbo.consumer;

import org.example.netty.nio.netty.dubbo.netty.NettyClient;
import org.example.netty.nio.netty.dubbo.service.HelloService;

public class Client {

    public static final String provider = "HelloService#hello#";

    public static void main(String[] args) {
        NettyClient customer = new NettyClient();
        HelloService service = (HelloService) customer.getBean(HelloService.class, provider);

        String s = service.sendMessage("hello!");
        System.out.println(s);

    }
}
