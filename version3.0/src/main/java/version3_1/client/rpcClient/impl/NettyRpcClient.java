package version3_1.client.rpcClient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import version3_1.client.handler.NettyClientInitializer;
import version3_1.client.rpcClient.RpcClient;
import version3_1.client.serviceCenter.ServiceCenter;
import version3_1.client.serviceCenter.ZKServiceCenter;
import version3_1.comment.domain.message.RpcRequest;
import version3_1.comment.domain.message.RpcResponse;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Author JH
 * @Date 2024/8/12 15:01
 * @Version 1.0
 */
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private final ServiceCenter serviceCenter;
    public NettyRpcClient(ServiceCenter serviceCenter){
        this.serviceCenter=serviceCenter;
    }
    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                //NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            // 从注册中心或者本地缓存中获取IP和端口号
            InetSocketAddress address = serviceCenter.serviceDiscovery(request.getInterfaceName());
            InetAddress host = address.getAddress();
            int port = address.getPort();
            // 和服务器建立连接
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            // 发送信息
            channel.writeAndFlush(request);
            // 阻塞式关闭channel，获取特定名字下的channel中的内容（这个在handler中设置）
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            System.out.println("服务器返回结果"+response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
