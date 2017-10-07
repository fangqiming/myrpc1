package top.rpc.server;

import java.util.concurrent.ConcurrentHashMap;

public class RegisterServicesCenter {
    //暴露接口的实现类存放容器              ConcurrentHashMap为线程安全的HashMap
    private static ConcurrentHashMap<String, Class> registerServices = new ConcurrentHashMap<>();

    //得到注册服务   也就是将整个HashMap返回出去
    public static ConcurrentHashMap<String, Class> getRegisterServices() {
        return registerServices;
    }

    //初始化HashMap
    public static void setRegisterServices(ConcurrentHashMap<String, Class> registerServices) {
        RegisterServicesCenter.registerServices = registerServices;
    }
}
