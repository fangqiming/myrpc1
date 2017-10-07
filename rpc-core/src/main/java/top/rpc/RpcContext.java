package top.rpc;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class RpcContext implements Serializable{
    private String serviceName;                     //服务名

    private String methodName;                      //方法名

    private Class<?>[] parameterTypes;              //参数类型

    private Object[] arguments;                     //参数值

    private InetSocketAddress localAddress;         //本地地址

    private InetSocketAddress remoteAddress;        //远端地址
    private long timeout = 10000;                   //默认超时时间  10秒

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String toString() {
        return "RpcContext{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", arguments=" + Arrays.toString(arguments) +
                ", localAddress=" + localAddress +
                ", remoteAddress=" + remoteAddress +
                ", timeout=" + timeout +
                '}';
    }
}
