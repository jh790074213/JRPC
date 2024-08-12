### 引入Netty框架

在客户端与服务端进行网络传输，采用Java原生的socket编程方式，效率低，引入`netty`高性能网络框架，进行优化

Netty 是一个异步的（使用IO多路复用+多线程任务处理并不是指异步IO本质还是同步的）、基于事件驱动的网络应用框架，用于快速开发可维护、高性能的网络服务器和客户端

netty和传统socket编程相比的优势

- io传输由BIO ->NIO模式；底层 池化技术复用资源
- 可以自主编写 编码/解码器，序列化器等等，可拓展性和灵活性高

- 支持TCP,UDP多种传输协议；支持堵塞返回和异步返回

#### pom.xml引入netty

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.51.Final</version>
    <scope>compile</scope>
</dependency>
```

#### 客户端重构

定义传输接口类，通过该接口可以灵活选择不同方式的传输类，耦合性低

```java
public interface RpcClient {
    // 消息发送
    RpcResponse sendRequest(RpcRequest request);
}
```

使用netty传输，实现接口

```java
public class NettyRpcClient implements RpcClient {
    private final String host;
    private final int port;
    public NettyRpcClient(String host,int port){
        this.host=host;
        this.port=port;
    }
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                //NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            // 和服务器建立连接
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            // 发送信息
            channel.writeAndFlush(request);
            // 阻塞式关闭channel，获取特定名字下的channel中的内容（这个在handler中设置）
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            System.out.println("服务器返回结果"+response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

NettyClientInitializer类，配置netty对**消息的处理机制**

- 指定编码器（将消息转为字节数组），解码器（将字节数组转为消息）

- 指定消息格式，消息长度，解决**沾包问题**
  - 什么是沾包问题？
  - netty默认底层通过TCP 进行传输，TCP**是面向流的协议**，接收方在接收到数据时无法直接得知一条消息的具体字节数，不知道数据的界限。由于TCP的流量控制机制，发生沾包或拆包，会导致接收的一个包可能会有多条消息或者不足一条消息，从而会出现接收方少读或者多读导致消息不能读完全的情况发生
  - 在发送消息时，先告诉接收方消息的长度，让接收方读取指定长度的字节，就能避免这个问题
- 指定对接收的消息的处理handler

注：这里的addLast没有先后顺序，netty通过加入的类实现的**接口**来自动识别类实现的是什么功能

```java
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 解决粘包，半包，前四个字节为内容长度
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ObjectEncoder());
        //JDK序列化将字节流转为JAVA对象
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String s) throws ClassNotFoundException {
                return Class.forName(s);
            }
        }));
        // 处理入站信息
        pipeline.addLast(new NettyClientHandler());
    }
}
```

对返回消息的处理

```java
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        // 接收到response, 给channel设计别名，让sendRequest里读取response
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
        ctx.channel().attr(key).set(rpcResponse);
        ctx.channel().close();
    }
    // 异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```

代理类修改，动态指定发送消息的实现类

```java
public class ClientProxy {
    // 定义接口动态指定实现类
    private RpcClient rpcClient;
    public ClientProxy(String host,int port,int choose){
        switch (choose){
            case 0:
                rpcClient=new NettyRpcClient(host,port);
                break;
            case 1:
                rpcClient=new SimpleSocketRpcClient(host,port);
        }
    }
    /**
     * 创建代理对象进行消息的发送
     * @param clazz 被代理的对象
     * @return 执行后的结果
     * @param <T> 被代理对象的类型
     */
    @SuppressWarnings("unchecked")
    public <T>T createProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    RpcRequest request = RpcRequest.builder()
                            .interfaceName(method.getDeclaringClass().getName())
                            .methodName(method.getName())
                            .params(args)
                            .paramsType(method.getParameterTypes())
                            .build();
                    RpcResponse rpcResponse = rpcClient.sendRequest(request);
                    return rpcResponse.getData();
                });
    }
}
```

#### 服务端重构

基于netty的rpc服务端实现

```java
@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private ServiceRegister serviceRegister;
    /**
     * 开启服务器，并监听端口
     * @param port 端口
     */
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("netty服务器启动");
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceRegister));
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
```

实现对传输数据的处理

```java
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceRegister serviceRegister;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String s) throws ClassNotFoundException {
                return Class.forName(s);
            }
        }));
        pipeline.addLast(new NettyServerHandler(serviceRegister));
    }
}
```

```java
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    // 服务注册器
    private ServiceRegister serviceRegister;
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
        // 得到服务实现类
        Object service = serviceRegister.getService(interfaceName);
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object r = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(r);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return RpcResponse.fail();
        }

    }

}
```

整体流程具体到类是

客户端调用RpcClient.sendRequest方法 --->NettyClientInitializer-->Encoder编码 --->发送

服务端RpcServer接收--->NettyServerInitializer-->Decoder解码--->NettyRPCServerHandler ---->getResponse调用---> 返回结果

客户端接收--->NettyServerInitializer-->Decoder解码--->NettyRPCServerHandler处理结果并返回给上层