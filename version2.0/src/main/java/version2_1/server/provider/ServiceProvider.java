package version2_1.server.provider;


import version2_1.server.register.ServiceRegister;
import version2_1.server.register.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/9 21:11
 * @Version 1.0
 */
public class ServiceProvider {
    // 保存接口类名，和实现类
    private Map<String,Object> interfaceProvider;
    private int port;
    private String host;
    private ServiceRegister serviceRegister;
    // 传入自身的IP和端口号
    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.serviceRegister = new ZKServiceRegister();
        this.interfaceProvider = new HashMap<>();

    }
    ////本地注册服务
    public void provideServiceInterface(Object service){
        String serviceName=service.getClass().getName();
        Class<?>[] interfaceName=service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName){
            // 本地映射表
            interfaceProvider.put(clazz.getName(),service);
            // 注册中心注册IP
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port));
        }

    }
    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
