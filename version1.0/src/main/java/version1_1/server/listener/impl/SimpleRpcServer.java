package version1_1.server.listener.impl;

import lombok.AllArgsConstructor;
import version1_1.server.listener.RpcServer;
import version1_1.server.register.ServiceRegister;
import version1_1.server.threads.ServiceThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author JH
 * @Date 2024/8/9 21:20
 * @Version 1.0
 */
@AllArgsConstructor
public class SimpleRpcServer implements RpcServer {
    private ServiceRegister serviceRegister;
    /**
     * 开启监听
     * @param port 监听端口
     */
    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("服务器启动了");
            while (true) {
                //如果没有连接，会堵塞在这里
                Socket socket = serverSocket.accept();
                //有连接，创建一个新的线程执行处理
                new Thread(new ServiceThread(socket,serviceRegister)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }
}
