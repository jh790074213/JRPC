package version4_1.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/13 15:46
 * @Version 3.0
 */
public interface ServiceCenter {
    /**
     * 根据服务名查找地址 IP+端口
     * @param serviceName 服务名
     * @return 地址信息
     */
    InetSocketAddress serviceDiscovery(String serviceName);
    /**
     * 判断是否可重试
     * @param serviceName 服务名
     * @return 是否可重试
     */
    boolean checkRetry(String serviceName) ;
}
