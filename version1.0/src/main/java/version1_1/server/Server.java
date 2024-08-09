package version1_1.server;

import version1_1.comment.service.UserService;
import version1_1.comment.service.impl.UserServiceImpl;
import version1_1.server.listener.RpcServer;
import version1_1.server.listener.impl.SimpleRpcServer;
import version1_1.server.register.ServiceRegister;

/**
 * @Author JH
 * @Date 2024/8/9 21:03
 * @Version 1.0
 */
public class Server {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();
        // 将服务注册到注册中心
        ServiceRegister serviceRegister=new ServiceRegister();
        serviceRegister.provideServiceInterface(userService);
        // 开启监听
        RpcServer rpcServer=new SimpleRpcServer(serviceRegister);
        rpcServer.start(9999);
    }
}
