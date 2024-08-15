package version3_1.client.retry;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import version3_1.client.rpcClient.RpcClient;
import version3_1.comment.domain.message.RpcRequest;
import version3_1.comment.domain.message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author JH
 * @Date 2024/8/15 15:26
 * @Version 3.0
 */
@Slf4j
public class GuavaRetry {
    public static RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient) {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //无论出现什么异常，都进行重试
                .retryIfException()
                //返回结果为 error时进行重试
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                //重试等待策略：等待 2s 后再进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //重试停止策略：重试达到 3 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (Exception e) {
            log.error("重试错误信息："+e);
        }
        return RpcResponse.fail();
    }
}
