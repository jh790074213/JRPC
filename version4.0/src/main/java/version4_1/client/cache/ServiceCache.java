package version4_1.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author JH
 * @Date 2024/8/14 15:35
 * @Version 3.0
 */
public class ServiceCache {
    //key: serviceName 服务名
    //value： addressList 服务提供者ip:port列表
    private static Map<String, List<String>> cache=new HashMap<>();

    //添加服务
    public void addServiceToCache(String serviceName,String address){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            System.out.println("将name为"+serviceName+"和地址为"+address+"的服务添加到本地缓存中");
        }else {
            List<String> addressList=new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName,addressList);
        }
    }
    //修改服务地址
    public void replaceServiceAddress(String serviceName,String oldAddress,String newAddress){
        if(cache.containsKey(serviceName)){
            List<String> addressList=cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
        }else {
            System.out.println("修改失败，服务不存在");
        }
    }
    //从缓存中取服务地址
    public  List<String> getServiceFromCache(String serviceName){
        if(!cache.containsKey(serviceName)) {
            return null;
        }
        List<String> a=cache.get(serviceName);
        return a;
    }
    //从缓存中删除服务地址
    public void delete(String serviceName,String address){
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        System.out.println("将name为"+serviceName+"和地址为"+address+"的服务从本地缓存中删除");
    }
}
