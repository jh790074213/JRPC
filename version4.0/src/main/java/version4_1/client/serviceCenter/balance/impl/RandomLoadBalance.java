package version4_1.client.serviceCenter.balance.impl;

import version4_1.client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @Author JH
 * @Date 2024/8/14 16:45
 * @Version 3.0
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random=new Random();
        int choose = random.nextInt(addressList.size());
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
