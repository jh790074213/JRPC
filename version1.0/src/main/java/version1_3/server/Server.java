package version1_3.server;


import version1_3.comment.service.UserService;
import version1_3.comment.service.impl.UserServiceImpl;
import version1_3.server.listener.RpcServer;
import version1_3.server.listener.impl.NettyRpcServer;
import version1_3.server.provider.ServiceProvider;

/**
 * @Author JH
 * @Date 2024/8/9 21:03
 * @Version 1.0
 */
public class Server {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();
        // 将服务注册到注册中心
        ServiceProvider serviceRegister=new ServiceProvider("127.0.0.1",9999);
        serviceRegister.provideServiceInterface(userService);
        // 开启监听
        RpcServer rpcServer=new NettyRpcServer(serviceRegister);
        rpcServer.start(9999);
    }
}
