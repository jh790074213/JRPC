package version1_1.server.register;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/9 21:11
 * @Version 1.0
 */
public class ServiceRegister {
    private Map<String,Object> interfaceRegister;
    public ServiceRegister(){
        this.interfaceRegister = new HashMap<>();
    }
    ////本地注册服务
    public void provideServiceInterface(Object service){
        String serviceName=service.getClass().getName();
        Class<?>[] interfaceName=service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName){
            interfaceRegister.put(clazz.getName(),service);
        }

    }
    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceRegister.get(interfaceName);
    }
}
