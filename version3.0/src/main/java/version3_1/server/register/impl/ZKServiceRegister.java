package version3_1.server.register.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import version3_1.server.register.ServiceRegister;

import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/13 16:15
 * @Version 3.0
 */
@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    private CuratorFramework client;
    private static final String ROOT_PATH = "RPC";
    private static final String RETRY = "CanRetry";
    public ZKServiceRegister(){
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
    }
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress,boolean canRetry) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if(client.checkExists().forPath("/" + serviceName) == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName +"/"+ getServiceAddress(serviceAddress);
            // 临时节点，服务器下线就删除节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            //如果这个服务是幂等性，就增加到节点中
            if (canRetry){
                path ="/"+RETRY+"/"+serviceName;
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } catch (Exception e) {
            log.error("注册服务异常:" + e);
        }
    }

    private String getServiceAddress(InetSocketAddress serviceAddress) {
        return serviceAddress.getHostName() +
                ":" +
                serviceAddress.getPort();
    }
    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
