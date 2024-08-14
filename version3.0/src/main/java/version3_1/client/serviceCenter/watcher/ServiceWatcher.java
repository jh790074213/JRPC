package version3_1.client.serviceCenter.watcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import version3_1.client.cache.ServiceCache;

/**
 * @Author JH
 * @Date 2024/8/14 15:41
 * @Version 1.0
 */
@AllArgsConstructor
@Getter
public class ServiceWatcher {
    private CuratorFramework client;
    private ServiceCache cache;
    public void watchToUpdate(String path){
        CuratorCache curatorCache = CuratorCache.build(client, path);
        // type 事件类型NODE_CREATED,NODE_CHANGED,NODE_DELETED;childData 更新前状态、数据，childData1 更新后状态、数据
        curatorCache.listenable().addListener((type, childData, childData1) -> {
            switch (type.name()){
                case "NODE_CREATED":
                    String[] paths = childData1.getPath().split("/");
                    // "/RPC/服务名/ip:port"
                    if(paths.length <= 2) break;
                    else {
                        String serviceName = paths[1];
                        String address = paths[2];
                        cache.addServiceToCache(serviceName,address);
                    }
                    break;
                case "NODE_DELETED": // 节点删除
                    String[] pathList_d= childData.getPath().split("/");
                    if(pathList_d.length<=2) break;
                    else {
                        String serviceName=pathList_d[1];
                        String address=pathList_d[2];
                        //将新注册的服务加入到本地缓存中
                        cache.delete(serviceName,address);
                    }
                    break;
                default:
                    break;
            }
        });
        // 开启监听
        curatorCache.start();
    }

}
