package version3_1.server.register;

import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/13 16:13
 * @Version 3.0
 */
public interface ServiceRegister {
    /**
     * 注册服务
     * @param serviceName 服务名
     * @param serviceAddress 服务器ip:port
     * @param canRetry 服务是否幂等
     */
    void register(String serviceName, InetSocketAddress serviceAddress,boolean canRetry);
}
