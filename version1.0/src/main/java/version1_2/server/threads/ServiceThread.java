package version1_2.server.threads;

import lombok.AllArgsConstructor;
import version1_2.comment.domain.message.RpcRequest;
import version1_2.comment.domain.message.RpcResponse;
import version1_2.server.register.ServiceRegister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Author JH
 * @Date 2024/8/9 21:23
 * @Version 1.0
 */
@AllArgsConstructor
public class ServiceThread implements Runnable{
    private Socket socket;
    private ServiceRegister serviceRegister;

    /**
     * 获取请求并调用执行方法，此方法只有使用socket时用
     */
    @Override
    public void run() {
        try {
            ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream=new ObjectInputStream(socket.getInputStream());
            //读取客户端传过来的request
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            //反射调用服务方法获取返回值
            RpcResponse rpcResponse=getResponse(rpcRequest);
            //向客户端写入response
            outputStream.writeObject(rpcResponse);
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * 通过反射执行方法
     * @param rpcRequest 客户端请求信息
     * @return 响应体
     */
    private RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        // 得到服务实现类
        Object service = serviceRegister.getService(interfaceName);
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object r = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(r);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            System.out.println("方法执行错误");
            e.printStackTrace();
            return RpcResponse.fail();
        }

    }
}
