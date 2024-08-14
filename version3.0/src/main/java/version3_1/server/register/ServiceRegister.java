package version3_1.server.register;

import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/13 16:13
 * @Version 1.0
 */
public interface ServiceRegister {
    // 注册服务，保存服务和地址信息
    void register(String serviceName, InetSocketAddress serviceAddress);
}
