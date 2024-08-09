### 实现一个基本的RPC调用

- 假设A，B位于不同的服务器，A想远程调用B中查询用户信息的方法（findUserById）
  - 客户端A实现：
    - 对于A调用findUserById时，内部将调用信息处理后发送给服务端B，告诉B要获取User
    - 外部调用方法，内部进行其它的处理——这种场景使用**动态代理**的方式，改写原本方法的处理逻辑
  - 客户端B实现：
    - 监听到A的请求后，接收A的调用信息，并根据信息得到A想调用的服务与方法
    - 根据信息找到对应的服务，通过**反射**进行调用后将结果发送回给A
  - A与B间通信：
    - 使用Java的socket网络编程进行通信
    - 为了方便A ，B之间 对接收的消息进行处理，需要将请求信息和返回信息封装成统一的消息格式