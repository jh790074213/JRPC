package version1_2.server.listener.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import version1_2.server.listener.RpcServer;
import version1_2.server.handler.NettyServerInitializer;
import version1_2.server.register.ServiceRegister;

/**
 * @Author JH
 * @Date 2024/8/12 16:31
 * @Version 1.0
 */

@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private ServiceRegister serviceRegister;

    /**
     * 开启服务器，并监听端口
     * @param port 端口
     */
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("netty服务器启动");
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceRegister));
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
