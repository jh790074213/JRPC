package version1_1.client.proxy;

import lombok.AllArgsConstructor;
import version1_1.client.IOClient;
import version1_1.comment.domain.message.RpcRequest;
import version1_1.comment.domain.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * @Author JH
 * @Date 2024/8/9 20:18
 * @Version 1.0
 */
public class ClientProxy {
    /**
     * 创建代理对象进行消息的发送
     * @param host 服务器ip
     * @param port 服务运行的端口
     * @param clazz 被代理的对象
     * @return 执行后的结果
     * @param <T> 被代理对象的类型
     */
    @SuppressWarnings("unchecked")
    public static <T>T createProxy(String host,int port,Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    RpcRequest request = RpcRequest.builder()
                            .interfaceName(method.getDeclaringClass().getName())
                            .methodName(method.getName())
                            .params(args)
                            .paramsType(method.getParameterTypes())
                            .build();
                    RpcResponse rpcResponse = IOClient.sentRpcRequest(host, port, request);
                    return rpcResponse.getData();
                });
    }
}
