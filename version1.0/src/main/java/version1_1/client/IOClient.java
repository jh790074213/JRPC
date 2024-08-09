package version1_1.client;

import version1_1.comment.domain.message.RpcRequest;
import version1_1.comment.domain.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author JH
 * @Date 2024/8/9 20:37
 * @Version 1.0
 */
public class IOClient {
    public static RpcResponse sentRpcRequest(String host,int port, RpcRequest request){

        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject(request);
            outputStream.flush();

            return (RpcResponse) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
