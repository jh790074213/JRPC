package version4_1.server.provider;


import lombok.Getter;
import version4_1.server.ratelimit.RateLimitProvider;
import version4_1.server.register.ServiceRegister;
import version4_1.server.register.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/9 21:11
 * @Version 4.0
 */
@Getter
public class ServiceProvider {
    // 保存接口类名，和实现类
    private Map<String,Object> interfaceProvider;
    private int port;
    private String host;
    private ServiceRegister serviceRegister;
    //限流器
    private RateLimitProvider rateLimitProvider;
    // 传入自身的IP和端口号
    public ServiceProvider(String host,int port){
        this.host = host;
        this.port = port;
        this.serviceRegister = new ZKServiceRegister();
        this.interfaceProvider = new HashMap<>();
        this.rateLimitProvider=new RateLimitProvider();
    }
    ////本地注册服务
    public void provideServiceInterface(Object service,boolean canRetry){
        Class<?>[] interfaceName=service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName){
            // 本地映射表
            interfaceProvider.put(clazz.getName(),service);
            // 注册中心注册IP
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port),canRetry);
        }
    }
    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}
