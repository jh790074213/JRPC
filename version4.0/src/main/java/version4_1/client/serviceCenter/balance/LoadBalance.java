package version4_1.client.serviceCenter.balance;

import java.util.List;

/**
 * @Author JH
 * @Date 2024/8/14 16:40
 * @Version 3.0
 */
public interface LoadBalance {
    /**
     * 实现负载均衡，返回分配的地址
     * @param addressList 地址列表
     * @return 分配的地址
     */
    String balance(List<String> addressList);

    /**
     * 添加节点
     * @param node 需要添加的节点
     */
    void addNode(String node) ;

    /**
     * 删除节点
     * @param node 需要删除的节点
     */
    void delNode(String node);
}
