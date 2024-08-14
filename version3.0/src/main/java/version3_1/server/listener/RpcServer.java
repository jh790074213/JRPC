package version3_1.server.listener;

/**
 * @Author JH
 * @Date 2024/8/9 21:20
 * @Version 1.0
 */
public interface RpcServer {
    //开启监听
    void start(int port);
    void stop();
}
