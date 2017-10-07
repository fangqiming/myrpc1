package top.rpc.server;

import top.rpc.exception.RpcException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServerImpl implements RpcServer {
    private int nThreads = 10;          //线程数 10
    private boolean isAlive = false;    //是否存活       默认为否
    private int port = 8989;            //注册的端口号   默认为 8989
    private ExecutorService executor;   //线程池

    public RpcServerImpl() {
        init();
    }   //默认的构造函数


    public RpcServerImpl(int port, int nThreads) {  //带参构造函数
        this.port = port;
        this.nThreads = nThreads;
        init();                                     //初始化线程池
    }

    public void init() {
        executor = Executors.newFixedThreadPool(nThreads);
    }   //初始化线程池

    @Override
    public void start() {                                                       //开启rpc服务 开启之前想要注册
        isAlive = true;                                                         //标记服务已经开启
        ServerSocket serverSocket = null;                                       //声明一个服务端socket
        try {
            serverSocket = new ServerSocket(port);                              //监听的端口
            System.out.println("Rpc服务启动成功...");                            //rpc服务开启
            while (true) {                                                      //死循环
                executor.execute(new RpcRequestHandlerTask(serverSocket.accept()));   //开启一个线程
                System.out.println("执行一次响应.." + new Date());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stop() {
        isAlive = false;
        executor.shutdown();
    }

    @Override
    public void register(String className, Class clazz) throws Exception {          //注册服务
        //RegisterServicesCenter 服务注册中心
        if (RegisterServicesCenter.getRegisterServices() != null) {                 //判断缓存是否已经存在该接口的代理
            RegisterServicesCenter.getRegisterServices().put(className, clazz);     //将被代理的接口 与具体的代理类放入到容器中
        } else {
            throw new RpcException("RPC服务未初始化");                               //如果为空，就表明rpc容器暂时还没有准备好
        }
    }

    @Override
    public boolean isAlive() {                                                      //判断师傅存活
        String status = (this.isAlive == true) ? "RPC服务已经启动" : "RPC服务已经关闭";
        System.out.println(status);
        return this.isAlive;
    }
}
