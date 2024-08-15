package version4_1.client.rpcClient;


import version4_1.comment.domain.message.RpcRequest;
import version4_1.comment.domain.message.RpcResponse;

/**
 * @Author JH
 * @Date 2024/8/12 14:59
 * @Version 1.0
 */
public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
