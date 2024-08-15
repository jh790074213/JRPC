package version4_1.comment.serializer;

import version4_1.comment.serializer.impl.JsonSerializer;
import version4_1.comment.serializer.impl.ObjectSerializer;

/**
 * @Author JH
 * @Date 2024/8/13 20:17
 * @Version 2.0
 */
public interface Serializer {
    /**
     * 把对象序列化成字节数组
     *
     * @param obj 对象
     * @return 序列化后值
     */
    byte[] serialize(Object obj);

    /**
     * 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
     * 其它方式需指定消息格式，再根据message转化成相应的对象
     *
     * @param bytes       传输的字节消息
     * @param messageType 消息类型
     * @return 字节数组转换后的对象
     */
    Object deserialize(byte[] bytes, int messageType);

    /**
     * 返回使用的序列器，是哪个
     *
     * @return 0：java自带序列化方式, 1: json序列化方式
     */
    int getType();

    /**
     * 根据序号取出序列化器，需要其它方式，实现这个接口即可
     *
     * @param code 指定序号
     * @return 序列化器
     */
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
