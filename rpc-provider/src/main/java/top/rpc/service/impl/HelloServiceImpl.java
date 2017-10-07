package top.rpc.service.impl;

import top.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {
    public String sayHello(String words) {
        System.out.println("hello:" + words);
        return "hello:" + words;
    }
}
