package top.rpc;


import top.rpc.server.RpcServer;
import top.rpc.server.RpcServerImpl;
import top.rpc.service.HelloService;
import top.rpc.service.impl.HelloServiceImpl;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new RpcServerImpl(8989,5);
        //暴露HelloService接口，具体实现为HelloServiceImpl
        System.out.println(HelloService.class.getName());
        rpcServer.register(HelloService.class.getName(),HelloServiceImpl.class);
        //启动rpc服务
        rpcServer.start();
    }
}
