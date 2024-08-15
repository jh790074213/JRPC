package version4_1.client.serviceCenter.balance.impl;

import version4_1.client.serviceCenter.balance.LoadBalance;

import java.util.*;

/**
 * @Author JH
 * @Date 2024/8/14 16:50
 * @Version 3.0
 */
public class ConsistencyHashBalance implements LoadBalance {
    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是，真实节点+"&&VN" + 序号
    private SortedMap<Integer, String> shards = new TreeMap<>();

    // 真实节点列表
    private List<String> realNodes = new LinkedList<>();

    /**
     * 初始化真实节点列表和虚拟节点
     * @param serviceList 节点的ip:port
     */
    private  void init(List<String> serviceList) {
        for (String server :serviceList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
        System.out.println("真实节点列表："+realNodes);
    }

    /**
     * 初始化和获得指定
     * @param node 根据node分配节点
     * @param serviceList 节点列表
     * @return 节点信息
     */
    public  String getServer(String node,List<String> serviceList) {
        init(serviceList);
        int hash = getHash(node);
        Integer key = null;
        // shards.tailMap(hash)获取比hash大的Map
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            // 得到顺时针最近的map的键
            key = shards.lastKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }

    @Override
    public void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     * @param str 需要计算hash的值
     * @return hash值
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
    @Override
    public String balance(List<String> addressList) {
        String random= UUID.randomUUID().toString();
        return getServer(random,addressList);
    }
}
