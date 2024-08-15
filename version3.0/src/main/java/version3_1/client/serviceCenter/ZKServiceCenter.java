package version3_1.client.serviceCenter;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import version3_1.client.cache.ServiceCache;
import version3_1.client.serviceCenter.balance.LoadBalance;
import version3_1.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import version3_1.client.serviceCenter.balance.impl.RandomLoadBalance;
import version3_1.client.serviceCenter.balance.impl.RoundLoadBalance;
import version3_1.client.serviceCenter.watcher.ServiceWatcher;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author JH
 * @Date 2024/8/13 15:53
 * @Version 1.0
 */
@Slf4j
public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    public static final String ROOT_PATH = "RPC";
    private ServiceCache cache;
    public static final String RETRY = "CanRetry";

    public ZKServiceCenter() {
        // zk重试策略
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(retry)
                .namespace(ROOT_PATH).build();
        client.start();
        System.out.println("zookeeper连接建立成功");
        this.cache = new ServiceCache();
        // 加入zookeeper事件监听器
        ServiceWatcher watcher = new ServiceWatcher(client, cache);
        // 监听启动
        watcher.watchToUpdate("/");
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // 先从本地缓存中找
            List<String> serviceList = cache.getServiceFromCache(serviceName);
            // 如果找不到，再去zookeeper中找,只会出现在初始化阶段
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            LoadBalance loadBalance = new ConsistencyHashBalance();
            // 实现负载均衡
            String s = loadBalance.balance(serviceList);
            return parseAddress(s);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for (String s : serviceList) {
                // 如果列表中有该服务
                if (s.equals(serviceName)) {
                    log.info("服务" + serviceName + "在白名单上，可进行重试");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            log.error("查询白名单异常:" + e);
        }
        return canRetry;
    }

    private InetSocketAddress parseAddress(String s) {
        String[] result = s.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
