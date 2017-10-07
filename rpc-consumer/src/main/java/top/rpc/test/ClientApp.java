package top.rpc.test;

import top.rpc.client.proxy.RemoteServiceImpl;
import top.rpc.service.HelloService;

public class ClientApp {
    public static void main(String[] args) {
        //获取动态代理的HelloService的“真实对象（其实内部不是真实的，被换成了调用远程方法）”
        HelloService helloService = RemoteServiceImpl.newRemoteProxyObject(HelloService.class);
        String result = helloService.sayHello("hello world");
        System.out.println(result);
        //启动10个线程去请求
        for (int i = 0; i < 10; i++) {
            String result1 = helloService.sayHello("fangqiming");
            System.out.println(result1);
//            new Thread(){
//                @Override
//                public void run() {
//                    for (int j = 0; j < 10; j++) {
//                        String result = helloService.sayHello("yyf");
//                        System.out.println(result);
//                    }
//                }
//            }.start();

        }

    }
}
