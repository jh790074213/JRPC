package version3_1.comment.serializer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import version3_1.comment.domain.message.MessageType;
import version3_1.comment.domain.message.RpcRequest;
import version3_1.comment.domain.message.RpcResponse;
import version3_1.comment.serializer.Serializer;

/**
 * @Author JH
 * @Date 2024/8/13 20:18
 * @Version 2.0
 */
@AllArgsConstructor
public  class  MessageEncoder extends MessageToByteEncoder<Object> {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        //1.写入消息类型，2个字节
        if(msg instanceof RpcRequest){
            out.writeShort(MessageType.REQUEST.getCode());
        }
        else if(msg instanceof RpcResponse){
            out.writeShort(MessageType.RESPONSE.getCode());
        }
        //2.写入序列化方式，2个字节
        out.writeShort(serializer.getType());
        //得到序列化数组
        byte[] serializeBytes = serializer.serialize(msg);
        //3.写入长度，4个字节
        out.writeInt(serializeBytes.length);
        //4.写入序列化数组
        out.writeBytes(serializeBytes);
    }
}
