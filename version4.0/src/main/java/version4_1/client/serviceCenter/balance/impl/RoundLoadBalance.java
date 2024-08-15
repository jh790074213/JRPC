package version4_1.client.serviceCenter.balance.impl;

import version4_1.client.serviceCenter.balance.LoadBalance;

import java.util.List;

/**
 * @Author JH
 * @Date 2024/8/14 16:43
 * @Version 3.0
 */
public class RoundLoadBalance implements LoadBalance {
    private static int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose=choose%addressList.size();
        System.out.println("负载均衡选择了"+choose+"号服务器");
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
