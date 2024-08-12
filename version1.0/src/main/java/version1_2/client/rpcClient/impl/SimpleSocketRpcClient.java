package version1_2.client.rpcClient.impl;

import version1_2.client.rpcClient.RpcClient;
import version1_2.comment.domain.message.RpcRequest;
import version1_2.comment.domain.message.RpcResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author JH
 * @Date 2024/8/9 20:37
 * @Version 1.0
 */
public class SimpleSocketRpcClient implements RpcClient {
    private final String host;
    private final int port;
    public SimpleSocketRpcClient(String host,int port){
        this.host=host;
        this.port=port;
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request){

        try(Socket socket = new Socket(host, port)) {
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
