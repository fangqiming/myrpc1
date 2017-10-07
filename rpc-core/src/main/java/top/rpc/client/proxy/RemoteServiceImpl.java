package top.rpc.client.proxy;

import java.lang.reflect.Proxy;

public class RemoteServiceImpl<T> {
    /**
     * 动态代理的真实对象的实现
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T newRemoteProxyObject(final Class<?> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new ProxyHandler(service));
    }
}
