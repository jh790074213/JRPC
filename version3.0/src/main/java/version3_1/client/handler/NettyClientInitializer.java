package version3_1.client.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import version3_1.comment.serializer.handler.MessageDecoder;
import version3_1.comment.serializer.handler.MessageEncoder;
import version3_1.comment.serializer.impl.JsonSerializer;

/**
 * @Author JH
 * @Date 2024/8/12 15:45
 * @Version 1.0
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 解决粘包，半包，前四个字节为内容长度
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,4,4,0,0));
        pipeline.addLast(new MessageEncoder(new JsonSerializer()));
        pipeline.addLast(new MessageDecoder());
        // 处理入站信息
        pipeline.addLast(new NettyClientHandler());
    }
}
