package version3_1.comment.serializer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import version3_1.comment.domain.message.MessageType;
import version3_1.comment.serializer.Serializer;

import java.util.List;

/**
 * @Author JH
 * @Date 2024/8/13 20:18
 * @Version 2.0
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //1.读取消息类型,2个字节
        short messageType = byteBuf.readShort();
        // 现在还只支持request与response请求
        if(messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()){
            System.out.println("暂不支持此种数据");
            return;
        }
        //2.读取序列化的方式&类型，2个字节
        short serializerType = byteBuf.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if(serializer == null)
            throw new RuntimeException("不存在对应的序列化器");
        //3.读取序列化数组长度，4个字节
        int length = byteBuf.readInt();
        //4.读取序列化数组
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes);
        Object deserialize= serializer.deserialize(bytes, messageType);
        log.info(deserialize.toString());
        list.add(deserialize);
    }
}
