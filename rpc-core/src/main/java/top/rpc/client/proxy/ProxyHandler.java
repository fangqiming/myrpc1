package top.rpc.client.proxy;


import top.rpc.RpcContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

/**
 * 动态代理处理程序
 */
public class ProxyHandler implements InvocationHandler {
    private long timeout = 1000;                                //超时等待时间
    private Class<?> service;                                   //被代理类
    //远程调用地址   InetSocketAddress该对象是可序列化的
    private InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 8989);     //远端地址

    public ProxyHandler(Class<?> service) {
        this.service = service;
    }                       //构造方法

    public Object invoke(Object object, Method method, Object[] args) throws Throwable {    //实际执行的方法
        //准备传输的对象
        RpcContext rpcContext = new RpcContext();                                           //声明一个rpc上下文
        rpcContext.setServiceName(service.getName());                                       //设置代理类的名称（服务端HashMap的key）
        rpcContext.setMethodName(method.getName());                                         //设置方法的名称
        rpcContext.setRemoteAddress(remoteAddress);                                         //设置远端的地址
        rpcContext.setArguments(args);                                                      //设置方法的参数
        rpcContext.setParameterTypes(method.getParameterTypes());                           //设置方法的参数类型

        return this.request(rpcContext);                                                    //开始请求远端方法
    }

    private Object request(RpcContext rpcContext) throws ClassNotFoundException {
        //使用线程池，主要是为了下面使用Future，异步得到结果，来做超时放弃处理
        ExecutorService executor = Executors.newFixedThreadPool(1);                         //声明一个线程池
        Object result = null;                                                               //设置结果
        Socket socket = null;                                                               //声明请求socket
        ObjectOutputStream os = null;
        ObjectInputStream is = null;


         Future future = executor.submit(new Callable() {                                   //
            public Object call() throws Exception {
                //执行并返回远程调用结果
                return request(rpcContext, socket, os, is);
            }
        });

        try {
            result = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("请求响应超时放弃..");
            release(socket, os, is);
            future.cancel(true);
        }
        executor.shutdown();
        return result;
    }

    /**
     * 远程调用请求
     * @param rpcContext
     * @param socket
     * @param os
     * @param is
     * @return
     * @throws ClassNotFoundException
     */
    private Object request(RpcContext rpcContext, Socket socket, ObjectOutputStream os, ObjectInputStream is) throws ClassNotFoundException {
        Object result = null;
        try {
            socket = new Socket(remoteAddress.getAddress(), remoteAddress.getPort());
            os = new ObjectOutputStream(socket.getOutputStream());
            os.writeObject(rpcContext);
//             shutdownOutput():执行此方法，显示的告诉服务端发送完毕
            socket.shutdownOutput();
            //阻塞等待服务器响应
            is = new ObjectInputStream(socket.getInputStream());
            result = is.readObject();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            release(socket, os, is);
        }
        return result;
    }

    private void release(Socket socket, ObjectOutputStream os, ObjectInputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
