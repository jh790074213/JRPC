package version1_3.client.serviceCenter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author JH
 * @Date 2024/8/13 15:53
 * @Version 1.0
 */
public class ZKServiceCenter implements ServiceCenter{
    private CuratorFramework client;
    public static final String ROOT_PATH = "RPC";

    public ZKServiceCenter() {
        // 重试策略
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(retry)
                .namespace(ROOT_PATH).build();
        client.start();
        System.out.println("连接建立成功");
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            // TODO 这里需要负载均衡
            String s = strings.get(0);
            return parseAddress(s);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private InetSocketAddress parseAddress(String s) {
        String[] result = s.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
