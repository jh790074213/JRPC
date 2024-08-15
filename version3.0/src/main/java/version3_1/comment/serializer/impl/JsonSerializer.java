package version3_1.comment.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import version3_1.comment.domain.message.RpcRequest;
import version3_1.comment.domain.message.RpcResponse;
import version3_1.comment.serializer.Serializer;

/**
 * @Author JH
 * @Date 2024/8/13 20:21
 * @Version 3.0
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 传输的消息分为request与response
        switch (messageType){
            case 0:
                RpcRequest request = JSON.parseObject(bytes, RpcRequest.class);
                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                // 对转换后的request中的params属性逐个进行类型判断
                for(int i = 0; i < objects.length; i++){
                    Class<?> paramsType = request.getParamsType()[i];
                    //判断每个对象类型是否和paramsTypes中的一致
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        //如果不一致，就行进行类型转换
                        objects[i] = JSON.parseObject(request.getParams()[i].toString(),request.getParamsType()[i]);
                    }else{
                        //如果一致就直接赋给objects[i]
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);
                // 只有成功时才有数据，否则会报空指针
                if(response.getCode() != 500){
                    Class<?> dataType = response.getDataType();
                    //判断转化后的response对象中的data的类型是否正确
                    if(! dataType.isAssignableFrom(response.getData().getClass())){
                        response.setData(JSON.parseObject(response.getData().toString(),dataType));
                    }
                }
                obj = response;
                break;
            default:
                System.out.println("暂时不支持此种消息");
                throw new RuntimeException();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
