package org.example.netty.nio.netty.dubbo.provider;

import org.example.netty.nio.netty.dubbo.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sendMessage(String msg) {
        System.out.println("客户端发过来的消息是: " + msg);
        if (msg != null) {
            return "我已经接收到你的消息了: " + "[" + msg + "]";
        }
        return "我已经收到你的请求了!";
    }
}
