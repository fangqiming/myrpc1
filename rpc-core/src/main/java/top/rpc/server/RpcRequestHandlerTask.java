package top.rpc.server;


import top.rpc.RpcContext;
import top.rpc.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Random;

/**
 * 接受远程调用并调用真实方法后响应
 */
public class RpcRequestHandlerTask implements Runnable {
    private Socket socket;

    public RpcRequestHandlerTask(Socket accept) {
        this.socket = accept;                       //线程启动后该socket被初始化
    }

    @Override
    public void run() {
        acceptAndResponce();
    }

    private void acceptAndResponce() {              //此处为接受到请求后执行的方法
        //这里使用ObjectOutputStream ObjectInputStream，直接通过对象传输，所以传输的对象必须实现了序列化接口，序列化这块可以优化
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        try {
            //模拟作业时间
            long time = 100 * new Random().nextInt(10);                         //模拟CPU执行任务的时间
            is = new ObjectInputStream(socket.getInputStream());                //获取到对象输入流
            //读取第一部分数据
            RpcContext context = (RpcContext) is.readObject();                  //将读取的对象强制转化为RPC上下文
            System.out.println("执行任务需要消耗：" + time + " " + context);     //执行任务需要消耗的时间
            Thread.sleep(time);                                                 //睡眠
            //从容器中得到已经注册的类
            Class clazz = RegisterServicesCenter.getRegisterServices().get(context.getServiceName());   //从容器中得到注册的类
            if (clazz == null) {
                throw new RpcException("没有找到类 " + context.getServiceName());                        //如果没有找到就报异常
            }
            Method method = clazz.getMethod(context.getMethodName(), context.getParameterTypes());      //得到方法
            if (method == null) {
                throw new RpcException("没有找到相应方法 " + context.getMethodName());                   //如果没有找到就报异常
            }
            //执行真正要调用的方法。对于实例都不是单利模式的，每次都clazz.newInstance()，有待改进
            Object result = method.invoke(clazz.newInstance(), context.getArguments());                 //此处存在问题，假设被远程调用的方法用到了对象内部的属性，那么就会导致调用失败
            os = new ObjectOutputStream(socket.getOutputStream());                                      //将得到对象写入到输出流中
            //返回执行结果给客户端
            os.writeObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
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
}
