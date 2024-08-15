package version4_1.client.proxy;


import version4_1.client.retry.GuavaRetry;
import version4_1.client.rpcClient.RpcClient;
import version4_1.client.rpcClient.impl.NettyRpcClient;
import version4_1.client.serviceCenter.ServiceCenter;
import version4_1.client.serviceCenter.ZKServiceCenter;
import version4_1.comment.domain.message.RpcRequest;
import version4_1.comment.domain.message.RpcResponse;

import java.lang.reflect.Proxy;


/**
 * @Author JH
 * @Date 2024/8/9 20:18
 * @Version 3.0
 */
public class ClientProxy {
    // 定义接口动态指定实现类
    private final RpcClient rpcClient;
    private final ServiceCenter serviceCenter;
    public ClientProxy(){
        serviceCenter = new ZKServiceCenter();
        rpcClient=new NettyRpcClient(serviceCenter);
    }
    /**
     * 创建代理对象进行消息的发送
     * @param clazz 被代理的对象
     * @return 执行后的结果
     * @param <T> 被代理对象的类型
     */
    @SuppressWarnings("unchecked")
    public <T>T createProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    RpcRequest request = RpcRequest.builder()
                            .interfaceName(method.getDeclaringClass().getName())
                            .methodName(method.getName())
                            .params(args)
                            .paramsType(method.getParameterTypes())
                            .build();
                    //数据传输
                    RpcResponse response;
                    //后续添加逻辑：为保持幂等性，只对白名单上的服务进行重试
                    if (serviceCenter.checkRetry(request.getInterfaceName())){
                        //调用retry框架进行重试操作
                        response= GuavaRetry.sendServiceWithRetry(request,rpcClient);
                    }else {
                        //只调用一次
                        response= rpcClient.sendRequest(request);
                    }
                    return response.getData();
                });
    }
}
