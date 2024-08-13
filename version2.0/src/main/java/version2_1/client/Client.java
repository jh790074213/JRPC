package version2_1.client;


import version2_1.client.proxy.ClientProxy;
import version2_1.comment.domain.po.User;
import version2_1.comment.service.UserService;

/**
 * @Author JH
 * @Date 2024/8/9 20:07
 * @Version 1.0
 */
public class Client {
    public static void main(String[] args) {
        // 创建代理对象进行消息的发送和接收，指定ip，端口号
        ClientProxy clientProxy = new ClientProxy();
        UserService userService = clientProxy.createProxy(UserService.class);

        User user = userService.findUserById(1L);
        System.out.println("查询的用户信息为：" + user.toString());
        User u = User.builder().id(100L).userName("jh").sex(true).build();
        Long userId = userService.insertUser(u);
        System.out.println("插入成功用户id为: " + userId);
    }
}