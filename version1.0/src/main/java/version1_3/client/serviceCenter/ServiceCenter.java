package version1_3.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/13 15:46
 * @Version 1.0
 */
public interface ServiceCenter {
    // 根据服务名查找地址 IP+端口
    InetSocketAddress serviceDiscovery(String serviceName);
}
