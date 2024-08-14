package version3_1.client.proxy;


import version3_1.client.rpcClient.RpcClient;
import version3_1.client.rpcClient.impl.NettyRpcClient;
import version3_1.comment.domain.message.RpcRequest;
import version3_1.comment.domain.message.RpcResponse;

import java.lang.reflect.Proxy;


/**
 * @Author JH
 * @Date 2024/8/9 20:18
 * @Version 3.0
 */
public class ClientProxy {
    // 定义接口动态指定实现类
    private RpcClient rpcClient;
    public ClientProxy(){
        rpcClient=new NettyRpcClient();
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
                    RpcResponse rpcResponse = rpcClient.sendRequest(request);
                    return rpcResponse.getData();
                });
    }
}
