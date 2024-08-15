package version4_1.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import version4_1.comment.domain.message.RpcRequest;
import version4_1.comment.domain.message.RpcResponse;
import version4_1.server.provider.ServiceProvider;
import version4_1.server.ratelimit.RateLimit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author JH
 * @Date 2024/8/12 16:42
 * @Version 4.0
 */
@Slf4j
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    // 服务注册器
    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        // 处理并获得结果
        RpcResponse rpcResponse = getResponse(rpcRequest);
        ctx.writeAndFlush(rpcResponse);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        //接口限流降级
        RateLimit rateLimit=serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if(!rateLimit.getToken()){
            //如果获取令牌失败，进行限流降级，快速返回结果
            log.info("服务限流！！");
            return RpcResponse.fail();
        }
        // 得到服务实现类
        Object service = serviceProvider.getService(interfaceName);
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object r = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(r);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("NettyServerHandler反射执行方法失败：" + e);
            return RpcResponse.fail();
        }
    }

}
