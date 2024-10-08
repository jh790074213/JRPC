package version2_1.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.AllArgsConstructor;
import version2_1.comment.serializer.handler.MessageDecoder;
import version2_1.comment.serializer.handler.MessageEncoder;
import version2_1.comment.serializer.impl.JsonSerializer;
import version2_1.server.provider.ServiceProvider;

/**
 * @Author JH
 * @Date 2024/8/12 16:40
 * @Version 1.0
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,4,4,0,0));
        //使用自定义的编/解码器
        pipeline.addLast(new MessageEncoder(new JsonSerializer()));
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
